package edu.oficina.servicios;

import edu.oficina.dao.ClienteRepositorio;
import edu.oficina.dao.TurnoRepositorio;
import edu.oficina.estructuras.ArbolBST;
import edu.oficina.modelo.Cliente;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/** Reglas de clientes; SQLite se actualiza antes que el BST. */
public final class ClienteService {
    private final ClienteRepositorio repositorio;
    private final TurnoRepositorio turnos;
    private final ArbolBST arbol = new ArbolBST();

    public ClienteService(ClienteRepositorio repositorio, TurnoRepositorio turnos) throws SQLException {
        this.repositorio = Objects.requireNonNull(repositorio);
        this.turnos = Objects.requireNonNull(turnos);
        for (Cliente cliente : repositorio.listar()) arbol.insertar(cliente);
    }

    public Cliente registrar(int codigo, String nombre, String telefono) throws SQLException {
        Cliente cliente = new Cliente(codigo, nombre, telefono, true);
        if (arbol.buscar(codigo) != null) throw new IllegalArgumentException("Código de cliente duplicado.");
        repositorio.insertar(cliente);
        arbol.insertar(cliente);
        return cliente;
    }

    public Cliente buscar(int codigo) {
        if (codigo <= 0) throw new IllegalArgumentException("El código debe ser positivo.");
        Cliente cliente = arbol.buscar(codigo);
        if (cliente == null) throw new IllegalArgumentException("Cliente no registrado.");
        return cliente;
    }

    public List<Cliente> listar() { return arbol.inorden(); }

    public Cliente actualizar(int codigo, String nombre, String telefono) throws SQLException {
        Cliente anterior = buscar(codigo);
        return guardar(new Cliente(codigo, nombre, telefono, anterior.isActivo()));
    }

    public Cliente cambiarEstado(int codigo, boolean activo) throws SQLException {
        Cliente anterior = buscar(codigo);
        if (!activo && turnos.tienePendienteCliente(codigo)) {
            throw new IllegalArgumentException("No se puede desactivar un cliente con turno pendiente.");
        }
        return guardar(new Cliente(codigo, anterior.getNombreCompleto(), anterior.getTelefono(), activo));
    }

    private Cliente guardar(Cliente cliente) throws SQLException {
        repositorio.actualizar(cliente);
        arbol.actualizar(cliente);
        return cliente;
    }
}
