# AtencionTurnos — Entrega final

Aplicación de consola de Programación II, Java 21, Maven y SQLite mediante JDBC.
Versión 1.0.0. Continúa los módulos de días 1–4; no es un proyecto reconstruido con
otra arquitectura. Preparación técnica: 30 de octubre de 2026.

## Equipo y repositorio

| Integrante | Nombre completo | Carné | Rama y responsabilidad |
|---|---|---|---|
| 1 | COMPLETAR | COMPLETAR | clientes-bst: clientes y BST |
| 2 | COMPLETAR | COMPLETAR | turnos-cola: turnos y cola propia |
| 3 | COMPLETAR | COMPLETAR | servicios-bd: servicios, JDBC, DAO, consultas y consola |

Grupo: **XX — COMPLETAR**.
Repositorio: https://github.com/KevinGMoranC/AtencionTurnos
Enlace final para entregar (confirmar después de los PR): **COMPLETAR/CONFIRMAR**.
Cada persona debe revisar su implementación y realizar sus propios commits y push.
El paquete local no fabrica autorías ni sustituye la revisión de las ramas reales.

## Ejecutar en VS Code

1. Instalar JDK 21 y Maven; ambos deben estar disponibles en la terminal.
2. Instalar Extension Pack for Java en VS Code.
3. Abrir la carpeta Proyecto_Final_Integrado, la que contiene pom.xml. En su repositorio,
   abrir la raíz equivalente una vez reunidos los módulos. Esperar la importación Maven.
4. Abrir Terminal > Nueva terminal y comprobar:

~~~powershell
java -version
mvn -version
mvn compile exec:java
~~~

Ambos comandos de versión deben indicar Java 21. Si VS Code selecciona otro JDK, usar
Java: Configure Java Runtime. Si Maven usa otro JDK, ajustar JAVA_HOME al JDK 21.
La primera compilación necesita Internet para descargar dependencias. No se requiere
un servidor de base de datos ni instalar SQLite por separado para ejecutar Java.

En PowerShell también se puede iniciar con:

~~~powershell
.\ejecutar.ps1
~~~

El script cambia a su propia carpeta para conservar las rutas relativas. La configuración
.vscode/launch.json permite ejecutar Main con el directorio correcto y consola integrada.
Si una política local impide ejecutar scripts, usar el comando Maven directo anterior;
no es necesario cambiar la política del sistema.

## Verificación automática

Desde la raíz integrada:

~~~powershell
mvn test-compile exec:java "-Dexec.mainClass=edu.oficina.pruebas.PruebasFinales" "-Dexec.classpathScope=test"
~~~

O ejecutar .\verificar.ps1. Resultado esperado: **21/21 programas de prueba correctos**
y **BUILD SUCCESS**. Son ejecutables main, no pruebas JUnit: mvn test por sí solo no
los ejecuta. El verificador conjunto incluye las 19 pruebas anteriores y las dos
validaciones finales de coherencia memoria/SQLite. No necesita dependencias nuevas.
Las bases de esas pruebas son temporales; no se toca data/turnos.db del proyecto.
Las órdenes individuales siguen disponibles mediante -Dexec.mainClass.

## Funcionalidades del menú

1. Clientes: registrar, listar inorden, buscar por BST, actualizar nombre/teléfono,
   activar o desactivar. El código es positivo, único e inmutable y el teléfono tiene
   ocho dígitos de texto. Los inactivos se conservan y no reciben turnos.
2. Servicios: registrar, listar, actualizar nombre y cambiar estado.
3. Crear turno para cliente y servicio existentes y activos.
4. Atender el primero de la cola global FIFO.
5. Cancelar un pendiente por número conservando el orden de los demás.
6. Consultar pendientes en orden de cola.
7. Consultar atendidos.
8. Consultar cancelados.
9. Consultar historial de un cliente.
10. Consultar resumen por servicio, incluidos los que tienen cero turnos, y el total.
11. Salir sin borrar información.

En submenús, 0 vuelve al principal. Para estado activo, usar s/n. Las entradas inválidas
muestran un mensaje y permiten continuar. Un cliente solo puede tener un pendiente.
No se desactivan clientes o servicios que tengan pendientes. No se cancelan atendidos
ni turnos ya cancelados. La cola vacía se informa sin terminar la aplicación.
El siguiente turno también puede consultarse sin retirarlo con TurnoService.consultarSiguiente().

## SQLite y datos iniciales

Se incluye **data/turnos.db**, con los cinco clientes activos del enunciado:

| Código | Nombre | Teléfono |
|---|---|---|
| 1001 | Ana López | 55501001 |
| 1002 | Carlos Pérez | 55501002 |
| 1003 | María García | 55501003 |
| 1004 | José Hernández | 55501004 |
| 1005 | Luisa Morales | 55501005 |

