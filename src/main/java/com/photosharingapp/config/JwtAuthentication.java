package com.photosharingapp.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.photosharingapp.model.AppUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JwtAuthentication extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    public JwtAuthentication (AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
    throws AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
        AppUser appUser;
        try {
            appUser = objectMapper.readValue(request.getInputStream(), AppUser.class);
        } catch (Exception e) {
            throw new RuntimeException("Unable to convert user from JSON to java Object");
        }
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
            User user = (User) authentication.getPrincipal();
            List<String> roles = new ArrayList<>();
            user.getAuthorities()
                    .forEach(authority -> {
                roles.add(authority.getAuthority());
            });
            String jwtToken = JWT.create()
                    .withIssuer(request.getRequestURI())
                    .withSubject(user.getUsername())
                    .withArrayClaim("roles", roles.stream().toArray(String[]::new))
                    .withExpiresAt(new Date(System.currentTimeMillis()+SecurityConstants.EXPIRATION_TIME))
                    .sign(Algorithm.HMAC256(SecurityConstants.SECRET));
            response.addHeader(SecurityConstants.HEADER_TYPE, SecurityConstants.TOKEN_PREFIX+jwtToken);
    }

}
