package edu.oficina.pruebas;
import edu.oficina.modelo.*;
import edu.oficina.persistencia.*;
import java.sql.*;
import java.time.LocalDateTime;
import static edu.oficina.pruebas.PreparacionPruebas.*;
public final class PruebaPersistencia {
    public static void main(String[] args) throws Exception {
        try (PreparacionPruebas p=new PreparacionPruebas()) {
            verificar(p.escalar("PRAGMA foreign_keys")==1,"Claves foráneas habilitadas");
            verificar(p.clientes.listar().size()==5 && p.servicios.listar().size()==3 && p.escalar("SELECT count(*) FROM turnos")==0,"Cinco clientes, tres servicios y cero turnos");
            verificar(p.clientes.buscar(1004).getNombreCompleto().equals("José Hernández"),"Datos iniciales exactos");
            new InicializadorBD(p.conexiones).inicializar();
            verificar(p.clientes.listar().size()==5,"Inicialización idempotente");
            p.clientes.actualizar(new Cliente(1001,"Ana editada","12345678",false));
            new InicializadorBD(p.conexiones).inicializar();
            verificar(!p.clientes.buscar(1001).isActivo() && p.clientes.buscar(1001).getNombreCompleto().equals("Ana editada"),"Reinicio no restablece datos editados");
            p.clientes.actualizar(new Cliente(1001,"Ana editada","12345678",true));
            Turno t=p.turnos.crearPendiente(1001,1);
            rechazar(() -> p.turnos.crearPendiente(1001,2),"Un pendiente también en persistencia");
            rechazar(() -> p.clientes.actualizar(new Cliente(1001,"Ana editada","12345678",false)),"Trigger impide desactivar cliente con pendiente");
            rechazar(() -> p.servicios.actualizar(new Servicio(1,"Información",false)),"Trigger impide desactivar servicio con pendiente");
            p.turnos.finalizar(t.getNumero(),Turno.Estado.ATENDIDO,LocalDateTime.now());
            rechazar(() -> p.sql("DELETE FROM clientes WHERE codigo=1001"),"Relación protege el historial");
            verificar(p.escalar("SELECT count(*) FROM turnos")==1,"Se conserva turno finalizado");
            verificar(p.turnos.crearPendiente(1001,2).getNumero()==2,"Nuevo turno después de finalizar");
            System.out.println("PRUEBA PERSISTENCIA CORRECTA");
        }
    }
}
