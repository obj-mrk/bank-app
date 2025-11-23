package mrk.adapters.persistence.mapper;

import mrk.adapters.persistence.entity.TransactionEntity;
import mrk.domain.model.Money;
import mrk.domain.model.Transaction;
import mrk.domain.model.enums.CurrencyType;

import java.util.UUID;

public class TransactionMapper {

    public static Transaction toDomain(TransactionEntity entity) {
        if (entity == null) return null;

        UUID id = entity.getId();

        UUID fromId = entity.getFromAccountEntity() != null
                ? entity.getFromAccountEntity().getId()
                : null;

        UUID toId = entity.getToAccountEntity() != null
                ? entity.getToAccountEntity().getId()
                : null;

        Money amount = Money.of(entity.getAmount(), CurrencyType.valueOf(entity.getCurrency()));

        UUID initiatedBy = entity.getInitiatedBy() != null
                ? entity.getInitiatedBy().getId()
                : null;

        Transaction txn = new Transaction(
                id,
                entity.getType(),
                fromId,
                toId,
                amount,
                entity.getDescription(),
                initiatedBy,
                entity.getIdempotencyKey(),
                entity.getCreatedAt(),
                entity.getState()
        );

        return txn;
    }

    public static TransactionEntity toEntity(Transaction domain,
                                             TransactionEntity target,
                                             mrk.adapters.persistence.entity.AccountEntity fromAccount,
                                             mrk.adapters.persistence.entity.AccountEntity toAccount,
                                             mrk.adapters.persistence.entity.UserEntity initiatedBy) {
        if (target == null) {
            target = new TransactionEntity();
        }

        target.setId(domain.getId());
        target.setType(domain.getType());
        target.setFromAccountEntity(fromAccount);
        target.setToAccountEntity(toAccount);

        target.setAmount(domain.getAmount().getAmount());
        target.setCurrency(domain.getAmount().getCurrency().name());

        target.setDescription(domain.getDescription());
        target.setInitiatedBy(initiatedBy);
        target.setIdempotencyKey(domain.getIdempotencyKey());
        target.setState(domain.getState());

        return target;
    }
}
