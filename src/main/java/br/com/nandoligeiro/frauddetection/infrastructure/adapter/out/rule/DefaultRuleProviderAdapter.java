package br.com.nandoligeiro.frauddetection.infrastructure.adapter.out.rule;

import br.com.nandoligeiro.frauddetection.application.port.out.RuleProviderPort;
import br.com.nandoligeiro.frauddetection.domain.fraud.model.FraudSeverity;
import br.com.nandoligeiro.frauddetection.domain.rule.model.FraudRule;
import br.com.nandoligeiro.frauddetection.domain.rule.model.HighAmountRule;
import br.com.nandoligeiro.frauddetection.domain.rule.model.InternationalRemoteRule;
import br.com.nandoligeiro.frauddetection.domain.transaction.model.vo.Money;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@ConditionalOnProperty(name = "fraud.rules.default-enabled", havingValue = "true")
public class DefaultRuleProviderAdapter implements RuleProviderPort {

    private final List<FraudRule> rules;

    public DefaultRuleProviderAdapter(
            @Value("${fraud.rules.high-amount.threshold:5000.00}") BigDecimal highAmountThreshold,
            @Value("${fraud.rules.high-amount.currency:BRL}") String currency,
            @Value("${fraud.rules.home-country:BR}") String homeCountry
    ) {
        this.rules = List.of(
                new HighAmountRule(
                        1,
                        100,
                        true,
                        FraudSeverity.HIGH,
                        Money.of(highAmountThreshold, currency)
                ),
                new InternationalRemoteRule(
                        1,
                        90,
                        true,
                        FraudSeverity.MEDIUM,
                        homeCountry
                )
        );
    }

    @Override
    public List<FraudRule> loadRules() {
        return rules;
    }
}
