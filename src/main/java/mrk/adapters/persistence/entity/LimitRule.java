package mrk.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import mrk.adapters.persistence.entity.enums.OperationType;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "limit_rules")
@Getter
@Setter
public class LimitRule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OperationType operationType;

    @Column(name = "limit_per_txn", precision = 19, scale = 2)
    private BigDecimal limitPerTxn;

    @Column(name = "limit_daily", precision = 19, scale = 2)
    private BigDecimal limitDaily;

    @Column(name = "limit_monthly", precision = 19, scale = 2)
    private BigDecimal limitMonthly;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}