package edu.oficina.estructuras;

/** Estructura inicial: inserción, búsqueda e inorden quedan pendientes. */
public final class ArbolBST {
    private NodoCliente raiz;

    public ArbolBST() {
        raiz = null;
    }

    public boolean estaVacio() {
        return raiz == null;
    }
}
