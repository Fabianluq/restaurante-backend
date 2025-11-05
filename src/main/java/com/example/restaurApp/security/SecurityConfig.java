package com.example.restaurApp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtRequestFilter jwtRequestFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(customUserDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // ========================================
                        // ENDPOINTS PÚBLICOS
                        // ========================================
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/recuperar-contrasenia").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/resetear-contrasenia").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/validar-rol").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // ========================================
                        // ENDPOINTS COMPARTIDOS (Todos los roles autenticados)
                        // ========================================
                        // Ver menú de productos (todos los roles)
                        .requestMatchers(HttpMethod.GET, "/productos").hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")

                        // Ver categorías (todos los roles)
                        .requestMatchers(HttpMethod.GET, "/categorias").hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")

                        // Ver estados (todos los roles)
                        .requestMatchers(HttpMethod.GET, "/estados/**").hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/estadoPedido/**").hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")

                        // Ver su propio perfil (endpoint específico para usuarios autenticados)
                        .requestMatchers(HttpMethod.GET, "/auth/me").authenticated()
                        // Cambiar contraseña requiere autenticación
                        .requestMatchers(HttpMethod.PUT, "/auth/cambiar-contrasenia").authenticated()
                        // Ver su propio perfil (todos los autenticados pueden ver su empleado)
                        .requestMatchers(HttpMethod.GET, "/empleados/{id}").authenticated()

                        // Ver pedidos (cada rol para su trabajo)
                        .requestMatchers(HttpMethod.GET, "/pedidos/**").hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")

                        // ========================================
                        // ROL: ADMIN - Control Total del Sistema
                        // ========================================
                        // Gestión de Empleados (CRUD completo)
                        .requestMatchers("/empleados/**").hasRole("ADMIN")

                        // Gestión de Roles (CRUD completo)
                        .requestMatchers("/roles/**").hasRole("ADMIN")

                        // Gestión de Productos (CRUD completo) - POST, PUT, DELETE solo ADMIN
                        .requestMatchers(HttpMethod.POST, "/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/productos/**").hasRole("ADMIN")

                        // Gestión de Categorías (CRUD completo) - POST, PUT, DELETE solo ADMIN
                        .requestMatchers(HttpMethod.POST, "/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/categorias/**").hasRole("ADMIN")

                        // Gestión de Clientes - MESEROs pueden ver clientes para crear pedidos
                        .requestMatchers(HttpMethod.GET, "/clientes").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.GET, "/clientes/**").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.POST, "/clientes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/clientes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/clientes/**").hasRole("ADMIN")

                        // Gestión de Mesas - MESEROs pueden ver mesas para gestionar pedidos
                        .requestMatchers(HttpMethod.GET, "/mesas").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.GET, "/mesas/**").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.POST, "/mesas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/mesas/**").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.DELETE, "/mesas/**").hasRole("ADMIN")

                        // Eliminar pedidos y reservas (solo ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/pedidos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/reservas/**").hasRole("ADMIN")

                        // Ver todos los pagos (para reportes)
                        .requestMatchers(HttpMethod.GET, "/pagos/**").hasAnyRole("ADMIN", "CAJERO")

                        // Reportes y estadísticas
                        .requestMatchers("/reportes/**").hasRole("ADMIN")

                        // ========================================
                        // ROL: COCINERO - Gestión de Cocina
                        // ========================================
                        // Cambiar estado de pedidos: Pendiente → En Preparación → Listo
                        .requestMatchers(HttpMethod.PUT, "/pedidos/{id}/estado/{idEstado}").hasAnyRole("ADMIN", "COCINERO")

                        // Endpoints específicos de cocina
                        .requestMatchers("/api/cocina/**").hasAnyRole("ADMIN", "COCINERO")

                        // ========================================
                        // ROL: MESERO - Atención al Cliente
                        // ========================================
                        // Crear nuevos pedidos
                        .requestMatchers(HttpMethod.POST, "/pedidos").hasAnyRole("ADMIN", "MESERO")

                        // Actualizar pedidos (para marcar como entregado)
                        .requestMatchers(HttpMethod.PUT, "/pedidos/{id}").hasAnyRole("ADMIN", "MESERO")

                        // Cambiar estado de pedidos a "Entregado"
                        .requestMatchers(HttpMethod.PUT, "/pedidos/{id}/entregar").hasAnyRole("ADMIN", "MESERO")

                        // Gestionar reservas (crear, ver, actualizar)
                        .requestMatchers(HttpMethod.GET, "/reservas/**").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.POST, "/reservas/**").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.PUT, "/reservas/**").hasAnyRole("ADMIN", "MESERO")

                        // Ocupar/liberar mesas (ya configurado arriba, pero manteniendo compatibilidad)
                        .requestMatchers(HttpMethod.PUT, "/mesas/{id}/ocupar").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.PUT, "/mesas/{id}/liberar").hasAnyRole("ADMIN", "MESERO")

                        // Endpoints específicos de mesero
                        .requestMatchers("/api/mesero/**").hasAnyRole("ADMIN", "MESERO")

                        // ========================================
                        // ROL: CAJERO - Procesamiento de Pagos
                        // ========================================
                        // Procesar pagos (crear pago)
                        .requestMatchers(HttpMethod.POST, "/pagos").hasAnyRole("ADMIN", "CAJERO")

                        // Ver facturas
                        .requestMatchers(HttpMethod.GET, "/facturas/**").hasAnyRole("ADMIN", "CAJERO")

                        // Endpoints específicos de cajero
                        .requestMatchers("/api/cajero/**").hasAnyRole("ADMIN", "CAJERO")

                        // ========================================
                        // TODOS LOS DEMÁS ENDPOINTS REQUIEREN AUTENTICACIÓN
                        // ========================================
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite cualquier origen (para producción, especifica los dominios exactos)
        configuration.setAllowedOriginPatterns(List.of("*"));
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // Permite credenciales (cookies, headers de autorización)
        configuration.setAllowCredentials(true);
        // Headers expuestos al cliente
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        // Tiempo de cache de la configuración CORS (en segundos)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}