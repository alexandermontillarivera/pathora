package io.pathora.catalog.shared.security;

import io.pathora.catalog.entities.User;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final JwtEncoder encoder;
  private final Duration expiration;

  public JwtService(JwtEncoder encoder, @Value("${app.jwt.expiration-minutes}") long minutes) {
    this.encoder = encoder;
    this.expiration = Duration.ofMinutes(minutes);
  }

  public Token issue(User user) {
    var now = Instant.now();
    var expiresAt = now.plus(expiration);
    var claims =
        JwtClaimsSet.builder()
            .issuer("pathora")
            .issuedAt(now)
            .expiresAt(expiresAt)
            .subject(user.getId().toString())
            .claim("email", user.getEmail())
            .claim("name", user.getFirstName())
            .build();
    var header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
    return new Token(
        encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue(), expiresAt);
  }

  public record Token(String value, Instant expiresAt) {}
}
