// package com.example.bai1.Config;

// import org.springframework.beans.factory.annotation.Autowired;
// import
// org.springframework.security.oauth2.core.OAuth2AuthenticationException;
// import
// org.springframework.security.web.authentication.AuthenticationFailureHandler;
// import org.springframework.stereotype.Component;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import com.example.bai1.Model.Account;
// import com.example.bai1.Model.Account.Role;
// import com.example.bai1.Repository.Doctor.AccountRepository;

// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// @Component
// public class OAuth2AuthenticationFailureHandler implements
// AuthenticationFailureHandlerr {

// @Autowired
// private AccountRepository accountRepository;

// @Override
// public void onAuthenticationFailure(HttpServletRequest request,
// HttpServletResponse response,
// AuthenticationException exception) throws IOException, ServletException {
// if (exception instanceof OAuth2AuthenticationException) {
// OAuth2AuthenticationException oauth2Exception =
// (OAuth2AuthenticationException) exception;
// String email = oauth2Exception.getOAuth2ErrorCode(); // Hoặc lấy từ exception
// nào đó

// Account existingAccount = accountRepository.findByEmail(email);
// if (existingAccount != null && existingAccount.getProvider() == null
// && existingAccount.getRole() == Role.PATIENT) {
// // Redirect đến /confirm-link để liên kết
// String redirectUrl = "/confirm-link?email=" + email;
// response.sendRedirect(redirectUrl);
// } else {
// // Nếu không phải PATIENT hoặc đã liên kết rồi, redirect về trang login
// response.sendRedirect("/login?error");
// }
// } else {
// // Nếu lỗi không phải OAuth2, bạn có thể xử lý lỗi khác
// response.sendRedirect("/login?error");
// }
// }
// }