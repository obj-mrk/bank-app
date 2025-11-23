package mrk.application.usecase.command;

import mrk.domain.model.Money;

import java.util.UUID;

public record DepositCommand(UUID accountId,
                             Money amount,
                             UUID initiatedByUserId,
                             String idempotencyKey) {
}
