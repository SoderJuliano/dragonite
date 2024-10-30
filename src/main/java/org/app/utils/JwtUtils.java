package org.app.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtUtils {
    public static Claims validateToken(String token) {
        return Jwts.parser()
                .setSigningKey("your-secret-key")
                .parseClaimsJws(token)
                .getBody();
    }
}
