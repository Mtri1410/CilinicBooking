package com.example.bai1.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.HeaderWriterFilter;

import com.example.bai1.Service.CustomOAuth2UserService;
import com.example.bai1.Service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
        @Autowired
        private CustomUserDetailsService customuserdetailservice;
        @Autowired
        private CustomSuccessHandler customSuccessHandler;
        @Autowired
        private CustomOAuth2UserService customOAuth2UserService;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                // System.out.println(">> Cấu hình Spring Security - DEV MODE: ALL SECURITY
                // DISABLED");
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/paymentv2/**").permitAll()
                                                .requestMatchers("/doctor/**").hasRole("DOCTOR")
                                                .requestMatchers("/user/home", "/home", "/", "/doctors-menu",
                                                                "/doctors-menu/**", "/doctors/**", "/EmailOTP")
                                                .permitAll()
                                                .requestMatchers("/reset-password").permitAll()
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers("/user/**").hasRole("PATIENT")
                                                .requestMatchers("/js/**", "/static/**", "/images/**", "/",
                                                                "/SignUp",
                                                                "/SignIn",
                                                                "/css/**", "/favicon.ico",
                                                                "/oauth2/**", "/login/oauth2/**",
                                                                "/about", "/contact", "/faq", "/terms",
                                                                "/error", "/error/**")
                                                .permitAll()
                                                .requestMatchers("/api/cart/checkout", "/api/cart/checkout-vnpay").authenticated()
                                                .requestMatchers("/api/doctor/**").permitAll()
                                                .anyRequest().authenticated())
                                .exceptionHandling(exception -> exception
                                                .accessDeniedHandler(new CustomAccessDeniedHandler()))
                                .csrf(csrf -> csrf.disable())
                                .oauth2Login(oauth -> oauth
                                                .loginPage("/SignIn")
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(customOAuth2UserService))
                                                .successHandler(customSuccessHandler)
                                                .failureUrl("/SignIn?error=true"))
                                .formLogin(form -> form
                                                .loginPage("/SignIn")
                                                .loginProcessingUrl("/SignIn")
                                                .usernameParameter("username")
                                                .passwordParameter("password")
                                                .successHandler(customSuccessHandler)
                                                .failureHandler((request, response, exception) -> {
                                                        System.err.println(
                                                                        ">> Lỗi đăng nhập: " + exception.getMessage());
                                                        response.sendRedirect("/SignIn?error=true");
                                                })
                                                .permitAll())
                                .rememberMe(rememberMe -> rememberMe
                                                .key("uniqueAndSecret")
                                                .tokenValiditySeconds(7 * 24 * 60 * 60))
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/SignIn?logout=true")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID", "remember-me")
                                                .permitAll());

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
                return configuration.getAuthenticationManager();
        }

}
