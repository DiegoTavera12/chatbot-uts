package com.uts.chatbotuts.infrastructure.adapter.out;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;

public class FuzzyMatcherService {

    private final JaroWinklerSimilarity jaroWinkler;

    public FuzzyMatcherService() {
        jaroWinkler = new JaroWinklerSimilarity();
    }

    // Devuelve un valor entre 0 y 1: cuanto más cercano a 1, mayor es la similitud
    public double getSimilarity(String s1, String s2) {
        return jaroWinkler.apply(s1, s2);
    }


}
