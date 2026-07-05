package br.com.nandoligeiro.frauddetection.infrastructure.adapter.kafka;

public record TriggeredRulePayload(
        String ruleId,
        int ruleVersion,
        String reason
) {
}
