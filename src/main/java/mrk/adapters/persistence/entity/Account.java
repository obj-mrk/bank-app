package mrk.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mrk.adapters.persistence.entity.enums.AccountStatus;
import mrk.adapters.persistence.entity.enums.AccountType;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "accounts", indexes = {
        @Index(name = "idx_accounts_user_id", columnList = "user_id")
})
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 30)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AccountType type;

    @Column(length = 3)
    private String currency = "RUB";

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "credit_limit", precision = 19, scale = 2)
    private BigDecimal creditLimit = BigDecimal.ZERO;

    @Column(name = "daily_limit", precision = 19, scale = 2)
    private BigDecimal dailyLimit = new BigDecimal("500000");

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Version
    private int version = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "fromAccount")
    private List<Transaction> outgoingTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "toAccount")
    private List<Transaction> incomingTransactions = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (balance == null) balance = BigDecimal.ZERO;
        if (creditLimit == null) creditLimit = BigDecimal.ZERO;
        if (dailyLimit == null) dailyLimit = new BigDecimal("500000");
    }
}
