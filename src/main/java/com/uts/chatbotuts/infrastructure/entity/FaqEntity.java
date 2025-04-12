package com.uts.chatbotuts.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "faq", schema = "public")
public class FaqEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "faq_seq")
    @SequenceGenerator(name = "faq_seq", sequenceName = "faq_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "pregunta", nullable = false, length = Integer.MAX_VALUE)
    private String pregunta;

    @Column(name = "respuesta", nullable = false, length = Integer.MAX_VALUE)
    private String respuesta;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @ManyToMany
    @JoinTable(name = "faq_palabras_claves",
            joinColumns = @JoinColumn(name = "id_faq"),
            inverseJoinColumns = @JoinColumn(name = "id_palabra"))
    private Set<PalabrasClaveEntity> palabrasClaves = new LinkedHashSet<>();

//    @OneToMany(mappedBy = "idFaqEntity")
//    private Set<FaqPalabrasClaveEntity> palabrasClaves = new LinkedHashSet<>();

    @Column(name = "veces_usada")
    private Long vecesUsada;

    @ColumnDefault("true")
    @Column(name = "estado", nullable = false)
    private Boolean estado = false;

}