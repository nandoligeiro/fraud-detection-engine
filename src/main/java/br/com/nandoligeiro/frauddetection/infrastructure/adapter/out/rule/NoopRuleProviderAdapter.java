package br.com.nandoligeiro.frauddetection.infrastructure.adapter.out.rule;

import br.com.nandoligeiro.frauddetection.application.port.out.RuleProviderPort;
import br.com.nandoligeiro.frauddetection.domain.rule.model.FraudRule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "fraud.rules.default-enabled", havingValue = "false", matchIfMissing = true)
public class NoopRuleProviderAdapter implements RuleProviderPort {

    @Override
    public List<FraudRule> loadRules() {
        return List.of();
    }
}
