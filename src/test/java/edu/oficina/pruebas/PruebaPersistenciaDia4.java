package edu.oficina.pruebas;

import edu.oficina.dao.ClienteDAO;
import edu.oficina.dao.TurnoDAO;
import edu.oficina.modelo.Cliente;
import edu.oficina.persistencia.ConexionBD;
import edu.oficina.persistencia.InicializadorBD;
import edu.oficina.persistencia.ProveedorConexion;
import edu.oficina.servicios.ClienteService;
import edu.oficina.servicios.TurnoService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import static edu.oficina.pruebas.PreparacionPruebas.*;

public final class PruebaPersistenciaDia4 {
    private static long escalar(ProveedorConexion proveedor,String sql) throws Exception {
        try (Connection c=proveedor.abrir();Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)) {
            r.next();return r.getLong(1);
        }
    }
    private static void ejecutar(ProveedorConexion proveedor,String sql) throws Exception {
        try (Connection c=proveedor.abrir();Statement s=c.createStatement()) { s.execute(sql); }
    }
    public static void main(String[] args) throws Exception {
        try (PreparacionPruebas p=new PreparacionPruebas()) {
            try (Connection c=p.conexiones.abrir();Statement s=c.createStatement();
                 ResultSet r=s.executeQuery("PRAGMA foreign_key_list(turnos)")) {
                int relaciones=0;
                while (r.next()) {
                    verificar(r.getString("table").equals("clientes") || r.getString("table").equals("servicios"),
                            "Relación de turnos a una entidad válida");
                    relaciones++;
                }
                verificar(relaciones==2,"Dos claves foráneas en turnos");
            }
            verificar(p.escalar("PRAGMA foreign_keys")==1,"Cada conexión mantiene claves foráneas habilitadas");
            verificar(p.escalar("SELECT count(*) FROM clientes")==5 && p.escalar("SELECT count(*) FROM servicios")==3
                    && p.escalar("SELECT count(*) FROM turnos")==0,"Carga inicial exacta sin turnos");
            p.clientes.actualizar(new Cliente(1003,"Edición persistente","00001111",false));
            TurnoService primeraSesion=new TurnoService(p.turnos);
            primeraSesion.generar(1001,1);primeraSesion.generar(1002,2);
            new InicializadorBD(p.conexiones).inicializar();
            ClienteDAO nuevosClientes=new ClienteDAO(p.conexiones);
            TurnoDAO nuevosTurnos=new TurnoDAO(p.conexiones);
            ClienteService clientesRecuperados=new ClienteService(nuevosClientes,nuevosTurnos);
            TurnoService turnosRecuperados=new TurnoService(nuevosTurnos);
            verificar(!clientesRecuperados.buscar(1003).isActivo()
                    && clientesRecuperados.buscar(1003).getNombreCompleto().equals("Edición persistente"),
                    "Nuevos DAO y servicio recuperan edición e inactivo después de cerrar conexiones");
            verificar(turnosRecuperados.listarPendientes().stream().map(t -> t.getNumero()).toList().equals(List.of(1L,2L)),
                    "Se recuperan pendientes en su orden desde SQLite");
            turnosRecuperados.cancelar(1);turnosRecuperados.atenderSiguiente();
            verificar(p.escalar("SELECT count(*) FROM turnos")==2
                    && p.escalar("SELECT count(*) FROM clientes")==5,"No se borran historiales ni se duplican datos iniciales");

            Path archivo=p.carpeta.resolve("inicializacion-fallida.db");
            ProveedorConexion proveedor=() -> ConexionBD.obtenerConexion(archivo);
            try {
                ejecutar(proveedor,"CREATE TABLE servicios(codigo INTEGER PRIMARY KEY CHECK(codigo>0), nombre TEXT NOT NULL CHECK(length(trim(nombre))>0), activo INTEGER NOT NULL CHECK(activo IN(0,1)))");
                ejecutar(proveedor,"CREATE TRIGGER bloquear_carga BEFORE INSERT ON servicios BEGIN SELECT RAISE(ABORT,'fallo inicializacion'); END");
                InicializadorBD inicializador=new InicializadorBD(proveedor);
                rechazar(inicializador::inicializar,"Fallo real durante la carga inicial");
                verificar(escalar(proveedor,"PRAGMA user_version")==0
                        && escalar(proveedor,"SELECT count(*) FROM sqlite_master WHERE type='table' AND name='clientes'")==0
                        && escalar(proveedor,"SELECT count(*) FROM servicios")==0,
                        "Rollback revierte esquema y datos parciales y no marca inicialización completa");
                ejecutar(proveedor,"DROP TRIGGER bloquear_carga");
                inicializador.inicializar();inicializador.inicializar();
                verificar(escalar(proveedor,"PRAGMA user_version")==1
                        && escalar(proveedor,"SELECT count(*) FROM clientes")==5
                        && escalar(proveedor,"SELECT count(*) FROM servicios")==3,
                        "Reintento válido carga una vez después del fallo");
            } finally { Files.deleteIfExists(archivo); }
            System.out.println("PERSISTENCIA DIA 4 CORRECTA");
        }
    }
}
