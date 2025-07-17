package org.roy.nursery.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Child {

    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String gender;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private ParentProfile parent;
}
