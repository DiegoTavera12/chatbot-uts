package com.uts.chatbotuts.infrastructure.adapter.in.web;

import com.uts.chatbotuts.application.port.in.EliminarFaqUseCase;
import com.uts.chatbotuts.application.port.in.ObtenerFaqsUseCase;
import com.uts.chatbotuts.application.port.in.RegistrarFaqUseCase;
import com.uts.chatbotuts.application.service.LazyFaqDataModel;
import com.uts.chatbotuts.domain.model.FaqDomain;
import com.uts.chatbotuts.domain.model.PalabraClave;
import com.uts.chatbotuts.infrastructure.adapter.out.FileStorageRepository;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@Component("faqMB")
@ViewScoped
public class FAQAdminBean implements Serializable {

    private RegistrarFaqUseCase registrarFaqUseCase;
    private ObtenerFaqsUseCase obtenerFaqsUseCase;
    private EliminarFaqUseCase eliminarFaqUseCase;

    // Modelo lazy para el DataTable de FAQs
    private LazyDataModel<FaqDomain> lazyFaqs;

    private FileStorageRepository fileStorageRepository;

    // Propiedades del formulario
    private Long faqId;
    private String pregunta;
    private String respuesta;
    // Para simplificar, se utiliza un campo de texto para ingresar las palabras clave separadas por comas
    private String palabrasClaveText;
    private Long vecesUsada = 0L;
    private Boolean estado = Boolean.TRUE;

    // Para eliminación múltiple
    private List<FaqDomain> selectedFaqs;

    public List<FaqDomain> getSelectedFaqs() {
        return selectedFaqs;
    }

    // Para edición o eliminación individual
    private FaqDomain selectedFaq;

    @Autowired
    public FAQAdminBean(RegistrarFaqUseCase registrarFaqUseCase,
                        ObtenerFaqsUseCase obtenerFaqsUseCase,
                        EliminarFaqUseCase eliminarFaqUseCase,
                        FileStorageRepository fileStorageRepository
    ) {
        this.registrarFaqUseCase = registrarFaqUseCase;
        this.obtenerFaqsUseCase = obtenerFaqsUseCase;
        this.eliminarFaqUseCase = eliminarFaqUseCase;
        this.fileStorageRepository = fileStorageRepository;
    }


    private boolean esConsultarPalabrasClave = false;

    public void asignarEsConsultarPalabrasClave(boolean esConsultarPalabrasClave) {
        this.esConsultarPalabrasClave = esConsultarPalabrasClave;
    }

    public FAQAdminBean() {
    }

    @PostConstruct
    public void init() {
        // Inicializa el LazyDataModel para cargar FAQs de forma paulatina
        lazyFaqs = new LazyFaqDataModel(obtenerFaqsUseCase);
        faqId = null;
        pregunta = null;
        respuesta = null;
        palabrasClaveText = null;
        vecesUsada = null;
        estado = Boolean.FALSE;
        selectedFaqs = null;
        selectedFaq = null;
        files = null;
        fileList = null;
    }

    /**
     * Prepara el formulario para crear una nueva FAQ.
     * Se reinician los campos y se crea un objeto FaqDomain vacío.
     */
    public void crearFaq() {
        this.pregunta = "";
        this.respuesta = "";
        this.palabrasClaveText = "";
        this.vecesUsada = 0L;
        this.estado = Boolean.TRUE;
        this.selectedFaq = new FaqDomain();
    }

    /**
     * Prepara el formulario para editar una FAQ existente.
     * Se cargan los datos de la FAQ seleccionada en los campos del formulario.
     */
    public void editarFaq(FaqDomain faq) {
        if (faq != null) {

            fileList = null;
            files = null;
            this.faqId = faq.getId();
            this.pregunta = faq.getPregunta();
            this.respuesta = faq.getRespuesta();
            // Se convierte la lista de palabras clave a una cadena separada por comas
            this.palabrasClaveText = faq.getPalabrasClave() != null ?
                    faq.getPalabrasClave().stream().map(PalabraClave::getPalabra).collect(Collectors.joining(", ")) : "";
            this.vecesUsada = faq.getVecesUsada();
            this.estado = faq.getEstado();
            fileList = obtenerFileList();
        }
    }

