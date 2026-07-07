package br.com.nandoligeiro.frauddetection.infrastructure.adapter.out.audit;

import br.com.nandoligeiro.frauddetection.application.port.out.FraudAlertAuditPort;
import br.com.nandoligeiro.frauddetection.domain.fraud.model.FraudAlert;
import br.com.nandoligeiro.frauddetection.domain.fraud.model.TriggeredRule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "fraud.audit.enabled", havingValue = "true")
public class JdbcAlertAuditAdapter implements FraudAlertAuditPort {

    private final String jdbcUrl;
    private final String username;
    private final String password;

    public JdbcAlertAuditAdapter(
            @Value("${fraud.audit.jdbc-url}") String jdbcUrl,
            @Value("${fraud.audit.username}") String username,
            @Value("${fraud.audit.password}") String password
    ) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public void save(FraudAlert alert) {
        String sql = "insert into fraud_alert_audit (alert_id, transaction_id, account_id, decision, severity, triggered_rules, created_at) values (?, ?, ?, ?, ?, ?, ?) on conflict (alert_id) do nothing";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, alert.alertId());
            statement.setString(2, alert.transactionId());
            statement.setString(3, alert.accountId());
            statement.setString(4, alert.decision().name());
            statement.setString(5, alert.severity().name());
            statement.setString(6, triggeredRules(alert));
            statement.setTimestamp(7, Timestamp.from(alert.createdAt()));
            statement.executeUpdate();
        } catch (Exception exception) {
            throw new IllegalStateException("could not persist alert audit", exception);
        }
    }

    private String triggeredRules(FraudAlert alert) {
        return alert.triggeredRules().stream().map(this::formatRule).collect(Collectors.joining(","));
    }

    private String formatRule(TriggeredRule rule) {
        return rule.ruleId() + ":" + rule.ruleVersion();
    }
}
