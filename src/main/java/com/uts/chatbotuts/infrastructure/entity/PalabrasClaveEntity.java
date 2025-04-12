package com.uts.chatbotuts.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "palabras_claves", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "palabras_claves_palabra_key", columnNames = {"palabra"})
})
public class PalabrasClaveEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "palabras_claves_seq")
    @SequenceGenerator(name = "palabras_claves_seq", sequenceName = "palabras_claves_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "palabra", nullable = false, length = 50)
    private String palabra;

}