package mrk.unit;

import mrk.common.errors.impl.CurrencyMismatchException;
import mrk.domain.model.Money;
import mrk.domain.model.enums.CurrencyType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void add_sameCurrency_ok() {
        Money m1 = Money.of(new BigDecimal("10.00"), CurrencyType.RUB);
        Money m2 = Money.of(new BigDecimal("5.00"), CurrencyType.RUB);

        Money result = m1.add(m2);

        assertEquals(new BigDecimal("15.00"), result.getAmount());
        assertEquals(CurrencyType.RUB, result.getCurrency());
    }

    @Test
    void add_differentCurrency_throws() {
        Money rub = Money.of(new BigDecimal("10.00"), CurrencyType.RUB);
        Money usd = Money.of(new BigDecimal("5.00"), CurrencyType.USD);

        assertThrows(CurrencyMismatchException.class, () -> rub.add(usd));
    }

    @Test
    void negativeAmount_allowedButDetectable() {
        Money m = Money.of(new BigDecimal("-1.00"), CurrencyType.RUB);
        assertTrue(m.isNegative());
    }
}
