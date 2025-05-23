package com.example.bai1.Config;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        // TODO Auto-generated method stub
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String redirectURL = request.getContextPath();

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

        response.sendRedirect(redirectURL);
    }

}
