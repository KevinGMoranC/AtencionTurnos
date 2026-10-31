package edu.oficina.pruebas;

import java.util.ArrayList;
import java.util.List;

/** Ejecuta las pruebas originales y de aceptación sin una dependencia nueva de JUnit. */
public final class PruebasFinales {
    @FunctionalInterface private interface Caso { void ejecutar() throws Exception; }
    private static void ejecutar(String nombre,Caso caso,List<String> fallos) {
        System.out.println("=== "+nombre+" ===");
        try { caso.ejecutar(); }
        catch (Exception | AssertionError e) {
            fallos.add(nombre); e.printStackTrace(System.err);
        }
    }
    public static void main(String[] args) {
        List<String> fallos=new ArrayList<>();
        ejecutar("PruebaBST",() -> PruebaBST.main(new String[0]),fallos);
        ejecutar("PruebaClientes",() -> PruebaClientes.main(new String[0]),fallos);
        ejecutar("PruebaCola",() -> PruebaCola.main(new String[0]),fallos);
        ejecutar("PruebaTurnos",() -> PruebaTurnos.main(new String[0]),fallos);
        ejecutar("PruebaServicios",() -> PruebaServicios.main(new String[0]),fallos);
        ejecutar("PruebaPersistencia",() -> PruebaPersistencia.main(new String[0]),fallos);
        ejecutar("PruebaIntegracionClientesDia3",() -> PruebaIntegracionClientesDia3.main(new String[0]),fallos);
        ejecutar("PruebaIntegracionTurnosDia3",() -> PruebaIntegracionTurnosDia3.main(new String[0]),fallos);
        ejecutar("PruebaConsultasDia3",() -> PruebaConsultasDia3.main(new String[0]),fallos);
        ejecutar("PruebaMenuDia3",() -> PruebaMenuDia3.main(new String[0]),fallos);
        ejecutar("PruebaLimitesBSTDia4",() -> PruebaLimitesBSTDia4.main(new String[0]),fallos);
        ejecutar("PruebaClientesDia4",() -> PruebaClientesDia4.main(new String[0]),fallos);
        ejecutar("PruebaRecargaClientesDia4",() -> PruebaRecargaClientesDia4.main(new String[0]),fallos);
        ejecutar("PruebaLimitesColaDia4",() -> PruebaLimitesColaDia4.main(new String[0]),fallos);
        ejecutar("PruebaTurnosDia4",() -> PruebaTurnosDia4.main(new String[0]),fallos);
        ejecutar("PruebaRecargaTurnosDia4",() -> PruebaRecargaTurnosDia4.main(new String[0]),fallos);
        ejecutar("PruebaServiciosDia4",() -> PruebaServiciosDia4.main(new String[0]),fallos);
        ejecutar("PruebaPersistenciaDia4",() -> PruebaPersistenciaDia4.main(new String[0]),fallos);
        ejecutar("PruebaRecursosJdbcDia4",() -> PruebaRecursosJdbcDia4.main(new String[0]),fallos);
        ejecutar("ValidacionFinalClientes",() -> ValidacionFinalClientes.main(new String[0]),fallos);
        ejecutar("ValidacionFinalTurnos",() -> ValidacionFinalTurnos.main(new String[0]),fallos);
        if (!fallos.isEmpty()) throw new AssertionError("Pruebas fallidas: "+fallos);
        System.out.println("RESULTADO FINAL: 21/21 programas de prueba correctos.");
    }
}
