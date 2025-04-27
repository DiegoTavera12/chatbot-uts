package com.uts.chatbotuts.infrastructure.adapter.in.web;

import com.uts.chatbotuts.application.port.in.ActualizarUsosFaqUseCase;
import com.uts.chatbotuts.application.port.in.ChatConsultaUseCase;
import com.uts.chatbotuts.domain.model.FaqDomain;
import com.uts.chatbotuts.infrastructure.adapter.out.CorreoService;
import com.uts.chatbotuts.infrastructure.adapter.out.FileFaqDTO;
import com.uts.chatbotuts.infrastructure.adapter.out.FileStorageRepository;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Getter
@Setter
@Named("chatBean")
@ViewScoped
public class ChatBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ChatConsultaUseCase chatConsultaUseCase;
    private ActualizarUsosFaqUseCase actualizarUsosFaqUseCase;
    private CorreoService correoService;

    private List<MensajeChat> listaMensajesChat;
    private String mensajeEntrada;
    private String correoDestinatario;


    List<String> faqs;
    List<String> faqsRespuestas;

    @Autowired
    public ChatBean(ChatConsultaUseCase chatConsultaUseCase, FileStorageRepository fileStorageRepository, ActualizarUsosFaqUseCase actualizarUsosFaqUseCase
            , CorreoService correoService) {
        this.chatConsultaUseCase = chatConsultaUseCase;
        this.fileStorageRepository = fileStorageRepository;
        this.actualizarUsosFaqUseCase = actualizarUsosFaqUseCase;
        this.correoService = correoService;
        iniciarListaMensajesChat();
    }

    public ChatBean() {
        iniciarListaMensajesChat();
    }

    private void iniciarListaMensajesChat() {


        listaMensajesChat = new ArrayList<>();
        // Mensaje inicial del sistema
        listaMensajesChat.add(new MensajeChat("Hola bienvenid@, dame tu duda", "left"));
    }

    List<FaqDomain> faqsDto = null;


    public void enviarCorreo() {

        String[] stringsCorreos = new String[1];
        stringsCorreos[0] = correoDestinatario;

        String asunto = "Chatbot UTS - " + LocalDateTime.now();

        correoService.enviarCorreoConHtmlYAdjuntos(stringsCorreos, asunto,generarHtml(listaMensajesChat), obtenerFileMultipartList(fileFaqDTOs, fileStorageRepository) );
    }

    public String generarHtml(List<MensajeChat> listaMensajesChat) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<title>Chat</title>");
        html.append("</head>");
        html.append("<body>");
        html.append("<ul>");

        for (MensajeChat mensaje : listaMensajesChat) {
            // Se determina el emisor según la alineacion
            String emisor = "left".equals(mensaje.getAlineacion()) ? "Tecnobot" : "usuario";
            html.append("<li>");
            html.append("<strong>").append(emisor).append(":</strong> ");
            html.append(mensaje.getTexto());
            html.append("</li>");
        }

        html.append("</ul>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    public void enviarMensaje() {
        if (mensajeEntrada == null || mensajeEntrada.trim().isEmpty()) {
            return;
        }

        // Agrega el mensaje del usuario al chat
        listaMensajesChat.add(new MensajeChat(mensajeEntrada, "right"));

        try {
            int option = Integer.parseInt(mensajeEntrada.trim());
            String respuesta = "\n\n";
            if (Objects.nonNull(faqs) && option <= faqs.size()) {
                respuesta += faqsRespuestas.get(option - 1);
            }
            listaMensajesChat.add(new MensajeChat(respuesta, "left"));
            faqId = faqsDto.get(option - 1).getId();
            obtenerFileList();
            actualizarUsosFaqUseCase.actualizarUsoFaq(faqId);
        } catch (NumberFormatException e) {

            faqsDto = null;

            String[] tokens = mensajeEntrada.split("\\s+");
            List<Long> idsPalabrasClaves = chatConsultaUseCase.obtenerIdsPalabrasClaves(tokens);
            List<Long> faqIds = chatConsultaUseCase.obtenerIdsFaqByPalabrasClaveIds(idsPalabrasClaves);

            faqsDto = chatConsultaUseCase.obtenerFaqsByIds(faqIds);
            faqs = new ArrayList<>();
            faqsRespuestas = new ArrayList<>();
            for (FaqDomain faqDomain : faqsDto) {
                faqs.add(faqDomain.getPregunta());
                faqsRespuestas.add(faqDomain.getRespuesta());
            }

            StringBuilder faqMessage = new StringBuilder("Seleccione una opción:\n");
            for (int i = 0; i < faqs.size(); i++) {
                faqMessage.append((i + 1)).append(". ").append(faqs.get(i)).append("\n");
            }
            listaMensajesChat.add(new MensajeChat(faqMessage.toString(), "left"));
        }
        mensajeEntrada = "";
    }

    private FileStorageRepository fileStorageRepository;
    private List<String> fileList;

    Long faqId = null;

    List<FileFaqDTO> fileFaqDTOs = new ArrayList<>();

    public List<String> obtenerFileList() {

        // Asumiendo que basePath está configurado en fileStorageRepository
        Path directory = Paths.get(fileStorageRepository.getBasePath(), faqId.toString());



        if (fileList == null || fileList.isEmpty()) {
            fileList = new ArrayList<>();
        }


        if (Files.exists(directory) && Files.isDirectory(directory)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
                for (Path entry : stream) {
                    fileList.add(entry.getFileName().toString());
                    fileFaqDTOs.add(new FileFaqDTO( faqId, entry.getFileName().toString()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fileList;
    }

    public StreamedContent downloadFile(String fileName) {
        try {
            // Construye la ruta completa al archivo (la carpeta es el id de la FAQ)
            Path filePath = Paths.get(fileStorageRepository.getBasePath(), faqId.toString(), fileName);
            // Abre el stream
            InputStream stream = Files.newInputStream(filePath);
            // Determina el content type (opcional)
            String contentType = Files.probeContentType(filePath);
            return DefaultStreamedContent.builder()
                    .name(fileName)
                    .contentType(contentType != null ? contentType : "application/octet-stream")
                    .stream(() -> stream)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<MultipartFile> obtenerFileMultipartList(List<FileFaqDTO> fileFaqDTOs, FileStorageRepository fileStorageRepository) {
        List<MultipartFile> multipartFiles = new ArrayList<>();

        // Recorremos cada objeto FileFaqDTO
        for (FileFaqDTO fileFaqDTO : fileFaqDTOs) {
            Path directory = Paths.get(fileStorageRepository.getBasePath(), fileFaqDTO.getFaqId().toString());

            if (Files.exists(directory) && Files.isDirectory(directory)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
                    for (Path entry : stream) {
                        // Se leen todos los bytes del archivo
                        byte[] content = Files.readAllBytes(entry);
                        // Se crea el MultipartFile usando la implementación personalizada
                        MultipartFile multipartFile = new CustomMultipartFile(
                                entry.getFileName().toString(), // nombre del archivo
                                entry.getFileName().toString(), // nombre original
                                "application/octet-stream",      // tipo MIME (ajustable según sea necesario)
                                content                         // contenido del archivo
                        );
                        multipartFiles.add(multipartFile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return multipartFiles;
    }
    // Implementación personalizada de MultipartFile
    public class CustomMultipartFile implements MultipartFile {

        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;

        public CustomMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.content = content;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getOriginalFilename() {
            return originalFilename;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content == null || content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return content;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            Files.write(dest.toPath(), content);
        }
    }
}
