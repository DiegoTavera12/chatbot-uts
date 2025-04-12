package com.uts.chatbotuts;

import com.uts.chatbotuts.application.port.in.InicializacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner {

    private final InicializacionUseCase inicializacionUseCase;

    @Override
    public void run(String... args) {
        inicializacionUseCase.inicializacion();
    }
}
