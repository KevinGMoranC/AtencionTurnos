# Demostración y defensa

## Preparar sin destruir información

Usar una copia nueva del proyecto integrado y de su base inicial sin turnos. No
restablecer una base de trabajo ni ejecutar el script SQL sobre datos existentes.
Si ya se probó el menú en la copia, extraer otra copia del ZIP para la demostración.
Confirmar java -version y mvn -version en Java 21. Ejecutar mvn compile exec:java.

## Siete minutos de ejecución

| Tiempo orientativo | Acción |
|---|---|
| 0:00–0:45 | Menú 1, opción 3, código 1003: María García; volver con 0 |
| 0:45–2:15 | Menú 3 cinco veces: 1001/1, 1002/2, 1003/1, 1004/3 y 1005/2 |
| 2:15–2:45 | Intentar otro para 1001: rechazo; menú 5, cancelar 3 |
| 2:45–3:30 | Menú 6: pendientes 1/2/4/5; menú 4 dos veces: atiende 1 y 2 |
| 3:30–4:15 | Menú 11; volver a ejecutar; menú 6: pendientes 4/5 |
| 4:15–5:00 | Menú 4 dos veces: 4 y 5; otra atención: aviso de cola vacía |
| 5:00–6:00 | Menú 9, cliente 1003: turno 3 cancelado; menú 10: resumen esperado |
| 6:00–7:00 | Mostrar una validación, la estructura del código y el reporte de pruebas |

Resumen: Información 0/1/1/2; Entrega 0/2/0/2; Actualización 0/1/0/1.
Total: 0 pendientes, 4 atendidos, 1 cancelado y 5 turnos. Las evidencias del reporte
corresponden a esa secuencia real con dos procesos.

## Tres minutos de preguntas

- Integrante 1: caso base de buscarNodo y de inorden; por qué el código no cambia;
  por qué un cliente inactivo permanece; qué ocurre si falla SQLite al actualizar;
  cómo una recarga fallida conserva el árbol anterior.
- Integrante 2: referencias primero, ultimo y siguiente; cancelar primer/intermedio/último;
  atender después de vaciar; diferencia entre desenlazar y guardar estado final;
  cómo se recupera FIFO desde la base.
- Integrante 3: claves, índice parcial, AUTOINCREMENT y user_version; PreparedStatement,
  ResultSet y cierre; orden de escritura respecto a memoria; separación MVC y LEFT JOIN
  para incluir servicios sin turnos.

Todos deben poder seguir una operación desde la vista al controlador, servicio, DAO
y SQLite y explicar la vuelta del resultado. Las preguntas se basan en el código
entregado, no en aportes o fechas inventadas.
