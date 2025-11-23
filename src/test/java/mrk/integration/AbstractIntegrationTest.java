package mrk.integration;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Базовый класс для интеграционных тестов.
 * Отвечает только за инфраструктуру: Testcontainers + настройки Spring.
 * Наследуешься от него и уже в подклассах пишешь сценарии.
 */
@Testcontainers
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractIntegrationTest {

    @Container
    @SuppressWarnings("resource")
    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("bankdb")
                    .withUsername("bank")
                    .withPassword("bank");

    static {
        // Явный старт контейнера до поднятия контекста
        postgres.start();
    }

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        // Подменяем datasource на тестовый контейнер
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // Для интеграционных тестов часто достаточно схемы от Hibernate
        // (в следующих итерациях можно переключиться на Flyway, если нужно).
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.flyway.enabled", () -> false);

        // Опционально: отключить лишний шум
        registry.add("spring.jpa.show-sql", () -> false);
    }
}
