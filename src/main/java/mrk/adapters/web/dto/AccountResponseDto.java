package mrk.adapters.web.dto;

import mrk.domain.model.Account;
import mrk.domain.model.Money;
import mrk.domain.model.enums.AccountStatus;
import mrk.domain.model.enums.AccountType;
import mrk.domain.model.enums.CurrencyType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountResponseDto(
        UUID id,
        UUID userId,
        AccountType type,
        String number,
        BigDecimal balance,
        CurrencyType currency,
        BigDecimal creditLimit,
        BigDecimal dailyLimit,
        AccountStatus status,
        Instant createdAt
) {

    public static AccountResponseDto fromDomain(Account account) {
        Money balance = account.getBalance();
        return new AccountResponseDto(
                account.getId(),
                account.getUserId(),
                account.getType(),
                account.getNumber(),
                balance.getAmount(),
                balance.getCurrency(),
                account.getCreditLimit().getAmount(),
                account.getDailyLimit().getAmount(),
                account.getStatus(),
                account.getCreatedAt()
        );
    }
}
