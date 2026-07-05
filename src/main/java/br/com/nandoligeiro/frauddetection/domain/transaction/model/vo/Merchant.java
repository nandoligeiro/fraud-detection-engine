package br.com.nandoligeiro.frauddetection.domain.transaction.model.vo;

public record Merchant(String merchantId, String mcc) {

    public Merchant {
        if (merchantId == null || merchantId.isBlank()) {
            throw new IllegalArgumentException("merchantId must not be blank");
        }
        if (mcc == null || mcc.isBlank()) {
            throw new IllegalArgumentException("mcc must not be blank");
        }
    }

    public static Merchant of(String merchantId, String mcc) {
        return new Merchant(merchantId, mcc);
    }

    public boolean hasCategory(String expectedMcc) {
        return mcc.equals(expectedMcc);
    }
}
