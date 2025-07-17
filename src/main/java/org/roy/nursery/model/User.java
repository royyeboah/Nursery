package org.roy.nursery.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "app_users")
public class User {

    @Id
    private Long id;

    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "user")
    private StaffProfile staffProfile;


}
