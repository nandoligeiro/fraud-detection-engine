package br.com.nandoligeiro.frauddetection.adapter.kafka;

public record TriggeredRulePayload(
        String ruleId,
        int ruleVersion,
        String reason
) {
}
