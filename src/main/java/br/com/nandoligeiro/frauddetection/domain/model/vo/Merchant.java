package br.com.nandoligeiro.frauddetection.domain.model.vo;

public record Merchant(String merchantId, String categoryCode) {

    public Merchant {
        if (merchantId == null || merchantId.isBlank()) {
            throw new IllegalArgumentException("merchantId must not be blank");
        }
        if (categoryCode == null || categoryCode.isBlank()) {
            throw new IllegalArgumentException("merchantCategoryCode must not be blank");
        }
    }

    public static Merchant of(String merchantId, String categoryCode) {
        return new Merchant(merchantId, categoryCode);
    }

    public boolean hasCategory(String expectedCategoryCode) {
        return categoryCode.equals(expectedCategoryCode);
    }
}
