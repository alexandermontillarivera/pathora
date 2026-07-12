package io.pathora.catalog.shared.email;

import io.pathora.catalog.entities.User;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.HtmlUtils;

@Service
public class EmailService {
  private static final Logger log = LoggerFactory.getLogger(EmailService.class);
  private final RestClient client = RestClient.create("https://api.resend.com");
  private final String apiKey;
  private final String from;
  private final String frontendUrl;

  public EmailService(
      @Value("${app.resend.api-key:}") String apiKey,
      @Value("${app.resend.from:Pathora <onboarding@resend.dev>}") String from,
      @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl) {
    this.apiKey = apiKey;
    this.from = from;
    this.frontendUrl = frontendUrl;
  }

  public void sendPasswordReset(User user, String token) {
    if (apiKey.isBlank()) {
      log.info(
          "Password reset requested for {}. Development link: {}/reset-password?token={}",
          user.getEmail(),
          frontendUrl,
          token);
      return;
    }
    var link = frontendUrl + "/reset-password?token=" + token;
    send(user.getEmail(), "Recupera tu acceso a Pathora", resetTemplate(user, link));
  }

  private void send(String recipient, String subject, String html) {
    try {
      client
          .post()
          .uri("/emails")
          .contentType(MediaType.APPLICATION_JSON)
          .header("Authorization", "Bearer " + apiKey)
          .body(
              Map.of(
                  "from", from, "to", new String[] {recipient}, "subject", subject, "html", html))
          .retrieve()
          .toBodilessEntity();
    } catch (RuntimeException ex) {
      log.warn("Email could not be sent: {}", ex.getMessage());
    }
  }

  private String resetTemplate(User user, String link) {
    var name = HtmlUtils.htmlEscape(user.getFirstName());
    var safeLink = HtmlUtils.htmlEscape(link);
    return """
      <!doctype html><html><body style="margin:0;background:#f4f1ea;font-family:Arial,sans-serif;color:#171714">
      <table role="presentation" width="100%%"><tr><td align="center" style="padding:48px 20px"><table role="presentation" width="100%%" style="max-width:560px;background:#fff;border:1px solid #dedbd3"><tr><td style="padding:42px">
      <div style="font-size:13px;letter-spacing:3px;text-transform:uppercase;color:#747168">Pathora · Seguridad</div>
      <h1 style="font-size:38px;line-height:1.08;font-weight:500;margin:38px 0 20px">Volvamos a tu camino, %s.</h1>
      <p style="font-size:16px;line-height:1.7;color:#55524c">Recibimos una solicitud para cambiar tu contraseña. Este enlace funciona durante 30 minutos y solo puede utilizarse una vez.</p>
      <a href="%s" style="display:inline-block;margin:24px 0;padding:14px 20px;background:#171714;color:#fff;text-decoration:none;border-radius:10px">Crear nueva contraseña</a>
      <p style="font-size:13px;line-height:1.6;color:#77736b">Si no hiciste esta solicitud, puedes ignorar este correo.</p>
      </td></tr></table></td></tr></table></body></html>
      """
        .formatted(name, safeLink);
  }

  public void sendWelcome(User user) {
    if (apiKey.isBlank()) {
      log.info("RESEND_API_KEY is not configured; welcome email skipped.");
      return;
    }
    try {
      client
          .post()
          .uri("/emails")
          .contentType(MediaType.APPLICATION_JSON)
          .header("Authorization", "Bearer " + apiKey)
          .body(
              Map.of(
                  "from",
                  from,
                  "to",
                  new String[] {user.getEmail()},
                  "subject",
                  "Tu próxima ruta comienza en Pathora",
                  "html",
                  template(user)))
          .retrieve()
          .toBodilessEntity();
    } catch (RuntimeException ex) {
      log.warn("Welcome email could not be sent: {}", ex.getMessage());
    }
  }

  private String template(User user) {
    var name = HtmlUtils.htmlEscape(user.getFirstName());
    return """
      <!doctype html><html><body style="margin:0;background:#f4f1ea;font-family:Arial,sans-serif;color:#171714">
      <table role="presentation" width="100%%" cellspacing="0" cellpadding="0"><tr><td align="center" style="padding:48px 20px">
      <table role="presentation" width="100%%" style="max-width:560px;background:#fff;border:1px solid #dedbd3"><tr><td style="padding:42px">
      <div style="font-size:13px;letter-spacing:3px;text-transform:uppercase;color:#747168">Pathora</div>
      <h1 style="font-size:38px;line-height:1.08;font-weight:500;margin:42px 0 20px">Bienvenido, %s.</h1>
      <p style="font-size:17px;line-height:1.7;color:#55524c;margin:0 0 30px">Tu espacio para descubrir carreras, explorar pensums y compartir experiencias con una comunidad que también está eligiendo su camino.</p>
      <div style="height:4px;width:72px;background:#d6ff3f;margin:38px 0"></div>
      <p style="font-size:14px;line-height:1.6;color:#77736b;margin:0">Explora con curiosidad. Decide con claridad.</p>
      </td></tr></table></td></tr></table></body></html>
      """
        .formatted(name);
  }
}
