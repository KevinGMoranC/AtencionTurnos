package edu.oficina.estructuras;

import edu.oficina.modelo.Cliente;
import java.util.Objects;

/** Nodo propio del BST. */
public final class NodoCliente {
    private final Cliente cliente;
    private NodoCliente izquierdo;
    private NodoCliente derecho;

    public NodoCliente(Cliente cliente) {
        this.cliente = Objects.requireNonNull(cliente, "El cliente es obligatorio.");
    }

    public Cliente getCliente() { return cliente; }
    public NodoCliente getIzquierdo() { return izquierdo; }
    public NodoCliente getDerecho() { return derecho; }

    // Solo las estructuras de este paquete pueden enlazar nodos.
    void setIzquierdo(NodoCliente izquierdo) { this.izquierdo = izquierdo; }
    void setDerecho(NodoCliente derecho) { this.derecho = derecho; }
}
