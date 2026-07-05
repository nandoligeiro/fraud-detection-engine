package br.com.nandoligeiro.frauddetection.application.detection.port.in;

public interface DetectFraudUseCase {

    FraudDetectionResult detect(EvaluateTransactionCommand command);
}
