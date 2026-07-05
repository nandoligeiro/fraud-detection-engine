package br.com.nandoligeiro.frauddetection.application.port.out;

import br.com.nandoligeiro.frauddetection.domain.rule.model.FraudRule;

import java.util.List;

public interface RuleProviderPort {

    List<FraudRule> loadRules();
}
