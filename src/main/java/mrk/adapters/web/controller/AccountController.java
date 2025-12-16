package mrk.adapters.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mrk.adapters.web.dto.AccountResponseDto;
import mrk.adapters.web.dto.OpenAccountRequestDto;
import mrk.adapters.web.security.user.AuthUserIdExtractor;
import mrk.application.usecase.OpenAccountUseCase;
import mrk.application.usecase.command.OpenAccountCommand;
import mrk.domain.model.Money;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
        UUID userId = AuthUserIdExtractor.userId(authentication);

        var initialBalance = Money.of(request.initialBalance(), request.currency());
        var creditLimit = Money.of(request.creditLimit(), request.currency());
        var dailyLimit = Money.of(request.dailyLimit(), request.currency());

        var command = new OpenAccountCommand(
                userId,
                request.type(),
                initialBalance,
                creditLimit,
                dailyLimit
        );

        var account = openAccountUseCase.execute(command);
        return AccountResponseDto.fromDomain(account);
    }
}
