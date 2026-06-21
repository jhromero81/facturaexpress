-- ============================================================
--  SISTEMA DE FACTURACIÓN ELECTRÓNICA - FacturaExpress
--  Esquema completo de base de datos
--  Motor: MySQL 8.4+ / InnoDB / utf8mb4
--  Ejecutado automáticamente por Spring Boot (spring.sql.init.mode=always)
-- ============================================================

CREATE DATABASE IF NOT EXISTS factura_express_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE factura_express_db;

-- ============================================================
-- TABLA: clientes
-- Almacena los clientes/terceros del sistema
-- Relación: 1 cliente -> N facturas
-- ============================================================
CREATE TABLE IF NOT EXISTS clientes (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(150)    NOT NULL COMMENT 'Nombre o razón social',
    email           VARCHAR(200)    NULL     COMMENT 'Correo electrónico',
    telefono        VARCHAR(20)     NULL     COMMENT 'Número de teléfono',
    direccion       TEXT            NULL     COMMENT 'Dirección fiscal',
    fecha_creacion  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha de registro',
    CONSTRAINT pk_clientes PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='Personas o empresas que reciben facturas';

-- ============================================================
-- TABLA: productos
-- Catálogo de productos/servicios facturables
-- Relación: 1 producto -> N detalles_factura
-- ============================================================
CREATE TABLE IF NOT EXISTS productos (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(150)    NOT NULL COMMENT 'Nombre del producto',
    descripcion     TEXT            NULL     COMMENT 'Descripción detallada',
    precio          DECIMAL(12,2)   NOT NULL COMMENT 'Precio unitario',
    stock           INT             NOT NULL DEFAULT 0 COMMENT 'Unidades disponibles',
    CONSTRAINT pk_productos PRIMARY KEY (id),
    CONSTRAINT chk_productos_precio CHECK (precio >= 0),
    CONSTRAINT chk_productos_stock  CHECK (stock >= 0)
) ENGINE=InnoDB COMMENT='Catálogo de productos y servicios';

-- ============================================================
-- TABLA: usuarios
-- Personal autorizado que opera el sistema
-- Roles: ADMIN, VENDEDOR, CONTADOR
-- ============================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    username        VARCHAR(50)     NOT NULL COMMENT 'Nombre de usuario (login)',
    password_hash   VARCHAR(255)    NOT NULL COMMENT 'Hash BCrypt de la contraseña',
    nombre          VARCHAR(150)    NOT NULL COMMENT 'Nombre completo del usuario',
    email           VARCHAR(200)    NULL     COMMENT 'Correo electrónico',
    rol             VARCHAR(20)     NOT NULL DEFAULT 'VENDEDOR' COMMENT 'ADMIN | VENDEDOR | CONTADOR',
    activo          BOOLEAN         NOT NULL DEFAULT TRUE COMMENT 'Indica si el usuario está activo',
    fecha_creacion  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha de creación',
    CONSTRAINT pk_usuarios PRIMARY KEY (id),
    CONSTRAINT uq_usuarios_username UNIQUE (username)
) ENGINE=InnoDB COMMENT='Usuarios del sistema';

-- ============================================================
-- TABLA: facturas
-- Cabecera de cada factura electrónica
-- Relación: N facturas -> 1 cliente, N facturas -> 1 usuario
-- ============================================================
CREATE TABLE IF NOT EXISTS facturas (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    cliente_id      BIGINT          NOT NULL COMMENT 'Cliente asociado (FK -> clientes.id)',
    usuario_id      BIGINT          NULL     COMMENT 'Usuario que generó la factura',
    fecha           TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora de emisión',
    total           DECIMAL(14,2)   NOT NULL DEFAULT 0.00 COMMENT 'Valor total con impuestos',
    estado          VARCHAR(20)     NOT NULL DEFAULT 'PENDIENTE' COMMENT 'PENDIENTE | ENVIADA | RECHAZADA',
    xml             TEXT            NULL     COMMENT 'Documento XML firmado',
    pdf             LONGBLOB        NULL     COMMENT 'Copia PDF para el cliente',
    cune            VARCHAR(96)     NULL     COMMENT 'Código Único de Nombre Electrónico - DIAN',
    firma_estado    VARCHAR(20)     NOT NULL DEFAULT 'pendiente' COMMENT 'pendiente | firmada | error',
    intentos_dian   INT             NOT NULL DEFAULT 0 COMMENT 'Contador de reintentos envío DIAN',
    correo_enviado  TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '0=pendiente, 1=enviado',
    CONSTRAINT pk_facturas PRIMARY KEY (id),
    CONSTRAINT fk_facturas_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_facturas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT chk_facturas_estado CHECK (estado IN ('PENDIENTE','ENVIADA','RECHAZADA')),
    CONSTRAINT chk_facturas_total  CHECK (total >= 0)
) ENGINE=InnoDB COMMENT='Documento electrónico validado por la DIAN';

