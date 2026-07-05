package br.com.nandoligeiro.frauddetection.domain.transaction.model.vo;

public record Merchant(String merchantId, String merchantCategoryCode) {
    public Merchant {
        if (merchantId == null || merchantId.isBlank()) {
            throw new IllegalArgumentException("merchantId must not be blank");
        }
        if (merchantCategoryCode == null || merchantCategoryCode.isBlank()) {
            throw new IllegalArgumentException("merchantCategoryCode must not be blank");
        }
    }

    public static Merchant of(String merchantId, String merchantCategoryCode) {
        return new Merchant(merchantId, merchantCategoryCode);
    }

    public boolean hasCategory(String expectedCategoryCode) {
        return merchantCategoryCode.equals(expectedCategoryCode);
    }
}
