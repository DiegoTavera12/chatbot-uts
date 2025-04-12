package com.uts.chatbotuts.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Embeddable
public class RutaRoleEntityId implements java.io.Serializable {
    private static final long serialVersionUID = 829202994080448240L;
    @Column(name = "id_ruta", nullable = false)
    private Long idRuta;

    @Column(name = "id_rol", nullable = false)
    private Long idRol;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RutaRoleEntityId entity = (RutaRoleEntityId) o;
        return Objects.equals(this.idRol, entity.idRol) &&
                Objects.equals(this.idRuta, entity.idRuta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRol, idRuta);
    }

}