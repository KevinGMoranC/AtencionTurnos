package edu.oficina.pruebas;

import edu.oficina.estructuras.ArbolBST;
import edu.oficina.modelo.Cliente;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Prueba aislada del módulo propio: no necesita DAO, SQLite ni otros servicios. */
public final class PruebaLimitesBSTDia4 {
    private static Cliente cliente(int codigo) {
        return new Cliente(codigo, "Cliente " + codigo, "00123456", true);
    }
    private static void verificar(boolean valor, String mensaje) {
        if (!valor) throw new AssertionError(mensaje);
        System.out.println("OK: " + mensaje);
    }
    private static void rechazar(Runnable accion, Class<? extends RuntimeException> tipo) {
        try { accion.run(); }
        catch (RuntimeException e) {
            if (tipo.isInstance(e)) return;
            throw e;
        }
        throw new AssertionError("No se rechazó la operación inválida.");
    }
    public static void main(String[] args) {
        ArbolBST arbol = new ArbolBST();
        verificar(arbol.estaVacio() && arbol.buscar(1) == null && arbol.inorden().isEmpty(),
                "Árbol vacío admite búsqueda y recorrido");
        rechazar(() -> arbol.insertar(null), NullPointerException.class);
        rechazar(() -> arbol.actualizar(cliente(1)), IllegalArgumentException.class);
        verificar(arbol.estaVacio(), "Intentos inválidos no cambian un árbol vacío");

        List<Integer> claves = new ArrayList<>();
        for (int i=1;i<=255;i++) claves.add(i);
        claves.add(Integer.MAX_VALUE);
        List<Integer> ordenadas = new ArrayList<>(claves);
        for (int semilla : new int[]{7,19,42}) {
            Collections.shuffle(claves, new Random(semilla));
            ArbolBST prueba = new ArbolBST();
            for (int clave : claves) prueba.insertar(cliente(clave));
            verificar(prueba.inorden().stream().map(Cliente::getCodigo).toList().equals(ordenadas),
                    "Inorden correcto para 256 claves, semilla " + semilla);
            for (int clave : ordenadas) {
                if (prueba.buscar(clave) == null || prueba.buscar(clave).getCodigo() != clave) {
                    throw new AssertionError("No se encontró " + clave);
                }
            }
            verificar(prueba.buscar(10000) == null, "Búsqueda inexistente devuelve null en la estructura");
            for (int clave : new int[]{claves.get(0),1,128,Integer.MAX_VALUE}) {
                prueba.actualizar(new Cliente(clave, "Actualizado " + clave, "00000000", false));
                verificar(!prueba.buscar(clave).isActivo(), "Se actualiza y conserva la clave " + clave);
            }
            var antes = prueba.inorden().stream().map(Cliente::getCodigo).toList();
            rechazar(() -> prueba.insertar(cliente(128)), IllegalArgumentException.class);
            rechazar(() -> prueba.actualizar(cliente(10000)), IllegalArgumentException.class);
            rechazar(() -> prueba.actualizar(null), NullPointerException.class);
            prueba.inorden().clear();
            verificar(prueba.inorden().stream().map(Cliente::getCodigo).toList().equals(antes),
                    "Duplicados, actualización inválida y listas externas no alteran el BST");
        }
        for (boolean ascendente : new boolean[]{true,false}) {
            ArbolBST sesgado = new ArbolBST();
            for (int i=1;i<=80;i++) sesgado.insertar(cliente(ascendente ? i : 81-i));
            verificar(sesgado.inorden().get(0).getCodigo() == 1
                    && sesgado.buscar(80).getCodigo() == 80, "Orden correcto en árbol sesgado");
        }
        System.out.println("LIMITES BST DIA 4 CORRECTOS");
    }
}
