package com.senac.Api_workshops.infra.config;

import com.senac.Api_workshops.application.dto.usuario.UsuarioPrincipalDTO;
import com.senac.Api_workshops.application.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();




        if (

                path.equals("/auth/login")
                //||path.startsWith("/")
                || path.equals("/auth/esqueciminhasenha")
                || path.equals("/auth/registrarnovasenha")
                || (path.equals("/usuarios") && method.equals("POST"))
                || path.startsWith("/swagger-resources")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/webjars")
                || path.startsWith("/swagger-ui")) {

            filterChain.doFilter(request, response);
            return;
        }


        try {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.replace("Bearer ", "");

                UsuarioPrincipalDTO usuarioPrincipal = tokenService.validarToken(token);

                var autenticacao = new UsernamePasswordAuthenticationToken(
                        usuarioPrincipal,
                        null,
                        usuarioPrincipal.autorizacao()
                );

                SecurityContextHolder.getContext().setAuthentication(autenticacao);
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token não informado");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token inválido");
        }
    }
}

