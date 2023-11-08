package com.cleverpine.specification.integration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "movies")
@Data
public class Movie {

    @Id
    private Long id;

    @Column
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    private Genre genre;

    @ManyToMany
    @JoinTable(name = "movies_actors", joinColumns = {
        @JoinColumn(name = "movie_id", referencedColumnName = "id")
    }, inverseJoinColumns =
    @JoinColumn(name = "actor_id", referencedColumnName = "id"))
    private Set<Actor> actors;

}
