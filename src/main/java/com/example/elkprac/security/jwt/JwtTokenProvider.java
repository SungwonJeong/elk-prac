package com.example.elkprac.security.jwt;

import com.example.elkprac.common.message.ErrorMessage;
import com.example.elkprac.security.exception.JwtValidationException;
import com.example.elkprac.security.util.CustomUserDetailsService;
import com.example.elkprac.user.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
@PropertySource("classpath:application-secret.yml")
public class JwtTokenProvider {

    public static final String ACCESS_TOKEN_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "RefreshToken";
    private static final String BEARER_PREFIX = "Bearer ";
    public static final String ROLE_KEY = "auth";

    private final CustomUserDetailsService customUserDetailsService;

    @Value("${jwt.secret.key.access}")
    private String accessTokenSecretKey;
    @Value("${jwt.secret.key.refresh}")
    private String refreshTokenSecretKey;

    @Value("${jwt.access.validity.in.seconds}")
    private long accessValidityInMs;
    @org.springframework.beans.factory.annotation.Value("${jwt.refresh.validity.in.seconds}")
    private long refreshValidityInMs;

    private Key accessTokenKey;
    private Key refreshTokenKey;

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] accessTokenBytes = Base64.getDecoder().decode(accessTokenSecretKey);
        accessTokenKey = Keys.hmacShaKeyFor(accessTokenBytes);

        byte[] refreshTokenBytes = Base64.getDecoder().decode(refreshTokenSecretKey);
        refreshTokenKey = Keys.hmacShaKeyFor(refreshTokenBytes);
    }

    public String resolveToken(HttpServletRequest request, String authorization) {
        String bearerToken = request.getHeader(authorization);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String createAccessToken(String username, UserRole role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + (accessValidityInMs * 1000L));
        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username)
                        .setIssuedAt(now)
                        .setExpiration(validity)
                        .signWith(accessTokenKey, signatureAlgorithm)
                        .compact();
    }

    public String createRefreshToken(String username, UserRole role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + (refreshValidityInMs * 1000L));
        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username)
                        .claim(ROLE_KEY, role)
                        .setIssuedAt(now)
                        .setExpiration(validity)
                        .signWith(refreshTokenKey, signatureAlgorithm)
                        .compact();
    }

    public boolean validateToken(String token, boolean isAccess) {
        Key key = isAccess ? accessTokenKey : refreshTokenKey;
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            handleSignatureException(isAccess);
        } catch (ExpiredJwtException e) {
            handleExpiredException(isAccess);
        } catch (UnsupportedJwtException e) {
            handleUnsupportedException(isAccess);
        } catch (MalformedJwtException e) {
            handleMalformedException(isAccess);
        } catch (IllegalArgumentException e) {
            handleIllegalArgumentException(isAccess);
        }
        return false;
    }

    private void handleSignatureException(boolean isAccess) {
        log.debug("Invalid JWT signature.");
        throw new JwtValidationException(isAccess ? ErrorMessage.INVALID_ACCESS_SIGNATURE : ErrorMessage.INVALID_REFRESH_SIGNATURE);
    }

    private void handleExpiredException(boolean isAccess) {
        log.debug("Expired JWT token.");
        throw new JwtValidationException(isAccess ? ErrorMessage.EXPIRED_ACCESS_TOKEN : ErrorMessage.EXPIRED_REFRESH_TOKEN);
    }

    private void handleUnsupportedException(boolean isAccess) {
        log.debug("Unsupported JWT token.");
        throw new JwtValidationException(isAccess ? ErrorMessage.UNSUPPORTED_ACCESS_TOKEN : ErrorMessage.UNSUPPORTED_REFRESH_TOKEN);
    }

    private void handleMalformedException(boolean isAccess) {
        log.debug("Invalid JWT token.");
        throw new JwtValidationException(isAccess ? ErrorMessage.INVALID_ACCESS_FORMAT : ErrorMessage.INVALID_REFRESH_FORMAT);
    }

    private void handleIllegalArgumentException(boolean isAccess) {
        log.debug("JWT token compact of handler are invalid.");
        throw new JwtValidationException(isAccess ? ErrorMessage.INVALID_ACCESS_FORMAT : ErrorMessage.INVALID_REFRESH_FORMAT);
    }

    public Claims getCustomerInfoFromAccessToken(String token) {
        return Jwts.parserBuilder().setSigningKey(accessTokenKey).build().parseClaimsJws(token).getBody();
    }

    public long getExpiryDuration(String bearerToken, boolean isAccess) {
        Key key = isAccess ? accessTokenKey : refreshTokenKey;
        String token = bearerToken.substring(7);
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        Date expiration = claims.getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    public Authentication createAuthentication(String username) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}

