package br.com.nandoligeiro.frauddetection.domain.service;

import br.com.nandoligeiro.frauddetection.domain.model.FraudSeverity;
import br.com.nandoligeiro.frauddetection.domain.model.Transaction;
import br.com.nandoligeiro.frauddetection.domain.model.TransactionChannel;
import br.com.nandoligeiro.frauddetection.domain.model.rule.HighAmountRule;
import br.com.nandoligeiro.frauddetection.domain.model.rule.InternationalCardNotPresentRule;
import br.com.nandoligeiro.frauddetection.domain.model.rule.RuleEvaluationResult;
import br.com.nandoligeiro.frauddetection.domain.model.vo.AccountId;
import br.com.nandoligeiro.frauddetection.domain.model.vo.CardId;
import br.com.nandoligeiro.frauddetection.domain.model.vo.GeoLocation;
import br.com.nandoligeiro.frauddetection.domain.model.vo.Merchant;
import br.com.nandoligeiro.frauddetection.domain.model.vo.Money;
import br.com.nandoligeiro.frauddetection.domain.model.vo.TransactionId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DeterministicRuleEngineTest {

    @Test
    void shouldReturnOnlyTriggeredRulesOrderedByPriority() {
        DeterministicRuleEngine engine = DeterministicRuleEngine.withRules(List.of(
                new InternationalCardNotPresentRule(1, 90, true, FraudSeverity.MEDIUM, "BR"),
                new HighAmountRule(1, 100, true, FraudSeverity.HIGH, Money.of(new BigDecimal("5000.00"), "BRL"))
        ));

        List<RuleEvaluationResult> results = engine.evaluate(transaction());

        assertThat(results).hasSize(2);
        assertThat(results.get(0).ruleId().value()).isEqualTo("HIGH_AMOUNT");
        assertThat(results.get(1).ruleId().value()).isEqualTo("INTERNATIONAL_CARD_NOT_PRESENT");
    }

    private Transaction transaction() {
        return Transaction.create(
                TransactionId.of("tx-123"),
                AccountId.of("acc-123"),
                CardId.of("card-123"),
                Money.of(new BigDecimal("6000.00"), "BRL"),
                Merchant.of("merchant-123", "5411"),
                TransactionChannel.CARD_NOT_PRESENT,
                GeoLocation.of("US", "MIAMI", 25.7617, -80.1918),
                Instant.parse("2026-07-05T10:15:30Z")
        );
    }
}
