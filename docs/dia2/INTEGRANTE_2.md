# Segunda jornada — Integrante 2

Sábado 24 de octubre de 2026. Rama: `turnos-cola`. Base confirmada por el grupo: primera entrega sin modificaciones. Se continuó esa copia local exacta.

## Qué se agregó

ColaTurnos: encolar, consultar siguiente, desencolar, cancelar por número conservando orden, listar y detectar pendientes por cliente. Se conservan constructor y estaVacia; Turno y NodoTurno no cambian. TurnoService genera, atiende FIFO, cancela y reconstruye pendientes. El DAO devuelve nuevos objetos con estado y fecha final. Se guarda antes de cambiar la cola. ColaTurnos.cancelar solo desenlaza; el servicio coordina la transición persistente.

## Archivos nuevos o modificados

- `src/main/java/edu/oficina/estructuras/ColaTurnos.java` — modificado
- `src/main/java/edu/oficina/servicios/TurnoService.java` — nuevo
- `src/test/java/edu/oficina/pruebas/PruebaCola.java` — nuevo
- `src/test/java/edu/oficina/pruebas/PruebaTurnos.java` — nuevo
- `docs/dia2/INTEGRANTE_2.md` — guía nueva.

Todos los Java están completos. Los originales no incluidos se conservan. No aplanar las rutas Maven ni copiar archivos de otro integrante en tu commit.

## Dependencias

Turno y NodoTurno originales. TurnoService necesita TurnoRepositorio y su DAO del integrante 3, que valida existencia y estado activo del cliente/servicio. Las pruebas usan los DAO, InicializadorBD y PreparacionPruebas del integrante 3 y los modelos originales. No necesitan ClienteService, ServicioService ni los cambios de BST de día 2.

## Estructura aislada, sin DAO ni Maven

Con tus archivos del día 1 y estos cambios, ejecutar desde la raíz:

```powershell
New-Item -ItemType Directory -Force target/prueba-cola | Out-Null
javac --release 21 -encoding UTF-8 -d target/prueba-cola src/main/java/edu/oficina/modelo/Turno.java src/main/java/edu/oficina/estructuras/NodoTurno.java src/main/java/edu/oficina/estructuras/ColaTurnos.java src/test/java/edu/oficina/pruebas/PruebaCola.java
java -cp target/prueba-cola edu.oficina.pruebas.PruebaCola
```

No necesita archivos de día 2 de otro responsable. Comprueba el orden, la búsqueda o las cancelaciones, el estado vacío y el rechazo de duplicados.

## Pruebas del módulo con sus dependencias

En una copia de comprobación con la base completa del día 1 y las dependencias indicadas, usando Java 21 y el POM actualizado del integrante 3:

```powershell
mvn test-compile exec:java "-Dexec.mainClass=edu.oficina.pruebas.PruebaCola" "-Dexec.classpathScope=test"
mvn test-compile exec:java "-Dexec.mainClass=edu.oficina.pruebas.PruebaTurnos" "-Dexec.classpathScope=test"
```

Se probaron por separado las combinaciones base día 1 + integrante 3, base + 1 y 3, y base + 2 y 3. Las pruebas de cada servicio no exigen los servicios ausentes. Si tu rama solo contiene tus fuentes del día 1, no es necesariamente un proyecto Maven compilable: primero hacen falta los modelos comunes y los contratos señalados. Disponerlos en una copia de comprobación no implica incluirlos en tu commit ni integrar las ramas remotas.

PreparacionPruebas crea bases temporales, las elimina al terminar y no utiliza data/turnos.db del usuario. Los triggers de fallo solo se crean en esas bases. Son programas main: `mvn test` por sí solo no los ejecuta.

## Incorporación en la misma rama

1. Comprobar tu rama y los cambios locales antes de copiar.
2. Copiar el CONTENIDO de esta carpeta a la raíz del repositorio, conservando las rutas. No copiar la carpeta contenedora dentro de src.
3. Agregar archivos nuevos y actualizar solo los indicados como modificados. No borrar src ni sustituir el repositorio entero.
4. Revisar el diff y seleccionar explícitamente solo tus archivos y esta guía; excluir target, bases temporales y dependencias ajenas usadas para probar.
5. Crear un commit nuevo en tu misma rama, conservando el historial del día 1. No se necesita reescribirlo.

Mensaje recomendado, después de revisar y probar:

```text
Implementar cola propia y gestión persistente de turnos con pruebas
```

No se creó ese commit ni se hizo ninguna subida desde esta preparación. El menú completo, los reportes, la integración remota y las pruebas finales quedan pendientes.
