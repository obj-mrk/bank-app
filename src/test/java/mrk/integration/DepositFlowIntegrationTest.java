package mrk.integration;

import mrk.application.usecase.DepositUseCase;
import mrk.application.usecase.OpenAccountUseCase;
import mrk.application.usecase.command.DepositCommand;
import mrk.application.usecase.command.OpenAccountCommand;
import mrk.domain.model.Account;
import mrk.domain.model.Money;
import mrk.domain.model.enums.AccountType;
import mrk.domain.model.enums.CurrencyType;
import mrk.domain.port.AccountRepository;
import mrk.domain.port.UserRepository;
import mrk.adapters.web.security.auth.AuthService;
import mrk.adapters.web.security.user.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционный тест:
 * - создаём пользователя через боевой AuthService
 * - работаем через application use-case’ы и domain ports
 * - не трогаем JPA-сущности напрямую
 */
class DepositFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OpenAccountUseCase openAccountUseCase;

    @Autowired
    private DepositUseCase depositUseCase;

    @Autowired
    private AccountRepository accountRepository;

    private UUID registerTestUserAndGetId(String email) {
        RegisterRequest request = new RegisterRequest(
                email,
                "P@ssw0rd!",
                "Integration User",
                "+79990000000",
                "Test address"
        );

        authService.register(request);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found after register"))
                .getId();
    }

    @Test
    void depositFlow_shouldIncreaseBalance() {
        // -------- 1. создаём пользователя --------
        String email = "it-" + UUID.randomUUID() + "@example.com";
        UUID userId = registerTestUserAndGetId(email);

        // -------- 2. открываем счет (use-case генерит id/номер/createdAt) --------
        Money initialBalance = Money.of(BigDecimal.ZERO, CurrencyType.RUB);
        Money creditLimit    = Money.of(new BigDecimal("100000.00"), CurrencyType.RUB);
        Money dailyLimit     = Money.of(new BigDecimal("50000.00"), CurrencyType.RUB);

        OpenAccountCommand openCmd = new OpenAccountCommand(
                userId,
                AccountType.CHECKING,
                initialBalance,
                creditLimit,
                dailyLimit
        );

        Account opened = openAccountUseCase.execute(openCmd);

        assertThat(opened.getId()).as("accountId должен быть сгенерирован").isNotNull();
        assertThat(opened.getBalance().getAmount())
                .as("начальный баланс должен быть 0.00")
                .isEqualByComparingTo("0.00");

        UUID accountId = opened.getId();

        // -------- 3. выполняем депозит --------
        Money depositAmount = Money.of(new BigDecimal("1000.00"), CurrencyType.RUB);

        DepositCommand depositCmd = new DepositCommand(
                accountId,
                depositAmount,
                userId,
                "idem-" + UUID.randomUUID()
        );

        depositUseCase.execute(depositCmd);

        // -------- 4. перечитываем счет и проверяем баланс --------
        Account reloaded = accountRepository.findById(accountId)
                .orElseThrow(() -> new AssertionError("Account not found after deposit"));

        assertThat(reloaded.getBalance().getAmount())
                .as("баланс после депозита должен быть 1000.00")
                .isEqualByComparingTo("1000.00");
    }
}
