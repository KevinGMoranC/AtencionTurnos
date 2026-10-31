package edu.oficina.controlador;

import edu.oficina.modelo.Turno;
import edu.oficina.servicios.ClienteService;
import edu.oficina.servicios.ServicioService;
import edu.oficina.servicios.TurnoService;
import edu.oficina.servicios.ConsultaService;
import edu.oficina.vista.VistaConsola;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/** Coordina la vista y los servicios sin SQL, nodos ni reglas duplicadas. */
public final class ControladorConsola {
    private final VistaConsola vista;
    private final ClienteService clientes;
    private final ServicioService servicios;
    private final TurnoService turnos;
    private final ConsultaService consultas;

    public ControladorConsola(VistaConsola vista, ClienteService clientes,
            ServicioService servicios, TurnoService turnos, ConsultaService consultas) {
        this.vista = Objects.requireNonNull(vista);
        this.clientes = Objects.requireNonNull(clientes);
        this.servicios = Objects.requireNonNull(servicios);
        this.turnos = Objects.requireNonNull(turnos);
        this.consultas = Objects.requireNonNull(consultas);
    }

    public void ejecutar() {
        try {
            while (true) {
                vista.menuPrincipal();
                try {
                    int opcion = vista.leerEntero("Opción");
                    switch (opcion) {
                        case 1 -> gestionarClientes();
                        case 2 -> gestionarServicios();
                        case 3 -> {
                            Turno t = turnos.generar(vista.leerEntero("Código del cliente"),
                                    vista.leerEntero("Código del servicio"));
                            vista.mensaje("Turno generado: " + t.getNumero());
                        }
                        case 4 -> atenderSiguiente();
                        case 5 -> {
                            Turno t = turnos.cancelar(vista.leerNumeroTurno("Número de turno"));
                            vista.mensaje("Turno " + t.getNumero() + " cancelado.");
                        }
                        case 6 -> mostrarTurnos(turnos.listarPendientes());
                        case 7 -> mostrarTurnos(consultas.listarPorEstado(Turno.Estado.ATENDIDO));
                        case 8 -> mostrarTurnos(consultas.listarPorEstado(Turno.Estado.CANCELADO));
                        case 9 -> mostrarTurnos(consultas.historialCliente(vista.leerEntero("Código del cliente")));
                        case 10 -> vista.mostrarResumen(consultas.resumenPorServicio());
                        case 11 -> { vista.mensaje("Datos conservados. Hasta luego."); return; }
                        default -> vista.mensaje("Opción inexistente.");
                    }
                } catch (IllegalArgumentException | SQLException e) { mostrarError(e); }
            }
        } catch (NoSuchElementException e) { vista.mensaje("Entrada finalizada. Datos conservados."); }
    }

    private void gestionarClientes() {
        while (true) {
            vista.menuClientes();
            try {
                int opcion = vista.leerEntero("Opción de clientes");
                switch (opcion) {
                    case 0 -> { return; }
                    case 1 -> {
                        clientes.registrar(vista.leerEntero("Código"), vista.leerTexto("Nombre completo"),
                                vista.leerTexto("Teléfono de 8 dígitos"));
                        vista.mensaje("Cliente registrado.");
                    }
                    case 2 -> vista.mostrarClientes(clientes.listar());
                    case 3 -> vista.mostrarCliente(clientes.buscar(vista.leerEntero("Código")));
                    case 4 -> {
                        clientes.actualizar(vista.leerEntero("Código"), vista.leerTexto("Nombre completo"),
                                vista.leerTexto("Teléfono de 8 dígitos"));
                        vista.mensaje("Cliente actualizado.");
                    }
                    case 5 -> {
                        clientes.cambiarEstado(vista.leerEntero("Código"), vista.leerActivo());
                        vista.mensaje("Estado del cliente actualizado.");
                    }
                    default -> vista.mensaje("Opción inexistente.");
                }
            } catch (IllegalArgumentException | SQLException e) { mostrarError(e); }
        }
    }

    private void gestionarServicios() {
        while (true) {
            vista.menuServicios();
            try {
                int opcion = vista.leerEntero("Opción de servicios");
                switch (opcion) {
                    case 0 -> { return; }
                    case 1 -> {
                        servicios.registrar(vista.leerEntero("Código"), vista.leerTexto("Nombre del servicio"));
                        vista.mensaje("Servicio registrado.");
                    }
                    case 2 -> vista.mostrarServicios(servicios.listar());
                    case 3 -> {
                        servicios.actualizar(vista.leerEntero("Código"), vista.leerTexto("Nombre del servicio"));
                        vista.mensaje("Servicio actualizado.");
                    }
                    case 4 -> {
                        servicios.cambiarEstado(vista.leerEntero("Código"), vista.leerActivo());
                        vista.mensaje("Estado del servicio actualizado.");
                    }
                    default -> vista.mensaje("Opción inexistente.");
                }
            } catch (IllegalArgumentException | SQLException e) { mostrarError(e); }
        }
    }

    private void atenderSiguiente() throws SQLException {
        Turno siguiente = turnos.consultarSiguiente();
        if (siguiente == null) { vista.mensaje("No existen turnos pendientes."); return; }
        vista.mensaje("Siguiente turno a atender:");
        mostrarTurno(siguiente);
        Turno atendido = turnos.atenderSiguiente();
        vista.mensaje("Turno " + atendido.getNumero() + " atendido.");
    }

    private void mostrarTurnos(List<Turno> lista) throws SQLException {
        if (lista.isEmpty()) { vista.mensaje("No hay turnos para esta consulta."); return; }
        for (Turno t : lista) mostrarTurno(t);
    }

    private void mostrarTurno(Turno t) throws SQLException {
        vista.mostrarTurno(t, clientes.buscar(t.getCodigoCliente()).getNombreCompleto(),
                servicios.buscar(t.getCodigoServicio()).getNombre());
    }

    private void mostrarError(Exception error) {
        vista.mensaje("Operación no completada: " + error.getMessage());
    }
}
