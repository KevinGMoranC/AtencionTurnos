# Cierre final — Integrante 2

Rama existente: turnos-cola. Responsabilidad: turnos y cola FIFO.

## Qué se entrega

- src/test/java/edu/oficina/pruebas/ValidacionFinalTurnos.java: nueva validación de cierre.
- docs/diagramas/UML_TURNOS.mmd: nueva vista UML propia.
- docs/final/INTEGRANTE_2.md: esta guía.

No se reescriben tus clases funcionales de días 1–4. Se conservaron sus firmas y se
verificaron en la unión final. La validación nueva compara los campos y el orden de la
instantánea del módulo con SQLite después de cada operación de su secuencia, incluyendo
rechazos y un fallo real de escritura. Usa los DAO y servicios reales ya existentes.

## Dependencias

Necesita la base común de días 1–4: modelos, estructuras, contratos, DAO, servicios y
PreparacionPruebas. No copiar clases ajenas dentro de tu entrega ni hacerlas pasar por
archivos nuevos propios. PruebasFinales del integrante 3 incluye esta prueba al cerrar
la unión completa; por eso la rama del integrante 3 necesita tus archivos al integrar.
No hay dependencias circulares nuevas ni cambios en DAO o en el menú.

## Prueba ejecutada y cómo repetirla

Se compiló con Java 21 y Maven y esta prueba pasó, junto con las otras veinte.
Desde una copia común con las dependencias, ejecutar:

~~~powershell
mvn test-compile exec:java "-Dexec.mainClass=edu.oficina.pruebas.ValidacionFinalTurnos" "-Dexec.classpathScope=test"
~~~

Las bases son temporales. El proyecto integrado completo también pasó la demostración
con dos procesos y el resumen del enunciado. Ver docs/REPORTE_PRUEBAS.md.

## Copiar a tu rama y conservar historial

Comparar antes de copiar. Agregar el contenido de Entrega_Final_Integrante2 a la raíz
de tu repositorio en turnos-cola, conservando rutas. Mantener todos los archivos previos.
Revisar el diff y seleccionar solo estos tres archivos. No subir la carpeta contenedora,
la referencia completa, target ni datos de demostración. Hacer un commit nuevo propio
y push a la misma rama. Mensaje sugerido después de revisar y probar:

~~~text
Cerrar validación final de turnos FIFO e historial persistido
~~~

No se hizo ese commit ni se realizó push desde esta preparación. El orden de PR,
archivos compartidos y comprobación de main están en GUIA_INTEGRACION_FINAL.md,
entregado por el integrante 3. Completar datos reales y practicar la defensa del área.
