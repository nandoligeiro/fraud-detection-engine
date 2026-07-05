package br.com.nandoligeiro.frauddetection.domain.fraud.model;

public record TriggeredRule(String ruleId, int ruleVersion, String reason) {
}
