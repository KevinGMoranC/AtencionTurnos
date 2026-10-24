package edu.oficina.estructuras;

import edu.oficina.modelo.Turno;
import java.util.Objects;

/** Nodo propio de la futura cola de pendientes. */
public final class NodoTurno {
    private final Turno turno;
    private NodoTurno siguiente;

    public NodoTurno(Turno turno) {
        this.turno = Objects.requireNonNull(turno, "El turno es obligatorio.");
    }

    public Turno getTurno() { return turno; }
    public NodoTurno getSiguiente() { return siguiente; }

    // El enlace de nodos queda limitado al paquete de estructuras.
    void setSiguiente(NodoTurno siguiente) { this.siguiente = siguiente; }
}
