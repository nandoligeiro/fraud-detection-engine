package br.com.nandoligeiro.frauddetection.application.port.out;

import br.com.nandoligeiro.frauddetection.domain.model.Transaction;

public interface TransactionEventPublisherPort {

    void publish(Transaction transaction);
}
