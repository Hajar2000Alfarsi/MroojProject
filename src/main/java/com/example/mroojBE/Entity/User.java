package com.example.mroojBE.Entity;

import com.example.mroojBE.Entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"farmer", "consultant"})
@EqualsAndHashCode(callSuper = false, exclude = {"farmer", "consultant"})
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 150)
    private String email;

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

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @Column(name = "preferred_language", nullable = false, length = 5)
    @Builder.Default
    private String preferredLanguage = "ar";

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Farmer farmer;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Consultant consultant;
}
