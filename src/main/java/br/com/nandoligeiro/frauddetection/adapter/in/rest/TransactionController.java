package br.com.nandoligeiro.frauddetection.adapter.in.rest;

import br.com.nandoligeiro.frauddetection.application.port.in.IngestTransactionUseCase;
import br.com.nandoligeiro.frauddetection.application.usecase.IngestTransactionCommand;
import br.com.nandoligeiro.frauddetection.application.usecase.IngestionResult;
import br.com.nandoligeiro.frauddetection.application.usecase.IngestionStatus;
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

    private final IngestTransactionUseCase ingestTransactionUseCase;

    public TransactionController(IngestTransactionUseCase ingestTransactionUseCase) {
        this.ingestTransactionUseCase = ingestTransactionUseCase;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> ingest(
            @RequestHeader("X-Trace-Id") @NotBlank String traceId,
            @RequestHeader("Idempotency-Key") @NotBlank String idempotencyKey,
            @Valid @RequestBody TransactionRequest request
    ) {
        IngestionResult result = ingestTransactionUseCase.ingest(toCommand(request));
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
