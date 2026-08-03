package com.example.mroojBE.Entity;

import com.example.mroojBE.Entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * USERS entity — root identity for every account (Farmer, Consultant, Admin).
 * The inverse (mappedBy) sides of the One-to-One links to Farmer/Consultant
 * live here purely for convenience navigation (user.getFarmer()); the
 * actual FK (user_id) lives on the FARMERS/CONSULTANTS side, per the ERD.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = {"farmer", "consultant"})
@EqualsAndHashCode(callSuper = false, exclude = {"farmer", "consultant"})
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Stored as a BCrypt hash — NEVER the plain-text password.
     * Hashing/verification happens in the service layer (Spring Security's
     * PasswordEncoder), not here in the entity.
     */
    @Column(nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    // Primitive `boolean` (not `Boolean`) guarantees this can never be null
    // at the language level — a wrapper type could still slip through as
    // null via reflection-based instantiation even with @Builder.Default.
    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    /**
     * NEW field (i18n) — ISO language code, e.g. "ar" or "en".
     * Defaults to Arabic since Mrooj primarily targets the Omani market.
     */
    @Column(name = "preferred_language", nullable = false, length = 5)
    @Builder.Default
    private String preferredLanguage = "ar";

    /**
     * Inverse side — Farmer.user is the owning side with the FK.
     * Nullable in practice: a CONSULTANT-role user has no Farmer profile.
     */
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Farmer farmer;

    /**
     * Inverse side — Consultant.user is the owning side with the FK.
     */
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Consultant consultant;
}