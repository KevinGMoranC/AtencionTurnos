# Día 4 — Integrante 1: Clientes y BST

Martes 27 de octubre de 2026. Rama existente: clientes-bst.

## Archivos revisados y base de compatibilidad

Se revisaron Cliente.java, NodoCliente.java, ArbolBST.java, ClienteService.java,
PruebaBST.java, PruebaClientes.java y PruebaIntegracionClientesDia3.java de nuestras
entregas anteriores. Para comprobar las dependencias se revisaron ClienteRepositorio,
TurnoRepositorio, ClienteDAO, ConexionBD y PreparacionPruebas. No se modifican los
archivos de los integrantes 2 y 3. La base local reúne los días 1, 2 y 3 entregados;
no se afirma haber inspeccionado ni actualizado las ramas autenticadas de GitHub.
Si tu copia incorpora cambios adicionales, comparar antes de aplicar este paquete.

## Revisión del módulo

Las operaciones de registro, búsqueda BST, inorden, actualización y cambio de estado
ya existían. Se conservan las firmas públicas anteriores. Cliente y los nodos siguen
encapsulados; los códigos no se editan y los inactivos permanecen en el árbol.
Las escrituras siguen guardándose primero en SQLite y luego en memoria.
Búsqueda e inorden siguen siendo recursivos en el BST propio; no se sustituyen por
colecciones estándar. No se cambia un archivo solo para producir un avance aparente.

## Avance de producción

Solo se modifica src/main/java/edu/oficina/servicios/ClienteService.java:

- Se añade recargarDesdePersistencia(), que usa el método listar() ya existente de
  ClienteRepositorio. No hay nuevas dependencias ni cambios en DAO o interfaz.
- Construye un BST local nuevo y sustituye la referencia del anterior únicamente
  cuando termina la lectura y la construcción completas. Si hay una SQLException o
  un dato inválido durante la construcción, la instancia conserva el árbol anterior
  y propaga el error al llamador.
- El constructor reutiliza esa reconstrucción al iniciar, conservando su firma.
- La referencia privada al árbol deja de ser final para permitir el reemplazo completo;
  no se expone al llamador ni se modifican los nodos desde fuera de la estructura.

La garantía es de sustitución todo-o-nada de la instantánea en memoria, no una nueva
transacción de escritura SQLite. La recarga no escribe, borra ni restablece registros.
La lista devuelta por el DAO se considera la fuente para reconstruir, incluidos inactivos.

## Archivos de esta entrega

| Ruta | Cambio |
|---|---|
| src/main/java/edu/oficina/servicios/ClienteService.java | Modificado |
| src/test/java/edu/oficina/pruebas/PruebaLimitesBSTDia4.java | Nuevo |
| src/test/java/edu/oficina/pruebas/PruebaClientesDia4.java | Nuevo |
| src/test/java/edu/oficina/pruebas/PruebaRecargaClientesDia4.java | Nuevo |
| docs/dia4/INTEGRANTE_1.md | Nueva guía |
| docs/dia4/resultados/PruebaLimitesBSTDia4.txt | Salida real nueva |
| docs/dia4/resultados/PruebaClientesDia4.txt | Salida real nueva |
| docs/dia4/resultados/PruebaRecargaClientesDia4.txt | Salida real nueva |
| docs/dia4/MANIFIESTO.json | Inventario y hashes |

Cliente.java, ArbolBST.java, NodoCliente.java y todas las pruebas anteriores se conservan
sin cambios y no se repiten en esta carpeta. No se entrega otro proyecto Maven.

## Contratos y forma de integrar el método nuevo

ClienteService mantiene el constructor ClienteService(ClienteRepositorio, TurnoRepositorio),
y registrar(int,String,String), buscar(int), listar(), actualizar(int,String,String)
y cambiarEstado(int,boolean). El constructor sigue declarando SQLException.

Nuevo método público:

~~~java
public void recargarDesdePersistencia() throws SQLException
~~~

No exige modificar el menú ni los otros servicios: al crear ClienteService ya se recarga
el árbol mediante ese método. Si en una operación administrativa se requiere actualizar
la instantánea de una instancia existente desde la base, el llamador puede ejecutar:

~~~java
clientes.recargarDesdePersistencia();
~~~

El llamador debe manejar SQLException como las demás operaciones persistentes. No se
agrega una opción al menú ni se modifica ControladorConsola, que pertenece al integrante 3.
Buscar y listar siguen usando el BST en memoria, no hacen una consulta SQL por búsqueda.
Una modificación externa no aparece automáticamente hasta una recarga o nueva instancia.
Se conserva la condición de una sesión por base; este método no ofrece coordinación de
varios procesos ni de escrituras externas concurrentes.

## Dependencias reales

Para la producción: Cliente, ArbolBST y NodoCliente propios; ClienteRepositorio y
TurnoRepositorio del integrante 3, ya incluidos desde el día 2. Las implementaciones
DAO existentes siguen siendo válidas sin cambios de constructor ni consultas.

PruebaLimitesBSTDia4 no necesita Maven, SQLite ni archivos de día 2 de otro integrante.
PruebaClientesDia4 usa PreparacionPruebas y DAO del integrante 3 y TurnoService del 2,
como quedaron en el día 2. PruebaRecargaClientesDia4 usa los contratos y DAO del 3 y un
decorador privado de repositorio solo de prueba: delega operaciones al DAO real y puede
simular un fallo o una lista inválida; no es otra clase DAO de producción.

