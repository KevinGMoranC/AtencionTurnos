package edu.oficina.pruebas;

import edu.oficina.estructuras.ColaTurnos;
import edu.oficina.modelo.Turno;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Oráculo de prueba auxiliar: la implementación real sigue usando sus nodos propios. */
public final class PruebaLimitesColaDia4 {
    private static Turno pendiente(long numero, int cliente) {
        return new Turno(numero, cliente, 1, LocalDateTime.now(), Turno.Estado.PENDIENTE, null);
    }
    private static void comprobar(boolean valor, String mensaje) {
        if (!valor) throw new AssertionError(mensaje);
    }
    private static void rechazar(Runnable accion, Class<? extends RuntimeException> tipo) {
        try { accion.run(); } catch (RuntimeException e) {
            if (tipo.isInstance(e)) return;
            throw e;
        }
        throw new AssertionError("Se aceptó una operación inválida");
    }
    public static void main(String[] args) {
        ColaTurnos cola = new ColaTurnos();
        comprobar(cola.estaVacia() && cola.consultarSiguiente()==null && cola.desencolar()==null
                && cola.cancelar(99)==null, "Cola vacía");
        rechazar(() -> cola.encolar(null), NullPointerException.class);
        cola.encolar(pendiente(1,1001));
        rechazar(() -> cola.encolar(pendiente(1,1002)), IllegalArgumentException.class);
        rechazar(() -> cola.encolar(pendiente(2,1001)), IllegalArgumentException.class);
        for (Turno.Estado estado : new Turno.Estado[]{Turno.Estado.ATENDIDO,Turno.Estado.CANCELADO}) {
            rechazar(() -> cola.encolar(new Turno(2,1002,1,LocalDateTime.now(),estado,LocalDateTime.now())),
                    IllegalArgumentException.class);
        }
        cola.cancelar(1); cola.encolar(pendiente(Long.MAX_VALUE,1002));
        comprobar(cola.cancelar(Long.MAX_VALUE).getNumero()==Long.MAX_VALUE && cola.estaVacia(),
                "Cancelación del único nodo y número long máximo");
        for (int semilla=0;semilla<12;semilla++) {
            ColaTurnos real = new ColaTurnos();
            List<Turno> esperado = new ArrayList<>();
            Random azar = new Random(semilla);
            int siguiente=1;
            for (int paso=0;paso<160;paso++) {
                int operacion=azar.nextInt(3);
                if (operacion==0 || esperado.isEmpty()) {
                    Turno t=pendiente(siguiente,1000+siguiente); siguiente++;
                    real.encolar(t); esperado.add(t);
                } else if (operacion==1) {
                    int posicion=azar.nextInt(esperado.size());
                    Turno t=esperado.remove(posicion);
                    comprobar(real.cancelar(t.getNumero()).getNumero()==t.getNumero(), "Cancelación por número");
                } else {
                    comprobar(real.desencolar().getNumero()==esperado.remove(0).getNumero(), "Orden FIFO");
                }
                comprobar(real.listarPendientes().stream().map(Turno::getNumero).toList()
                        .equals(esperado.stream().map(Turno::getNumero).toList()), "Orden tras operación mixta");
                comprobar(real.estaVacia()==esperado.isEmpty(), "Extremos coherentes");
                comprobar((real.consultarSiguiente()==null && esperado.isEmpty())
                        || (!esperado.isEmpty() && real.consultarSiguiente().getNumero()==esperado.get(0).getNumero()),
                        "Consulta del siguiente");
            }
            real.listarPendientes().clear();
            comprobar(real.listarPendientes().size()==esperado.size(), "Lista externa no altera enlaces");
        }
        System.out.println("LIMITES COLA DIA 4 CORRECTOS: 1920 operaciones deterministas y casos límite.");
    }
}
