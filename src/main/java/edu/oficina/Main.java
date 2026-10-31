package edu.oficina;

import edu.oficina.controlador.ControladorConsola;
import edu.oficina.dao.ClienteDAO;
import edu.oficina.dao.ServicioDAO;
import edu.oficina.dao.TurnoDAO;
import edu.oficina.persistencia.InicializadorBD;
import edu.oficina.servicios.ClienteService;
import edu.oficina.servicios.ServicioService;
import edu.oficina.servicios.TurnoService;
import edu.oficina.servicios.ConsultaService;
import edu.oficina.vista.VistaConsola;
import java.sql.SQLException;

/** Punto de entrada: inicializa SQLite, conecta las capas y abre el menú. */
public final class Main {
    private Main() { }

    public static void main(String[] args) {
        try {
            new InicializadorBD().inicializar();
            ClienteDAO clienteDAO = new ClienteDAO();
            ServicioDAO servicioDAO = new ServicioDAO();
            TurnoDAO turnoDAO = new TurnoDAO();
            ClienteService clientes = new ClienteService(clienteDAO, turnoDAO);
            ServicioService servicios = new ServicioService(servicioDAO, turnoDAO);
            TurnoService turnos = new TurnoService(turnoDAO);
            ConsultaService consultas = new ConsultaService(turnoDAO, clienteDAO);
            try (VistaConsola vista = new VistaConsola(System.in, System.out)) {
                new ControladorConsola(vista, clientes, servicios, turnos, consultas).ejecutar();
            }
        } catch (SQLException e) {
            System.err.println("No se pudo iniciar AtencionTurnos: " + e.getMessage());
            System.exit(1);
        }
    }
}
