package com.uts.chatbotuts.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "faq_palabras_claves", schema = "public")
public class FaqPalabrasClaveEntity {
    @EmbeddedId
    private FaqPalabrasClaveIdEntity id;

    @MapsId("idFaq")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_faq", nullable = false)
    private FaqEntity idFaqEntity;

    @MapsId("idPalabra")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_palabra", nullable = false)
    private PalabrasClaveEntity idPalabra;

}