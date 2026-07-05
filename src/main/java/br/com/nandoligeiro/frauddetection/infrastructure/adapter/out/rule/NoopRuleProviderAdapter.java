package br.com.nandoligeiro.frauddetection.infrastructure.adapter.out.rule;

import br.com.nandoligeiro.frauddetection.application.port.out.RuleProviderPort;
import br.com.nandoligeiro.frauddetection.domain.model.rule.FraudRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NoopRuleProviderAdapter implements RuleProviderPort {

    @Override
    public List<FraudRule> loadRules() {
        return List.of();
    }
}
