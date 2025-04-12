package com.uts.chatbotuts.application.port.in;

import com.uts.chatbotuts.domain.model.UsuarioDomain;

public interface RegistrarUsuarioYAsignarRoleUseCase {

    UsuarioDomain registrarUsuario(UsuarioDomain usuarioDomain, long idRole);

}
