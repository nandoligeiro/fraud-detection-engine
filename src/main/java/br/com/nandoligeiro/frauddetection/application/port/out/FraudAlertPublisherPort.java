package br.com.nandoligeiro.frauddetection.application.port.out;

import br.com.nandoligeiro.frauddetection.domain.fraud.model.FraudAlert;

public interface FraudAlertPublisherPort {

    void publish(FraudAlert alert);
}
