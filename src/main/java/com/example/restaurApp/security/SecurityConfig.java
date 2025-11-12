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
                        // ENDPOINTS PÚBLICOS (Sin autenticación)
                        // ========================================
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/recuperar-contrasenia").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/resetear-contrasenia").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/validar-rol").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Reservas públicas (clientes sin autenticación)
                        .requestMatchers(HttpMethod.POST, "/reservas/publica").permitAll()
                        .requestMatchers(HttpMethod.GET, "/reservas/publica/disponibilidad").permitAll()
                        .requestMatchers(HttpMethod.GET, "/reservas/publica/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/reservas/publica/**").permitAll()

                        // Menú público - PRODUCTOS (sin autenticación)
                        .requestMatchers(HttpMethod.GET, "/productos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/productos/**").permitAll()

                        // Menú público - CATEGORÍAS (sin autenticación)
                        .requestMatchers(HttpMethod.GET, "/categorias").permitAll()
                        .requestMatchers(HttpMethod.GET, "/categorias/**").permitAll()

                        // ========================================
                        // ENDPOINTS COMPARTIDOS (Todos los roles autenticados)
                        // ========================================
                        // Ver estados (todos los roles)
                        .requestMatchers(HttpMethod.GET, "/estados/**")
                                .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/estadoPedido/**")
                                .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/estadoMesa/**")
                                .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/estadoProducto/**")
                                .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")

                        // Ver su propio perfil
                        .requestMatchers(HttpMethod.GET, "/auth/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/auth/cambiar-contrasenia").authenticated()
                        .requestMatchers(HttpMethod.GET, "/empleados/{id}").authenticated()

                        // Ver pedidos (cada rol para su trabajo)
                        .requestMatchers(HttpMethod.GET, "/pedidos/{id}/total")
                                .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/pedidos/{id}")
                                .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/pedidos/mesa/**")
                                .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/pedidos/estado/**")
                                .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/pedidos/buscar")
                                .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/pedidos/empleado/**")
                                .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/pedidos")
                                .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")

                        // Ver detalles de pedido
                        .requestMatchers(HttpMethod.GET, "/detalles-pedido")
                                .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/detalles-pedido/{id}")
                                .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/detalles-pedido/pedido/**")
                                .hasAnyRole("ADMIN", "COCINERO", "MESERO", "CAJERO")

                        // ========================================
                        // ROL: COCINERO - Gestión de Cocina
                        // ========================================
                        .requestMatchers(HttpMethod.GET, "/pedidos/cocina").hasAnyRole("ADMIN", "COCINERO")
                        .requestMatchers(HttpMethod.PUT, "/pedidos/{id}/estado/{idEstado}")
                                .hasAnyRole("ADMIN", "COCINERO", "MESERO")
                        .requestMatchers(HttpMethod.PUT, "/detalles-pedido/{id}/estado/{estadoDetalleId}")
                                .hasAnyRole("ADMIN", "COCINERO")

                        // ========================================
                        // ROL: MESERO - Atención al Cliente
                        // ========================================
                        .requestMatchers(HttpMethod.PUT, "/empleados/mesas/**/ocupar").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.POST, "/pedidos").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.PUT, "/pedidos/{id}").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.POST, "/detalles-pedido/{pedidoId}/detalles")
                                .hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.PUT, "/detalles-pedido/{id}").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.GET, "/reservas").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.GET, "/reservas/{id}").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.GET, "/reservas/cliente/**").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.GET, "/reservas/estado/**").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.POST, "/reservas").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.PUT, "/reservas/{id}").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.PUT, "/reservas/{id}/**").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.GET, "/clientes").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.GET, "/clientes/buscar/**").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.GET, "/clientes/{id}").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.GET, "/mesas").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.GET, "/mesas/{id}").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.GET, "/mesas/estado/**").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.PUT, "/mesas/{id}").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.PUT, "/mesas/{id}/ocupar").hasAnyRole("ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.PUT, "/mesas/{id}/liberar").hasAnyRole("ADMIN", "MESERO")

                        // ========================================
                        // ROL: CAJERO - Procesamiento de Pagos
                        // ========================================
                        .requestMatchers(HttpMethod.POST, "/pagos").hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/pagos").hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/pagos/{id}").hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/pagos/pedido/**").hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/facturas/**").hasAnyRole("ADMIN", "CAJERO")

                        // ========================================
                        // ROL: ADMIN - Control Total del Sistema
                        // ========================================
                        .requestMatchers("/empleados/**").hasRole("ADMIN")
                        .requestMatchers("/roles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/productos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/categorias").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/clientes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/clientes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/clientes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/clientes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/mesas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/mesas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/mesas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/pedidos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/reservas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/detalles-pedido/**").hasRole("ADMIN")
                        .requestMatchers("/reportes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/dashboard").hasRole("ADMIN")
                        .requestMatchers("/notificaciones/**").hasRole("ADMIN")

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