Las regresiones del día 3 requieren también las consultas y menú que entregó el integrante 3.
Si tu rama solo contiene tus propios archivos, no compilará por Maven sin esas dependencias.
Puedes disponer de ellas en una copia temporal de comprobación, sin agregarlas a tu commit.
No se ha realizado la integración remota de las ramas.

## Compilación y pruebas en VS Code

Con JDK 21 y Maven, abrir la raíz que contiene pom.xml. Confirmar que ambos usan Java 21:

~~~powershell
java -version
mvn -version
mvn test-compile
~~~

Ejecutar las pruebas nuevas sobre la base común con sus dependencias:

~~~powershell
mvn test-compile exec:java "-Dexec.mainClass=edu.oficina.pruebas.PruebaLimitesBSTDia4" "-Dexec.classpathScope=test"
mvn test-compile exec:java "-Dexec.mainClass=edu.oficina.pruebas.PruebaClientesDia4" "-Dexec.classpathScope=test"
mvn test-compile exec:java "-Dexec.mainClass=edu.oficina.pruebas.PruebaRecargaClientesDia4" "-Dexec.classpathScope=test"
~~~

Se usa la propiedad exec.mainClass del POM del día 2, sin cambiar ese POM aquí.
Son programas main de prueba: mvn test por sí solo no los ejecuta.

Para probar únicamente los límites del árbol en tu rama, sin los DAO:

~~~powershell
New-Item -ItemType Directory -Force target/prueba-bst-dia4 | Out-Null
javac --release 21 -encoding UTF-8 -d target/prueba-bst-dia4 src/main/java/edu/oficina/modelo/Cliente.java src/main/java/edu/oficina/estructuras/NodoCliente.java src/main/java/edu/oficina/estructuras/ArbolBST.java src/test/java/edu/oficina/pruebas/PruebaLimitesBSTDia4.java
java -cp target/prueba-bst-dia4 edu.oficina.pruebas.PruebaLimitesBSTDia4
~~~

## Resultados ejecutados en esta preparación

- Maven compiló fuentes y pruebas con release 21: BUILD SUCCESS.
- PruebaLimitesBSTDia4 pasó: vacío, 256 claves en tres órdenes deterministas, búsquedas,
  inorden, códigos extremos, actualización, duplicados, entradas nulas y árboles sesgados.
- PruebaClientesDia4 pasó: códigos positivos extremos, nombres con acento/apóstrofo,
  nombres inválidos, teléfonos de longitud/formato incorrecto, ceros iniciales,
  duplicado inactivo, actualización sin reactivación, bloqueos por pendientes,
  desactivación después de cancelar o atender y fallo real al guardar el estado.
- PruebaRecargaClientesDia4 pasó: adopta ediciones y altas persistidas, conserva inactivos,
  falla la consulta y mantiene la instantánea anterior, falla una construcción parcial,
  acepta un repositorio vacío sin borrar la base y recarga repetidamente sin duplicados.
- Pasaron también las diez pruebas anteriores: PruebaBST, PruebaClientes, PruebaCola,
  PruebaTurnos, PruebaServicios, PruebaPersistencia, PruebaIntegracionClientesDia3,
  PruebaIntegracionTurnosDia3, PruebaConsultasDia3 y PruebaMenuDia3.
- Se ejecutó además el comando Maven documentado para PruebaRecargaClientesDia4:
  BUILD SUCCESS. Las salidas completas nuevas están en docs/dia4/resultados/.

Las pruebas persistentes utilizaron bases temporales del apoyo existente y no tocaron
data/turnos.db del usuario. Los triggers de fallo y las modificaciones directas de DAO
se realizaron solo allí. La lista vacía o duplicada simulada se devuelve desde un
decorador de prueba; no se borran ni duplican registros reales para provocarla.
Esta verificación es local sobre la base reconstruida, no una comprobación de GitHub.

## Incorporar sin perder el historial

1. Revisar que estás en clientes-bst y que no tienes cambios locales inesperados.
2. Comparar ClienteService.java antes de copiar; el manifiesto incluye el hash de la
   base esperada. Si difiere de tu versión, revisar el diff antes de sustituirlo.
3. Copiar el contenido de Entrega_Dia4_Integrante1 a la raíz del repositorio conservando
   src/main/java, src/test/java y docs. Mantener todos los archivos anteriores no incluidos.
4. Revisar los cambios y seleccionar solo los archivos propios de esta jornada.
   No agregar target, bases temporales ni dependencias ajenas usadas para probar.
5. Hacer un nuevo commit en clientes-bst. No crear ramas nuevas, borrar commits ni
   reescribir el historial. No es necesario incluir archivos de otros integrantes.

Mensaje de commit recomendado, después de revisar y probar:

~~~text
Añadir recarga segura del BST y pruebas de clientes para día 4
~~~

No se crearon commits ni ramas ni se realizaron merges o subidas desde esta preparación.

## Pendientes para integrar

- Confirmar que la copia real de la rama conserva los contratos y paquetes revisados.
- Disponer de los módulos de integrantes 2 y 3 al ejecutar pruebas persistentes o el menú.
- Repetir las pruebas en el resultado revisado de integrar las ramas antes de la entrega final.
- Coordinar la documentación y la defensa final con el grupo. No se modifican ni se
  completan en esta carpeta los módulos o entregables que corresponden a los demás.