ALTER TABLE facturas ADD COLUMN usuario_id    BIGINT       NULL AFTER cliente_id;
ALTER TABLE facturas ADD COLUMN xml           TEXT         NULL AFTER estado;
ALTER TABLE facturas ADD COLUMN pdf           LONGBLOB     NULL AFTER xml;
ALTER TABLE facturas ADD COLUMN cune          VARCHAR(96)  NULL AFTER pdf;
ALTER TABLE facturas ADD COLUMN firma_estado  VARCHAR(20)  NOT NULL DEFAULT 'pendiente' AFTER cune;
ALTER TABLE facturas ADD COLUMN intentos_dian INT          NOT NULL DEFAULT 0 AFTER firma_estado;
ALTER TABLE facturas ADD COLUMN correo_enviado TINYINT(1)  NOT NULL DEFAULT 0 AFTER intentos_dian;

CREATE INDEX idx_facturas_cliente ON facturas(cliente_id);
CREATE INDEX idx_facturas_usuario ON facturas(usuario_id);
CREATE INDEX idx_facturas_fecha   ON facturas(fecha);
CREATE INDEX idx_facturas_estado  ON facturas(estado);

-- ============================================================
-- TABLA: detalles_factura
-- Líneas de detalle de cada factura (producto, cantidad, precio)
-- Resuelve la relación N:M entre facturas y productos
-- ============================================================
CREATE TABLE IF NOT EXISTS detalles_factura (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    factura_id      BIGINT          NOT NULL COMMENT 'Factura a la que pertenece (FK)',
    producto_id     BIGINT          NOT NULL COMMENT 'Producto facturado (FK)',
    cantidad        INT             NOT NULL COMMENT 'Unidades vendidas',
    precio_unitario DECIMAL(12,2)   NOT NULL COMMENT 'Precio unitario al momento de facturar',
    subtotal        DECIMAL(14,2)   NOT NULL COMMENT 'Subtotal (cantidad * precio_unitario)',
    descuento       DECIMAL(10,2)   NOT NULL DEFAULT 0.00 COMMENT 'Descuento aplicado al ítem',
    CONSTRAINT pk_detalles_factura PRIMARY KEY (id),
    CONSTRAINT fk_detalles_factura  FOREIGN KEY (factura_id)  REFERENCES facturas(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_detalles_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_detalles_cantidad  CHECK (cantidad > 0),
    CONSTRAINT chk_detalles_subtotal  CHECK (subtotal >= 0),
    CONSTRAINT chk_detalles_descuento CHECK (descuento >= 0)
) ENGINE=InnoDB COMMENT='Líneas de productos por factura';

ALTER TABLE detalles_factura ADD COLUMN descuento DECIMAL(10,2) NOT NULL DEFAULT 0.00 AFTER subtotal;

-- ============================================================
-- TABLA: reportes
-- Reportes contables y tributarios generados
-- ============================================================
CREATE TABLE IF NOT EXISTS reportes (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    tipo            VARCHAR(50)     NOT NULL COMMENT 'ventas_diarias | impuestos | inventario | etc.',
    fecha_inicio    DATE            NOT NULL COMMENT 'Inicio del rango del reporte',
    fecha_fin       DATE            NOT NULL COMMENT 'Fin del rango del reporte',
    archivo         VARCHAR(255)    NULL     COMMENT 'Ruta o nombre del archivo generado',
    usuario_id      BIGINT          NULL     COMMENT 'Usuario que generó el reporte',
    fecha_creacion  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_reportes PRIMARY KEY (id),
    CONSTRAINT fk_reportes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT chk_reportes_fechas CHECK (fecha_fin >= fecha_inicio)
) ENGINE=InnoDB COMMENT='Reportes contables y tributarios';

CREATE INDEX idx_reportes_fechas  ON reportes(fecha_inicio, fecha_fin);
CREATE INDEX idx_reportes_tipo    ON reportes(tipo);
CREATE INDEX idx_reportes_usuario ON reportes(usuario_id);

-- ============================================================
-- TABLA: errores_sistema
-- Registro de fallos en procesos críticos (DIAN, firma, BD, correo)
-- ============================================================
CREATE TABLE IF NOT EXISTS errores_sistema (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    mensaje         TEXT            NOT NULL COMMENT 'Descripción técnica del error',
    tipo            VARCHAR(50)     NOT NULL COMMENT 'firma | dian | bd | correo | otro',
    factura_id      BIGINT          NULL     COMMENT 'Factura asociada (puede ser NULL)',
    resuelto        TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '0=pendiente, 1=resuelto',
    fecha_resolucion DATETIME       NULL     COMMENT 'Fecha en que se resolvió el error',
    fecha_creacion  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_errores_sistema PRIMARY KEY (id),
    CONSTRAINT fk_errores_factura FOREIGN KEY (factura_id) REFERENCES facturas(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT chk_errores_resuelto CHECK (resuelto IN (0,1))
) ENGINE=InnoDB COMMENT='Registro de fallos en procesos críticos';

CREATE INDEX idx_errores_factura  ON errores_sistema(factura_id);
CREATE INDEX idx_errores_tipo     ON errores_sistema(tipo);
CREATE INDEX idx_errores_resuelto ON errores_sistema(resuelto);

-- ============================================================
-- TABLA: logs_auditoria
-- Auditoría de accesos y operaciones críticas
-- ============================================================
CREATE TABLE IF NOT EXISTS logs_auditoria (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    usuario_id      BIGINT          NOT NULL COMMENT 'Usuario que realizó la acción',
    accion          VARCHAR(200)    NOT NULL COMMENT 'Descripción de la acción realizada',
    tabla_afectada  VARCHAR(50)     NULL     COMMENT 'Tabla sobre la que actuó',
    registro_id     BIGINT          NULL     COMMENT 'ID del registro afectado',
    ip_origen       VARCHAR(45)     NULL     COMMENT 'IP desde donde se realizó la acción',
    fecha           TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_logs_auditoria PRIMARY KEY (id),
    CONSTRAINT fk_logs_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Auditoría de accesos y operaciones críticas';

CREATE INDEX idx_logs_usuario ON logs_auditoria(usuario_id);
CREATE INDEX idx_logs_fecha   ON logs_auditoria(fecha);
CREATE INDEX idx_logs_tabla   ON logs_auditoria(tabla_afectada);

-- ============================================================
-- TABLA: configuracion_empresa
-- Configuración general de la empresa (módulo de configuración)
-- ============================================================
CREATE TABLE IF NOT EXISTS configuracion_empresa (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    nit                 VARCHAR(20)     NOT NULL COMMENT 'NIT de la empresa',
    razon_social        VARCHAR(200)    NOT NULL COMMENT 'Razón social',
    email_facturacion   VARCHAR(200)    NULL     COMMENT 'Email de facturación',
    telefono            VARCHAR(20)     NULL     COMMENT 'Teléfono de contacto',
    direccion           TEXT            NULL     COMMENT 'Dirección fiscal',
    resolucion_dian     VARCHAR(50)     NULL     COMMENT 'Número de resolución DIAN activa',
    certificado_vence   DATE            NULL     COMMENT 'Fecha de vencimiento del certificado digital',
    notif_email         TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Notificaciones por email activas',
    notif_push          TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Notificaciones push activas',
    alertas_dian        TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Alertas de facturación DIAN activas',
    recordatorios       TINYINT(1)      NOT NULL DEFAULT 1 COMMENT 'Recordatorios automáticos activos',
    actualizada         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_configuracion_empresa PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='Configuración de la empresa';

-- ============================================================
-- DATOS DE PRUEBA
-- ============================================================

-- Usuarios por defecto (contraseña: 'admin123')
-- Hash BCrypt generado con LoginService.hashPassword("admin123")
INSERT IGNORE INTO usuarios (username, password_hash, nombre, email, rol, activo) VALUES
('admin',    '$2a$10$EWNk7IvbIFRJJUi5exXkYOX8srQIlqaDz.wtHMo8HncR4.BADvvRu', 'Administrador',    'admin@facturaexpress.com',    'ADMIN',    TRUE),
('vendedor', '$2a$10$EWNk7IvbIFRJJUi5exXkYOX8srQIlqaDz.wtHMo8HncR4.BADvvRu', 'Vendedor Demo',    'vendedor@facturaexpress.com', 'VENDEDOR', TRUE),
('contador', '$2a$10$EWNk7IvbIFRJJUi5exXkYOX8srQIlqaDz.wtHMo8HncR4.BADvvRu', 'Contador Demo',    'contador@facturaexpress.com', 'CONTADOR', TRUE);

-- Clientes de ejemplo
INSERT IGNORE INTO clientes (id, nombre, email, telefono, direccion) VALUES
(1, 'Tech Solutions SAS',       'compras@techsolutions.co',     '6015550101', 'Calle 72 # 10-45, Bogotá'),
(2, 'Comercial Andina Ltda',    'facturas@comercialandina.co',  '6015550202', 'Av. El Dorado # 68C-61, Bogotá'),
(3, 'Juan Pérez',               'juan.perez@gmail.com',         '3001234567', 'Carrera 15 # 85-32, Bogotá'),
(4, 'María Fernanda Gómez',     'maria.gomez@email.com',        '3109876543', 'Calle 100 # 20-30, Medellín'),
(5, 'Carlos Rodríguez L.',      'carlos.rodriguez@email.com',   '3156789012', 'Av. siempre viva # 1-23, Cali');

-- Productos de ejemplo
INSERT IGNORE INTO productos (id, nombre, descripcion, precio, stock) VALUES
(1, 'Laptop Dell Inspiron 15',  'Laptop con procesador Intel i5, 16GB RAM, 512GB SSD',   2850000.00, 25),
(2, 'Mouse Inalámbrico Logitech', 'Mouse ergonómico inalámbrico con receptor USB',            45000.00, 150),
(3, 'Teclado Mecánico RGB',     'Teclado mecánico retroiluminado con switches Cherry MX',   180000.00, 80),
(4, 'Monitor LG 24"',           'Monitor IPS Full HD de 24 pulgadas',                        750000.00, 40),
(5, 'Memoria USB 64GB',         'USB 3.0 de alta velocidad, 64GB de capacidad',               35000.00, 200),
(6, 'Webcam HD 1080p',          'Cámara web con micrófono integrado y resolución Full HD',   120000.00, 60),
(7, 'Hub USB C 7 puertos',      'Hub multipuerto con HDMI, USB 3.0, lector de tarjetas',      85000.00, 45),
(8, 'Audífonos Bluetooth',     'Audífonos inalámbricos con cancelación de ruido activa',    250000.00, 30);

-- Configuración de la empresa
INSERT IGNORE INTO configuracion_empresa (id, nit, razon_social, email_facturacion, telefono, direccion, resolucion_dian, certificado_vence) VALUES
(1, '900.123.456-7', 'Industrias Metalúrgicas S.A.S', 'facturacion@metalurgicasas.com', '+57 601 555 7890', 'Calle 123 #45-67, Bogotá', '18764000001', '2026-06-15');

-- Facturas de ejemplo (últimos 30 días)
INSERT IGNORE INTO facturas (id, cliente_id, usuario_id, fecha, total, estado, cune, firma_estado, intentos_dian, correo_enviado) VALUES
(1, 1, 2, DATE_SUB(NOW(), INTERVAL 28 DAY),  3075000.00, 'ENVIADA',  'CUNE-2026-0001-ABC123XYZ',  'firmada', 1, 1),
(2, 2, 2, DATE_SUB(NOW(), INTERVAL 21 DAY),  1250000.00, 'ENVIADA',  'CUNE-2026-0002-DEF456UVW',  'firmada', 1, 1),
(3, 3, 2, DATE_SUB(NOW(), INTERVAL 14 DAY),   435000.00, 'ENVIADA',  'CUNE-2026-0003-GHI789RST',  'firmada', 0, 1),
(4, 1, 2, DATE_SUB(NOW(), INTERVAL 7 DAY),   5900000.00, 'ENVIADA',  'CUNE-2026-0004-JKL012MNO',  'firmada', 2, 1),
(5, 4, 2, DATE_SUB(NOW(), INTERVAL 3 DAY),    850000.00, 'ENVIADA',  'CUNE-2026-0005-PQR345STU',  'firmada', 0, 0),
(6, 5, 2, DATE_SUB(NOW(), INTERVAL 1 DAY),   2100000.00, 'PENDIENTE', NULL,                    'pendiente', 0, 0),
(7, 2, 2, NOW(),                               980000.00, 'PENDIENTE', NULL,                    'pendiente', 0, 0);

-- Detalles de las facturas
INSERT IGNORE INTO detalles_factura (id, factura_id, producto_id, cantidad, precio_unitario, subtotal, descuento) VALUES
-- Factura 1: Tech Solutions
(1,  1, 1, 1, 2850000.00, 2850000.00, 0.00),
(2,  1, 2, 3,   45000.00,  121500.00, 13500.00),
(3,  1, 5, 3,   35000.00,  103500.00, 1500.00),
-- Factura 2: Comercial Andina
(4,  2, 4, 1,  750000.00,  750000.00, 0.00),
(5,  2, 3, 1,  180000.00,  180000.00, 0.00),
(6,  2, 2, 2,   45000.00,   90000.00, 0.00),
(7,  2, 5, 4,   35000.00,  140000.00, 0.00),
-- Factura 3: Juan Pérez
(8,  3, 2, 1,   45000.00,   45000.00, 0.00),
(9,  3, 5, 2,   35000.00,   70000.00, 0.00),
(10, 3, 6, 1,  120000.00,  120000.00, 0.00),
-- Factura 4: Tech Solutions
(11, 4, 1, 2, 2850000.00, 5700000.00, 0.00),
(12, 4, 8, 1,  250000.00,  200000.00, 50000.00),
-- Factura 5: María Gómez
(13, 5, 6, 2,  120000.00,  240000.00, 0.00),
(14, 5, 7, 1,   85000.00,   85000.00, 0.00),
(15, 5, 2, 1,   45000.00,   45000.00, 0.00),
-- Factura 6: Carlos Rodríguez
(16, 6, 4, 2,  750000.00, 1500000.00, 0.00),
(17, 6, 8, 1,  250000.00,  250000.00, 0.00),
(18, 6, 7, 2,   85000.00,  170000.00, 0.00),
-- Factura 7: Comercial Andina
(19, 7, 3, 2,  180000.00,  360000.00, 0.00),
(20, 7, 5, 5,   35000.00,  175000.00, 0.00),
(21, 7, 6, 1,  120000.00,  120000.00, 0.00),
(22, 7, 2, 5,   45000.00,  225000.00, 0.00);

-- Reportes de ejemplo
INSERT IGNORE INTO reportes (id, tipo, fecha_inicio, fecha_fin, archivo, usuario_id) VALUES
(1, 'ventas_diarias', DATE_SUB(NOW(), INTERVAL 30 DAY), NOW(), 'reportes/ventas_abril_2026.pdf', 3),
(2, 'impuestos',      DATE_SUB(NOW(), INTERVAL 90 DAY), NOW(), 'reportes/impuestos_trimestre.pdf', 3);

-- Errores de ejemplo
INSERT IGNORE INTO errores_sistema (id, mensaje, tipo, factura_id, resuelto, fecha_resolucion) VALUES
(1, 'Timeout conexión DIAN - reintento exitoso en intento 2', 'dian', 1, 1, DATE_SUB(NOW(), INTERVAL 27 DAY)),
(2, 'Error de firma digital - certificado próximo a vencer', 'firma', 4, 0, NULL);

-- Logs de auditoría de ejemplo
INSERT IGNORE INTO logs_auditoria (id, usuario_id, accion, tabla_afectada, registro_id, ip_origen, fecha) VALUES
(1, 2, 'INSERT factura',    'facturas',  1, '192.168.1.10', DATE_SUB(NOW(), INTERVAL 28 DAY)),
(2, 2, 'FIRMA DIGITAL',     'facturas',  1, '192.168.1.10', DATE_SUB(NOW(), INTERVAL 28 DAY)),
(3, 2, 'ENVIO DIAN',        'facturas',  1, '192.168.1.10', DATE_SUB(NOW(), INTERVAL 28 DAY)),
(4, 2, 'INSERT factura',    'facturas',  2, '192.168.1.10', DATE_SUB(NOW(), INTERVAL 21 DAY)),
(5, 2, 'INSERT factura',    'facturas',  3, '192.168.1.10', DATE_SUB(NOW(), INTERVAL 14 DAY)),
(6, 2, 'INSERT factura',    'facturas',  4, '192.168.1.10', DATE_SUB(NOW(), INTERVAL 7 DAY)),
(7, 2, 'INSERT factura',    'facturas',  5, '192.168.1.10', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(8, 3, 'SELECT reporte',    'reportes',  1, '192.168.1.12', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(9, 2, 'INSERT factura',    'facturas',  6, '192.168.1.10', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(10,2, 'INSERT factura',    'facturas',  7, '192.168.1.10', NOW());
