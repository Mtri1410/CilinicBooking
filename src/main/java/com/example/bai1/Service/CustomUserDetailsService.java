package com.example.bai1.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.bai1.Model.Account;
import com.example.bai1.Model.AccountDetails;
import com.example.bai1.Model.CustomUserDetail;
import com.example.bai1.Repository.Doctor.AccountRepository;

import jakarta.annotation.PostConstruct;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @PostConstruct
    public void init() {
        System.out.println(">> CustomUserDetailsService đã được khởi tạo!");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(">> Tìm tài khoản:--------------------------------------------------- ");
        Account account = accountRepository.findByUsername(username);

        if (account == null) {
            System.out.println(">> Không tìm thấy tài khoản với username: " + username);

        }
        System.out.println(">> Tìm thấy tài khoản: " + account.getUsername());
        return new CustomUserDetail(account);

    }

}
