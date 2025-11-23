package mrk.adapters.persistence.mapper;

import mrk.adapters.persistence.entity.AccountEntity;
import mrk.domain.model.Account;
import mrk.domain.model.Money;
import mrk.domain.model.enums.AccountStatus;
import mrk.domain.model.enums.AccountType;
import mrk.domain.model.enums.CurrencyType;

import java.time.Instant;
import java.util.UUID;

public class AccountMapper {

    public static Account toDomain(AccountEntity entity) {
        if (entity == null) return null;

        UUID id = entity.getId();
        UUID userId = entity.getUserEntity().getId();
        AccountType type = entity.getType();
        String number = entity.getNumber();

        Money balance = Money.of(entity.getBalance(), CurrencyType.valueOf(entity.getCurrency()));
        Money creditLimit = Money.of(entity.getCreditLimit(), CurrencyType.valueOf(entity.getCurrency()));
        Money dailyLimit = Money.of(entity.getDailyLimit(), CurrencyType.valueOf(entity.getCurrency()));

        AccountStatus status = entity.getStatus();
        Instant createdAt = entity.getCreatedAt();

        return new Account(
                id,
                userId,
                type,
                number,
                balance,
                creditLimit,
                dailyLimit,
                status,
                createdAt
        );
    }

    public static AccountEntity toEntity(Account domain, AccountEntity target, mrk.adapters.persistence.entity.UserEntity userEntity) {
        if (target == null) {
            target = new AccountEntity();
        }

        target.setId(domain.getId());
        target.setUserEntity(userEntity);
        target.setNumber(domain.getNumber());
        target.setType(domain.getType());

        target.setCurrency(domain.getBalance().getCurrency().name());
        target.setBalance(domain.getBalance().getAmount());
        target.setCreditLimit(domain.getCreditLimit().getAmount());
        target.setDailyLimit(domain.getDailyLimit().getAmount());

        target.setStatus(domain.getStatus());

        return target;
    }
}