    /**
     * Registra una nueva FAQ o actualiza una existente.
     */
    public Long registrarFaq() {
        FacesContext context = FacesContext.getCurrentInstance();
        Long idReturn = null;
        try {
            FaqDomain faq;
            if (Objects.isNull(faqId)) {
                // Creación de nueva FAQ
                faq = new FaqDomain();
                faq.setPregunta(pregunta);
                faq.setRespuesta(respuesta);
                // Convertir el texto de palabras clave en una lista de objetos PalabraClave
                //faq.setPalabrasClave(convertirPalabrasClave(palabrasClaveText));
                faq.setVecesUsada(0L);
                faq.setEstado(estado);
                faq.setCreatedAt(Instant.now());
                faq.setEsBuscarPalabrasClave(esConsultarPalabrasClave);
                faq.setPalabrasClaveString(palabrasClaveText);
                idReturn = registrarFaqUseCase.guardarFaq(faq).getId();
                context.addMessage(null, new FacesMessage("FAQ registrada correctamente"));
            } else {
                // Actualización de la FAQ existente
                selectedFaq.setPregunta(pregunta);
                selectedFaq.setRespuesta(respuesta);
                //selectedFaq.setPalabrasClave(convertirPalabrasClave(palabrasClaveText));
                selectedFaq.setEstado(estado);
                selectedFaq.setUpdatedAt(Instant.now());
                selectedFaq.setEsBuscarPalabrasClave(esConsultarPalabrasClave);
                selectedFaq.setPalabrasClaveString(palabrasClaveText);
                idReturn = registrarFaqUseCase.guardarFaq(selectedFaq).getId();
                context.addMessage(null, new FacesMessage("FAQ actualizada correctamente"));
            }
            limpiarFormulario();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Infor", "Si no se visualizan las palabras claves recargue la página o la tabla aplicando un filtro"));
            return idReturn;
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar la FAQ"));
        }
        return idReturn;
    }

    /**
     * Elimina una FAQ individual.
     */
    public void eliminarFaq() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (selectedFaq != null && selectedFaq.getId() != null) {
                eliminarFaqUseCase.eliminarFaq(selectedFaq.getId());
                context.addMessage(null, new FacesMessage("FAQ eliminada correctamente"));
                selectedFaq = null;
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar la FAQ"));
        }
    }

    /**
     * Elimina las FAQs seleccionadas (eliminación múltiple).
     */
    public void eliminarSeleccion() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (selectedFaqs != null && !selectedFaqs.isEmpty()) {
                for (FaqDomain faq : selectedFaqs) {
                    eliminarFaqUseCase.eliminarFaq(faq.getId());
                }
                context.addMessage(null, new FacesMessage("FAQs eliminadas correctamente"));
                selectedFaqs = null;
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron eliminar las FAQs seleccionadas"));
        }
    }

    /**
     * Limpia los campos del formulario.
     */
    private void limpiarFormulario() {
        this.pregunta = "";
        this.respuesta = "";
        this.palabrasClaveText = "";
        this.vecesUsada = 0L;
        this.estado = Boolean.TRUE;
        this.selectedFaq = null;
        this.esConsultarPalabrasClave = false;
    }

