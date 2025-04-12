package com.uts.chatbotuts.application.service;

import com.uts.chatbotuts.infrastructure.adapter.out.persistence.RoleRepositoryAdapter;
import com.uts.chatbotuts.infrastructure.entity.RutaRoleEntityId;
import com.uts.chatbotuts.application.port.in.InicializacionUseCase;
import com.uts.chatbotuts.infrastructure.entity.UsuarioEntity;
import com.uts.chatbotuts.infrastructure.entity.RoleEntity;
import com.uts.chatbotuts.infrastructure.entity.RutaEntity;
import com.uts.chatbotuts.infrastructure.entity.RutaRoleEntity;
import com.uts.chatbotuts.infrastructure.persistence.repository.RoleJpaRepository;
import com.uts.chatbotuts.infrastructure.persistence.repository.UsuarioJpaRepository;
import com.uts.chatbotuts.infrastructure.persistence.repository.RutaJpaRepository;
import com.uts.chatbotuts.infrastructure.persistence.repository.RutaRoleJpaRepository;
import com.uts.chatbotuts.infrastructure.utils.RolesConstantes;
import com.uts.chatbotuts.infrastructure.utils.RutasConstantes;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
@Service
@Transactional
public class InicializacionService implements InicializacionUseCase {

    private static final Logger logger = LoggerFactory.getLogger(InicializacionService.class);

    private final RoleJpaRepository repositorioRol;
    private final UsuarioJpaRepository repositorioUsuario;
    private final RutaJpaRepository repositorioRuta;
    private final RutaRoleJpaRepository repositorioRutaRol;
    private final PasswordEncoder codificadorContrasena;
    private final RoleRepositoryAdapter roleRepositoryAdapter;

    // Datos iniciales para el usuario administrador, inyectados desde el archivo properties
    @Value("${admin.nombre}")
    private String adminNombre;

    @Value("${admin.apellido}")
    private String adminApellido;

    @Value("${admin.documentoIdentificacion}")
    private String adminDocumentoIdentificacion;

    @Value("${admin.correoElectronico}")
    private String adminCorreoElectronico;

    @Value("${admin.contrasena}")
    private String adminContrasena;

    @Override
    public void inicializacion() {
        // Crear obtener roles
        RoleEntity rolAdmin = obtenerOCrearRol(RolesConstantes.ROL_ADMINISTRADOR);
        RoleEntity rolUsuario = obtenerOCrearRol(RolesConstantes.ROL_USUARIO);

        // Crear usuario administrador si no existe
        if (!repositorioUsuario.existsByDocumentoIdentificacion( adminDocumentoIdentificacion) && !repositorioUsuario.existsByDocumentoIdentificacionAndRol_Id(adminDocumentoIdentificacion, rolUsuario.getId())) {
            crearUsuarioAdmin(rolAdmin);
            logger.info("Usuario 'Admin' creado y asignado al rol {}", RolesConstantes.ROL_ADMINISTRADOR);
        }

        // Crear u obtener rutas
        RutaEntity rutaLogin = obtenerOCrearRuta(RutasConstantes.URL_LOGIN);
        RutaEntity rutaHome = obtenerOCrearRuta(RutasConstantes.URL_HOME);
        RutaEntity rutaNosotros = obtenerOCrearRuta(RutasConstantes.URL_NOSOTROS);
        RutaEntity rutaPreguntas = obtenerOCrearRuta(RutasConstantes.URL_PREGUNTAS);
        RutaEntity rutaAdmin = obtenerOCrearRuta(RutasConstantes.URL_ADMIN);
        RutaEntity rutaUsuario = obtenerOCrearRuta(RutasConstantes.URL_USUARIO);

        // Asociar rutas con roles
        asociarRutaRolSiNoExiste(rutaLogin, rolAdmin);
        asociarRutaRolSiNoExiste(rutaLogin, rolUsuario);
        asociarRutaRolSiNoExiste(rutaHome, rolAdmin);
        asociarRutaRolSiNoExiste(rutaHome, rolUsuario);
        asociarRutaRolSiNoExiste(rutaNosotros, rolAdmin);
        asociarRutaRolSiNoExiste(rutaPreguntas, rolAdmin);
        asociarRutaRolSiNoExiste(rutaAdmin, rolAdmin);
        asociarRutaRolSiNoExiste(rutaUsuario, rolUsuario);
    }

    private RoleEntity obtenerOCrearRol(String nombreRol) {
        RoleEntity rol = repositorioRol.findByNombre(nombreRol).orElse(null);
        if (rol == null) {
            rol = new RoleEntity();
            rol.setNombre(nombreRol);
            rol.setDescripcion(nombreRol.equals(RolesConstantes.ROL_ADMINISTRADOR) ?
                    RolesConstantes.ROL_ADMINISTRADOR_DESCRIPCION : RolesConstantes.ROL_USUARIO_DESCRIPCION);
            rol = repositorioRol.save(rol);
            logger.info("Rol '{}' creado.", nombreRol);
        }
        return rol;
    }

    private RutaEntity obtenerOCrearRuta(String urlRuta) {
        RutaEntity ruta = repositorioRuta.findByRutaUrl(urlRuta).orElse(null);
        if (ruta == null) {
            ruta = new RutaEntity();
            ruta.setRutaUrl(urlRuta);
            ruta = repositorioRuta.save(ruta);
            logger.info("Ruta '{}' creada.", urlRuta);
        }
        return ruta;
    }

    private void crearUsuarioAdmin(RoleEntity rolAdmin) {
        UsuarioEntity usuarioAdmin = new UsuarioEntity();
        usuarioAdmin.setNombre(adminNombre);
        usuarioAdmin.setApellido(adminApellido);
        usuarioAdmin.setDocumentoIdentificacion(adminDocumentoIdentificacion);
        usuarioAdmin.setCorreoElectronico(adminCorreoElectronico);
        usuarioAdmin.setContrasena(codificadorContrasena.encode(adminContrasena));
        usuarioAdmin.setEstado(true);
        usuarioAdmin.setCreatedAt(Instant.now());
        usuarioAdmin.setRol(rolAdmin);
        repositorioUsuario.save(usuarioAdmin);
    }

    private void asociarRutaRolSiNoExiste(RutaEntity ruta, RoleEntity rol) {
        if (!repositorioRutaRol.existsByIdRuta_IdRutaAndIdRol_Id(ruta.getIdRuta(), rol.getId())) {
            RutaRoleEntity rutaRole = new RutaRoleEntity();
            rutaRole.setId(new RutaRoleEntityId());
            rutaRole.setIdRol(rol);
            rutaRole.setIdRuta(ruta);
            repositorioRutaRol.save(rutaRole);
            logger.info("Relación 'ruta_rol' creada para el rol '{}' y la ruta '{}'.", rol.getNombre(), ruta.getRutaUrl());
        }
    }
}
