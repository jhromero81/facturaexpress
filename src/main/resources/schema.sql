-- ============================================================
-- Esquema de base de datos para FacturaExpress
-- Motor: MySQL / InnoDB con cotejamiento utf8mb4
-- ============================================================

-- Crea la base de datos si no existe, con soporte completo de Unicode
CREATE DATABASE IF NOT EXISTS factura_express_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE factura_express_db;

-- ============================================================
-- Tabla: clientes
-- Almacena la información de los clientes/terceros
-- Relacionada con: facturas (1 cliente -> N facturas)
-- ============================================================
CREATE TABLE IF NOT EXISTS clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- Identificador único del cliente
    nombre VARCHAR(150) NOT NULL,            -- Nombre o razón social
    email VARCHAR(200),                      -- Correo electrónico
    telefono VARCHAR(20),                    -- Número de teléfono
    direccion TEXT,                          -- Dirección física
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Fecha de registro
) ENGINE=InnoDB;

-- ============================================================
-- Tabla: productos
-- Catálogo de productos/servicios facturables
-- Relacionada con: detalles_factura (1 producto -> N detalles)
-- ============================================================
CREATE TABLE IF NOT EXISTS productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- Identificador único del producto
    nombre VARCHAR(150) NOT NULL,            -- Nombre del producto
    descripcion TEXT,                        -- Descripción detallada
    precio DECIMAL(12, 2) NOT NULL,          -- Precio unitario (hasta 999,999,999,999.99)
    stock INT NOT NULL DEFAULT 0             -- Cantidad disponible en inventario
) ENGINE=InnoDB;

-- ============================================================
-- Tabla: facturas
-- Cabecera de cada factura electrónica
-- Relacionada con: clientes (FK), detalles_factura (1 factura -> N detalles)
-- ============================================================
CREATE TABLE IF NOT EXISTS facturas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- Número de factura (consecutivo)
    cliente_id BIGINT NOT NULL,              -- Cliente asociado (FK -> clientes.id)
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Fecha de emisión
    total DECIMAL(14, 2) NOT NULL DEFAULT 0.00, -- Total de la factura
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',  -- Estado: PENDIENTE, ENVIADA, etc.
    CONSTRAINT fk_factura_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id)
) ENGINE=InnoDB;

-- ============================================================
-- Tabla: detalles_factura
-- Líneas de detalle de cada factura (producto, cantidad, precio)
-- Relacionada con: facturas (FK con DELETE CASCADE), productos (FK)
-- ============================================================
CREATE TABLE IF NOT EXISTS detalles_factura (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- Identificador único del detalle
    factura_id BIGINT NOT NULL,              -- Factura a la que pertenece (FK)
    producto_id BIGINT NOT NULL,             -- Producto facturado (FK)
    cantidad INT NOT NULL,                   -- Cantidad del producto
    precio_unitario DECIMAL(12, 2) NOT NULL,  -- Precio unitario al momento de facturar
    subtotal DECIMAL(14, 2) NOT NULL,        -- Subtotal (cantidad * precio_unitario)
    CONSTRAINT fk_detalle_factura FOREIGN KEY (factura_id) REFERENCES facturas(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
) ENGINE=InnoDB;

-- ============================================================
-- Tabla: usuarios
-- Usuarios del sistema con autenticación por hash SHA-256
-- Los roles controlan los permisos: ADMIN, VENDEDOR, CONTADOR
-- ============================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- Identificador único del usuario
    username VARCHAR(50) NOT NULL UNIQUE,    -- Nombre de usuario (login)
    password_hash VARCHAR(64) NOT NULL,      -- Hash SHA-256 de la contraseña
    nombre VARCHAR(150) NOT NULL,            -- Nombre completo del usuario
    email VARCHAR(200),                      -- Correo electrónico
    rol VARCHAR(20) NOT NULL DEFAULT 'VENDEDOR',  -- Rol: ADMIN, VENDEDOR, CONTADOR
    activo BOOLEAN DEFAULT TRUE,             -- Indica si el usuario está activo
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Fecha de creación
) ENGINE=InnoDB;

-- ============================================================
-- Seed data: usuarios por defecto
-- Todos comparten la misma contraseña: 'admin123'
-- Hash SHA-256: 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
-- ============================================================
INSERT INTO usuarios (username, password_hash, nombre, email, rol) VALUES
('admin',    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Administrador', 'admin@facturaexpress.com', 'ADMIN'),
('vendedor', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Vendedor Demo', 'vendedor@facturaexpress.com', 'VENDEDOR'),
('contador', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Contador Demo', 'contador@facturaexpress.com', 'CONTADOR');
