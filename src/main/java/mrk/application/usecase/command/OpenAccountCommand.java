package mrk.application.usecase.command;

import mrk.domain.model.Money;
import mrk.domain.model.enums.AccountType;

import java.util.UUID;

public record OpenAccountCommand(
        UUID userId,
        AccountType type,
        Money initialBalance,
        Money creditLimit,
        Money dailyLimit
) {}
