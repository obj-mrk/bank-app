package mrk.adapters.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mrk.adapters.web.dto.AccountResponseDto;
import mrk.adapters.web.dto.OpenAccountRequestDto;
import mrk.application.usecase.OpenAccountUseCase;
import mrk.application.usecase.command.OpenAccountCommand;
import mrk.domain.model.Money;
import mrk.security.user.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final OpenAccountUseCase openAccountUseCase;

    @PostMapping
    public AccountResponseDto openAccount(
            @RequestBody @Valid OpenAccountRequestDto request,
            Authentication authentication
    ) {
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = principal.getId();

        // Генерируем id счёта на уровне контроллера
        UUID accountId = UUID.randomUUID();

        var initialBalance = Money.of(request.initialBalance(), request.currency());
        var creditLimit = Money.of(request.creditLimit(), request.currency());
        var dailyLimit = Money.of(request.dailyLimit(), request.currency());

        var command = new OpenAccountCommand(
                accountId,
                userId,
                request.type(),
                generateAccountNumber(),
                initialBalance,
                creditLimit,
                dailyLimit,
                Instant.now()
        );

        var account = openAccountUseCase.execute(command);
        return AccountResponseDto.fromDomain(account);
    }

    private String generateAccountNumber() {
        return "ACC-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
