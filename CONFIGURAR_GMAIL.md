# ============================================
# CONFIGURACIÓN DE GMAIL - PASO A PASO
# ============================================

## PASO 1: Crear Contraseña de Aplicación en Gmail

1. Ve a: https://myaccount.google.com/apppasswords
   - Si no ves esta opción, primero activa la verificación en 2 pasos:
     https://myaccount.google.com/signinoptions/two-step-verification

2. Selecciona "Correo" en "Seleccionar aplicación"
3. Selecciona "Otro (nombre personalizado)" en "Seleccionar dispositivo"
4. Escribe "RestaurApp" y haz clic en "Generar"
5. Copia la contraseña de 16 dígitos que aparece (formato: xxxx xxxx xxxx xxxx)
   - La necesitarás en el PASO 2

## PASO 2: Actualizar application.properties

Abre el archivo: `/Users/ferney/restaurante-backend/src/main/resources/application.properties`

Reemplaza estas líneas:

```properties
# ANTES (líneas 21-22 y 31):
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-password-o-app-password
app.notificaciones.email.remitente=noreply@restaurante.com

# DESPUÉS (reemplaza con tus datos):
spring.mail.username=TU-EMAIL-REAL@gmail.com
spring.mail.password=TU-CONTRASEÑA-DE-16-DIGITOS-SIN-ESPACIOS
app.notificaciones.email.remitente=TU-EMAIL-REAL@gmail.com
```

### Ejemplo:
```properties
spring.mail.username=ferney@example.com
spring.mail.password=abcd efgh ijkl mnop
app.notificaciones.email.remitente=ferney@example.com
```

## PASO 3: Reiniciar el Backend

Después de actualizar `application.properties`:
1. Detén el servidor Spring Boot (Ctrl+C)
2. Reinicia el servidor (mvn spring-boot:run o desde tu IDE)

## PASO 4: Probar que Funciona

### Probar Recuperación de Contraseña:
1. Ve al frontend: http://localhost:4200/recuperar-contrasenia
2. Ingresa el email de un empleado registrado
3. Revisa el correo (incluye spam)
4. Deberías recibir un email HTML con botón para restablecer contraseña

### Probar Reservas:
1. Crea una reserva desde el frontend
2. Confirma la reserva
3. Revisa el correo del cliente
4. Deberías recibir un email HTML de confirmación

## ⚠️ IMPORTANTE:

- NO uses tu contraseña normal de Gmail
- USA la contraseña de aplicación de 16 dígitos
- Si tienes problemas, verifica que la verificación en 2 pasos esté activada
- El email debe ser el mismo en `spring.mail.username` y `app.notificaciones.email.remitente`

## 🐛 Troubleshooting:

**Error: "Username and Password not accepted"**
- Verifica que estés usando la contraseña de aplicación (16 dígitos)
- No uses tu contraseña normal de Gmail

**Error: "Application-specific password required"**
- Activa la verificación en 2 pasos primero
- Luego genera la contraseña de aplicación

**No llegan los emails:**
- Revisa la carpeta de spam
- Verifica los logs del backend para ver errores
- Asegúrate de que el backend se reinició después de cambiar las propiedades

