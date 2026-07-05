package br.com.nandoligeiro.frauddetection.application.detection.port.out;

import br.com.nandoligeiro.frauddetection.domain.model.rule.FraudRule;

import java.util.List;

public interface ActiveRulePort {

    List<FraudRule> loadRules();
}
