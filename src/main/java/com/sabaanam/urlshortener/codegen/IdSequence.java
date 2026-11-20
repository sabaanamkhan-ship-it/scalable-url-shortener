package com.sabaanam.urlshortener.codegen;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "id_sequence")
@Getter
@Setter
@NoArgsConstructor
public class IdSequence {

    @Id
    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "next_value", nullable = false)
    private Long nextValue;
}