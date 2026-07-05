package br.com.nandoligeiro.frauddetection.domain.transaction.model.vo;

public record AccountId(String value) {
    public AccountId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("accountId must not be blank");
        }
    }

    public static AccountId of(String value) {
        return new AccountId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
