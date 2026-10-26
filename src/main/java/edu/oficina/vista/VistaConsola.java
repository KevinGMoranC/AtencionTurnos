package edu.oficina.vista;

import edu.oficina.modelo.Cliente;
import edu.oficina.modelo.Servicio;
import edu.oficina.modelo.Turno;
import edu.oficina.reportes.ResumenServicio;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

/** Entrada y presentación. No contiene SQL ni reglas de atención. */
public final class VistaConsola implements AutoCloseable {
    private final Scanner entrada;
    private final PrintStream salida;

    public VistaConsola(InputStream entrada, PrintStream salida) {
        this.entrada = new Scanner(Objects.requireNonNull(entrada), StandardCharsets.UTF_8);
        this.salida = Objects.requireNonNull(salida);
    }

    public void mensaje(String mensaje) { salida.println(mensaje); }

    public String leerTexto(String pregunta) {
        salida.print(pregunta + ": ");
        if (!entrada.hasNextLine()) throw new NoSuchElementException("Fin de entrada");
        return entrada.nextLine().trim();
    }

    public int leerEntero(String pregunta) {
        try { return Integer.parseInt(leerTexto(pregunta)); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Ingrese un número entero válido."); }
    }

    public long leerNumeroTurno(String pregunta) {
        try { return Long.parseLong(leerTexto(pregunta)); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Ingrese un número de turno válido."); }
    }

    public boolean leerActivo() {
        String valor = leerTexto("Activo (s/n)");
        if (valor.equalsIgnoreCase("s")) return true;
        if (valor.equalsIgnoreCase("n")) return false;
        throw new IllegalArgumentException("Escriba s o n para el estado.");
    }

    public void menuPrincipal() {
        mensaje(""); mensaje("ATENCION TURNOS — OFICINA DE SERVICIOS");
        mensaje("1. Gestión de clientes");
        mensaje("2. Gestión de servicios");
        mensaje("3. Generar turno");
        mensaje("4. Atender siguiente turno");
        mensaje("5. Cancelar turno");
        mensaje("6. Turnos pendientes");
        mensaje("7. Turnos atendidos");
        mensaje("8. Turnos cancelados");
        mensaje("9. Historial de un cliente");
        mensaje("10. Resumen por servicio");
        mensaje("11. Salir");
    }

    public void menuClientes() {
        mensaje("CLIENTES: 1 Registrar | 2 Listar | 3 Buscar | 4 Actualizar nombre y teléfono | 5 Cambiar estado | 0 Volver");
    }

    public void menuServicios() {
        mensaje("SERVICIOS: 1 Registrar | 2 Listar | 3 Actualizar nombre | 4 Cambiar estado | 0 Volver");
    }

    public void mostrarCliente(Cliente c) {
        salida.printf("%d | %s | %s | %s%n", c.getCodigo(), c.getNombreCompleto(), c.getTelefono(),
                c.isActivo() ? "Activo" : "Inactivo");
    }

    public void mostrarClientes(List<Cliente> clientes) {
        mensaje("Código | Nombre completo | Teléfono | Estado");
        if (clientes.isEmpty()) mensaje("No hay clientes registrados.");
        clientes.forEach(this::mostrarCliente);
    }

    public void mostrarServicios(List<Servicio> servicios) {
        mensaje("Código | Servicio | Estado");
        if (servicios.isEmpty()) mensaje("No hay servicios registrados.");
        for (Servicio s : servicios) salida.printf("%d | %s | %s%n", s.getCodigo(), s.getNombre(),
                s.isActivo() ? "Activo" : "Inactivo");
    }

    public void mostrarTurno(Turno t, String cliente, String servicio) {
        salida.printf("Turno %d | Cliente %d: %s | Servicio %d: %s | %s | Generado: %s | Finalizado: %s%n",
                t.getNumero(), t.getCodigoCliente(), cliente, t.getCodigoServicio(), servicio,
                t.getEstado(), t.getFechaGeneracion(),
                t.getFechaFinalizacion() == null ? "-" : t.getFechaFinalizacion().toString());
    }

    public void mostrarResumen(List<ResumenServicio> filas) {
        mensaje("Servicio | Pendientes | Atendidos | Cancelados | Total");
        long pendientes = 0, atendidos = 0, cancelados = 0, total = 0;
        for (ResumenServicio f : filas) {
            salida.printf("%d %s | %d | %d | %d | %d%n", f.codigoServicio(), f.nombreServicio(),
                    f.pendientes(), f.atendidos(), f.cancelados(), f.total());
            pendientes += f.pendientes(); atendidos += f.atendidos();
            cancelados += f.cancelados(); total += f.total();
        }
        salida.printf("TOTAL | %d | %d | %d | %d%n", pendientes, atendidos, cancelados, total);
    }

    /** La vista cierra su lector, pero no la salida recibida. */
    public void close() { entrada.close(); }
}
