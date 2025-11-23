package mrk.adapters.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import mrk.domain.model.enums.CurrencyType;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositRequestDto(
        @NotNull UUID accountId,
        @NotNull @Positive BigDecimal amount,
        @NotNull CurrencyType currency
) { }
