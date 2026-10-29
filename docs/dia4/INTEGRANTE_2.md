# Día 4 — Integrante 2: turnos y cola FIFO

Rama existente: turnos-cola. Jornada de consolidación previa a integración final.

## Base revisada

Se revisaron Turno.java, NodoTurno.java, ColaTurnos.java, TurnoService.java y las pruebas
propias de días 2 y 3, además de TurnoRepositorio y TurnoDAO del integrante 3.
La base corresponde a los días 1–3 confirmados por el grupo. No se inspeccionó una
copia autenticada ni se modificó el repositorio remoto.

La creación, validación de activos, pendiente único, atención FIFO, cancelación,
numeración persistida y fechas ya estaban implementadas. No se reescriben.
La cola sigue usando primero/ultimo y NodoTurno propio; ArrayList solo devuelve listas.
No se usa LinkedList ni ArrayDeque para implementarla.

## Cambio de producción

Se modifica únicamente src/main/java/edu/oficina/servicios/TurnoService.java:

- Añade recargarDesdePersistencia(), construyendo primero una cola local nueva.
- Publica la nueva cola solamente después de leer y encolar todos los pendientes.
  Una SQLException o un registro inválido conserva la cola anterior y propaga el error.
- El constructor reutiliza esa operación. Su firma pública no cambia.
- La referencia privada cola deja de ser final para reemplazar la instantánea completa.
  Las operaciones de generar, consultar, listar, atender y cancelar no cambian.

Es una sustitución todo-o-nada en memoria, no una nueva transacción de escritura.
La recarga no cambia estados, no borra historial y no escribe en SQLite.
La consulta del DAO existente ordena por numero; ese orden se conserva al encolar.

## Archivos nuevos y modificados

| Ruta | Cambio |
|---|---|
| src/main/java/edu/oficina/servicios/TurnoService.java | Modificado |
| src/test/java/edu/oficina/pruebas/PruebaLimitesColaDia4.java | Nuevo |
| src/test/java/edu/oficina/pruebas/PruebaTurnosDia4.java | Nuevo |
| src/test/java/edu/oficina/pruebas/PruebaRecargaTurnosDia4.java | Nuevo |
| docs/dia4/INTEGRANTE_2.md | Nueva guía |
| docs/dia4/resultados/PruebaLimitesColaDia4.txt | Salida real |
| docs/dia4/resultados/PruebaTurnosDia4.txt | Salida real |
| docs/dia4/resultados/PruebaRecargaTurnosDia4.txt | Salida real |
| docs/dia4/MANIFIESTO_2.json | Inventario y hashes |

Turno, NodoTurno, ColaTurnos, modelos de clientes y todos los DAO permanecen intactos.
No se repiten en esta entrega. No es un proyecto Maven independiente.

## Dependencias y método nuevo

Se mantiene TurnoService(TurnoRepositorio). TurnoRepositorio y TurnoDAO del integrante 3
no requieren cambios. La numeración automática, entidades activas y escrituras
transaccionales siguen delegándose en el DAO existente.

Método nuevo:

~~~java
public void recargarDesdePersistencia() throws SQLException
~~~

No requiere modificar el menú: al construir TurnoService ya se reconstruye la cola.
Si se necesita refrescar una instancia existente después de un cambio administrativo,
el llamador puede usar turnos.recargarDesdePersistencia() y manejar SQLException.
No se añade una opción de menú ni sincronización automática entre aplicaciones.
Se mantiene la condición de una única sesión sobre la base; las escrituras externas
pueden dejar la instantánea desactualizada hasta recargar o crear una nueva instancia.

Las pruebas persistentes requieren PreparacionPruebas, los DAO y el esquema del
integrante 3 de días 2 y 3. No necesitan la recarga nueva de ClienteService del integrante 1.
PruebaLimitesColaDia4 solo necesita tus modelos, nodo y cola originales.

## Pruebas ejecutadas

La copia común compiló con JDK 21 y Maven. Pasaron las tres pruebas nuevas:

- PruebaLimitesColaDia4: 1920 operaciones deterministas contrastadas con una lista
  auxiliar de prueba, cancelaciones, vacío, duplicados, estados finales inválidos,
  número long máximo y protección frente a cambios en la lista devuelta.
- PruebaTurnosDia4: cancelar primero/último/intermedio/único, FIFO, números persistidos
  no reutilizados, estados y fechas intactos ante fallo real de escritura.
- PruebaRecargaTurnosDia4: recuperación desde DAO, consulta fallida, construcción
  parcial inválida, rechazo de finalizados y recargas sin duplicación ni borrado.

También pasaron las diez pruebas anteriores, las tres del integrante 1 del día 4 y las
tres del integrante 3: 19 en total. Se verificó la secuencia del catedrático con dos
procesos Java independientes. Las salidas propias se incluyen en resultados/.
Todo se ejecutó sobre bases temporales, sin tocar data/turnos.db del usuario.

## Ejecutar en VS Code

Con JDK 21 y Maven, abrir la raíz con pom.xml y disponer de la base común y dependencias:

~~~powershell
mvn test-compile exec:java "-Dexec.mainClass=edu.oficina.pruebas.PruebaLimitesColaDia4" "-Dexec.classpathScope=test"
mvn test-compile exec:java "-Dexec.mainClass=edu.oficina.pruebas.PruebaTurnosDia4" "-Dexec.classpathScope=test"
mvn test-compile exec:java "-Dexec.mainClass=edu.oficina.pruebas.PruebaRecargaTurnosDia4" "-Dexec.classpathScope=test"
~~~

Para la estructura aislada, incluso si tu rama aún no tiene Maven o los DAO:

~~~powershell
New-Item -ItemType Directory -Force target/prueba-cola-dia4 | Out-Null
javac --release 21 -encoding UTF-8 -d target/prueba-cola-dia4 src/main/java/edu/oficina/modelo/Turno.java src/main/java/edu/oficina/estructuras/NodoTurno.java src/main/java/edu/oficina/estructuras/ColaTurnos.java src/test/java/edu/oficina/pruebas/PruebaLimitesColaDia4.java
java -cp target/prueba-cola-dia4 edu.oficina.pruebas.PruebaLimitesColaDia4
~~~

Las pruebas son programas main; mvn test por sí solo no las ejecuta. No incluir los
archivos de otros integrantes en tu commit para resolver dependencias de comprobación.
Usar una copia común de pruebas cuando haga falta.

## Copiar y subir a la misma rama

Comparar TurnoService antes de copiar. Conservar todos los archivos no incluidos.
Copiar el contenido de Entrega_Dia4_Integrante2 a la raíz del repositorio, manteniendo
src/main/java, src/test/java y docs. No copiar la carpeta contenedora dentro de src.
Revisar el diff y seleccionar únicamente archivos propios, sin target ni bases temporales.
Crear un commit nuevo en turnos-cola, conservando los anteriores; no crear ramas nuevas
ni reescribir el historial. Mensaje recomendado, después de revisar y probar:

~~~text
Consolidar recarga de pendientes y pruebas de cola y turnos para día 4
~~~

No se crearon commits ni se hicieron subidas desde esta preparación.

## Pendiente para días 5 y 6

Disponer de los módulos compartidos en la integración revisada; ejecutar de nuevo las
pruebas sobre el resultado real de las ramas; resolver conflictos con los responsables;
preparar explicación de enlaces, FIFO y persistencia para la defensa. La documentación
final, diagramas e informe se completarán coordinadamente en las jornadas restantes.
