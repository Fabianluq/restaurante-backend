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
                        // RUTAS PÚBLICAS (Sin autenticación)
                        // ========================================
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/clientes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/productos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/categorias/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // ========================================
                        // RUTAS COMPARTIDAS (Todos los roles autenticados)
                        // ========================================
                        // Estados: Todos los roles necesitan ver estados para su trabajo
                        .requestMatchers(HttpMethod.GET, "/estados/**").authenticated()

                        // Empleados: Todos pueden obtener su propio perfil (necesario después del
                        // login)
                        .requestMatchers(HttpMethod.GET, "/empleados/{id}").authenticated()

                        // Pedidos: Todos los roles pueden ver pedidos (cada uno para su trabajo)
                        .requestMatchers(HttpMethod.GET, "/pedidos/**").authenticated()

                        // ========================================
                        // ROL: ADMIN - Control Total del Sistema
                        // ========================================
                        // Gestión de Roles (CRUD completo)
                        .requestMatchers("/roles/**").hasRole("ADMIN")

                        // Gestión de Productos (CRUD completo - GET ya es público)
                        .requestMatchers(HttpMethod.POST, "/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/productos/**").hasRole("ADMIN")

                        // Gestión de Categorías (CRUD completo - GET ya es público)
                        .requestMatchers(HttpMethod.POST, "/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/categorias/**").hasRole("ADMIN")

                        // Gestión de Mesas (CRUD completo)
                        .requestMatchers("/mesas/**").hasRole("ADMIN")

                        // Gestión de Clientes (CRUD completo - GET ya es público)
                        .requestMatchers(HttpMethod.POST, "/clientes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/clientes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/clientes/**").hasRole("ADMIN")

                        // Gestión de Empleados (CRUD completo - GET por ID ya está arriba para todos)
                        .requestMatchers(HttpMethod.GET, "/empleados").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/empleados/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/empleados/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/empleados/**").hasRole("ADMIN")
                        .requestMatchers("/empleados/**/cambiar-contrasena").hasRole("ADMIN")

                        // Eliminación de Pedidos y Reservas (solo admin puede eliminar)
                        .requestMatchers(HttpMethod.DELETE, "/pedidos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/reservas/**").hasRole("ADMIN")

                        // Ver todos los pagos (para reportes y auditoría)
                        .requestMatchers(HttpMethod.GET, "/pagos/**").hasAnyRole("ADMIN", "CAJERO")

                        // ========================================
                        // ROL: MESERO - Gestión de Pedidos y Atención al Cliente
                        // ========================================
                        // Crear nuevos pedidos
                        .requestMatchers(HttpMethod.POST, "/pedidos").hasAnyRole("MESERO", "ADMIN")

                        // Actualizar pedidos existentes (modificar productos, cantidades, etc.)
                        .requestMatchers(HttpMethod.PUT, "/pedidos/{id}").hasAnyRole("MESERO", "ADMIN")

                        // Cambiar estado de pedidos (marcar como entregado)
                        .requestMatchers(HttpMethod.PUT, "/pedidos/{id}/estado/{idEstado}")
                        .hasAnyRole("MESERO", "COCINERO", "ADMIN")

                        // Ocupar mesas para comenzar a atender
                        .requestMatchers("/empleados/mesas/{id}/ocupar").hasAnyRole("MESERO", "ADMIN")

                        // Gestión de Reservas (crear, ver, actualizar - NO eliminar)
                        .requestMatchers(HttpMethod.GET, "/reservas/**").hasAnyRole("MESERO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/reservas/**").hasAnyRole("MESERO", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/reservas/**").hasAnyRole("MESERO", "ADMIN")

                        // ========================================
                        // ROL: COCINERO - Gestión de Cocina
                        // ========================================
                        // Ver pedidos (ya está en rutas compartidas arriba)
                        // Cambiar estado de pedidos: Pendiente → En Preparación → Listo
                        // Nota: Ya está cubierto arriba con MESERO y ADMIN

                        // ========================================
                        // ROL: CAJERO - Procesamiento de Pagos
                        // ========================================
                        // Crear pagos (procesar cobros de pedidos)
                        .requestMatchers(HttpMethod.POST, "/pagos").hasAnyRole("CAJERO", "ADMIN")

                        // Ver pagos (ya está arriba con ADMIN)

                        // Obtener y generar facturas
                        .requestMatchers(HttpMethod.GET, "/facturas/**").hasAnyRole("CAJERO", "ADMIN")

                        // ========================================
                        // CUALQUIER OTRA PETICIÓN
                        // ========================================
                        .anyRequest().authenticated()

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