//    /**
//     * Método auxiliar para convertir el texto de palabras clave (separadas por comas) en una lista de objetos PalabraClave.
//     * Se asume que cada objeto PalabraClave tiene al menos un atributo 'nombre'.
//     */
//    private List<PalabraClave> convertirPalabrasClave(String texto) {
//        if (texto == null || texto.trim().isEmpty()) {
//            return null;
//        }
//        return Arrays.stream(texto.split(","))
//                .map(String::trim)
//                .filter(s -> !s.isEmpty())
//                .map(s -> new PalabraClave(null, s))
//                .collect(Collectors.toList());
//    }

    private List<UploadedFile> archivos = new ArrayList<>();

    public String getSessionId() {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getExternalContext().getSessionId(false);
    }

    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        try {
            String sessionId = getSessionId();
            // Define la ruta relativa donde se almacenará el archivo temporal
            String relativePath = "temp/" + sessionId + "/" + file.getFileName();
            // Guarda el archivo usando el repositorio
            fileStorageRepository.saveFile(relativePath, file);
            // Puedes almacenar en una lista la ruta o información necesaria para su manejo
            archivos.add(file);
        } catch (IOException e) {
            e.printStackTrace();
            // Manejo de errores: notificar al usuario, etc.
        }
    }

    public void registrar() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.getExternalContext().getFlash().setKeepMessages(true);
        // Lógica para guardar la FAQ en la base de datos y obtener el id generado
        Long idFaq = registrarFaq();
        String contextPath = facesContext.getExternalContext().getRequestContextPath();

        if (idFaq != null) {
            this.faqId = idFaq;
            uploadMultiple();
            // Obtener el ID de sesión para acceder a la carpeta temporal del usuario
            String sessionId = getSessionId();
            // Define la ruta relativa origen (temporal) y destino (definitiva)
            String sourceRelativePath = "temp/" + sessionId;
            String targetRelativePath = idFaq.toString();


            boolean renamed = fileStorageRepository.renameDirectory(sourceRelativePath, targetRelativePath);
            if (!renamed) {
                System.out.println("Error al renombrar la carpeta temporal para la FAQ " + idFaq);
            }
        }

        ejecutarEliminacionesPendientes();

        limpiarFormulario();
        facesContext.getExternalContext().redirect(contextPath + "/admin/faqs.xhtml");
        facesContext.responseComplete();
    }

    private void ejecutarEliminacionesPendientes() {
        for (String fileName : pendingDeletedFiles) {
            try {
                // Construir la ruta completa: basePath/faqId/fileName
                Path filePath = Paths.get(fileStorageRepository.getBasePath(), faqId.toString(), fileName);
                Files.deleteIfExists(filePath);
            } catch (Exception e) {
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar " + fileName));
            }
        }
        // Limpiar la lista de pendientes
        pendingDeletedFiles.clear();
    }

    public void eliminarArchivo(String fileName) {
        try {
            // Construir la ruta completa: basePath/faqId/fileName
            Path filePath = Paths.get(fileStorageRepository.getBasePath(), faqId.toString(), fileName);
            File file = filePath.toFile();
            if (file.exists()) {
                if (file.delete()) {
                    FacesMessage msg = new FacesMessage("Archivo eliminado", fileName + " fue eliminado.");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    // Reinicia la lista para que se actualice en la vista
                    fileList = null;
                } else {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar " + fileName);
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Atención", "El archivo " + fileName + " no existe.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Excepción al eliminar " + fileName);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            e.printStackTrace();
        }
    }

    private UploadedFiles files;

    public void uploadMultiple() {
        if (files != null) {
            String sessionId = getSessionId();
            for (UploadedFile f : files.getFiles()) {
                try {
                    // Define la ruta relativa en la carpeta temporal del usuario
                    String relativePath = "temp/" + sessionId + "/" + f.getFileName();
                    fileStorageRepository.saveFile(relativePath, f);
                    FacesMessage message = new FacesMessage("Successful", f.getFileName() + " is uploaded.");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                } catch (IOException e) {
                    FacesMessage error = new FacesMessage("Error", "Error uploading " + f.getFileName());
                    FacesContext.getCurrentInstance().addMessage(null, error);
                    e.printStackTrace();
                }
            }
        }
    }

    private List<String> fileList;

    // Getters y setters para files, faqId y fileList

    public List<String> obtenerFileList() {
        if (faqId != null && (fileList == null || fileList.isEmpty())) {
            // Asumiendo que basePath está configurado en fileStorageRepository
            Path directory = Paths.get(fileStorageRepository.getBasePath(), faqId.toString());
            fileList = new ArrayList<>();
            if (Files.exists(directory) && Files.isDirectory(directory)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
                    for (Path entry : stream) {
                        fileList.add(entry.getFileName().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    private List<String> pendingDeletedFiles = new ArrayList<>();

    public void marcarEliminarArchivo(String fileName) {
        // Agregar el archivo a la lista de pendientes si no está ya marcado
        if (!pendingDeletedFiles.contains(fileName)) {
            pendingDeletedFiles.add(fileName);
        }
        // Remover el archivo de la lista de la vista para que no se muestre
        if (fileList != null) {
            fileList.remove(fileName);
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Eliminación pendiente", fileName + " se eliminará al guardar."));
    }

}
