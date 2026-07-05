package br.com.nandoligeiro.frauddetection.application.detection.port.out;

import br.com.nandoligeiro.frauddetection.domain.model.FraudAlert;

public interface AlertSinkPort {

    void send(FraudAlert alert);
}
