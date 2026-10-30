package edu.oficina.pruebas;

import edu.oficina.dao.ClienteRepositorio;
import edu.oficina.modelo.Cliente;
import edu.oficina.servicios.ClienteService;
import java.sql.SQLException;
import java.util.List;
import static edu.oficina.pruebas.PreparacionPruebas.*;

/** Recarga todo-o-nada, con SQLite real y un decorador de repositorio solo para pruebas. */
public final class PruebaRecargaClientesDia4 {
    private static final class RepositorioControlado implements ClienteRepositorio {
        private final ClienteRepositorio real;
        private boolean fallarLectura;
        private List<Cliente> lecturaAlternativa;
        private RepositorioControlado(ClienteRepositorio real) { this.real = real; }
        public List<Cliente> listar() throws SQLException {
            if (fallarLectura) throw new SQLException("Fallo de consulta simulado");
            return lecturaAlternativa == null ? real.listar() : lecturaAlternativa;
        }
        public Cliente buscar(int codigo) throws SQLException { return real.buscar(codigo); }
        public void insertar(Cliente c) throws SQLException { real.insertar(c); }
        public void actualizar(Cliente c) throws SQLException { real.actualizar(c); }
    }

    private static List<String> firma(ClienteService servicio) {
        return servicio.listar().stream().map(c -> c.getCodigo() + "|" + c.getNombreCompleto()
                + "|" + c.getTelefono() + "|" + c.isActivo()).toList();
    }

    public static void main(String[] args) throws Exception {
        try (PreparacionPruebas p = new PreparacionPruebas()) {
            RepositorioControlado repositorio = new RepositorioControlado(p.clientes);
            ClienteService clientes = new ClienteService(repositorio,p.turnos);
            clientes.registrar(1010,"Cliente propio","00123456");

            // Simula una actualización administrativa fuera de esta instancia.
            // Solo afecta la base temporal; no implica sincronización automática entre aplicaciones.
            p.clientes.actualizar(new Cliente(1001,"Cambio persistido","00003333",false));
            p.clientes.insertar(new Cliente(1011,"Alta persistida","00004444",true));
            verificar(clientes.buscar(1001).isActivo(), "El árbol es una instantánea hasta recargar");
            clientes.recargarDesdePersistencia();
            verificar(!clientes.buscar(1001).isActivo()
                    && clientes.buscar(1001).getNombreCompleto().equals("Cambio persistido")
                    && clientes.buscar(1011).getTelefono().equals("00004444"),
                    "Recarga adopta la fuente persistente, incluidos inactivos y altas");
            var anterior = firma(clientes);
            long registros = p.escalar("SELECT count(*) FROM clientes");

            repositorio.fallarLectura = true;
            try {
                clientes.recargarDesdePersistencia();
                throw new AssertionError("Se esperaba SQLException de recarga");
            } catch (SQLException esperado) { }
            verificar(firma(clientes).equals(anterior), "Fallo de consulta conserva todo el árbol anterior");
            repositorio.fallarLectura = false;

            repositorio.lecturaAlternativa = List.of(new Cliente(2000,"Primer nodo","12345678",true),
                    new Cliente(2000,"Duplicado","87654321",false));
            rechazar(clientes::recargarDesdePersistencia, "Fallo durante la construcción del nuevo BST");
            verificar(firma(clientes).equals(anterior), "No se publica un árbol construido a medias");
            rechazar(() -> clientes.buscar(2000), "No queda visible el primer nodo de la carga fallida");

            repositorio.lecturaAlternativa = List.of();
            clientes.recargarDesdePersistencia();
            verificar(clientes.listar().isEmpty(), "Un repositorio vacío produce un árbol vacío");
            verificar(p.escalar("SELECT count(*) FROM clientes") == registros,
                    "Recargar nunca borra ni escribe los clientes de SQLite");
            repositorio.lecturaAlternativa = null;
            clientes.recargarDesdePersistencia(); clientes.recargarDesdePersistencia();
            verificar(firma(clientes).equals(anterior), "Recargas repetidas no acumulan ni duplican nodos");
            ClienteService reinicio = new ClienteService(p.clientes,p.turnos);
            verificar(firma(reinicio).equals(anterior), "Constructor y recarga explícita comparten la misma reconstrucción");
            repositorio.fallarLectura = true;
            try {
                new ClienteService(repositorio,p.turnos);
                throw new AssertionError("El arranque debe informar el fallo de carga");
            } catch (SQLException esperado) { }
            System.out.println("RECARGA CLIENTES DIA 4 CORRECTA");
        }
    }
}
