package edu.oficina.pruebas;
import edu.oficina.modelo.Turno;
import edu.oficina.estructuras.ColaTurnos;
import java.time.LocalDateTime;
import java.util.List;
/** Se puede compilar solo con los archivos propios del integrante 2. */
public final class PruebaCola {
    private static Turno turno(int n) {
        return new Turno(n,1000+n,1,LocalDateTime.now(),Turno.Estado.PENDIENTE,null);
    }
    private static void comprobar(boolean b) { if (!b) throw new AssertionError("Cola incorrecta"); }
    public static void main(String[] args) {
        ColaTurnos cola = new ColaTurnos();
        comprobar(cola.estaVacia() && cola.desencolar() == null);
        for (int n=1;n<=5;n++) cola.encolar(turno(n));
        comprobar(cola.consultarSiguiente().getNumero() == 1);
        cola.cancelar(3);
        comprobar(cola.listarPendientes().stream().map(Turno::getNumero).toList().equals(List.of(1L,2L,4L,5L)));
        cola.cancelar(1); cola.cancelar(5);
        comprobar(cola.desencolar().getNumero() == 2);
        comprobar(cola.desencolar().getNumero() == 4 && cola.estaVacia());
        cola.encolar(turno(6)); cola.cancelar(6);
        comprobar(cola.estaVacia());
        cola.encolar(turno(7));
        try { cola.encolar(new Turno(8,1007,1,LocalDateTime.now(),Turno.Estado.PENDIENTE,null));
            throw new AssertionError("Se aceptó otro pendiente del cliente");
        } catch (IllegalArgumentException esperado) { }
        cola.listarPendientes().clear();
        comprobar(!cola.estaVacia());
        System.out.println("Cola: FIFO, cancelaciones de extremos y medio, y pendiente único correctos.");
    }
}
