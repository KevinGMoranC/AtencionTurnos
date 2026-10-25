package edu.oficina.estructuras;

import edu.oficina.modelo.Cliente;
import java.util.Objects;

/** Nodo propio del BST. */
public final class NodoCliente {
    private Cliente cliente;
    private NodoCliente izquierdo;
    private NodoCliente derecho;

    public NodoCliente(Cliente cliente) {
        this.cliente = Objects.requireNonNull(cliente, "El cliente es obligatorio.");
    }

    public Cliente getCliente() { return cliente; }
    public NodoCliente getIzquierdo() { return izquierdo; }
    public NodoCliente getDerecho() { return derecho; }

    // Permite sincronizar la versión inmutable del cliente sin alterar su clave.
    void setCliente(Cliente cliente) {
        Objects.requireNonNull(cliente, "El cliente es obligatorio.");
        if (cliente.getCodigo() != this.cliente.getCodigo()) {
            throw new IllegalArgumentException("No se puede cambiar la clave del nodo.");
        }
        this.cliente = cliente;
    }

    // Solo las estructuras de este paquete pueden enlazar nodos.
    void setIzquierdo(NodoCliente izquierdo) { this.izquierdo = izquierdo; }
    void setDerecho(NodoCliente derecho) { this.derecho = derecho; }
}
