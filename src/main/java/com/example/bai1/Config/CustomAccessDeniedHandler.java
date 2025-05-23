package com.example.bai1.Config;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.core.Authentication;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String redirectURL = request.getContextPath();

        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            for (GrantedAuthority authority : authorities) {
                String role = authority.getAuthority();

                if (role.equals("ROLE_ADMIN")) {
                    redirectURL += "/admin/dashboard";
                    break;
                } else if (role.equals("ROLE_DOCTOR")) {
                    redirectURL += "/doctor";
                    break;
                } else if (role.equals("ROLE_PATIENT")) {
                    redirectURL += "/user/home";
                    break;
                }
            }
        } else {
            redirectURL += "/user/home";
        }

        response.sendRedirect(redirectURL);
    }

}
