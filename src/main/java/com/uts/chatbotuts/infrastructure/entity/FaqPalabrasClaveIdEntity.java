package com.uts.chatbotuts.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class FaqPalabrasClaveIdEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 6736109612528714722L;
    @Column(name = "id_faq", nullable = false)
    private Long idFaq;

    @Column(name = "id_palabra", nullable = false)
    private Long idPalabra;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FaqPalabrasClaveIdEntity entity = (FaqPalabrasClaveIdEntity) o;
        return Objects.equals(this.idFaq, entity.idFaq) &&
                Objects.equals(this.idPalabra, entity.idPalabra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFaq, idPalabra);
    }

}