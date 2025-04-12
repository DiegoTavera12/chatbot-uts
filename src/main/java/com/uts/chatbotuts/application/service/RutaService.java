package com.uts.chatbotuts.application.service;

import com.uts.chatbotuts.application.port.in.RutaUseCase;
import com.uts.chatbotuts.domain.model.RoleDomain;
import com.uts.chatbotuts.domain.model.Ruta;
import com.uts.chatbotuts.application.port.out.RutaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RutaService implements RutaUseCase {

    private final RutaRepository rutaRepository;

    public List<Ruta> listar() {
        return rutaRepository.findAll();
    }

    public Map<String, List<String>> getRutaRolMappings(){
        Map<String, List<String>> map = new HashMap<>();
        List<Ruta> rutas = listar();
        for (Ruta ruta : rutas) {
            if (ruta.getRoleDomains() != null && !ruta.getRoleDomains().isEmpty()){
                List<String> roles = ruta.getRoleDomains().stream()
                        .map(RoleDomain::getNombre)
                        .collect(Collectors.toList());
                map.put(ruta.getRutaUrl(), roles);
            }
        }
        return map;
    }

}
