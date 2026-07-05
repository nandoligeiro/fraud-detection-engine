package br.com.nandoligeiro.frauddetection.infrastructure.adapter.in.rest;

import br.com.nandoligeiro.frauddetection.application.transaction.port.in.IngestTransactionCommand;
import br.com.nandoligeiro.frauddetection.application.transaction.port.in.IngestionResult;
import br.com.nandoligeiro.frauddetection.application.transaction.port.in.IngestionStatus;
import br.com.nandoligeiro.frauddetection.application.transaction.port.in.TransactionIngestionUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transactions")
@Validated
public class TransactionController {

    private final TransactionIngestionUseCase useCase;

    public TransactionController(TransactionIngestionUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> ingest(
            @RequestHeader("X-Trace-Id") @NotBlank String traceId,
            @Valid @RequestBody TransactionRequest request
    ) {
        IngestionResult result = useCase.ingest(toCommand(request));
        TransactionResponse response = new TransactionResponse(result.transactionId(), result.status());

        if (result.status() == IngestionStatus.DUPLICATED) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        return ResponseEntity.accepted().body(response);
    }

    private IngestTransactionCommand toCommand(TransactionRequest request) {
        return new IngestTransactionCommand(
                request.transactionId(),
                request.accountId(),
                request.cardId(),
                request.amount(),
                request.currency(),
                request.merchantId(),
                request.merchantCategoryCode(),
                request.channel(),
                request.country(),
                request.city(),
                request.latitude(),
                request.longitude(),
                request.occurredAt()
        );
    }
}
