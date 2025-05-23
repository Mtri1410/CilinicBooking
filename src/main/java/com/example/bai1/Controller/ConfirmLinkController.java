package com.example.bai1.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.bai1.Model.Account;
import com.example.bai1.Model.Account.AuthProvider;
import com.example.bai1.Model.Account.Role;
import com.example.bai1.Repository.Doctor.AccountRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ConfirmLinkController {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private HttpSession session;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/confirm-link")
    public String confirmLink(Model model, @RequestParam String email) {
        // Lưu email Google tạm vào session để dùng sau khi xác nhận
        session.setAttribute("google_email", email);

        Account account = accountRepository.findByEmail(email);
        model.addAttribute("email", email);
        model.addAttribute("canLink",
                account != null && account.getProvider() == null && account.getRole() == Role.PATIENT);

        return "confirm-link";
    }

    @PostMapping("/link-account/confirm")
    public String linkAccount(HttpServletRequest request) {
        String email = (String) session.getAttribute("google_email");
        if (email == null)
            return "redirect:/login?error";

        Account account = accountRepository.findByEmail(email);
        if (account != null && account.getProvider() == null && account.getRole() == Role.PATIENT) {
            // Gán provider GOOGLE
            account.setProvider(AuthProvider.GOOGLE); // Enum hoặc String
            accountRepository.save(account);

            // Tự động đăng nhập lại
            List<GrantedAuthority> authorities = List
                    .of(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()));
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null,
                    authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);

            session.removeAttribute("google_email");
            return "redirect:/"; // hoặc trang chính
        }

        return "redirect:/Sigin?error";
    }
}
