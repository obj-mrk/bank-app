package mrk.adapters.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import mrk.domain.model.enums.AccountType;
import mrk.domain.model.enums.CurrencyType;

import java.math.BigDecimal;

public record OpenAccountRequestDto(
        @NotNull AccountType type,

        @NotNull @PositiveOrZero BigDecimal initialBalance,

        @NotNull @PositiveOrZero BigDecimal creditLimit,

        @NotNull @PositiveOrZero BigDecimal dailyLimit,

        @NotNull CurrencyType currency
) { }
