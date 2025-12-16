package mrk.persistence.mapper;

import mrk.domain.model.Money;
import mrk.domain.model.enums.CurrencyType;

import java.math.BigDecimal;

public class MoneyMapper {

    public static Money toDomain(BigDecimal amount, String currencyCode) {
        if (amount == null || currencyCode == null) return null;
        return Money.of(amount, CurrencyType.valueOf(currencyCode));
    }

    public static BigDecimal toAmount(Money money) {
        return money == null ? null : money.getAmount();
    }

    public static String toCurrencyCode(Money money) {
        return money == null ? null : money.getCurrency().name();
    }
}
