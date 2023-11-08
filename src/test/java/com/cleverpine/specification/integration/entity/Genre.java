package com.cleverpine.specification.integration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "genres")
@Data
public class Genre {

    @Id
    private Long id;

    @Column
    private String name;

}
