# Guía de integración final — tres ramas existentes

## Base y alcance

El grupo confirmó que los días 1–4 se subieron exactamente como fueron entregados.
Esta versión local une esas fuentes y agrega solo pruebas de cierre, ejecución,
documentación y base inicial. No se presupone que main ya las contenga. No se han
creado ramas, commits, PR, pushes ni merges desde esta preparación.

Hay cuatro carpetas: tres diferencias por responsable y Proyecto_Final_Integrado,
que contiene la unión completa. Las carpetas individuales no son proyectos Maven
independientes y no se deben subir como subcarpetas dentro del repositorio.

## Qué incorpora cada integrante

| Rama | Archivos finales propios |
|---|---|
| clientes-bst | ValidacionFinalClientes.java, UML_CLIENTES.mmd y docs/final/INTEGRANTE_1.md |
| turnos-cola | ValidacionFinalTurnos.java, UML_TURNOS.mmd y docs/final/INTEGRANTE_2.md |
| servicios-bd | PruebasFinales.java, pom.xml, .gitignore, configuración VS Code, scripts, base inicial y documentación final compartida |

El inventario exacto y los hashes están en docs/final/MANIFIESTO_FINAL.json. Los archivos
no incluidos en las diferencias se mantienen desde los días anteriores. El proyecto
integrado contiene los Java completos, los DAO, el script SQL y todas las pruebas.
No hubo cambios de producción necesarios tras la revisión: las funciones ya estaban
implementadas y pasaron las pruebas; no se reescribieron para simular un avance.

Cada persona compara y copia solo el CONTENIDO de su carpeta en su misma rama,
conservando rutas. Revisa el diff y selecciona explícitamente sus archivos. No agregar
dependencias de otro integrante copiadas a una carpeta de comprobación, target ni una
base de demostración. Cada uno realizará su propio commit y push, conservando historial.

## Archivos compartidos y conflictos

Main, POM, .gitignore, scripts, configuración VS Code, base, README y documentos globales
se coordinan por el integrante 3. Los módulos de clientes y turnos siguen perteneciendo
a 1 y 2. El enum de estado continúa dentro de Turno y no se añade otra clase Estado.

- Si POM aparece diferente, conservar release 21, dependencias SQLite/SLF4J y la propiedad
  exec.mainClass. El cambio final es versión 1.0.0; no eliminar configuración funcional.
- Si README difiere, conservar la versión final y completar los datos reales del grupo.
- No mezclar una base SQLite binaria con la de otra persona. Guardar la base de trabajo
  fuera de la operación de integración y usar una copia inicial para el ZIP final.
- No aceptar automáticamente un lado completo de conflictos en src. Comparar firmas,
  archivos de base y cambios. Las entregas no tienen rutas compartidas entre responsables.
- Los documentos históricos de días anteriores se conservan. La guía vigente es esta
  y el README final, no instrucciones antiguas que describen un avance incompleto.

Antes de integrar, comprobar el historial de las ramas y que comparten una base común.
Si hay cambios no suministrados, ramas sin ancestro común o contenido inesperado, revisar
con el equipo; no forzar el historial ni sobrescribir el repositorio con la referencia.

## Orden de revisión e integración

1. Cada integrante sube su cierre a su rama original después de revisarlo y probarlo.
2. Abrir los tres PR como propuestas revisables, sin fusión automática.
3. Revisar localmente la unión de todos los módulos contra Proyecto_Final_Integrado.
4. Integrar clientes-bst, después turnos-cola y finalmente servicios-bd para incorporar
   configuración, arranque y documentación compartida. Hasta reunir las tres ramas,
   un estado intermedio puede no compilar: no declararlo terminado ni presentarlo.
5. Después de cada conflicto revisar el resultado, y al completar las tres ramas ejecutar
   la verificación completa y la demostración sobre una copia de datos iniciales.

No crear ramas adicionales para este procedimiento ni realizar merges sin revisión.

## Cómo crear los Pull Requests

En GitHub, abrir el repositorio, ir a Pull requests y New pull request. Elegir **base:
main** y **compare: la rama propia**, revisar Commits y Files changed y crear la propuesta
con un título y descripción claros. Repetir para las tres ramas existentes. Es el flujo
que describe la [documentación de creación de PR de GitHub](https://docs.github.com/en/pull-requests/how-tos/create-pull-requests/creating-a-pull-request).

En cada descripción indicar: parte del módulo, cambios finales, pruebas ejecutadas y
dependencias. Pedir revisión al grupo y confirmar que no se incluyeron código ajeno,
binarios de pruebas ni datos particulares. No habilitar auto-merge.

Cuando se apruebe la integración y no haya conflictos pendientes, usar la modalidad de
merge commit si la configuración lo permite: incorpora los commits de la rama en la
base y conserva ese detalle del historial, según [GitHub: pull request merges](https://docs.github.com/en/pull-requests/reference/pull-request-merges).
Si el repositorio exige otra estrategia, revisar con el grupo cómo conservar la evidencia
real de aportes. No hacer commits en nombre de otra persona.

## Comprobar main después de los PR

En una copia limpia de main actualizada, sin cambios locales pendientes, verificar que
aparezcan pom.xml, data/turnos.db, sql/crear_y_cargar.sql, todos los paquetes src/main,
las pruebas y documentos del inventario LISTA_ARCHIVOS.md. Revisar autores e historial
en GitHub. No basta con ver tres nombres en README para acreditar aportes reales.

Con Java 21 y Maven:

~~~powershell
java -version
mvn -version
mvn test-compile exec:java "-Dexec.mainClass=edu.oficina.pruebas.PruebasFinales" "-Dexec.classpathScope=test"
mvn compile exec:java
~~~

La verificación debe terminar en 21/21 y BUILD SUCCESS. El menú debe funcionar en sus
once opciones. Repetir docs/DEMOSTRACION.md con cierre y reapertura reales. Verificar
que la base de entrega, en una copia sin usar, siga con 5 clientes/3 servicios/0 turnos.
La aplicación en uso no debe borrar los turnos cuando se reinicia.

## Preparar el ZIP de Canvas

Usar el proyecto integrado sin target ni .git y con base inicial sin turnos. Completar
nombres, carnés y grupo en README. Confirmar el enlace del repositorio con las tres
contribuciones revisadas. El archivo GrupoXX_AtencionTurnos.zip preparado es la versión
local completa; sustituir XX por el número real y generar una copia actualizada si se
editaron datos del equipo después. El paquete de cuatro carpetas es para coordinar al
equipo, no es el ZIP que se debe subir al catedrático.
