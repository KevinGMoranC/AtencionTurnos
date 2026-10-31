package edu.oficina.pruebas;
import edu.oficina.modelo.*;
import edu.oficina.persistencia.InicializadorBD;
import edu.oficina.servicios.TurnoService;
import java.time.LocalDateTime;
import java.util.List;
import static edu.oficina.pruebas.PreparacionPruebas.*;

/** Prueba turnos con los DAO del integrante 3; no utiliza ClienteService ni ServicioService. */
public final class PruebaTurnos {
    public static void main(String[] args) throws Exception {
        try (PreparacionPruebas p = new PreparacionPruebas()) {
            TurnoService s = new TurnoService(p.turnos);
            verificar(s.atenderSiguiente() == null, "Atención de cola vacía");
            int[] servicios={1,2,1,3,2};
            for (int i=0;i<5;i++) verificar(s.generar(1001+i,servicios[i]).getNumero()==i+1,"Número automático " + (i+1));
            rechazar(() -> s.generar(1001,1), "Dos pendientes por cliente");
            Turno cancelado=s.cancelar(3);
            verificar(cancelado.getEstado()==Turno.Estado.CANCELADO && cancelado.getFechaFinalizacion()!=null,"Estado y fecha de cancelación");
            verificar(s.listarPendientes().stream().map(Turno::getNumero).toList().equals(List.of(1L,2L,4L,5L)),"Cancelación conserva orden");
            Turno atendido=s.atenderSiguiente();
            verificar(atendido.getNumero()==1 && atendido.getEstado()==Turno.Estado.ATENDIDO && atendido.getFechaFinalizacion()!=null,"Atención FIFO y fecha");
            verificar(s.atenderSiguiente().getNumero()==2,"Segundo atendido");
            new InicializadorBD(p.conexiones).inicializar();
            TurnoService reinicio=new TurnoService(p.turnos);
            verificar(reinicio.listarPendientes().stream().map(Turno::getNumero).toList().equals(List.of(4L,5L)),"Reconstrucción de pendientes al reiniciar");
            verificar(reinicio.atenderSiguiente().getNumero()==4 && reinicio.atenderSiguiente().getNumero()==5,"FIFO después de reinicio");
            verificar(reinicio.atenderSiguiente()==null,"Sin pendientes restantes");
            rechazar(() -> reinicio.cancelar(1),"Cancelar atendido");
            rechazar(() -> reinicio.cancelar(3),"Repetir cancelación");
            rechazar(() -> p.turnos.finalizar(3,Turno.Estado.ATENDIDO,LocalDateTime.now()),"DAO tampoco reactiva cancelado");
            verificar(p.escalar("SELECT count(*) FROM turnos WHERE estado='ATENDIDO'")==4 && p.escalar("SELECT count(*) FROM turnos WHERE estado='CANCELADO'")==1,"Estados persistidos de la secuencia");
            verificar(reinicio.generar(1001,1).getNumero()==6,"Numeración no reutilizada");
            p.sql("CREATE TRIGGER falla_turno BEFORE UPDATE ON turnos BEGIN SELECT RAISE(ABORT,'fallo de prueba'); END");
            rechazar(() -> reinicio.atenderSiguiente(),"Fallo al atender");
            rechazar(() -> reinicio.cancelar(6),"Fallo al cancelar");
            verificar(reinicio.consultarSiguiente().getNumero()==6 && p.turnos.tienePendienteCliente(1001),"Cola y base intactas ante errores");
            p.sql("DROP TRIGGER falla_turno");
            reinicio.cancelar(6);
            p.sql("CREATE TRIGGER falla_generacion BEFORE INSERT ON turnos BEGIN SELECT RAISE(ABORT,'fallo de prueba'); END");
            rechazar(() -> reinicio.generar(1001,1),"Fallo al generar");
            verificar(reinicio.listarPendientes().isEmpty() && p.escalar("SELECT count(*) FROM turnos")==6,"No se encola lo que no se guardó");
            p.sql("DROP TRIGGER falla_generacion");
            p.clientes.actualizar(new Cliente(1001,"Ana López","55501001",false));
            rechazar(() -> reinicio.generar(1001,1),"Cliente inactivo");
            p.servicios.actualizar(new Servicio(1,"Información",false));
            rechazar(() -> reinicio.generar(1002,1),"Servicio inactivo");
            rechazar(() -> reinicio.generar(9999,2),"Cliente no registrado");
            rechazar(() -> reinicio.generar(1002,99),"Servicio no registrado");
            System.out.println("PRUEBA TURNOS CORRECTA");
        }
    }
}
