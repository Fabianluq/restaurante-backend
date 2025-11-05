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

    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
    // .csrf(csrf -> csrf.disable())
    // .authorizeHttpRequests(auth -> auth
    // // ========================================
    // // ENDPOINTS PÚBLICOS
    // // ========================================
    // .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
    // .requestMatchers(HttpMethod.POST, "/auth/recuperar-contrasenia").permitAll()
    // .requestMatchers(HttpMethod.POST, "/auth/resetear-contrasenia").permitAll()
    // .requestMatchers(HttpMethod.GET, "/auth/validar-rol").permitAll()
    // .requestMatchers("/v3/api-docs/**", "/swagger-ui/**",
    // "/swagger-ui.html").permitAll()

    // // ========================================
    // // ENDPOINTS PÚBLICOS (Sin autenticación)
    // // IMPORTANTE: Estas reglas deben ir ANTES de las autenticadas
    // // ========================================

    // // Reservas públicas (clientes sin autenticación)
    // .requestMatchers(HttpMethod.POST, "/reservas/publica").permitAll()
    // .requestMatchers(HttpMethod.GET,
    // "/reservas/publica/disponibilidad").permitAll()
    // .requestMatchers(HttpMethod.GET, "/reservas/publica/**").permitAll()
    // .requestMatchers(HttpMethod.PUT, "/reservas/publica/**").permitAll()

    // // Menú público - PRODUCTOS (sin autenticación)
    // // Todas las rutas GET de productos son públicas
    // .requestMatchers(HttpMethod.GET, "/productos/**").permitAll()

    // // Menú público - CATEGORÍAS (sin autenticación)
    // // Todas las rutas GET de categorías son públicas
    // .requestMatchers(HttpMethod.GET, "/categorias/**").permitAll()

    // // ========================================
    // // ENDPOINTS COMPARTIDOS (Todos los roles autenticados)
    // // NOTA: Las rutas GET de productos y categorías ya están públicas arriba
    // // Estas reglas solo aplican para POST, PUT, DELETE que requieren
    // autenticación
    // // ========================================

    // // Ver estados (todos los roles)
    // .requestMatchers(HttpMethod.GET, "/estados/**")
    // .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
    // .requestMatchers(HttpMethod.GET, "/estadoPedido/**")
    // .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
    // .requestMatchers(HttpMethod.GET, "/estadoMesa/**")
    // .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
    // .requestMatchers(HttpMethod.GET, "/estadoProducto/**")
    // .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")

    // // Ver su propio perfil (endpoint específico para usuarios autenticados)
    // .requestMatchers(HttpMethod.GET, "/auth/me").authenticated()

    // // Cambiar contraseña requiere autenticación
    // .requestMatchers(HttpMethod.PUT, "/auth/cambiar-contrasenia").authenticated()

    // // Ver su propio perfil (todos los autenticados pueden ver su empleado)
    // .requestMatchers(HttpMethod.GET, "/empleados/{id}").authenticated()

    // // Ver pedidos (cada rol para su trabajo)
    // // IMPORTANTE: Las reglas específicas deben ir ANTES de las generales
    // // Primero las específicas con parámetros, luego las generales
    // .requestMatchers(HttpMethod.GET, "/pedidos/{id}/total")
    // .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
    // .requestMatchers(HttpMethod.GET, "/pedidos/{id}")
    // .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
    // .requestMatchers(HttpMethod.GET, "/pedidos/mesa/**")
    // .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
    // .requestMatchers(HttpMethod.GET, "/pedidos/estado/**")
    // .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
    // .requestMatchers(HttpMethod.GET, "/pedidos/buscar")
    // .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
    // .requestMatchers(HttpMethod.GET, "/pedidos/empleado/**")
    // .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
    // // Esta regla general debe ir AL FINAL para evitar conflictos
    // .requestMatchers(HttpMethod.GET, "/pedidos").hasAnyRole("ADMIN", "COCINERO",
    // "MESERO", "CAJERO")

    // // Ver detalles de pedido
    // .requestMatchers(HttpMethod.GET, "/detalles-pedido")
    // .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
    // .requestMatchers(HttpMethod.GET, "/detalles-pedido/{id}")
    // .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
    // .requestMatchers(HttpMethod.GET, "/detalles-pedido/pedido/**")
    // .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")

    // // ========================================
    // // ROL: COCINERO - Gestión de Cocina
    // // IMPORTANTE: Reglas específicas ANTES de las generales
    // // ========================================
    // // Ver pedidos para cocina (endpoint específico)
    // .requestMatchers(HttpMethod.GET, "/pedidos/cocina").hasAnyRole("ADMIN",
    // "COCINERO")

    // // Cambiar estado de pedidos: Pendiente → En Preparación → Listo
    // // COCINERO puede cambiar a "En Preparación" y "Listo"
    // .requestMatchers(HttpMethod.PUT, "/pedidos/{id}/estado/{idEstado}")
    // .hasAnyRole("ADMIN", "COCINERO", "MESERO")

    // // Cambiar estado de detalles de pedido
    // .requestMatchers(HttpMethod.PUT,
    // "/detalles-pedido/{id}/estado/{estadoDetalleId}")
    // .hasAnyRole("ADMIN", "COCINERO")

    // // Endpoints específicos de cocina
    // .requestMatchers("/api/cocina/**").hasAnyRole("ADMIN", "COCINERO")

    // // ========================================
    // // ROL: MESERO - Atención al Cliente
    // // IMPORTANTE: Reglas específicas ANTES de /empleados/** y /mesas/**
    // // ========================================
    // // Ocupar mesas - CRÍTICO: Debe ir ANTES de /empleados/**
    // .requestMatchers(HttpMethod.PUT,
    // "/empleados/mesas/**/ocupar").hasAnyRole("ADMIN", "MESERO")

    // // Crear nuevos pedidos
    // .requestMatchers(HttpMethod.POST, "/pedidos").hasAnyRole("ADMIN", "MESERO")

    // // Actualizar pedidos (para marcar como entregado)
    // .requestMatchers(HttpMethod.PUT, "/pedidos/{id}").hasAnyRole("ADMIN",
    // "MESERO")

    // // Ver pedidos por empleado (sus propios pedidos) - MESERO y ADMIN
    // // NOTA: Ya está cubierto arriba en compartidos, pero restringimos PUT/POST
    // aquí

    // // Agregar detalles a un pedido
    // .requestMatchers(HttpMethod.POST, "/detalles-pedido/{pedidoId}/detalles")
    // .hasAnyRole("ADMIN", "MESERO")

    // // Actualizar detalles de pedido
    // .requestMatchers(HttpMethod.PUT, "/detalles-pedido/{id}").hasAnyRole("ADMIN",
    // "MESERO")

    // // Gestionar reservas (crear, ver, actualizar) - SOLO rutas NO públicas
    // // NOTA: Las rutas /reservas/publica/** ya están permitidas arriba
    // .requestMatchers(HttpMethod.GET, "/reservas").hasAnyRole("ADMIN", "MESERO")
    // // Excluir rutas públicas antes de aplicar wildcard general
    // .requestMatchers(HttpMethod.GET, "/reservas/{id}").hasAnyRole("ADMIN",
    // "MESERO")
    // .requestMatchers(HttpMethod.GET, "/reservas/cliente/**").hasAnyRole("ADMIN",
    // "MESERO")
    // .requestMatchers(HttpMethod.GET, "/reservas/estado/**").hasAnyRole("ADMIN",
    // "MESERO")
    // .requestMatchers(HttpMethod.POST, "/reservas").hasAnyRole("ADMIN", "MESERO")
    // .requestMatchers(HttpMethod.PUT, "/reservas/{id}").hasAnyRole("ADMIN",
    // "MESERO")
    // .requestMatchers(HttpMethod.PUT, "/reservas/{id}/**").hasAnyRole("ADMIN",
    // "MESERO")

    // // Ver clientes para crear pedidos
    // .requestMatchers(HttpMethod.GET, "/clientes").hasAnyRole("ADMIN", "MESERO")
    // .requestMatchers(HttpMethod.GET, "/clientes/buscar/**").hasAnyRole("ADMIN",
    // "MESERO")
    // .requestMatchers(HttpMethod.GET, "/clientes/{id}").hasAnyRole("ADMIN",
    // "MESERO")

    // // Ver mesas para gestionar pedidos
    // .requestMatchers(HttpMethod.GET, "/mesas").hasAnyRole("ADMIN", "MESERO")
    // .requestMatchers(HttpMethod.GET, "/mesas/{id}").hasAnyRole("ADMIN", "MESERO")
    // .requestMatchers(HttpMethod.GET, "/mesas/estado/**").hasAnyRole("ADMIN",
    // "MESERO")
    // .requestMatchers(HttpMethod.PUT, "/mesas/{id}").hasAnyRole("ADMIN", "MESERO")

    // // Ocupar/liberar mesas (alternativos si existen)
    // .requestMatchers(HttpMethod.PUT, "/mesas/{id}/ocupar").hasAnyRole("ADMIN",
    // "MESERO")
    // .requestMatchers(HttpMethod.PUT, "/mesas/{id}/liberar").hasAnyRole("ADMIN",
    // "MESERO")

    // // Endpoints específicos de mesero
    // .requestMatchers("/api/mesero/**").hasAnyRole("ADMIN", "MESERO")

    // // ========================================
    // // ROL: CAJERO - Procesamiento de Pagos
    // // IMPORTANTE: CAJERO necesita ver pedidos para procesar pagos
    // // NOTA: GET /pedidos ya está permitido arriba en la sección compartida
    // // ========================================
    // // Procesar pagos (crear pago)
    // .requestMatchers(HttpMethod.POST, "/pagos").hasAnyRole("ADMIN", "CAJERO")

    // // Ver todos los pagos
    // .requestMatchers(HttpMethod.GET, "/pagos").hasAnyRole("ADMIN", "CAJERO")
    // .requestMatchers(HttpMethod.GET, "/pagos/{id}").hasAnyRole("ADMIN", "CAJERO")
    // .requestMatchers(HttpMethod.GET, "/pagos/pedido/**").hasAnyRole("ADMIN",
    // "CAJERO")

    // // Ver facturas
    // .requestMatchers(HttpMethod.GET, "/facturas/**").hasAnyRole("ADMIN",
    // "CAJERO")

    // // Endpoints específicos de cajero
    // .requestMatchers("/api/cajero/**").hasAnyRole("ADMIN", "CAJERO")

    // // ========================================
    // // ROL: ADMIN - Control Total del Sistema
    // // IMPORTANTE: Las reglas generales van AL FINAL
    // // ========================================
    // // Gestión de Empleados (CRUD completo) - REGLA GENERAL AL FINAL
    // // Esta regla debe ir DESPUÉS de las específicas como
    // /empleados/mesas/**/ocupar
    // .requestMatchers("/empleados/**").hasRole("ADMIN")

    // // Gestión de Roles (CRUD completo)
    // .requestMatchers("/roles/**").hasRole("ADMIN")

    // // Gestión de Productos (CRUD completo) - POST, PUT, DELETE solo ADMIN
    // .requestMatchers(HttpMethod.POST, "/productos").hasRole("ADMIN")
    // .requestMatchers(HttpMethod.POST, "/productos/**").hasRole("ADMIN")
    // .requestMatchers(HttpMethod.PUT, "/productos/**").hasRole("ADMIN")
    // .requestMatchers(HttpMethod.DELETE, "/productos/**").hasRole("ADMIN")

    // // Gestión de Categorías (CRUD completo) - POST, PUT, DELETE solo ADMIN
    // .requestMatchers(HttpMethod.POST, "/categorias").hasRole("ADMIN")
    // .requestMatchers(HttpMethod.POST, "/categorias/**").hasRole("ADMIN")
    // .requestMatchers(HttpMethod.PUT, "/categorias/**").hasRole("ADMIN")
    // .requestMatchers(HttpMethod.DELETE, "/categorias/**").hasRole("ADMIN")

    // // Gestión de Clientes - CREAR, EDITAR, ELIMINAR solo ADMIN
    // .requestMatchers(HttpMethod.POST, "/clientes").hasRole("ADMIN")
    // .requestMatchers(HttpMethod.POST, "/clientes/**").hasRole("ADMIN")
    // .requestMatchers(HttpMethod.PUT, "/clientes/**").hasRole("ADMIN")
    // .requestMatchers(HttpMethod.DELETE, "/clientes/**").hasRole("ADMIN")

    // // Gestión de Mesas - CREAR, ELIMINAR solo ADMIN
    // // PUT ya está cubierto arriba para MESERO
    // .requestMatchers(HttpMethod.POST, "/mesas").hasRole("ADMIN")
    // .requestMatchers(HttpMethod.POST, "/mesas/**").hasRole("ADMIN")
    // .requestMatchers(HttpMethod.DELETE, "/mesas/**").hasRole("ADMIN")

    // // Eliminar pedidos y reservas (solo ADMIN)
    // .requestMatchers(HttpMethod.DELETE, "/pedidos/**").hasRole("ADMIN")
    // .requestMatchers(HttpMethod.DELETE, "/reservas/**").hasRole("ADMIN")
    // .requestMatchers(HttpMethod.DELETE, "/detalles-pedido/**").hasRole("ADMIN")

    // // Reportes y estadísticas
    // .requestMatchers("/reportes/**").hasRole("ADMIN")
    // .requestMatchers(HttpMethod.GET, "/dashboard").hasRole("ADMIN")

    // // Notificaciones (solo ADMIN)
    // .requestMatchers("/notificaciones/**").hasRole("ADMIN")

    // // ========================================
    // // TODOS LOS DEMÁS ENDPOINTS REQUIEREN AUTENTICACIÓN
    // // ========================================
    // .anyRequest().authenticated())
    // .authenticationProvider(authenticationProvider())
    // .sessionManagement(sess ->
    // sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    // http.addFilterBefore(jwtRequestFilter,
    // UsernamePasswordAuthenticationFilter.class);

    // return http.build();
    // }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // ========================================
                        // MVP: TODOS LOS ENDPOINTS SON PÚBLICOS
                        // Los permisos se manejan desde el frontend
                        // ========================================
                        .anyRequest().permitAll())
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