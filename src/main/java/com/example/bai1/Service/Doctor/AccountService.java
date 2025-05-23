package com.example.bai1.Service.Doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.bai1.Model.Account;
import com.example.bai1.Model.User;
import com.example.bai1.Model.Account.Role;
import com.example.bai1.Repository.Doctor.AccountRepository;
import com.example.bai1.Repository.Doctor.UserRepository;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    public boolean registerAccount(Account account, String phone, String adress, String fullname) {
        if (accountRepository.findByEmail(account.getEmail()) != null
                || accountRepository.findByUsername(account.getUsername()) != null) {
            return false;
        }
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setRole(Role.PATIENT);
        Account saved = accountRepository.save(account);
        if (account.getRole() == Role.PATIENT) {
            User user = new User();
            user.setAccount(account);
            user.setAddress(adress);
            user.setFullName(fullname);
            user.setPhoneNumber(phone);
            User saveduser = userRepository.save(user);
        }
        System.out.println("Saved Account ID: " + saved.getAccountId());
        return true;
    }
}
