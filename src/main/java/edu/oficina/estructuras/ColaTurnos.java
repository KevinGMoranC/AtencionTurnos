package edu.oficina.estructuras;

import edu.oficina.modelo.Turno;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Cola propia de pendientes. Las transiciones persistentes pertenecen a TurnoService. */
public final class ColaTurnos {
    private NodoTurno primero;
    private NodoTurno ultimo;

    public ColaTurnos() { primero = null; ultimo = null; }
    public boolean estaVacia() { return primero == null; }

    public void encolar(Turno turno) {
        Objects.requireNonNull(turno, "El turno es obligatorio.");
        if (turno.getEstado() != Turno.Estado.PENDIENTE) {
            throw new IllegalArgumentException("Solo se encolan turnos pendientes.");
        }
        for (NodoTurno n = primero; n != null; n = n.getSiguiente()) {
            if (n.getTurno().getNumero() == turno.getNumero()) {
                throw new IllegalArgumentException("Número de turno duplicado en la cola.");
            }
            if (n.getTurno().getCodigoCliente() == turno.getCodigoCliente()) {
                throw new IllegalArgumentException("El cliente ya tiene un turno pendiente.");
            }
        }
        NodoTurno nuevo = new NodoTurno(turno);
        if (estaVacia()) primero = nuevo;
        else ultimo.setSiguiente(nuevo);
        ultimo = nuevo;
    }

    public Turno consultarSiguiente() {
        return estaVacia() ? null : primero.getTurno();
    }

    /** Retira el primero; no cambia su estado ni escribe en SQLite. */
    public Turno desencolar() {
        if (estaVacia()) return null;
        Turno turno = primero.getTurno();
        primero = primero.getSiguiente();
        if (estaVacia()) ultimo = null;
        return turno;
    }

    /** Retira por número preservando el orden; TurnoService guarda antes la cancelación. */
    public Turno cancelar(long numero) {
        NodoTurno anterior = null;
        NodoTurno actual = primero;
        while (actual != null) {
            if (actual.getTurno().getNumero() == numero) {
                if (anterior == null) primero = actual.getSiguiente();
                else anterior.setSiguiente(actual.getSiguiente());
                if (actual == ultimo) ultimo = anterior;
                return actual.getTurno();
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
        return null;
    }

    public boolean tienePendienteCliente(int codigo) {
        for (NodoTurno n = primero; n != null; n = n.getSiguiente()) {
            if (n.getTurno().getCodigoCliente() == codigo) return true;
        }
        return false;
    }

    public List<Turno> listarPendientes() {
        List<Turno> turnos = new ArrayList<>();
        for (NodoTurno n = primero; n != null; n = n.getSiguiente()) turnos.add(n.getTurno());
        return turnos;
    }
}
