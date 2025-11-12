CREATE TABLE users
(
    id            UUID PRIMARY KEY,
    email         VARCHAR(100) UNIQUE NOT NULL,
    password      VARCHAR(255)        NOT NULL,
    name          VARCHAR(100),
    phone         VARCHAR(20),
    address       VARCHAR(255),
    role          VARCHAR(20) DEFAULT 'USER',
    status        VARCHAR(20) DEFAULT 'ACTIVE',
    created_at    TIMESTAMP   DEFAULT NOW()
);

CREATE TABLE accounts
(
    id           UUID PRIMARY KEY,
    user_id      UUID                          NOT NULL REFERENCES users (id),
    number       VARCHAR(30) UNIQUE            NOT NULL,
    type         VARCHAR(20)                   NOT NULL, -- CHECKING/SAVINGS/CREDIT
    currency     VARCHAR(3)     DEFAULT 'RUB',
    balance      NUMERIC(19, 2) DEFAULT 0      NOT NULL,
    credit_limit NUMERIC(19, 2) DEFAULT 0      NOT NULL,
    daily_limit  NUMERIC(19, 2) DEFAULT 500000 NOT NULL,
    status       VARCHAR(20)    DEFAULT 'ACTIVE',
    version      INT            DEFAULT 0      NOT NULL,
    created_at   TIMESTAMP      DEFAULT NOW()
);

CREATE INDEX idx_accounts_user_id ON accounts (user_id);

CREATE TABLE transactions
(
    id              UUID PRIMARY KEY,
    type            VARCHAR(20)    NOT NULL, -- DEPOSIT, WITHDRAWAL, TRANSFER, COMMISSION, REVERSAL
    from_account_id UUID REFERENCES accounts (id),
    to_account_id   UUID REFERENCES accounts (id),
    amount          NUMERIC(19, 2) NOT NULL CHECK (amount > 0),
    currency        VARCHAR(3)  DEFAULT 'RUB',
    description     VARCHAR(255),
    initiated_by    UUID REFERENCES users (id),
    idempotency_key VARCHAR(100),
    state           VARCHAR(20) DEFAULT 'POSTED',
    related_txn_id  UUID REFERENCES transactions (id),
    created_at      TIMESTAMP   DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_txn_idempotency ON transactions (idempotency_key);

CREATE TABLE password_reset_tokens
(
    id         UUID PRIMARY KEY,
    user_id    UUID                NOT NULL REFERENCES users (id),
    token      VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMP           NOT NULL,
    used       BOOLEAN DEFAULT FALSE
);

CREATE TABLE limit_rules
(
    id             UUID PRIMARY KEY,
    operation_type VARCHAR(20) NOT NULL,
    limit_per_txn  NUMERIC(19, 2),
    limit_daily    NUMERIC(19, 2),
    limit_monthly  NUMERIC(19, 2),
    created_at     TIMESTAMP DEFAULT NOW()
);

CREATE TABLE fee_rules
(
    id             UUID PRIMARY KEY,
    operation_type VARCHAR(20) NOT NULL,
    percent_fee    NUMERIC(5, 2)  DEFAULT 0,
    min_fee        NUMERIC(19, 2) DEFAULT 0,
    max_fee        NUMERIC(19, 2) DEFAULT 0,
    created_at     TIMESTAMP      DEFAULT NOW()
);