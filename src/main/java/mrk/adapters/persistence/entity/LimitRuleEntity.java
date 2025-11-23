package mrk.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.boot.actuate.endpoint.OperationType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "limit_rules")
@Getter
@Setter
public class LimitRuleEntity {
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