package edu.oficina.pruebas;

import edu.oficina.modelo.Turno;
import edu.oficina.servicios.TurnoService;
import java.time.LocalDateTime;
import java.util.List;
import static edu.oficina.pruebas.PreparacionPruebas.*;

/** Contrasta la cola con todos los campos pendientes persistidos en cada transición. */
public final class ValidacionFinalTurnos {
    private record Fila(long numero,int cliente,int servicio,LocalDateTime creacion,
            Turno.Estado estado,LocalDateTime finalizacion) { }
    private static List<Fila> filas(List<Turno> turnos) {
        return turnos.stream().map(t -> new Fila(t.getNumero(),t.getCodigoCliente(),t.getCodigoServicio(),
                t.getFechaGeneracion(),t.getEstado(),t.getFechaFinalizacion())).toList();
    }
    private static void coherente(TurnoService servicio,PreparacionPruebas p) throws Exception {
        verificar(filas(servicio.listarPendientes()).equals(filas(p.turnos.listarPendientes())),
                "Campos y orden de cola coinciden con los pendientes de SQLite");
    }
    public static void main(String[] args) throws Exception {
        try (PreparacionPruebas p=new PreparacionPruebas()) {
            TurnoService servicio=new TurnoService(p.turnos);
            int[] codigos={1,2,1,3,2};
            for (int i=0;i<5;i++) { servicio.generar(1001+i,codigos[i]); coherente(servicio,p); }
            servicio.cancelar(3); coherente(servicio,p);
            servicio.atenderSiguiente(); coherente(servicio,p);
            servicio.atenderSiguiente(); coherente(servicio,p);
            verificar(servicio.generar(1001,3).getNumero()==6,"Cliente finalizado solicita nuevo turno sin reutilizar número");
            coherente(servicio,p);
            p.sql("CREATE TRIGGER falla_final_turnos BEFORE UPDATE ON turnos BEGIN SELECT RAISE(ABORT,'fallo final'); END");
            rechazar(servicio::atenderSiguiente,"Atención fallida no desincroniza"); coherente(servicio,p);
            p.sql("DROP TRIGGER falla_final_turnos");
            TurnoService reinicio=new TurnoService(p.turnos); coherente(reinicio,p);
            reinicio.cancelar(6); coherente(reinicio,p);
            verificar(reinicio.atenderSiguiente().getNumero()==4,"FIFO después de reconstruir"); coherente(reinicio,p);
            verificar(reinicio.atenderSiguiente().getNumero()==5,"Último turno FIFO"); coherente(reinicio,p);
            verificar(reinicio.atenderSiguiente()==null && p.turnos.historialCliente(1001).size()==2,
                    "Cola vacía e historial íntegro");
            verificar(p.escalar("SELECT count(*) FROM turnos")==6,"No se elimina historial al cerrar turnos");
            System.out.println("VALIDACION FINAL TURNOS CORRECTA");
        }
    }
}
