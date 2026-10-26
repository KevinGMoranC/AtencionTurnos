package edu.oficina.pruebas;

import edu.oficina.modelo.Turno;
import edu.oficina.servicios.ConsultaService;
import edu.oficina.servicios.ServicioService;
import edu.oficina.servicios.TurnoService;
import edu.oficina.reportes.ResumenServicio;
import java.util.List;
import static edu.oficina.pruebas.PreparacionPruebas.*;

/** Comprueba las consultas con la secuencia original del catedrático. */
public final class PruebaConsultasDia3 {
    public static void main(String[] args) throws Exception {
        try (PreparacionPruebas p = new PreparacionPruebas()) {
            ConsultaService consultas = new ConsultaService(p.turnos, p.clientes);
            ServicioService servicios = new ServicioService(p.servicios, p.turnos);
            servicios.registrar(4, "Sin turnos"); servicios.cambiarEstado(4, false);
            verificar(consultas.resumenPorServicio().stream().allMatch(f -> f.total() == 0),
                    "Todos los servicios aparecen inicialmente en cero");
            TurnoService turnos = new TurnoService(p.turnos);
            int[] codigos = {1,2,1,3,2};
            for (int i=0;i<5;i++) turnos.generar(1001+i, codigos[i]);
            turnos.cancelar(3); turnos.atenderSiguiente(); turnos.atenderSiguiente();
            TurnoService recargado = new TurnoService(p.turnos);
            recargado.atenderSiguiente(); recargado.atenderSiguiente();
            verificar(consultas.listarPorEstado(Turno.Estado.ATENDIDO).stream().map(Turno::getNumero).toList()
                    .equals(List.of(1L,2L,4L,5L)), "Consulta de atendidos ordenada");
            verificar(consultas.listarPorEstado(Turno.Estado.CANCELADO).get(0).getNumero() == 3,
                    "Consulta de cancelados");
            verificar(consultas.historialCliente(1003).get(0).getEstado() == Turno.Estado.CANCELADO,
                    "Historial de María García incluye turno cancelado");
            verificar(consultas.historialCliente(1005).size() == 1, "Historial filtra por cliente");
            rechazar(() -> consultas.historialCliente(9999), "Historial de cliente inexistente");
            verificar(p.turnos.buscar(9999) == null, "Búsqueda de turno inexistente");
            var filas = consultas.resumenPorServicio();
            verificar(fila(filas,1,0,1,1,2) && fila(filas,2,0,2,0,2) && fila(filas,3,0,1,0,1),
                    "Resumen exacto del enunciado");
            verificar(fila(filas,4,0,0,0,0), "Servicio inactivo sin turnos aparece en cero");
            verificar(filas.stream().mapToLong(ResumenServicio::total).sum() == 5,
                    "Total sin duplicación por joins");
            System.out.println("CONSULTAS DIA 3 CORRECTAS");
        }
    }
    private static boolean fila(List<ResumenServicio> filas, int codigo, long p, long a, long c, long total) {
        return filas.stream().anyMatch(f -> f.codigoServicio() == codigo && f.pendientes() == p
                && f.atendidos() == a && f.cancelados() == c && f.total() == total);
    }
}
