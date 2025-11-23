package mrk.domain.model;

import mrk.domain.model.enums.UserRole;
import mrk.domain.model.enums.UserStatus;
import mrk.common.errors.impl.InvalidUserException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class User {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final String name;
    private final UserRole role;
    private final Instant createdAt;

    private UserStatus status;

    // ---------- Фабрика ----------
    public static User createNew(
            UUID id,
            String email,
            String passwordHash,
            String name,
            UserRole role,
            Instant createdAt
    ) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(email);
        Objects.requireNonNull(passwordHash);
        Objects.requireNonNull(role);
        Objects.requireNonNull(createdAt);

        if (email.isBlank()) {
            throw new InvalidUserException("Email cannot be blank");
        }

        if (passwordHash.isBlank()) {
            throw new InvalidUserException("Password hash cannot be blank");
        }

        return new User(
                id,
                email,
                passwordHash,
                name,
                role,
                UserStatus.ACTIVE,
                createdAt
        );
    }

    public static User rehydrate(
            UUID id,
            String email,
            String passwordHash,
            String name,
            UserRole role,
            UserStatus status,
            Instant createdAt
    ) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(email);
        Objects.requireNonNull(passwordHash);
        Objects.requireNonNull(role);
        Objects.requireNonNull(status);
        Objects.requireNonNull(createdAt);

        return new User(id, email, passwordHash, name, role, status, createdAt);
    }

    private User(
            UUID id,
            String email,
            String passwordHash,
            String name,
            UserRole role,
            UserStatus status,
            Instant createdAt
    ) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    // ---------- Бизнес-операции ----------
    public void lock() {
        this.status = UserStatus.LOCKED;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    // ---------- Геттеры ----------
    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserStatus getStatus() {
        return status;
    }

    public UserRole getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getName() {
        return name;
    }
}
