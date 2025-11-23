package mrk.application.usecase.command;

import mrk.domain.model.Money;

import java.util.UUID;

public record TransferCommand(
        UUID fromAccountId,
        UUID toAccountId,
        Money amount,
        UUID initiatedByUserId,
        String idempotencyKey
) {
}
