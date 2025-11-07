package com.io.sdk.dynamic_engine.auth.security;

import com.io.sdk.dynamic_engine.config.SecurityConfig;
// Import exceptions from com.auth0.jwt
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Logger instance
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, CustomUserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            String token = null;

            if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
                token = header.substring(7);
            }

            if (token != null) {
                // We'll let getUsernameFromToken() do the validation.
                // It will throw specific exceptions if the token is
                // expired, malformed, or has an invalid signature.

                String username = tokenProvider.getUsernameFromToken(token);
                var userDetails = userDetailsService.loadUserByUsername(username);

                Set<SimpleGrantedAuthority> authorities = tokenProvider.getRolesFromToken(token)
                        .stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .collect(Collectors.toSet());

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);

            // Use com.auth0.jwt exceptions
        } catch (TokenExpiredException ex) {
            // Handle expired tokens specifically
            logger.warn("JWT token has expired: {}", ex.getMessage());
            SecurityConfig.writeJsonResponse(response, 401, "Unauthorized", "Token has expired");
        } catch (JWTVerificationException ex) {
            // Handle other common JWT validation errors (invalid signature, malformed
            // token, etc.)
            logger.warn("Invalid JWT token: {}", ex.getMessage());
            SecurityConfig.writeJsonResponse(response, 401, "Unauthorized", "Invalid JWT token");
        } catch (Exception ex) {
            // Handle any other unexpected exceptions in auth processing
            logger.error("Authentication error: {}", ex.getMessage(), ex);
            SecurityConfig.writeJsonResponse(response, 401, "Unauthorized", "Authentication error: " + ex.getMessage());
        }
    }
}