package com.uts.chatbotuts.infrastructure.adapter.in.web;

import com.uts.chatbotuts.application.port.in.ObtenerFaqsUseCase;
import com.uts.chatbotuts.application.port.in.RegistrarFaqUseCase;
import com.uts.chatbotuts.domain.model.FaqDomain;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Component
@Named("faqAdminMB2")
@ViewScoped
@Getter
@Setter
public class FAQAdminBean2 implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private RegistrarFaqUseCase registrarFaqUseCase;
    private ObtenerFaqsUseCase obtenerFaqsUseCase;



    private boolean sectionVisible = false;
    private String pregunta;
    private String respuesta;
    private String palabrasClave;
    private Part documento;
    private boolean buscarPalabrasClave = false;

    private FaqDomain preguntaSeleccionada;
    private List<FaqDomain> faqDomainList;

    @Autowired
    public FAQAdminBean2(RegistrarFaqUseCase registrarFaqUseCase, ObtenerFaqsUseCase obtenerFaqsUseCase) {
        this.registrarFaqUseCase = registrarFaqUseCase;
        this.obtenerFaqsUseCase = obtenerFaqsUseCase;
    }

    public FAQAdminBean2() {}

    @PostConstruct
    public void init() {
        this.faqDomainList = obtenerFaqsUseCase.obtenerFaqs();
    }

    public String cargarDetalles(FaqDomain faqDomain) {
        this.preguntaSeleccionada = faqDomain;
        return null; // Se permanece en la misma página
    }

    public String toggleSection() {
        this.sectionVisible = !this.sectionVisible;
        return null;
    }

    public void guardarPregunta() {
        //registrarFaqUseCase.guardarFaq(this.pregunta, this.respuesta, this.palabrasClave, this.buscarPalabrasClave);
    }
}
