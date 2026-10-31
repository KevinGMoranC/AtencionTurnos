package edu.oficina.pruebas;

import edu.oficina.controlador.ControladorConsola;
import edu.oficina.vista.VistaConsola;
import edu.oficina.servicios.ClienteService;
import edu.oficina.servicios.ServicioService;
import edu.oficina.servicios.TurnoService;
import edu.oficina.servicios.ConsultaService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import static edu.oficina.pruebas.PreparacionPruebas.*;

/** Recorre el controlador real con entradas y salidas de consola en memoria. */
public final class PruebaMenuDia3 {
    private static String ejecutar(PreparacionPruebas p, String... lineas) throws Exception {
        String entrada = String.join(System.lineSeparator(), lineas) + System.lineSeparator();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (PrintStream salida = new PrintStream(buffer, true, StandardCharsets.UTF_8);
             VistaConsola vista = new VistaConsola(
                     new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)), salida)) {
            new ControladorConsola(vista, new ClienteService(p.clientes, p.turnos),
                    new ServicioService(p.servicios, p.turnos), new TurnoService(p.turnos),
                    new ConsultaService(p.turnos, p.clientes)).ejecutar();
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws Exception {
        try (PreparacionPruebas p = new PreparacionPruebas()) {
            String salida = ejecutar(p,
                "1", "1", "1010", "Cliente nuevo", "00123456",
                "4", "1010", "Cliente actualizado", "87654321", "3", "1010", "0",
                "2", "1", "4", "Servicio nuevo", "3", "4", "Servicio actualizado", "2", "0",
                "3", "1010", "4", "6",
                "1", "5", "1010", "n", "0",
                "2", "4", "4", "n", "0",
                "5", "1", "8", "9", "1010", "10",
                "1", "5", "1010", "n", "2", "0",
                "3", "1001", "1", "4", "7", "6", "4", "11");
            verificar(p.clientes.buscar(1010).getNombreCompleto().equals("Cliente actualizado")
                    && !p.clientes.buscar(1010).isActivo(), "Menú registra, actualiza y cambia estado del cliente");
            verificar(p.servicios.buscar(4).getNombre().equals("Servicio actualizado")
                    && p.servicios.buscar(4).isActivo(), "Menú actualiza servicio y rechaza desactivación con pendiente");
            verificar(salida.contains("No se puede desactivar un cliente con turno pendiente")
                    && salida.contains("No se puede desactivar un servicio con turnos pendientes"),
                    "El menú presenta los rechazos de ambos módulos");
            verificar(salida.contains("TOTAL | 0 | 0 | 1 | 1"), "Resumen visible después de cancelar");
            verificar(salida.contains("Turno 2 atendido.") && salida.contains("ATENDIDO")
                    && salida.contains("CANCELADO"), "Atención y consultas desde el menú");
            verificar(salida.contains("Ana López") && salida.contains("Información"),
                    "La atención muestra número, nombre del cliente y servicio");
            verificar(salida.contains("No existen turnos pendientes.")
                    && salida.contains("Datos conservados. Hasta luego."), "Cola vacía y salida normal");
            verificar(p.turnos.listarPendientes().isEmpty(), "No quedan pendientes después del recorrido");
        }
        try (PreparacionPruebas p = new PreparacionPruebas()) {
            String salida = ejecutar(p,
                "texto", "99", "1", "1", "0", "Nombre", "12345678",
                "1", "1011", "", "12345678", "1", "1011", "Nombre", "abcdef12",
                "3", "9999", "5", "1001", "talvez", "0", "5", "abc", "4", "11");
            verificar(salida.contains("número entero válido") && salida.contains("Opción inexistente"),
                    "Opciones y números inválidos permiten continuar");
            verificar(salida.contains("nombre completo es obligatorio") && salida.contains("8 dígitos")
                    && salida.contains("código del cliente debe ser positivo"), "Validaciones de campos visibles");
            verificar(salida.contains("Cliente no registrado") && salida.contains("Escriba s o n")
                    && salida.contains("número de turno válido"), "Código inexistente y estado/turno inválidos");
            verificar(p.clientes.listar().size() == 5, "Los intentos inválidos no crean registros");
            verificar(ejecutar(p).contains("Entrada finalizada"), "Fin de entrada sin bucle infinito");
        }
        try (PreparacionPruebas p = new PreparacionPruebas()) {
            p.sql("CREATE TRIGGER falla_ui BEFORE UPDATE ON turnos BEGIN SELECT RAISE(ABORT,'fallo de prueba UI'); END");
            String salida = ejecutar(p, "3", "1001", "1", "4", "6", "11");
            verificar(salida.contains("fallo de prueba UI") && salida.contains("PENDIENTE"),
                    "El menú informa un fallo de SQLite y continúa mostrando el pendiente");
            verificar(p.turnos.listarPendientes().size() == 1, "No se pierde el turno ante el fallo desde el menú");
        }
        System.out.println("MENU DIA 3 CORRECTO");
    }
}
