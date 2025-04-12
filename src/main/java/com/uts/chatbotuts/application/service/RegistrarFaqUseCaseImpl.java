package com.uts.chatbotuts.application.service;

import com.uts.chatbotuts.application.port.in.RegistrarFaqUseCase;
import com.uts.chatbotuts.application.port.out.FaqPalabrasClaveRepository;
import com.uts.chatbotuts.application.port.out.FaqRepository;
import com.uts.chatbotuts.application.port.out.GeminiIAServicePort;
import com.uts.chatbotuts.application.port.out.PalabraClaveRepository;
import com.uts.chatbotuts.domain.model.FaqDomain;
import com.uts.chatbotuts.domain.model.PalabraClave;
import com.uts.chatbotuts.domain.service.ProcesadorPalabrasClave;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class RegistrarFaqUseCaseImpl implements RegistrarFaqUseCase {

    private final FaqRepository faqRepository;
    private final PalabraClaveRepository palabraClaveRepository;
    private final GeminiIAServicePort geminiService;
    private final FaqPalabrasClaveRepository faqPalabrasClaveRepository;
    private ProcesadorPalabrasClave procesadorPalabrasClave;


    @Override
    public FaqDomain guardarFaq(FaqDomain domain) {
        procesadorPalabrasClave = new ProcesadorPalabrasClave();
        FaqDomain faq;
        // Si el objeto ya tiene ID, es una actualización, de lo contrario es una creación.
        if (domain.getId() != null) {
            // Recupera la FAQ existente y actualiza los campos.
            faq = faqRepository.findById(domain.getId());

            if (Objects.isNull(faq)) {
                throw new RuntimeException("FAQ no encontrada para actualizar");
            }

            faq.setEstado(domain.getEstado());
            faq.setPregunta(domain.getPregunta());
            faq.setRespuesta(domain.getRespuesta());
            faq.setUpdatedAt(Instant.now());

        } else {
            // Crear una nueva FAQ.
            faq = new FaqDomain();
            faq.setPregunta(domain.getPregunta());
            faq.setRespuesta(domain.getRespuesta());
            faq.setCreatedAt(Instant.now());
            faq.setUpdatedAt(Instant.now());
            faq.setEstado(domain.getEstado());
            faq.setVecesUsada(0L);
        }

        // Persiste la FAQ (creación o actualización).
        faq = faqRepository.save(faq);

        // Procesa las palabras clave a partir del string recibido.
        String palabrasClave = domain.getPalabrasClaveString();
        if (domain.isEsBuscarPalabrasClave()) {
            String extraPalabras = geminiService.obtenerPalabrasClave(domain.getPregunta(), domain.getRespuesta());
            if (palabrasClave != null && !palabrasClave.trim().isEmpty()) {
                palabrasClave += ", ";
            }
            palabrasClave += extraPalabras;
        }


        // Procesa y transforma el string de palabras clave en una lista.
        List<String> listaNuevasPalabras = procesadorPalabrasClave.procesar(palabrasClave);

        // Si es una actualización, revisa las asociaciones actuales para eliminar las que ya no correspondan.
        if (domain.getId() != null) {
            List<Long> palabrasClaveAsociadas = faqPalabrasClaveRepository.buscarPalabrasClaveIdsByFaqId(faq.getId());
            for (Long palabraId : palabrasClaveAsociadas) {
                // Se asume la existencia de un método que obtiene el objeto PalabraClave a partir de su ID.
                PalabraClave palabraExistente = palabraClaveRepository.obtenerPorId(palabraId);
                if (palabraExistente != null && !listaNuevasPalabras.contains(palabraExistente.getPalabra())) {
                    faqPalabrasClaveRepository.eliminarAsociacion(faq.getId(), palabraId);
                }
            }
        }

        // Para cada palabra nueva, se comprueba si ya existe en el repositorio y se crea la asociación si aún no existe.
        for (String palabra : listaNuevasPalabras) {
            // Buscar la palabra en el repositorio.
            PalabraClave pk = palabraClaveRepository.buscarPorPalabra(palabra);
            if (pk == null) {
                pk = new PalabraClave();
                pk.setPalabra(palabra);
                pk = palabraClaveRepository.save(pk);
            }
            // Se crea la asociación si no existe.
            if (!faqPalabrasClaveRepository.existeAsociacion(faq.getId(), pk.getId())) {
                faqPalabrasClaveRepository.guardarAsociacion(faq.getId(), pk.getId());
            }
        }

        return faq;
    }

}
