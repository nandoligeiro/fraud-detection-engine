package br.com.nandoligeiro.frauddetection.infrastructure.adapter.out.audit;

import br.com.nandoligeiro.frauddetection.domain.fraud.model.FraudAlert;
import br.com.nandoligeiro.frauddetection.domain.fraud.model.FraudDecision;
import br.com.nandoligeiro.frauddetection.domain.fraud.model.FraudSeverity;
import br.com.nandoligeiro.frauddetection.domain.fraud.model.TriggeredRule;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.DriverManager;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
class JdbcAlertAuditAdapterTest {

    @Container
    static final GenericContainer<?> db = new GenericContainer<>("postgres:16-alpine")
            .withEnv("POSTGRES_DB", "fraud")
            .withEnv("POSTGRES_USER", "fraud")
            .withEnv("POSTGRES_PASSWORD", "fraud")
            .withExposedPorts(5432);

    @Test
    void shouldSaveAuditRow() throws Exception {
        String url = "jdbc:postgresql://" + db.getHost() + ":" + db.getMappedPort(5432) + "/fraud";
        createTable(url);

        JdbcAlertAuditAdapter adapter = new JdbcAlertAuditAdapter(url, "fraud", "fraud");
        FraudAlert alert = new FraudAlert("alert-test-001", "tx-test-001", "account-test-001", FraudSeverity.HIGH, FraudDecision.SUSPICIOUS, List.of(new TriggeredRule("HIGH_AMOUNT", 1, "High amount")), Instant.parse("2026-01-01T10:00:00Z"));

        adapter.save(alert);

        try (var connection = DriverManager.getConnection(url, "fraud", "fraud");
             var statement = connection.prepareStatement("select count(*) from fraud_alert_audit")) {
            try (var resultSet = statement.executeQuery()) {
                resultSet.next();
                assertThat(resultSet.getInt(1)).isEqualTo(1);
            }
        }
    }

    private void createTable(String url) throws Exception {
        String sql = "create table if not exists fraud_alert_audit (alert_id varchar(80) primary key, transaction_id varchar(80) not null, account_id varchar(80) not null, decision varchar(30) not null, severity varchar(30) not null, triggered_rules text not null, created_at timestamp not null)";
        try (var connection = DriverManager.getConnection(url, "fraud", "fraud"); var statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
