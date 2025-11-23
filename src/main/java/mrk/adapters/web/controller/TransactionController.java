package mrk.adapters.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mrk.adapters.web.dto.DepositRequestDto;
import mrk.adapters.web.dto.TransferRequestDto;
import mrk.adapters.web.dto.TransactionResponseDto;
import mrk.adapters.web.dto.WithdrawRequestDto;
import mrk.application.usecase.DepositUseCase;
import mrk.application.usecase.TransferUseCase;
import mrk.application.usecase.WithdrawUseCase;
import mrk.application.usecase.command.DepositCommand;
import mrk.application.usecase.command.TransferCommand;
import mrk.application.usecase.command.WithdrawCommand;
import mrk.domain.model.Money;
import mrk.security.user.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final TransferUseCase transferUseCase;

    // ---------- DEPOSIT ----------

    @PostMapping("/deposit")
    public TransactionResponseDto deposit(
            @RequestBody @Valid DepositRequestDto request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            Authentication authentication
    ) {
        UUID userId = extractUserId(authentication);

        Money amount = Money.of(request.amount(), request.currency());

        var command = new DepositCommand(
                request.accountId(),
                amount,
                userId,
                idempotencyKey
        );

        var txn = depositUseCase.execute(command);

        return TransactionResponseDto.fromDomain(txn);
    }

    // ---------- WITHDRAW ----------

    @PostMapping("/withdraw")
    public TransactionResponseDto withdraw(
            @RequestBody @Valid WithdrawRequestDto request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            Authentication authentication
    ) {
        UUID userId = extractUserId(authentication);

        Money amount = Money.of(request.amount(), request.currency());

        var command = new WithdrawCommand(
                request.accountId(),
                amount,
                userId,
                idempotencyKey
        );

        var txn = withdrawUseCase.execute(command);

        return TransactionResponseDto.fromDomain(txn);
    }

    // ---------- TRANSFER ----------

    @PostMapping("/transfer")
    public TransactionResponseDto transfer(
            @RequestBody @Valid TransferRequestDto request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            Authentication authentication
    ) {
        UUID userId = extractUserId(authentication);

        Money amount = Money.of(request.amount(), request.currency());

        var command = new TransferCommand(
                request.fromAccountId(),
                request.toAccountId(),
                amount,
                userId,
                idempotencyKey
        );

        var txn = transferUseCase.execute(command);

        return TransactionResponseDto.fromDomain(txn);
    }

    // ---------- Вспомогательный метод извлечения userId ----------

    private UUID extractUserId(Authentication authentication) {
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        return principal.getId();
    }
}
