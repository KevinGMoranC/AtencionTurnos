# Día 3 — Integrante 1

Domingo 25 de octubre de 2026. Rama existente: `clientes-bst`.

## Base revisada

El grupo confirmó que los archivos del día 2 se subieron exactamente como fueron entregados. Se reconstruyó esa base a partir de las entregas locales; no se afirma haber inspeccionado las ramas autenticadas ni haber integrado GitHub.

## Qué cambia y qué no se repite

ClienteService y ArbolBST del día 2 ya registran y actualizan SQLite antes de sincronizar el árbol, buscan mediante BST, listan inorden, recargan al construir y conservan inactivos. No se vuelven a enviar ni reescribir. El avance nuevo es PruebaIntegracionClientesDia3, que usa el TurnoService real para impedir desactivación con pendientes, cancelar, desactivar, reconstruir y conservar historial. Se prueba también un nombre con apóstrofo y teléfono con ceros iniciales.

## Archivos de esta carpeta

- `src/test/java/edu/oficina/pruebas/PruebaIntegracionClientesDia3.java` — nuevo
- `docs/dia3/INTEGRANTE_1.md` — guía nueva del responsable.

Los archivos no incluidos se conservan desde las entregas previas. No es un proyecto completo: se agregan solo los cambios. Todos los Java incluidos están completos.

## Dependencias para compilar y probar

Requiere la base completa del día 2: ClienteService y BST propios, TurnoService del integrante 2 y los DAO del 3. Para la comprobación de historial usa ConsultaService y las consultas nuevas del integrante 3 del día 3. No se copian DAO ni servicios ajenos en esta carpeta.

## Contratos coordinados

Se conservan sin cambios los constructores y métodos del día 2:

| Clase | Métodos usados por el menú |
|---|---|
| ClienteService | registrar(int,String,String), buscar(int), listar(), actualizar(int,String,String), cambiarEstado(int,boolean) |
| ServicioService | registrar(int,String), buscar(int), listar(), actualizar(int,String), cambiarEstado(int,boolean) |
| TurnoService | generar(int,int), consultarSiguiente(), listarPendientes(), atenderSiguiente(), cancelar(long) |

ClienteService recibe ClienteRepositorio y TurnoRepositorio. TurnoService recibe TurnoRepositorio.
ServicioService recibe ServicioRepositorio y TurnoRepositorio. Las consultas nuevas van por
ConsultasTurnosRepositorio y ConsultaService; no cambian el contrato TurnoRepositorio.
Las listas de consulta son resultados auxiliares y no sustituyen BST o cola.
Main crea una única instancia de cada servicio con memoria por sesión.

## Pruebas

En una copia de comprobación que reúna la base del día 2 y las dependencias del día 3, con JDK 21 y Maven:

```powershell
mvn test-compile exec:java "-Dexec.mainClass=edu.oficina.pruebas.PruebaBST" "-Dexec.classpathScope=test"
mvn test-compile exec:java "-Dexec.mainClass=edu.oficina.pruebas.PruebaClientes" "-Dexec.classpathScope=test"
mvn test-compile exec:java "-Dexec.mainClass=edu.oficina.pruebas.PruebaIntegracionClientesDia3" "-Dexec.classpathScope=test"
```

Las pruebas anteriores permanecen intactas y se volvieron a ejecutar. Las pruebas usan bases temporales creadas por PreparacionPruebas; no modifican data/turnos.db del usuario. Son ejecutables main: mvn test no los ejecuta por sí solo. Las ramas aisladas siguen necesitando las fuentes compartidas para compilar.

## Subida a la misma rama

1. Revisar la rama actual, cambios locales y la última versión remota.
2. Comparar antes de copiar. Agregar el contenido de esta carpeta a la raíz del repositorio conservando rutas Maven. No subir el ZIP ni la carpeta contenedora como sustituto del código.
3. Mantener todos los archivos de días 1 y 2 no incluidos aquí. No borrar src ni reemplazar el repositorio entero.
4. Revisar el diff y seleccionar solo los archivos propios, incluida esta guía. No agregar las dependencias ajenas usadas para pruebas ni target o bases temporales.
5. Crear un commit nuevo en la misma rama; conservar los anteriores. La integración con main será revisada posteriormente por el grupo.

Mensaje recomendado, tras revisar y probar el trabajo:

```text
Verificar integración de clientes, BST, turnos y persistencia en día 3
```

No se crearon ramas, commits ni merges ni se hicieron subidas desde esta preparación. Ver docs/dia3/COORDINACION.md en la entrega del integrante 3.
