-- MonkeyClub PostgreSQL schema
-- Execute inside your target DB (example: monkeyclub)

CREATE EXTENSION IF NOT EXISTS pgcrypto;

DO $$
BEGIN
    CREATE TYPE user_role AS ENUM ('ADMIN', 'RECEPCION', 'CAJERO', 'ENTRENADOR');
EXCEPTION
    WHEN duplicate_object THEN NULL;
END
$$;

DO $$
BEGIN
    CREATE TYPE client_status AS ENUM ('ACTIVO', 'VENCIDO', 'SUSPENDIDO');
EXCEPTION
    WHEN duplicate_object THEN NULL;
END
$$;

DO $$
BEGIN
    CREATE TYPE membership_status AS ENUM ('ACTIVA', 'VENCIDA', 'CANCELADA');
EXCEPTION
    WHEN duplicate_object THEN NULL;
END
$$;

DO $$
BEGIN
    CREATE TYPE membership_history_action AS ENUM ('VENTA', 'RENOVACION');
EXCEPTION
    WHEN duplicate_object THEN NULL;
END
$$;

DO $$
BEGIN
    CREATE TYPE payment_method AS ENUM ('EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'QR', 'OTRO');
EXCEPTION
    WHEN duplicate_object THEN NULL;
END
$$;

DO $$
BEGIN
    CREATE TYPE sale_type AS ENUM ('MEMBERSHIP', 'PRODUCT');
EXCEPTION
    WHEN duplicate_object THEN NULL;
END
$$;

DO $$
BEGIN
    CREATE TYPE sale_status AS ENUM ('COMPLETADA', 'ANULADA');
EXCEPTION
    WHEN duplicate_object THEN NULL;
END
$$;

DO $$
BEGIN
    CREATE TYPE inventory_movement_type AS ENUM ('ENTRADA', 'AJUSTE_POSITIVO', 'AJUSTE_NEGATIVO', 'VENTA');
EXCEPTION
    WHEN duplicate_object THEN NULL;
END
$$;

DO $$
BEGIN
    CREATE TYPE attendance_method AS ENUM ('QR', 'CODIGO', 'MANUAL');
EXCEPTION
    WHEN duplicate_object THEN NULL;
END
$$;

DO $$
BEGIN
    CREATE TYPE cash_session_status AS ENUM ('ABIERTA', 'CERRADA');
EXCEPTION
    WHEN duplicate_object THEN NULL;
END
$$;

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role user_role NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS clients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    document VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    status client_status NOT NULL DEFAULT 'ACTIVO',
    internal_notes VARCHAR(2000)
);

