package br.com.nandoligeiro.frauddetection.application.transaction.port.out;

import br.com.nandoligeiro.frauddetection.domain.transaction.model.Transaction;

public interface TransactionEventPublisherPort {

    void publish(Transaction transaction);
}
