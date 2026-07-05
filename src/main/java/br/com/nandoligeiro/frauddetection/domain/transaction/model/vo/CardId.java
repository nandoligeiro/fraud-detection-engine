package br.com.nandoligeiro.frauddetection.domain.transaction.model.vo;

public record CardId(String value) {

    public CardId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("cardId must not be blank");
        }
    }

    public static CardId of(String value) {
        return new CardId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
