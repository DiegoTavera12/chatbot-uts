package com.uts.chatbotuts.infrastructure.adapter.out;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.IOException;
import java.io.InputStream;

public class NlpService {

    private Tokenizer tokenizer;

    public NlpService() throws IOException {
        // Se carga el modelo de tokenización para español (descargado y ubicado en src/main/resources/models/es-token.bin)
        try (InputStream modelIn = getClass().getResourceAsStream("/models/es-token.bin")) {
            TokenizerModel model = new TokenizerModel(modelIn);
            tokenizer = new TokenizerME(model);
        }
    }

    // Método que tokeniza una oración en palabras
    public String[] tokenize(String sentence) {
        return tokenizer.tokenize(sentence);
    }

}
