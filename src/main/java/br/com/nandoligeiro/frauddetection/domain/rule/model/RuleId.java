package br.com.nandoligeiro.frauddetection.domain.rule.model;

public record RuleId(String value) {

    public RuleId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ruleId must not be blank");
        }
    }

    public static RuleId of(String value) {
        return new RuleId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
