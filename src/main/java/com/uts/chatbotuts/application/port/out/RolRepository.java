package com.uts.chatbotuts.application.port.out;

import com.uts.chatbotuts.domain.model.RoleDomain;

public interface RolRepository {

    RoleDomain obtenerRolByNombre(String rol);

}
