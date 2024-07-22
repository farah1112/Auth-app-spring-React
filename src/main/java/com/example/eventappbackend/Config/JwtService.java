package com.example.eventappbackend.Config;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.secret-Key}")
    private String secretKey;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public <T> T  extractClaim(String token, Function<Claims,T> claimResolver) {
        final Claims claims = extracAllClaims(token);
        return claimResolver.apply(claims);
    }
    public Claims extracAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKet())
                .build()
                .parseClaimsJws(token)
                .getBody();


    }


    public String generateToken(UserDetails userDetails){
       return generateToken(new  HashMap<>(),userDetails);
   }
    public  <V, K> String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return buildToken(claims, userDetails,jwtExpiration);
    }
    private String buildToken(Map<String, Object> extraClaims,
                              UserDetails userDetails,
                              long jwtExpiration)
    {
        var authority = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+jwtExpiration))
                .claim("authority", authority)
                .signWith(getSignInKet())
                .compact();
    }
    public boolean IsTokenValid(String token , UserDetails userDetails)
    {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()))&& !IsTokenExpired(token);

    }
    public boolean IsTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    private Key getSignInKet() {
        byte [] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
