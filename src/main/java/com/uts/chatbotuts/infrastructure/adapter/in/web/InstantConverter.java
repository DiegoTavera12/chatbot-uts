package com.uts.chatbotuts.infrastructure.adapter.in.web;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@FacesConverter(value = "instantConverter", forClass = Instant.class)
public class InstantConverter implements Converter<Instant> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            .withZone(ZoneId.systemDefault());

    @Override
    public String getAsString(FacesContext context, UIComponent component, Instant value) {
        return (value != null) ? FORMATTER.format(value) : "";
    }

    @Override
    public Instant getAsObject(FacesContext context, UIComponent component, String value) {
        return (value != null && !value.isEmpty()) ? Instant.from(FORMATTER.parse(value)) : null;
    }
}

