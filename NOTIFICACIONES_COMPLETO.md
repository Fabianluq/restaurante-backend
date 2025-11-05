# Sistema de Notificaciones - Email, SMS y WhatsApp

## ✅ Funcionalidades Implementadas

### Backend (Spring Boot)

1. **Servicio de Notificaciones (`NotificacionService`)**
   - ✅ Envío de emails HTML con plantillas Thymeleaf
   - ✅ Envío de SMS usando Twilio
   - ✅ Envío de mensajes WhatsApp usando Twilio
   - ✅ Configuración por variables de entorno
   - ✅ Manejo de errores sin afectar la operación principal

2. **Plantillas de Email HTML**
   - ✅ `confirmacion-reserva.html` - Email de confirmación con diseño profesional
   - ✅ `cancelacion-reserva.html` - Email de cancelación con diseño profesional
   - ✅ Diseño responsive y moderno
   - ✅ Colores y branding del restaurante

3. **Integración con Reservas**
   - ✅ Notificaciones automáticas al confirmar reserva
   - ✅ Notificaciones automáticas al cancelar reserva
   - ✅ Manejo de errores sin interrumpir el flujo

## 📦 Dependencias Agregadas

### pom.xml
```xml
<!-- Twilio para SMS y WhatsApp -->
<dependency>
    <groupId>com.twilio.sdk</groupId>
    <artifactId>twilio</artifactId>
    <version>9.16.0</version>
</dependency>
```

## 🔧 Configuración

### application.properties

```properties
# Configuración de Notificaciones
app.notificaciones.email.habilitado=true
app.notificaciones.email.remitente=noreply@restaurante.com
app.notificaciones.sms.habilitado=false
app.notificaciones.whatsapp.habilitado=false

# Configuración de Twilio (SMS y WhatsApp)
app.notificaciones.twilio.account-sid=
app.notificaciones.twilio.auth-token=
app.notificaciones.twilio.numero-sms=
app.notificaciones.twilio.numero-whatsapp=

# Nombre del restaurante
app.nombre-restaurante=RestaurApp
```

## 📧 Configuración de Email (Gmail)

1. **Crear Contraseña de Aplicación en Gmail:**
   - Ve a: https://myaccount.google.com/apppasswords
   - Genera una contraseña para "Mail"
   - Usa esa contraseña en `spring.mail.password`

2. **Actualizar application.properties:**
```properties
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-app-password-de-16-digitos
app.notificaciones.email.remitente=tu-email@gmail.com
```

## 📱 Configuración de Twilio (SMS/WhatsApp)

1. **Crear cuenta en Twilio:**
   - Regístrate en: https://www.twilio.com/
   - Obtén tu Account SID y Auth Token desde el dashboard

2. **Para SMS:**
   - Obtén un número de teléfono desde Twilio Console
   - Configura en `application.properties`:
   ```properties
   app.notificaciones.sms.habilitado=true
   app.notificaciones.twilio.account-sid=tu-account-sid
   app.notificaciones.twilio.auth-token=tu-auth-token
   app.notificaciones.twilio.numero-sms=+1234567890
   ```

3. **Para WhatsApp:**
   - Activa WhatsApp Sandbox en Twilio Console
   - Configura en `application.properties`:
   ```properties
   app.notificaciones.whatsapp.habilitado=true
   app.notificaciones.twilio.numero-whatsapp=whatsapp:+14155238886
   ```

## 🎨 Plantillas de Email

Las plantillas están ubicadas en:
- `src/main/resources/templates/email/confirmacion-reserva.html`
- `src/main/resources/templates/email/cancelacion-reserva.html`

### Características:
- ✅ Diseño responsive (móvil y desktop)
- ✅ Colores del restaurante (naranja #FF7A00)
- ✅ Iconos y mensajes claros
- ✅ Información completa de la reserva
- ✅ Botones de acción (Ver mis reservas, Hacer nueva reserva)

## 🚀 Uso

### Automático
Las notificaciones se envían automáticamente cuando:
- Una reserva es confirmada (`confirmarReservaPublica`)
- Una reserva es cancelada (`cancelarReserva`)

### Manual (si es necesario)
```java
@Autowired
private NotificacionService notificacionService;

// Enviar notificación de confirmación
notificacionService.enviarConfirmacionReserva(reserva);

// Enviar notificación de cancelación
notificacionService.enviarCancelacionReserva(reserva);
```

## 🔒 Seguridad

- Las credenciales se manejan mediante variables de entorno
- Manejo de errores sin exponer información sensible
- Validación de configuración antes de enviar notificaciones

## 📝 Notas

1. **Email:** Ya está configurado y funcionando (solo necesitas actualizar credenciales)
2. **SMS/WhatsApp:** Requieren cuenta de Twilio y configuración adicional
3. **Formato de teléfono:** El servicio formatea automáticamente números colombianos (+57)
4. **Logs:** Todos los errores se registran en logs sin afectar la operación

## 🐛 Troubleshooting

### Email no se envía:
- Verifica credenciales de Gmail
- Usa contraseña de aplicación, no tu contraseña normal
- Verifica que `app.notificaciones.email.habilitado=true`

### SMS/WhatsApp no funciona:
- Verifica que Twilio esté inicializado correctamente
- Revisa los logs para errores de autenticación
- Asegúrate de tener crédito en tu cuenta de Twilio
- Para WhatsApp, verifica que el número esté en formato correcto

## 📄 Archivos Creados/Modificados

**Backend:**
- `src/main/java/com/example/restaurApp/service/NotificacionService.java` (NUEVO)
- `src/main/java/com/example/restaurApp/service/ReservaService.java` (actualizado)
- `src/main/resources/templates/email/confirmacion-reserva.html` (NUEVO)
- `src/main/resources/templates/email/cancelacion-reserva.html` (NUEVO)
- `src/main/resources/application.properties` (actualizado)
- `pom.xml` (actualizado con dependencia Twilio)

