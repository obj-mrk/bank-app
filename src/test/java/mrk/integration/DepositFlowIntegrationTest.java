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
import mrk.security.auth.AuthService;
import mrk.security.user.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Пример “правильного” интеграционного теста:
 * - создаём пользователя через боевой AuthService
 * - работаем с доменными портами / use-case слоями
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

    /**
     * Утилита для регистрации пользователя в тесте через боевой AuthService.
     * Возвращает id созданного пользователя как доменной модели.
     */
    private UUID registerTestUserAndGetId(String email) {
        // DTO из security-слоя
        RegisterRequest request = new RegisterRequest(
                email,
                "P@ssw0rd!",          // тестовый пароль
                "Integration User",   // имя
                "+79990000000",       // телефон
                "Test address"        // адрес
        );

        // создаёт UserEntity в БД и возвращает JWT, который нам здесь не нужен
        authService.register(request);  // AuthService.save + PasswordEncoder + JwtService

        // затем через доменный порт получаем доменного пользователя
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found after register"))
                .getId();
    }

    @Test
    void depositFlow_shouldIncreaseBalance() {
        // -------- 1. создаём пользователя через AuthService --------
        String email = "it-" + UUID.randomUUID() + "@example.com";
        UUID userId = registerTestUserAndGetId(email);

        // -------- 2. открываем счет через доменный use case --------
        UUID accountId = UUID.randomUUID();

        Money initialBalance = Money.of(BigDecimal.ZERO, CurrencyType.RUB);
        Money creditLimit    = Money.of(new BigDecimal("100000.00"), CurrencyType.RUB);
        Money dailyLimit     = Money.of(new BigDecimal("50000.00"), CurrencyType.RUB);

        OpenAccountCommand openCmd = new OpenAccountCommand(
                accountId,
                userId,
                AccountType.CHECKING,
                "ACC-" + accountId.toString().substring(0, 8),
                initialBalance,
                creditLimit,
                dailyLimit,
                Instant.now()
        );

        Account opened = openAccountUseCase.execute(openCmd);
        assertThat(opened.getId()).isEqualTo(accountId);
        assertThat(opened.getBalance().getAmount())
                .as("начальный баланс должен быть 0.00")
                .isEqualByComparingTo("0.00");

        // -------- 3. выполняем депозит через доменный use case --------
        Money depositAmount = Money.of(new BigDecimal("1000.00"), CurrencyType.RUB);

        DepositCommand depositCmd = new DepositCommand(
                accountId,
                depositAmount,
                userId,
                "idem-" + UUID.randomUUID()  // идемпотентный ключ
        );

        depositUseCase.execute(depositCmd);

        // -------- 4. перечитываем счет через доменный репозиторий --------
        Account reloaded = accountRepository.findById(accountId)
                .orElseThrow(() -> new AssertionError("Account not found after deposit"));

        assertThat(reloaded.getBalance().getAmount())
                .as("баланс после депозита должен быть 1000.00")
                .isEqualByComparingTo("1000.00");
    }
}
