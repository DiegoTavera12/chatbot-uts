package com.uts.chatbotuts.infrastructure.adapter.in.web;

import com.uts.chatbotuts.application.port.in.EliminarUsuarioUseCase;
import com.uts.chatbotuts.application.port.in.ObtenerUsuariosUseCase;
import com.uts.chatbotuts.application.port.in.RegistrarUsuarioYAsignarRoleUseCase;
import com.uts.chatbotuts.application.service.LazyUsuarioDataModel;
import com.uts.chatbotuts.domain.model.UsuarioDomain;
import com.uts.chatbotuts.infrastructure.utils.RolesConstantes;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.LazyDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Component("usuarioMB")
@ViewScoped
public class UsuarioMB implements Serializable {

    private RegistrarUsuarioYAsignarRoleUseCase registrarUsuarioYAsignarRoleUseCase;
    private ObtenerUsuariosUseCase obtenerUsuariosUseCase;
    private EliminarUsuarioUseCase eliminarUsuarioUseCase;

    // Modelo lazy para el DataTable
    private LazyDataModel<UsuarioDomain> lazyUsuarios;

    // Propiedades del formulario
    private Long id;
    private String nombre;
    private String apellido;
    private String documentoIdentificacion;
    private String documentoEstudiante;
    private String correoElectronico;
    private Boolean estado = true;

    private Instant createdAt;
    private Instant updatedAt;
    private String contrasena;

    private String role = "Admin";

    private List<String> roles = Arrays.asList("Admin", "Usuario");

    // Para eliminación múltiple
    private List<UsuarioDomain> selectedUsuarios;

    // Para edición o eliminación individual
    private UsuarioDomain selectedUsuario;

    @Autowired
    public UsuarioMB(RegistrarUsuarioYAsignarRoleUseCase registrarUsuarioYAsignarRoleUseCase,
                     ObtenerUsuariosUseCase obtenerUsuariosUseCase, EliminarUsuarioUseCase eliminarUsuarioUseCase) {
        this.registrarUsuarioYAsignarRoleUseCase = registrarUsuarioYAsignarRoleUseCase;
        this.obtenerUsuariosUseCase = obtenerUsuariosUseCase;
        this.eliminarUsuarioUseCase = eliminarUsuarioUseCase;
    }

    public UsuarioMB() {
    }

    @PostConstruct
    public void init() {
        // Inicializa el LazyDataModel para cargar usuarios de forma paulatina
        lazyUsuarios = new LazyUsuarioDataModel(obtenerUsuariosUseCase);
    }

    /**
     * Prepara el formulario para crear un nuevo usuario.
     * Se reinician los campos y se crea un objeto UsuarioDomain vacío.
     */
    public void crearUsuario() {
        this.nombre = "";
        this.apellido = "";
        this.documentoIdentificacion = "";
        this.documentoEstudiante = "";
        this.correoElectronico = "";
        this.contrasena = "";
        this.role = null;
        this.selectedUsuario = new UsuarioDomain();
        this.contrasena = "";
        this.esRegistrarContrasena = Boolean.TRUE;
    }

    /**
     * Prepara el formulario para editar un usuario existente.
     * Se cargan los datos del usuario seleccionado en los campos del formulario.
     */
    public void editarUsuario(UsuarioDomain usuario) {
        if (usuario != null) {
            this.nombre = usuario.getNombre();
            this.apellido = usuario.getApellido();
            this.documentoIdentificacion = usuario.getDocumentoIdentificacion();
            this.documentoEstudiante = usuario.getDocumentoEstudiante();
            this.correoElectronico = usuario.getCorreoElectronico();
            this.role = usuario.getRole().getDescripcion();
            //this.contrasena = usuario.getContrasena();
            this.esRegistrarContrasena = Boolean.FALSE;
        }
    }

    public boolean isEsRegistrarContrasena() {
        return esRegistrarContrasena;
    }

    private boolean esRegistrarContrasena = Boolean.FALSE;

