package com.uts.chatbotuts.infrastructure.config.filter;

import com.uts.chatbotuts.application.port.in.RutaUseCase;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@RequiredArgsConstructor
@Component
public class AutorizacionFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AutorizacionFilter.class);
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final RutaUseCase rutaUseCase;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        log.info("Solicitud entrante para URI: {}", requestURI);

        // Permitir recursos estáticos y páginas públicas sin verificación adicional
        if (isPublicResource(requestURI)) {
            log.debug("Recurso público, permitiendo acceso: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // Verificar si el usuario está autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Usuario no autenticado intentando acceder a: {}", requestURI);
            response.sendRedirect(request.getContextPath() + "/login.xhtml");
            return;
        }

        // Obtener mapa de rutas y roles
        Map<String, List<String>> mapaRutas = rutaUseCase.getRutaRolMappings();
        log.debug("Mapa de rutas y roles obtenido: {}", mapaRutas);

        // Buscar una ruta coincidente, considerando patrones comodín
        Entry<String, List<String>> rutaCoincidente = buscarRutaCoincidente(requestURI, mapaRutas);

        if (rutaCoincidente == null) {
            log.warn("No se encontró ninguna ruta coincidente para: {}", requestURI);
            request.getRequestDispatcher("/403.xhtml").forward(request, response);
            return;
        }

        List<String> rolesRequeridos = rutaCoincidente.getValue();
        log.debug("Ruta coincidente encontrada: {} con roles: {}", rutaCoincidente.getKey(), rolesRequeridos);

        // Verificar si el usuario tiene alguno de los roles requeridos
        boolean accesoRol = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> {
                    String authority = grantedAuthority.getAuthority();
                    return rolesRequeridos.contains(authority);
                });

        if (!accesoRol) {
            log.warn("Acceso denegado para usuario '{}' a URI '{}'. Roles requeridos: {}",
                    authentication.getName(), requestURI, rolesRequeridos);
            request.getRequestDispatcher("/403.xhtml").forward(request, response);
            return;
        }

        log.debug("Acceso permitido para usuario '{}' a URI '{}'.", authentication.getName(), requestURI);
        filterChain.doFilter(request, response);
    }

    /**
     * Busca una ruta coincidente en el mapa de rutas, considerando patrones comodín.
     *
     * @param requestURI La URI solicitada
     * @param mapaRutas El mapa de rutas y roles
     * @return La entrada del mapa que coincide con la URI solicitada, o null si no hay coincidencia
     */
    private Entry<String, List<String>> buscarRutaCoincidente(String requestURI, Map<String, List<String>> mapaRutas) {
        // Primero intentamos encontrar una coincidencia exacta
        if (mapaRutas.containsKey(requestURI)) {
            return Map.entry(requestURI, mapaRutas.get(requestURI));
        }

        // Si no hay coincidencia exacta, buscamos patrones comodín
        for (Entry<String, List<String>> entry : mapaRutas.entrySet()) {
            String pattern = entry.getKey();
            if (pattern.contains("*") && pathMatcher.match(pattern, requestURI)) {
                log.debug("Patrón comodín coincidente encontrado: {} para URI: {}", pattern, requestURI);
                return entry;
            }
        }

        return null;
    }

    private boolean isPublicResource(String requestURI) {
        return requestURI.equals("/login.xhtml") ||
                requestURI.equals("/403.xhtml") ||
                requestURI.startsWith("/jakarta.faces.resource/") ||
                requestURI.equals("/favicon.ico");
    }
}