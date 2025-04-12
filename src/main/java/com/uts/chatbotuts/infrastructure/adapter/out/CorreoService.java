package com.uts.chatbotuts.infrastructure.adapter.out;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CorreoService {

    private final JavaMailSender javaMailSender;

    // Inyectar el correo desde application.properties
    @Value("${spring.mail.username}")
    private String emailRemitente;

    public void enviarCorreoConHtmlYAdjuntos(String[] destinatarios, String asunto, String cuerpoHtml, List<MultipartFile> archivos) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            // Usar el correo configurado como remitente
            helper.setFrom(emailRemitente);
            helper.setTo(destinatarios);
            helper.setSubject(asunto);

            // Procesar imágenes inline en el cuerpo HTML
            String cuerpoHtmlProcesado = cuerpoHtml;

            // Setear cuerpo procesado con imágenes inline
            helper.setText(cuerpoHtmlProcesado, true);

            // Procesar archivos adjuntos adicionales
            if (archivos != null && !archivos.isEmpty()) {
                for (MultipartFile archivo : archivos) {
                    helper.addAttachment(archivo.getOriginalFilename(), archivo);
                }
            }

            // Enviar correo
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo: " + e.getMessage(), e);
        }
    }

}
