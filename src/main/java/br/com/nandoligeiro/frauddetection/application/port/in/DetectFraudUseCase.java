package br.com.nandoligeiro.frauddetection.application.port.in;

public interface DetectFraudUseCase {

    FraudDetectionResult detect(EvaluateTransactionCommand command);
}
