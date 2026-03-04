-- Fix for existing DB created with native enums + Spring Hibernate update
-- Use this only if you want to migrate enum columns to varchar.

BEGIN;

DROP INDEX IF EXISTS uq_cash_sessions_open_per_cashier;

ALTER TABLE users               ALTER COLUMN role TYPE varchar(255) USING role::text;
ALTER TABLE clients             ALTER COLUMN status TYPE varchar(255) USING status::text;
ALTER TABLE memberships         ALTER COLUMN status TYPE varchar(255) USING status::text;
ALTER TABLE membership_history  ALTER COLUMN action TYPE varchar(255) USING action::text;
ALTER TABLE sales               ALTER COLUMN type TYPE varchar(255) USING type::text;
ALTER TABLE sales               ALTER COLUMN status TYPE varchar(255) USING status::text;
ALTER TABLE sales               ALTER COLUMN payment_method TYPE varchar(255) USING payment_method::text;
ALTER TABLE inventory_movements ALTER COLUMN type TYPE varchar(255) USING type::text;
ALTER TABLE attendance_records  ALTER COLUMN method TYPE varchar(255) USING method::text;
ALTER TABLE cash_sessions       ALTER COLUMN status TYPE varchar(255) USING status::text;

CREATE UNIQUE INDEX uq_cash_sessions_open_per_cashier
    ON cash_sessions(cashier_id)
    WHERE status = 'ABIERTA';

DROP TYPE IF EXISTS user_role;
DROP TYPE IF EXISTS client_status;
DROP TYPE IF EXISTS membership_status;
DROP TYPE IF EXISTS membership_history_action;
DROP TYPE IF EXISTS payment_method;
DROP TYPE IF EXISTS sale_type;
DROP TYPE IF EXISTS sale_status;
DROP TYPE IF EXISTS inventory_movement_type;
DROP TYPE IF EXISTS attendance_method;
DROP TYPE IF EXISTS cash_session_status;

COMMIT;
