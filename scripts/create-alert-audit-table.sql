create table if not exists fraud_alert_audit (
    alert_id varchar(80) primary key,
    transaction_id varchar(80) not null,
    account_id varchar(80) not null,
    decision varchar(30) not null,
    severity varchar(30) not null,
    triggered_rules text not null,
    created_at timestamp not null
);

create index if not exists idx_fraud_alert_audit_transaction_id
    on fraud_alert_audit (transaction_id);

create index if not exists idx_fraud_alert_audit_created_at
    on fraud_alert_audit (created_at);
