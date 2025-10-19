package com.example.restaurApp.controllers;

import com.example.restaurApp.config.TestSecurityConfig;
import com.example.restaurApp.service.NotificacionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(controllers = NotificacionController.class)
@Import(TestSecurityConfig.class)
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificacionService notificacionService;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void enviarNotificacionPersonalizada_adminPermitido() throws Exception {
        Mockito.doNothing().when(notificacionService).enviarNotificacionPersonalizada(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        mockMvc.perform(post("/notificaciones/enviar")
                        .param("destinatario", "mail@dom.com")
                        .param("asunto", "test")
                        .param("mensaje", "hola")
                        .with(csrf())
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }
}


