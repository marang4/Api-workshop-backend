package com.senac.Api_workshops.infra.config;

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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        try{
            if (path.equals("/auth/login") || path.startsWith("swagger-resources")
                    || path.startsWith("/v3/api-docs")
                    || path.startsWith("/")
                    || path.startsWith("/webjars")
                    || path.startsWith("/swagger-ui")) {
                filterChain.doFilter(request, response);
                return;
            }

            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ") ) {
                String token = header.replace("Bearer ", "");

                var usuario = tokenService.validarToken(token);

                var autorizacao = new UsernamePasswordAuthenticationToken(usuario.getEmail(),
                        null,
                        usuario.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(autorizacao);
                filterChain.doFilter(request, response);
            }else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token não informado");
                return;
                //filterChain.doFilter(request, response); colcoar aqui e comentar cosigo acima
            }
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("token nao informado");
            return;
        }

    }
}
