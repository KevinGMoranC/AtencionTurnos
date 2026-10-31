package edu.oficina.pruebas;
import edu.oficina.modelo.Cliente;
import edu.oficina.estructuras.ArbolBST;
import java.util.List;
/** Se puede compilar solo con los archivos propios del integrante 1. */
public final class PruebaBST {
    private static void comprobar(boolean valor) { if (!valor) throw new AssertionError("BST incorrecto"); }
    public static void main(String[] args) {
        ArbolBST arbol = new ArbolBST();
        comprobar(arbol.estaVacio());
        for (int c : new int[]{1003,1001,1005,1002,1004}) {
            arbol.insertar(new Cliente(c,"Cliente " + c,"55501001",true));
        }
        comprobar(arbol.buscar(1003).getCodigo() == 1003);
        comprobar(arbol.buscar(9999) == null);
        comprobar(arbol.inorden().stream().map(Cliente::getCodigo).toList().equals(List.of(1001,1002,1003,1004,1005)));
        arbol.actualizar(new Cliente(1003,"Nombre actualizado","12345678",false));
        comprobar(!arbol.buscar(1003).isActivo());
        comprobar(arbol.buscar(1003).getNombreCompleto().equals("Nombre actualizado"));
        try { arbol.insertar(new Cliente(1003,"Duplicado","12345678",true));
            throw new AssertionError("Se aceptó duplicado");
        } catch (IllegalArgumentException esperado) { }
        arbol.inorden().clear();
        comprobar(arbol.inorden().size() == 5);
        System.out.println("BST: inserción, búsqueda, orden, actualización y duplicados correctos.");
    }
}
