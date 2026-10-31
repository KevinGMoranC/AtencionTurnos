package edu.oficina.pruebas;

import edu.oficina.modelo.Turno;
import edu.oficina.servicios.ClienteService;
import edu.oficina.servicios.ServicioService;
import edu.oficina.servicios.TurnoService;
import java.util.List;
import static edu.oficina.pruebas.PreparacionPruebas.*;

/** Coordina los tres servicios reales, sin duplicar DAO ni estructuras. */
public final class PruebaIntegracionTurnosDia3 {
    public static void main(String[] args) throws Exception {
        try (PreparacionPruebas p = new PreparacionPruebas()) {
            ClienteService clientes = new ClienteService(p.clientes, p.turnos);
            ServicioService servicios = new ServicioService(p.servicios, p.turnos);
            TurnoService turnos = new TurnoService(p.turnos);
            clientes.registrar(2001, "Cliente de integración", "12345678");
            clientes.registrar(2002, "Segundo cliente", "87654321");
            servicios.registrar(4, "Servicio de integración");
            servicios.cambiarEstado(4, false);
            rechazar(() -> turnos.generar(2001, 4), "Turnos rechaza servicio desactivado por ServicioService");
            servicios.cambiarEstado(4, true);
            clientes.cambiarEstado(2001, false);
            rechazar(() -> turnos.generar(2001, 4), "Turnos rechaza cliente desactivado por ClienteService");
            clientes.cambiarEstado(2001, true);
            verificar(turnos.generar(2001, 4).getNumero() == 1, "Primer número persistido tras rechazos de validación");
            verificar(turnos.generar(2002, 2).getNumero() == 2, "Numeración siguiente");
            rechazar(() -> turnos.generar(2001, 1), "Un solo pendiente entre servicios distintos");
            rechazar(() -> servicios.cambiarEstado(4, false), "Servicio con pendiente no se desactiva");
            rechazar(() -> clientes.cambiarEstado(2001, false), "Cliente con pendiente no se desactiva");
            TurnoService recargado = new TurnoService(p.turnos);
            verificar(recargado.listarPendientes().stream().map(Turno::getNumero).toList().equals(List.of(1L,2L)),
                    "Reconstrucción ordenada desde SQLite");
            Turno atendido = recargado.atenderSiguiente();
            verificar(atendido.getNumero() == 1 && atendido.getEstado() == Turno.Estado.ATENDIDO
                    && atendido.getFechaFinalizacion() != null, "FIFO y fecha de atención");
            Turno cancelado = recargado.cancelar(2);
            verificar(cancelado.getEstado() == Turno.Estado.CANCELADO
                    && cancelado.getFechaFinalizacion() != null, "Cancelación y fecha persistida");
            clientes.cambiarEstado(2001, false);
            servicios.cambiarEstado(4, false);
            verificar(recargado.generar(2002, 1).getNumero() == 3, "No reutiliza números de atendidos o cancelados");
            verificar(new TurnoService(p.turnos).consultarSiguiente().getNumero() == 3,
                    "Solo se reconstruye el pendiente vigente");
            verificar(p.turnos.buscar(1).getEstado() == Turno.Estado.ATENDIDO
                    && p.turnos.buscar(2).getEstado() == Turno.Estado.CANCELADO,
                    "Las consultas nuevas conservan el estado de cada turno");
            System.out.println("INTEGRACION TURNOS DIA 3 CORRECTA");
        }
    }
}
