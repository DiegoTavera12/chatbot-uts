package com.uts.chatbotuts.application.service;

import com.uts.chatbotuts.application.port.in.RegistrarUsuarioYAsignarRoleUseCase;
import com.uts.chatbotuts.application.port.out.RolRepository;
import com.uts.chatbotuts.application.port.out.UsuarioTransactionalRepository;
import com.uts.chatbotuts.domain.model.RoleDomain;
import com.uts.chatbotuts.domain.model.UsuarioDomain;
import com.uts.chatbotuts.infrastructure.utils.RolesConstantes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RegistrarUsuarioYAsignarRolUseCaseImpl implements RegistrarUsuarioYAsignarRoleUseCase {


    private final UsuarioTransactionalRepository usuarioTransactionalRepository;
    private final RolRepository rolRepository;

    @Override
    public UsuarioDomain registrarUsuario(UsuarioDomain usuarioDomain, long idRole) {
        try {

         RoleDomain roleDomain = rolRepository.obtenerRolByNombre(idRole==1? RolesConstantes.ROL_ADMINISTRADOR : RolesConstantes.ROL_USUARIO);

            return usuarioTransactionalRepository.registrarUsuarioYAsignarRol(usuarioDomain, roleDomain.getId());
        } catch (Exception ex) {
            throw new RuntimeException("Error al registrar usuario", ex);
        }
    }
}
