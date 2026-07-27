package com.example.mroojBE.Entity;

import com.example.mroojBE.Entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    //do not make it null, if it null make it false
    @Column(nullable = false)
    //to use default value
    @Builder.Default
    private Boolean enabled = true;

    @OneToOne(mappedBy = "user")
    private Farmer farmer;

    @OneToOne(mappedBy = "user")
    private Consultant consultant;

}