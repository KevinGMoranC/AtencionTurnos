# Revisión del enunciado original

La revisión usa el texto y las tablas de las capturas originales. No se agregan
interfaz gráfica, autenticación, web ni API externa.

| Requisito | Implementación y evidencia |
|---|---|
| Java 21 y Maven | release 21 en pom.xml; compilación real con Maven |
| POO y encapsulamiento | Modelos con campos privados, servicios y clases con responsabilidades separadas |
| Código de cliente positivo, único, inmutable | Cliente valida valor; no setter de código; BST rechaza repetido; PK de SQLite |
| Nombre obligatorio y teléfono de ocho dígitos | Constructor Cliente y pruebas de datos válidos/inválidos, ceros iniciales y Unicode no numérico |
| Registrar, buscar, listar, actualizar y estados | ClienteService y submenú conectado en ControladorConsola |
| BST propio | ArbolBST y NodoCliente; claves por código |
| Búsqueda e inorden recursivos | buscarNodo e inorden privados recursivos; pruebas de árboles variados |
| Inactivos permanecen en árbol e historial | Recarga sin filtrarlos; consultas por cliente conservan turnos |
| Servicios: registro, listado, actualización y estados | ServicioService y ServicioDAO; menú conectado |
| No desactivar con pendientes | Comprobaciones de servicios y triggers SQLite; pruebas negativas |
| Turnos solo para entidades activas existentes | TurnoDAO verifica cliente/servicio en la transacción y trigger de refuerzo |
| Un pendiente por cliente | Validación en cola/DAO e índice único parcial |
| Número automático sin reutilizar | PK AUTOINCREMENT, sin borrado de turnos desde la aplicación; secuencia demostrada 1–5 y pruebas de siguientes |
| Fechas y tres estados | Turno.Estado, fechaGeneracion/fechaFinalizacion; CHECK de estado y fecha; cierres de DAO |
| Cola general FIFO propia | ColaTurnos y NodoTurno; no LinkedList ni ArrayDeque; independiente del servicio |
| Consultar primero sin retirar | consultarSiguiente(); pruebas de estructura y servicio |
| Atención FIFO y cola vacía | Vista muestra número/cliente/servicio; servicio guarda atención antes de desencolar; aviso en vacío |
| Cancelación por número y orden restante | Enlaces propios; estado/fecha guardados antes del retiro; pruebas de primero/intermedio/último/único |
| No cancelar finalizados | Cola solo de pendientes y condición en DAO; rechazo de cancelación repetida |
| Consultas pendientes/atendidos/cancelados | Cola y ConsultaService/TurnoDAO ordenados por número |
| Historial de cliente | ConsultaService y historialCliente con parámetro preparado |
| Resumen por servicio y cero turnos | LEFT JOIN, CASE/COUNT y ResumenServicio; prueba con servicio sin turnos incluso inactivo |
| Colección adicional | ArrayList de resultados/recorridos, sin reemplazar estructuras propias |
| Ruta data/turnos.db | ConexionBD usa Path relativo; scripts fijan directorio del proyecto |
| Tablas, PK y FK | clientes, servicios, turnos; esquema SQL y verificación de relaciones |
| foreign_keys en conexiones | PRAGMA al abrir cada conexión; prueba del valor 1 |
| PreparedStatement y ResultSet | DAO tipados, parámetros preparados; consultas procesadas y recursos cerrados |
| Persistencia al cerrar y abrir | Demostración real en dos procesos Java; estados y cola recuperados |
| Carga inicial una sola vez | InicializadorBD transaccional y marcador user_version; prueba de reinicio sin restablecer ediciones |
| Fuente principal SQLite | Guardar antes de actualizar memoria; constructores recargan; pruebas de errores y coherencia campo a campo |
| Interfaces usadas efectivamente | Repositorios y ProveedorConexion implementados/inyectados en ejecución |
| MVC y responsabilidad única | Vista, controlador, servicios, DAO, persistencia y modelos separados; Main solo conecta |
| Menú de once opciones y entradas inválidas | ControladorConsola y VistaConsola; PruebaMenuDia3 y demostración |
| Base inicial y script | data/turnos.db con 5/3/0; script probado en base nueva equivalente |
| README, UML, ER e informe | README, docs/DIAGRAMAS.md, fuentes Mermaid y REPORTE_PRUEBAS.md |
| Rutas relativas y proyecto portable | Código y scripts sin rutas particulares; comprobación del paquete extraído |
| Entregas individuales compatibles | Diferencias por responsable; manifiesto y comparación con integrado |
| Git con aportes de los tres | Pendiente de revisión humana de sus ramas/PR; este paquete no crea historial ni autorías |
| Datos del grupo y defensa | Espacios por completar; guía de demostración y preguntas |

## Alcance de las afirmaciones

Las pruebas se ejecutaron sobre las copias locales exactas que confirmó el grupo.
No se verificó una copia autenticada de GitHub ni se afirma que main esté integrado.
El número de grupo, nombres/carnés y revisión de PR son pendientes humanos visibles.
No se conocen errores funcionales en los escenarios probados; no se promete coordinación
de escrituras simultáneas desde varios procesos ni bases con esquemas ajenos.
