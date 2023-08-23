package com.techplanet.expertbridge.fusion.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author dofoleta
 */
@Component
public class JwtUtil implements Serializable {

    @Autowired
    AesService aesService;

    @Value("${aes.encryption.key}")
    private String omnixEncryptionKey;

    @Value("${jwt.signing.key}")
    private String jwtSigningKey;

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSigningKey).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public String getChannelFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSigningKey).parseClaimsJws(token).getBody();
        return (String) claims.get("Channel");
    }

    public String getIPFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSigningKey).parseClaimsJws(token).getBody();
        return (String) claims.get("IP");
    }

    public String getEncryptionKeyFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSigningKey).parseClaimsJws(token).getBody();
        return (String) claims.get("auth");
    }

    public boolean userHasRole(String token, String role) {
        Claims claims = Jwts.parser().setSigningKey(jwtSigningKey).parseClaimsJws(token).getBody();
        String roles = (String) claims.get("roles").toString();
        return roles.contains(role);
    }

}
