package edu.oficina.estructuras;

/** Estructura inicial: encolar, desencolar y cancelar quedan pendientes. */
public final class ColaTurnos {
    private NodoTurno primero;
    private NodoTurno ultimo;

    public ColaTurnos() {
        primero = null;
        ultimo = null;
    }

    public boolean estaVacia() {
        return primero == null;
    }
}
