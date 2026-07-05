package br.com.nandoligeiro.frauddetection.application.port.in;

public record IngestionResult(
        String transactionId,
        IngestionStatus status
) {

    public static IngestionResult accepted(String transactionId) {
        return new IngestionResult(transactionId, IngestionStatus.ACCEPTED);
    }

    public static IngestionResult duplicated(String transactionId) {
        return new IngestionResult(transactionId, IngestionStatus.DUPLICATED);
    }
}
