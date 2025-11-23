package mrk.application.usecase.command;

import mrk.domain.model.Money;
import mrk.domain.model.enums.AccountType;

import java.time.Instant;
import java.util.UUID;

public record OpenAccountCommand(
        UUID accountId,
        UUID userId,
        AccountType type,
        String number,
        Money initialBalance,
        Money creditLimit,
        Money dailyLimit,
        Instant createdAt
) { }
