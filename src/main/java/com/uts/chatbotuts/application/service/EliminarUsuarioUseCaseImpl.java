package com.uts.chatbotuts.application.service;

import com.uts.chatbotuts.application.port.in.EliminarUsuarioUseCase;
import com.uts.chatbotuts.application.port.out.UsuarioTransactionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EliminarUsuarioUseCaseImpl implements EliminarUsuarioUseCase {

    private final UsuarioTransactionalRepository usuarioTransactionalRepository;

    @Override
    public void eliminarUsuarios(List<Long> ids) {
        usuarioTransactionalRepository.eliminarUsuarios(ids);
    }

    @Override
    public void eliminarUsuario(Long id) {
        usuarioTransactionalRepository.eliminarUsuario(id);
    }
}
