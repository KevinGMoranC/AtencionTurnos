package edu.oficina.estructuras;

import edu.oficina.modelo.Cliente;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** BST propio por código. Búsqueda y recorrido inorden recursivos. */
public final class ArbolBST {
    private NodoCliente raiz;

    public ArbolBST() { raiz = null; }
    public boolean estaVacio() { return raiz == null; }

    public void insertar(Cliente cliente) {
        Objects.requireNonNull(cliente, "El cliente es obligatorio.");
        raiz = insertar(raiz, cliente);
    }

    private NodoCliente insertar(NodoCliente nodo, Cliente cliente) {
        if (nodo == null) return new NodoCliente(cliente);
        int clave = cliente.getCodigo();
        if (clave < nodo.getCliente().getCodigo()) {
            nodo.setIzquierdo(insertar(nodo.getIzquierdo(), cliente));
        } else if (clave > nodo.getCliente().getCodigo()) {
            nodo.setDerecho(insertar(nodo.getDerecho(), cliente));
        } else {
            throw new IllegalArgumentException("Código de cliente duplicado.");
        }
        return nodo;
    }

    public Cliente buscar(int codigo) {
        NodoCliente nodo = buscarNodo(raiz, codigo);
        return nodo == null ? null : nodo.getCliente();
    }

    private NodoCliente buscarNodo(NodoCliente nodo, int codigo) {
        if (nodo == null || codigo == nodo.getCliente().getCodigo()) return nodo;
        return buscarNodo(codigo < nodo.getCliente().getCodigo()
                ? nodo.getIzquierdo() : nodo.getDerecho(), codigo);
    }

    public List<Cliente> inorden() {
        List<Cliente> clientes = new ArrayList<>();
        inorden(raiz, clientes);
        return clientes;
    }

    private void inorden(NodoCliente nodo, List<Cliente> clientes) {
        if (nodo == null) return;
        inorden(nodo.getIzquierdo(), clientes);
        clientes.add(nodo.getCliente());
        inorden(nodo.getDerecho(), clientes);
    }

    public void actualizar(Cliente cliente) {
        Objects.requireNonNull(cliente, "El cliente es obligatorio.");
        NodoCliente nodo = buscarNodo(raiz, cliente.getCodigo());
        if (nodo == null) throw new IllegalArgumentException("Cliente no registrado.");
        nodo.setCliente(cliente);
    }
}
