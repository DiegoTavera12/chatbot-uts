package com.uts.chatbotuts.application.port.in;

import java.util.List;

public interface EliminarUsuarioUseCase {

    public void eliminarUsuarios(List<Long> ids);

    void eliminarUsuario(Long id);

}
