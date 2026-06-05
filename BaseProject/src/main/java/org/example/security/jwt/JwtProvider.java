package org.example.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.example.exception.CustomException;
import org.example.model.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component

public class JwtProvider {
    @Value("${jwt.secret}")
    private String secret;


    public String generateAccessToken(User  user) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        Date exp = new Date(new Date().getTime() + 300_000L);

       return  Jwts
                .builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().getRoleName())
                .expiration(exp)
                .issuedAt(new Date())
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(User  user) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        Date exp = new Date(new Date().getTime() + (86_400_000L * 30));

        return  Jwts
                .builder()
                .subject(user.getUsername())
                .expiration(exp)
                .issuedAt(new Date())
                .signWith(key)
                .compact();
    }

    public boolean validateJwtToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload() != null;
        }catch (ExpiredJwtException ex){
            throw new CustomException("Token đã hết hạn");
        }catch (SignatureException ex){
            throw new CustomException("Token không hợp lệ");
        }catch (MalformedJwtException ex){
            throw new CustomException("Token không đúng định dạng");
        }
    }

    public String getUsernameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
         return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public String getRoleFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("role").toString();
    }


}
