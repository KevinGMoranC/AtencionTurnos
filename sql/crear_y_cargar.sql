PRAGMA foreign_keys = ON;
BEGIN;
CREATE TABLE IF NOT EXISTS clientes (
 codigo INTEGER PRIMARY KEY CHECK(codigo > 0),
 nombre_completo TEXT NOT NULL CHECK(length(trim(nombre_completo)) > 0),
 telefono TEXT NOT NULL CHECK(length(telefono) = 8 AND telefono NOT GLOB '*[^0-9]*'),
 activo INTEGER NOT NULL CHECK(activo IN (0,1)));

CREATE TABLE IF NOT EXISTS servicios (
 codigo INTEGER PRIMARY KEY CHECK(codigo > 0),
 nombre TEXT NOT NULL CHECK(length(trim(nombre)) > 0),
 activo INTEGER NOT NULL CHECK(activo IN (0,1)));

CREATE TABLE IF NOT EXISTS turnos (
 numero INTEGER PRIMARY KEY AUTOINCREMENT,
 codigo_cliente INTEGER NOT NULL REFERENCES clientes(codigo),
 codigo_servicio INTEGER NOT NULL REFERENCES servicios(codigo),
 fecha_generacion TEXT NOT NULL,
 estado TEXT NOT NULL CHECK(estado IN ('PENDIENTE','ATENDIDO','CANCELADO')),
 fecha_finalizacion TEXT,
 CHECK((estado = 'PENDIENTE' AND fecha_finalizacion IS NULL)
 OR (estado <> 'PENDIENTE' AND fecha_finalizacion IS NOT NULL)));

CREATE UNIQUE INDEX IF NOT EXISTS un_pendiente_por_cliente
 ON turnos(codigo_cliente) WHERE estado = 'PENDIENTE' ;

CREATE TRIGGER IF NOT EXISTS impedir_desactivar_cliente BEFORE UPDATE OF activo ON clientes
 WHEN NEW.activo = 0 AND EXISTS(SELECT 1 FROM turnos WHERE codigo_cliente = OLD.codigo AND estado = 'PENDIENTE')
 BEGIN SELECT RAISE(ABORT, 'Cliente con turno pendiente'); END;

CREATE TRIGGER IF NOT EXISTS impedir_desactivar_servicio BEFORE UPDATE OF activo ON servicios
 WHEN NEW.activo = 0 AND EXISTS(SELECT 1 FROM turnos WHERE codigo_servicio = OLD.codigo AND estado = 'PENDIENTE')
 BEGIN SELECT RAISE(ABORT, 'Servicio con turnos pendientes'); END;

CREATE TRIGGER IF NOT EXISTS exigir_entidades_activas BEFORE INSERT ON turnos
 WHEN COALESCE((SELECT activo FROM clientes WHERE codigo = NEW.codigo_cliente),0) <> 1
 OR COALESCE((SELECT activo FROM servicios WHERE codigo = NEW.codigo_servicio),0) <> 1
 BEGIN SELECT RAISE(ABORT, 'Cliente y servicio deben existir y estar activos'); END;
INSERT INTO clientes VALUES(1001,'Ana López','55501001',1);
INSERT INTO clientes VALUES(1002,'Carlos Pérez','55501002',1);
INSERT INTO clientes VALUES(1003,'María García','55501003',1);
INSERT INTO clientes VALUES(1004,'José Hernández','55501004',1);
INSERT INTO clientes VALUES(1005,'Luisa Morales','55501005',1);
INSERT INTO servicios VALUES(1,'Información',1);
INSERT INTO servicios VALUES(2,'Entrega de documentos',1);
INSERT INTO servicios VALUES(3,'Actualización de datos',1);
PRAGMA user_version = 1;
COMMIT;