CREATE TABLE IF NOT EXISTS membership_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(1000),
    duration_days INTEGER NOT NULL CHECK (duration_days > 0),
    price NUMERIC(12, 2) NOT NULL CHECK (price >= 0),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    name VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    barcode VARCHAR(255) UNIQUE,
    price NUMERIC(12, 2) NOT NULL CHECK (price >= 0),
    stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
    min_stock INTEGER NOT NULL DEFAULT 0 CHECK (min_stock >= 0),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS memberships (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    client_id UUID NOT NULL REFERENCES clients(id),
    plan_id UUID NOT NULL REFERENCES membership_plans(id),
    start_date DATE,
    end_date DATE,
    status membership_status,
    created_by UUID REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS sales (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    sale_number VARCHAR(255) NOT NULL UNIQUE,
    type sale_type NOT NULL,
    status sale_status NOT NULL DEFAULT 'COMPLETADA',
    client_id UUID REFERENCES clients(id),
    membership_id UUID REFERENCES memberships(id),
    payment_method payment_method NOT NULL,
    total_amount NUMERIC(12, 2) NOT NULL CHECK (total_amount >= 0),
    notes VARCHAR(2000),
    annulment_reason VARCHAR(1000),
    annulled_by UUID REFERENCES users(id),
    created_by UUID NOT NULL REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS sale_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    sale_id UUID NOT NULL REFERENCES sales(id) ON DELETE CASCADE,
    product_id UUID REFERENCES products(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(12, 2) NOT NULL CHECK (unit_price >= 0),
    subtotal NUMERIC(12, 2) NOT NULL CHECK (subtotal >= 0)
);

CREATE TABLE IF NOT EXISTS client_audits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    client_id UUID NOT NULL REFERENCES clients(id),
    changed_by UUID REFERENCES users(id),
    action VARCHAR(255) NOT NULL,
    detail VARCHAR(4000) NOT NULL
);

CREATE TABLE IF NOT EXISTS membership_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    membership_id UUID NOT NULL REFERENCES memberships(id),
    client_id UUID NOT NULL REFERENCES clients(id),
    plan_id UUID NOT NULL REFERENCES membership_plans(id),
    start_date DATE,
    previous_end_date DATE,
    new_end_date DATE,
    action membership_history_action,
    performed_by UUID REFERENCES users(id),
    receipt_number VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS inventory_movements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    product_id UUID NOT NULL REFERENCES products(id),
    type inventory_movement_type NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    reason VARCHAR(1000),
    provider VARCHAR(500),
    performed_by UUID REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS attendance_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    client_id UUID NOT NULL REFERENCES clients(id),
    registered_by UUID NOT NULL REFERENCES users(id),
    method attendance_method NOT NULL,
    access_granted BOOLEAN NOT NULL,
    message VARCHAR(500) NOT NULL,
    check_in_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS cash_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    cashier_id UUID NOT NULL REFERENCES users(id),
    opening_amount NUMERIC(12, 2) NOT NULL CHECK (opening_amount >= 0),
    closing_amount NUMERIC(12, 2),
    expected_amount NUMERIC(12, 2),
    difference NUMERIC(12, 2),
    opened_at TIMESTAMPTZ NOT NULL,
    closed_at TIMESTAMPTZ,
    status cash_session_status NOT NULL
);

CREATE TABLE IF NOT EXISTS notification_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    client_id UUID NOT NULL REFERENCES clients(id),
    days_before_expiry INTEGER NOT NULL CHECK (days_before_expiry >= 0),
    message VARCHAR(1000) NOT NULL,
    channel VARCHAR(255) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_clients_name ON clients(first_name, last_name);
CREATE INDEX IF NOT EXISTS idx_clients_phone ON clients(phone);

CREATE INDEX IF NOT EXISTS idx_memberships_client_end_date ON memberships(client_id, end_date DESC);
CREATE INDEX IF NOT EXISTS idx_memberships_end_status ON memberships(end_date, status);

CREATE INDEX IF NOT EXISTS idx_sales_created_at ON sales(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_sales_status_created_at ON sales(status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_sales_created_by_status_created_at ON sales(created_by, status, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_sale_items_sale_id ON sale_items(sale_id);
CREATE INDEX IF NOT EXISTS idx_sale_items_product_id ON sale_items(product_id);

CREATE INDEX IF NOT EXISTS idx_client_audits_client_id_created_at ON client_audits(client_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_membership_history_client_id_created_at ON membership_history(client_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_inventory_movements_product_id_created_at ON inventory_movements(product_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_attendance_records_check_in_at ON attendance_records(check_in_at DESC);
CREATE INDEX IF NOT EXISTS idx_attendance_records_client_check_in_at ON attendance_records(client_id, check_in_at DESC);

CREATE INDEX IF NOT EXISTS idx_cash_sessions_cashier_status ON cash_sessions(cashier_id, status);
CREATE UNIQUE INDEX IF NOT EXISTS uq_cash_sessions_open_per_cashier
    ON cash_sessions(cashier_id)
    WHERE status = 'ABIERTA'::cash_session_status;

CREATE INDEX IF NOT EXISTS idx_notification_logs_created_at ON notification_logs(created_at DESC);

-- Optional seed users are created by backend DataInitializer on startup.
