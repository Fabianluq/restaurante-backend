package com.example.restaurApp.service;

import com.example.restaurApp.entity.Empleado;
import com.example.restaurApp.entity.PasswordResetToken;
import com.example.restaurApp.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository tokenRepository;

    @Autowired
    public PasswordResetTokenService(PasswordResetTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public String generarToken(Empleado empleado) {
        // Invalidar tokens anteriores del empleado
        tokenRepository.deleteByEmpleadoId(empleado.getId());

        // Generar token único (UUID)
        String token = UUID.randomUUID().toString();

        // Crear entidad de token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmpleado(empleado);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1)); // Expira en 1 hora
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);

        return token;
    }

    public Optional<PasswordResetToken> validarToken(String token) {
        Optional<PasswordResetToken> resetTokenOpt = tokenRepository.findByToken(token);
        
        if (resetTokenOpt.isEmpty()) {
            return Optional.empty();
        }

        PasswordResetToken resetToken = resetTokenOpt.get();

        // Verificar que no esté usado y que no haya expirado
        if (resetToken.isUsed() || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return Optional.empty();
        }

        return Optional.of(resetToken);
    }

    @Transactional
    public void marcarComoUsado(String token) {
        Optional<PasswordResetToken> resetTokenOpt = tokenRepository.findByToken(token);
        if (resetTokenOpt.isPresent()) {
            PasswordResetToken resetToken = resetTokenOpt.get();
            resetToken.setUsed(true);
            tokenRepository.save(resetToken);
        }
    }

    @Transactional
    public void limpiarTokensExpirados() {
        // Limpiar tokens expirados (puede ejecutarse en un scheduled task)
        tokenRepository.findAll().forEach(token -> {
            if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
                tokenRepository.delete(token);
            }
        });
    }
}

