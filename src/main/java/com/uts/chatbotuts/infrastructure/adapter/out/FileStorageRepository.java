package com.uts.chatbotuts.infrastructure.adapter.out;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

import lombok.Getter;
import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class FileStorageRepository {

    // Define la ruta base configurable desde application.properties
    @Value("${uploads.base.path:/ruta/local/uploads}")
    private String basePath;

    /**
     * Guarda un archivo en la ruta relativa proporcionada.
     * @param relativePath Ruta relativa desde la base, por ejemplo "temp/SESSION_ID/nombreArchivo.ext"
     * @param file Archivo a guardar.
     * @throws IOException Si ocurre algún error durante la escritura.
     */
    public void saveFile(String relativePath, UploadedFile file) throws IOException {
        Path fullPath = Paths.get(basePath, relativePath);
        // Asegúrate de que el directorio existe
        Files.createDirectories(fullPath.getParent());
        // Guarda el archivo. Se sobreescribe si ya existe.
        try (InputStream input = file.getInputStream()) {
            Files.copy(input, fullPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Renombra o mueve un directorio.
     * @param sourceRelativePath Ruta relativa del directorio origen.
     * @param targetRelativePath Ruta relativa del directorio destino.
     * @return true si se realizó el cambio.
     */
    public boolean renameDirectory(String sourceRelativePath, String targetRelativePath) {
        Path source = Paths.get(basePath, sourceRelativePath);
        Path target = Paths.get(basePath, targetRelativePath);
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
