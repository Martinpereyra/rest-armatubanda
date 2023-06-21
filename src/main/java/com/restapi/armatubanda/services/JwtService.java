package com.restapi.armatubanda.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "OcJBhdfIvJRSpwUCfPoO1RSlM3Sa+EsmZZ8oOnrPqtDz4uV/10Ysv1+eARAHd1b4Chb0yJfZG32+xknxOPjIB8SFvFdkamgf23WOjBP2gJnOf76yrt6wTwwS3B7EwG+v5J0dPc9MHKMNIXp0pGo1x73SzS9v2MaPDDxAtivEygPSW3fnWj3TNVVzJB6gWmaFaxXLdOSX4oZVOg2eyc6yQBAxFGwdzVdLTfE8iRy5hxXjmKjm4qJSWVFEym6sVkzdW47xR98CSvm7BVzCOjh8Rca+nToKkLP5wbPi6SFv4PFFLFkG5kcbIDypXFE5vmt8dMVm84HZI6ulaVesf7gGL1PFYLvRUbzdVQM3Gb4mplU=";


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extrae un unico claim del jwt token aplicando una funcion pasada como parametro.
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    // Extrae todos los claims del jwt token.
    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    // Decodifica y retorna la signIn key
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Toma como parametro un map de string-object que contiene los claims y los detalles del usuario
    // que queremos agregar al generar el jwt token
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) // seteo la fecha de creacion
                .setExpiration(new Date(System.currentTimeMillis()+ 1000 * 60 * 24)) // seteo la fecha de expiracion
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // firmo el token
                .compact(); // metodo que genera el token


    }

    // Genera un token sin claims
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }


}
