package br.com.nandoligeiro.frauddetection.domain.model;

public record TriggeredRule(
        String ruleId,
        int ruleVersion,
        String reason
) {
}
