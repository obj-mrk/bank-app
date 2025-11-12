package mrk.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import mrk.adapters.persistence.entity.enums.OperationType;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "fee_rules")
@Getter
@Setter
public class FeeRule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OperationType operationType;

    @Column(name = "percent_fee", precision = 5, scale = 2)
    private BigDecimal percentFee = BigDecimal.ZERO;

    @Column(name = "min_fee", precision = 19, scale = 2)
    private BigDecimal minFee = BigDecimal.ZERO;

    @Column(name = "max_fee", precision = 19, scale = 2)
    private BigDecimal maxFee = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}

