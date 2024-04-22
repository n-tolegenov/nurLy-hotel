package com.dev.nurlyhotel.security.jwt;

import com.dev.nurlyhotel.security.user.HotelUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/*
    JWT — это JSON Web Tokens,
    простой и безопасный способ передачи информации между клиентом и сервером с помощью шифрования.

    JWT состоит из трех частей:
    1. заголовок header - содержит информацию о том, как должна вычисляться JWT подпись: header = { "alg": "HS256", "typ": "JWT"}

    2. полезные данные payload - это полезные данные, которые хранятся внутри JWT.
        Эти данные также называют JWT-claims (заявки).
        Существует список стандартных заявок для JWT payload — вот некоторые из них:
            iss (issuer) — определяет приложение, из которого отправляется токен.
            sub (subject) — определяет тему токена.
            exp (expiration time) — время жизни токена.

    3. подпись signature:
    signature = HMAC_SHA256(secret, base64urlEncoding(header) + '.' + base64urlEncoding(payload))
        secret — это ключ для шифровки и проверки подписи. Он генерируется и хранится на сервере и используется для подписи токена при генерации.
*/

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;

    @Value("${auth.token.expirationInMils}")
    private int jwtExpirationTime;

    public String generateJwtTokenForUser(Authentication authentication){
        HotelUserDetails userPrinciple = (HotelUserDetails) authentication.getPrincipal();
        List<String> roles = userPrinciple.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return Jwts.builder().setSubject(userPrinciple.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationTime))
                .signWith(key(), SignatureAlgorithm.HS256).compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserNameFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(token);
            return true;
        }catch (MalformedJwtException e){
            logger.error("Invalid jwt token : {} ", e.getMessage());
        }catch (ExpiredJwtException e){
            logger.error("Expired token : {} ", e.getMessage());
        }catch (UnsupportedJwtException e){
            logger.info("This token is not supported: {} ", e.getMessage());
        }catch (IllegalArgumentException e){
            logger.error("No claims found : {} ", e.getMessage());
        }
        return false;
    }
}
