# Cierre final — Integrante 3

Rama existente: servicios-bd. Responsabilidad: servicios, SQLite, DAO, consola y archivos
comunes. Las funciones ya estaban completas y pasaron la revisión; no se reescribieron
DAO ni reglas sin necesidad. El esquema y el script SQL del día 2 se conservan y se
incluyen en la referencia completa. Los nombres de paquetes y firmas se mantienen.

## Cambios finales propios

- Nuevo PruebasFinales.java: ejecuta los 21 programas, incluidas las validaciones nuevas
  de integrantes 1 y 2. Falla la verificación si hay una excepción o AssertionError.
- pom.xml: versión 1.0.0; conserva Java 21, dependencias y elección de clase main.
- .gitignore: permite versionar únicamente launch.json dentro de la configuración VS Code.
- .vscode/launch.json y scripts ejecutar.ps1/verificar.ps1: directorio y consola correctos.
- data/turnos.db: nueva base inicial 5 clientes/3 servicios activos y cero turnos.
- README, guía de integración, inventario, UML general/ER, matriz de cumplimiento,
  reporte, evidencias y demostración final. El manifiesto muestra las rutas exactas.

La base inicial se creó ejecutando el Main Java actual, y su contenido, integridad y
relaciones se compararon con una base nueva creada por el script SQL. No sustituir
con ese binario una base de trabajo que deba conservarse: preparar la copia de entrega
aparte y revisar cualquier data/turnos.db existente antes de copiarla.

## Verificaciones reales

Compilación Java 21 y Maven: BUILD SUCCESS. Verificador y script verificar.ps1: 21/21.
Demostración sobre copia del binario inicial con dos procesos: búsqueda de 1003,
turnos 1–5, rechazo de duplicado, cancelación 3, atención 1/2, reinicio y cola 4/5,
atención restante, historial y resumen 0/4/1/5. La base original de entrega queda sin turnos.
Las evidencias son salida real de procesos; los PNG representan extractos renderizados,
no una fotografía de una ventana. Los TXT contienen los registros completos.

## Dependencias

El menú sigue usando ClienteService y TurnoService de las otras ramas, junto con los
modelos y estructuras originales. PruebasFinales necesita todos los tests previos y
las dos validaciones finales de los otros integrantes. No duplicar sus fuentes en tu
commit para resolver la compilación de una rama aislada. Usar una copia común de
comprobación y luego integrar mediante PR revisados.

## Copiar y subir

Comparar archivos antes de actualizar. Copiar solo el contenido de Entrega_Final_Integrante3
a la raíz de servicios-bd conservando rutas e incluyendo los archivos ocultos .gitignore
y .vscode. Mantener los archivos anteriores no incluidos. Revisar POM y README, y no
sobrescribir bases de trabajo. Seleccionar únicamente rutas propias en el commit.
Mensaje recomendado después de revisar:

~~~text
Preparar ejecución, pruebas conjuntas, base inicial y documentación final
~~~

No se crearon commits, pushes, ramas ni merges. Revisar GUIA_INTEGRACION_FINAL.md para
el orden de PR y la comparación con Proyecto_Final_Integrado. No entregar el paquete
de distribución al catedrático: el ZIP de Canvas contiene solo el proyecto integrado.

Pendiente humano: nombres, carnés, grupo, revisión de PR y repetición de aceptación
sobre main. No se conocen errores funcionales en los escenarios ejecutados localmente.