    /**
     * Registra un nuevo usuario o actualiza uno existente.
     * Se invoca desde el diálogo de creación/edición.
     */
    public void registrar() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {


            UsuarioDomain usuario;
            if (selectedUsuario == null || selectedUsuario.getId() == null) {
                // Creación de nuevo usuario
                usuario = new UsuarioDomain(null, nombre, apellido, documentoIdentificacion,
                        documentoEstudiante, correoElectronico, estado, null, null, contrasena != null && !contrasena.isEmpty() ? contrasena : null, null);
                registrarUsuarioYAsignarRoleUseCase.registrarUsuario(usuario, "Admin".equals(role) ? 1 : 2);
                context.addMessage(null, new FacesMessage("Usuario registrado correctamente"));
            } else {
                // Actualización del usuario existente
                selectedUsuario.setNombre(nombre);
                selectedUsuario.setApellido(apellido);
                selectedUsuario.setDocumentoIdentificacion(documentoIdentificacion);
                selectedUsuario.setDocumentoEstudiante(documentoEstudiante);
                selectedUsuario.setCorreoElectronico(correoElectronico);
                selectedUsuario.setEstado(estado);
                selectedUsuario.setContrasena(contrasena);
                registrarUsuarioYAsignarRoleUseCase.registrarUsuario(selectedUsuario, "Admin".equals(role) ? 1 : 2);
                context.addMessage(null, new FacesMessage("Usuario actualizado correctamente"));
            }
            // Reinicia el formulario y refresca el DataTable
            limpiarFormulario();
            // Reinicializa el LazyDataModel para que se vuelvan a cargar los usuarios actualizados
            lazyUsuarios = new LazyUsuarioDataModel(obtenerUsuariosUseCase);
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar el usuario, verifica los datos ingresados, recuerda que el número de identificación y correo electronico no puede ya estar registrado"));
        }
    }


    /**
     * Elimina un usuario individual.
     * Se invoca desde el diálogo de confirmación individual.
     */
    public void eliminarUsuario() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (selectedUsuario != null && selectedUsuario.getId() != null) {


                eliminarUsuarioUseCase.eliminarUsuario(selectedUsuario.getId());

                context.addMessage(null, new FacesMessage("Usuario eliminado correctamente"));
                // Reiniciar el usuario seleccionado
                selectedUsuario = null;
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar el usuario"));
        }
    }

    /**
     * Prepara la eliminación individual.
     * Con este método se puede asignar el usuario seleccionado para eliminar.
     * (El f:setPropertyActionListener del botón ya asigna el valor a selectedUsuario).
     */
    public void prepararEliminarUsuario() {
        // Este método se puede dejar vacío o utilizarse para tareas adicionales previas a la eliminación.
    }

    /**
     * Elimina los usuarios seleccionados (eliminación múltiple).
     */
    public void eliminarSeleccion() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (selectedUsuarios != null && !selectedUsuarios.isEmpty()) {

                //List<Long> ids = selectedUsuarios.stream().map(UsuarioDomain::getId).collect(Collectors.toList());

                for (UsuarioDomain usuario : selectedUsuarios) {

                    eliminarUsuarioUseCase.eliminarUsuario(usuario.getId());

                }

                //eliminarUsuarioUseCase.eliminarUsuarios(usuario.getId());
                context.addMessage(null, new FacesMessage("Usuarios eliminados correctamente"));
                // Reinicia la selección
                selectedUsuarios = null;

            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron eliminar los usuarios seleccionados"));
        }
    }

    /**
     * Limpia los campos del formulario y resetea el usuario seleccionado.
     */
    private void limpiarFormulario() {
        this.nombre = "";
        this.apellido = "";
        this.documentoIdentificacion = "";
        this.documentoEstudiante = "";
        this.correoElectronico = "";
        this.role = null;
        this.selectedUsuario = null;
        this.contrasena = "";
        this.estado = true;
        this.esRegistrarContrasena = Boolean.FALSE;
    }

    private void actulizarTabla() {
        lazyUsuarios = new LazyUsuarioDataModel(obtenerUsuariosUseCase);
    }

    public void generarContrasena() {


        String ultimos3Identificacion = "";
        String primeros3Nombre = "";
        String ultimos3Apellido = "";

        if (documentoIdentificacion != null && documentoIdentificacion.length() >= 3) {
            ultimos3Identificacion = documentoIdentificacion.substring(documentoIdentificacion.length() - 3);
        } else if (documentoIdentificacion != null) {
            ultimos3Identificacion = documentoIdentificacion;
        }

        if (nombre != null && nombre.length() >= 3) {
            primeros3Nombre = nombre.substring(0, 3);
        } else if (nombre != null) {
            primeros3Nombre = nombre;
        }

        if (apellido != null && apellido.length() >= 3) {
            ultimos3Apellido = apellido.substring(apellido.length() - 3);
        } else if (apellido != null) {
            ultimos3Apellido = apellido;
        }

        String milisegundos = String.valueOf(System.currentTimeMillis());
        String ultimosMiliSegundos = "";
        if (milisegundos != null && milisegundos.length() >= 3) {
            ultimosMiliSegundos = milisegundos.substring(milisegundos.length() - 3);
        } else {
            ultimosMiliSegundos = milisegundos;
        }


        // Concatena las partes para formar la contraseña
        this.contrasena = ultimos3Identificacion + primeros3Nombre + ultimos3Apellido + ultimosMiliSegundos;

    }

}
