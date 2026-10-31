package edu.oficina.pruebas;

import edu.oficina.modelo.Turno;
import edu.oficina.servicios.TurnoService;
import java.util.List;
import static edu.oficina.pruebas.PreparacionPruebas.*;

/** Extremos de cola y errores de escritura usando la persistencia real del integrante 3. */
public final class PruebaTurnosDia4 {
    public static void main(String[] args) throws Exception {
        try (PreparacionPruebas p = new PreparacionPruebas()) {
            TurnoService servicio=new TurnoService(p.turnos);
            for (int i=0;i<5;i++) servicio.generar(1001+i,1+i%3);
            servicio.cancelar(1); servicio.cancelar(5); servicio.cancelar(3);
            verificar(servicio.listarPendientes().stream().map(Turno::getNumero).toList().equals(List.of(2L,4L)),
                    "Cancelar primero, último e intermedio conserva el orden");
            verificar(servicio.atenderSiguiente().getNumero()==2, "Atención después de cancelaciones");
            servicio.cancelar(4);
            verificar(servicio.consultarSiguiente()==null && servicio.atenderSiguiente()==null,
                    "Cancelación del único pendiente deja extremos vacíos");
            rechazar(() -> servicio.cancelar(4), "Cancelar dos veces");
            rechazar(() -> servicio.cancelar(2), "Cancelar atendido");
            rechazar(() -> servicio.cancelar(0), "Número no positivo");
            rechazar(() -> servicio.generar(0,1), "Código de cliente no positivo");
            rechazar(() -> servicio.generar(1001,0), "Código de servicio no positivo");
            verificar(servicio.generar(1001,2).getNumero()==6, "Volver a encolar sin reutilizar números");
            p.sql("CREATE TRIGGER falla_cierre_dia4 BEFORE UPDATE ON turnos BEGIN SELECT RAISE(ABORT,'fallo cierre'); END");
            rechazar(servicio::atenderSiguiente, "Fallo al atender");
            rechazar(() -> servicio.cancelar(6), "Fallo al cancelar");
            verificar(servicio.consultarSiguiente().getNumero()==6
                    && p.turnos.buscar(6).getEstado()==Turno.Estado.PENDIENTE
                    && p.turnos.buscar(6).getFechaFinalizacion()==null,
                    "Ni estado, fecha ni cola cambian si SQLite rechaza el cierre");
            p.sql("DROP TRIGGER falla_cierre_dia4");
            servicio.cancelar(6);
            p.sql("CREATE TRIGGER falla_generar_dia4 BEFORE INSERT ON turnos BEGIN SELECT RAISE(ABORT,'fallo alta'); END");
            rechazar(() -> servicio.generar(1001,1), "Fallo al crear no publica un pendiente");
            verificar(servicio.listarPendientes().isEmpty(), "Cola vacía después del fallo de creación");
            p.sql("DROP TRIGGER falla_generar_dia4");
            verificar(servicio.generar(1001,1).getNumero()==7,
                    "Los turnos persistidos mantienen numeración sin reutilización");
            System.out.println("TURNOS DIA 4 CORRECTOS");
        }
    }
}
