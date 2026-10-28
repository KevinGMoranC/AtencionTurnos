package edu.oficina.pruebas;

import edu.oficina.dao.ClienteDAO;
import edu.oficina.dao.ServicioDAO;
import edu.oficina.dao.TurnoDAO;
import edu.oficina.modelo.Cliente;
import edu.oficina.modelo.Servicio;
import edu.oficina.modelo.Turno;
import edu.oficina.persistencia.InicializadorBD;
import edu.oficina.persistencia.ProveedorConexion;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import static edu.oficina.pruebas.PreparacionPruebas.*;

/** Observa cierres de los DAO reales mediante proxies de prueba, sin cambiar producción. */
public final class PruebaRecursosJdbcDia4 {
    private static final class Observador {
        private final ProveedorConexion real;
        private final int[] abiertos=new int[3];
        private final int[] cerrados=new int[3];
        private int preparados;
        private Observador(ProveedorConexion real) { this.real=real; }
        private Connection abrir() throws Exception { return envolver(real.abrir(),Connection.class,0); }

        @SuppressWarnings("unchecked")
        private <T> T envolver(T objeto,Class<T> interfaz,int categoria) {
            abiertos[categoria]++;
            boolean[] cerrado={false};
            return (T) Proxy.newProxyInstance(PruebaRecursosJdbcDia4.class.getClassLoader(),
                    new Class<?>[]{interfaz},(proxy,metodo,argumentos) -> {
                        Object retorno;
                        try { retorno=metodo.invoke(objeto,argumentos); }
                        catch (InvocationTargetException e) { throw e.getCause(); }
                        if (metodo.getName().equals("close") && !cerrado[0]) {
                            cerrado[0]=true;cerrados[categoria]++;
                        }
                        if (retorno instanceof PreparedStatement s) {
                            preparados++;return envolver(s,PreparedStatement.class,1);
                        }
                        if (retorno instanceof Statement s) return envolver(s,Statement.class,1);
                        if (retorno instanceof ResultSet r) return envolver(r,ResultSet.class,2);
                        return retorno;
                    });
        }
    }
    public static void main(String[] args) throws Exception {
        try (PreparacionPruebas p=new PreparacionPruebas()) {
            Observador observador=new Observador(p.conexiones);
            ProveedorConexion proveedor=() -> {
                // El proveedor JDBC solo declara SQLException; abrir no añade otros errores.
                try { return observador.abrir(); }
                catch (java.sql.SQLException e) { throw e; }
                catch (Exception e) { throw new java.sql.SQLException(e); }
            };
            new InicializadorBD(proveedor).inicializar();
            ClienteDAO clientes=new ClienteDAO(proveedor);
            ServicioDAO servicios=new ServicioDAO(proveedor);
            TurnoDAO turnos=new TurnoDAO(proveedor);
            clientes.listar();clientes.buscar(1001);
            clientes.insertar(new Cliente(2000,"Cliente de recursos","00001111",true));
            clientes.actualizar(new Cliente(2000,"Nombre editado","00002222",true));
            servicios.listar();servicios.buscar(1);
            servicios.insertar(new Servicio(4,"Servicio de recursos",true));
            servicios.actualizar(new Servicio(4,"Servicio editado",true));
            Turno turno=turnos.crearPendiente(2000,4);
            turnos.listarPendientes();turnos.tienePendienteCliente(2000);turnos.tienePendienteServicio(4);
            turnos.finalizar(turno.getNumero(),Turno.Estado.ATENDIDO,LocalDateTime.now());
            turnos.buscar(turno.getNumero());turnos.listarPorEstado(Turno.Estado.ATENDIDO);
            turnos.historialCliente(2000);turnos.resumenPorServicio();
            rechazar(() -> clientes.insertar(new Cliente(2000,"Duplicado","12345678",true)),
                    "Excepción de inserción cierra sus recursos");
            rechazar(() -> turnos.crearPendiente(9999,1),"Validación dentro de transacción cierra recursos");
            Turno pendiente=turnos.crearPendiente(1001,1);
            p.sql("CREATE TRIGGER falla_recursos BEFORE UPDATE ON turnos BEGIN SELECT RAISE(ABORT,'fallo observado'); END");
            rechazar(() -> turnos.finalizar(pendiente.getNumero(),Turno.Estado.CANCELADO,LocalDateTime.now()),
                    "Rollback tras SQLException también libera recursos");
            String[] nombres={"Connection","Statement/PreparedStatement","ResultSet"};
            for (int i=0;i<3;i++) {
                verificar(observador.abiertos[i]>0 && observador.abiertos[i]==observador.cerrados[i],
                        nombres[i]+": abiertos="+observador.abiertos[i]+", cerrados="+observador.cerrados[i]);
            }
            verificar(observador.preparados>0,"Se observaron PreparedStatement en operaciones reales");
            System.out.println("RECURSOS JDBC DIA 4 CORRECTOS");
        }
    }
}
