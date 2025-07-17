package org.roy.nursery.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class ParentProfile {

    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String fullName;

    @OneToMany(mappedBy = "parent")
    private List<Child> children;
}