Servicios activos: 1 Información; 2 Entrega de documentos; 3 Actualización de datos.
**La base de entrega contiene cero turnos.** Ejecutar siempre desde la raíz; la ruta
predeterminada es relativa. Cada conexión habilita foreign_keys. Los parámetros se
escriben con PreparedStatement y las consultas se procesan con ResultSet y cierre seguro.

InicializadorBD carga esquema y datos una sola vez en una transacción, con user_version=1.
No borra ni restablece al iniciar. Si falta la base, crea una nueva con el mismo contenido.
Los números usan INTEGER PRIMARY KEY AUTOINCREMENT: los turnos persistidos no reutilizan
números, aunque se atiendan o cancelen. Un índice único parcial impide dos pendientes
por cliente. Se conservan los turnos finalizados, con fecha de atención o cancelación.

Para crear una base NUEVA mediante sqlite3 (opcional, solo si está instalado):

~~~text
sqlite3 data/turnos.db
.read sql/crear_y_cargar.sql
.quit
~~~

El script es para una base nueva, no para reiniciar una que contenga trabajo.
No copiar el binario inicial sobre una base de desarrollo con información que se deba
conservar. Hacer una copia separada para demostración y entrega. La base con turnos de
la demostración NO es la que se incluye en este paquete.

## Arquitectura

- modelo: Cliente, Servicio y Turno inmutables; Turno.Estado define los tres estados.
- estructuras: NodoCliente y ArbolBST propios; NodoTurno y ColaTurnos propios.
  La búsqueda y el inorden del BST son recursivos. No se usa una cola estándar.
- dao: contratos de repositorio y DAO tipados. Se usan efectivamente interfaces por
  inyección de constructor; las implementaciones manejan JDBC y no muestran menús.
- persistencia: ConexionBD, ProveedorConexion e InicializadorBD.
- servicios: reglas de clientes, servicios y turnos; ConsultaService para las consultas.
- reportes: ResumenServicio, resultado de la agregación SQL con LEFT JOIN.
- vista: VistaConsola, entrada y presentación.
- controlador: ControladorConsola, flujo del menú y llamadas a los servicios.
- Main: prepara la base y conecta las capas, sin concentrar reglas de negocio.

ArrayList se utiliza para resultados y recorridos auxiliares, sin sustituir el BST
ni los enlaces de la cola. Los DAO cierran recursos con try-with-resources.
Primero se confirma la escritura en SQLite y luego se actualiza el BST o la cola.
Los constructores reconstruyen desde SQLite, incluidos clientes inactivos y pendientes
ordenados por número. Las recargas explícitas sustituyen la instantánea completa solo
si la lectura/construcción termina correctamente.

## Límites de uso y verificación

Usar una sola instancia de consola y una instancia de cada servicio con memoria por base.
No se ofrece coordinación de varias aplicaciones o escrituras externas simultáneas;
si se modifica la base fuera de la aplicación, recargar o reiniciar antes de continuar.
Los nombres de cliente y servicio mostrados en historiales son sus nombres actuales:
el enunciado exige conservar códigos y estados, no copias históricas de esos nombres.

No se detectaron errores funcionales en los escenarios ejecutados. Eso no sustituye
la aceptación del grupo sobre la integración real de sus ramas. Los nombres, carnés,
grupo y confirmación del enlace final deben completarse antes de entregar.

## Documentación

- docs/DIAGRAMAS.md y docs/diagramas/: UML y entidad-relación en Mermaid.
- docs/REPORTE_PRUEBAS.md: resultados y evidencias reales.
- docs/CUMPLIMIENTO.md: revisión del enunciado requisito por requisito.
- docs/DEMOSTRACION.md: secuencia de diez minutos y preguntas de defensa.
- docs/final/INTEGRANTE_1.md, INTEGRANTE_2.md e INTEGRANTE_3.md: cierre por responsable.
- GUIA_INTEGRACION_FINAL.md: archivos, PR, conflictos y comprobación de main.
- LISTA_ARCHIVOS.md: inventario relativo final.

Se conservan docs/dia2, dia3 y dia4 como registro de entregas anteriores; este README
es la instrucción vigente. No subir las carpetas Entrega_Final_IntegranteN dentro del
repositorio: copiar su CONTENIDO manteniendo rutas, después de comparar.

## Entrega al catedrático

El ZIP GrupoXX_AtencionTurnos.zip contiene solo el proyecto integrado, no las carpetas
de distribución. Cambiar XX por el grupo real y completar los datos personales antes
de subirlo a Canvas. El paquete de coordinación de cuatro carpetas es para el equipo.
No se crearon commits, ramas o PR ni se realizaron pushes o merges en su nombre.
