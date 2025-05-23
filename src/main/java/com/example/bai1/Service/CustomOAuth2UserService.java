package com.example.bai1.Service;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.bai1.Model.Account;
import com.example.bai1.Model.User;
import com.example.bai1.Model.Account.Role;
import com.example.bai1.Model.Account.AuthProvider;
import com.example.bai1.Repository.Doctor.AccountRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        Account account = accountRepository.findByEmail(email);

        if (account == null) {
            // New user, create an account
            account = new Account();
            account.setEmail(email);
            // You might want to extract name parts if available from oAuth2User
            // e.g., String name = oAuth2User.getAttribute("name");
            account.setUsername(email); // Or generate a unique username, or use name
            account.setPassword(""); // No password for OAuth2 users initially, or generate one
            
            // Set a default role for new OAuth2 users.
            // If you want specific users (e.g., your email) to be ADMIN by default upon first OAuth2 login,
            // you can add that logic here.
            // For now, let's default to PATIENT for new OAuth2 sign-ups.
            account.setRole(Role.PATIENT); 
            
            // Assuming you have an AuthProvider enum in your Account model
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            if ("google".equalsIgnoreCase(registrationId)) {
                account.setProvider(AuthProvider.GOOGLE);
            } else if ("facebook".equalsIgnoreCase(registrationId)) {
                // account.setProvider(AuthProvider.FACEBOOK); // If you add Facebook login
            } else {
                account.setProvider(AuthProvider.LOCAL); // Or a generic OAUTH_PROVIDER
            }
            
            account = accountRepository.save(account);
        } else {
            // Existing user, update details if necessary (e.g., provider if they re-link)
            // For example, update AuthProvider if it was null or different
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            AuthProvider currentProvider = null;
            if ("google".equalsIgnoreCase(registrationId)) {
                currentProvider = AuthProvider.GOOGLE;
            } // Add other providers if any

            if (account.getProvider() == null || account.getProvider() == AuthProvider.LOCAL) { // Only update if not already set or was local
                account.setProvider(currentProvider);
                account = accountRepository.save(account);
            }
            // Potentially update other attributes like name, picture URL if they change in OAuth2 provider
        }

        // Now, build the OAuth2User with authorities based on the role in the DB Account entity
        return buildOAuthUser(account, oAuth2User);
    }

    private OAuth2User buildOAuthUser(Account account, OAuth2User oAuth2User) {
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("appUserId", account.getAccountId());
        attributes.put("role", account.getRole().name()); // Add persisted role to attributes

        // Authority should be ROLE_ADMIN, ROLE_PATIENT, etc.
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + account.getRole().name().toUpperCase());

        return new DefaultOAuth2User(
                Collections.singleton(authority),
                attributes,
                "email" // nameAttributeKey: the attribute in oAuth2User.getAttributes() that represents the user's name/ID
        );
    }
}
