package com.example.bai1.Service.Doctor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bai1.Model.User;
import com.example.bai1.Repository.Doctor.SchedulesRepository;
import com.example.bai1.Repository.Doctor.UserRepository;
import com.example.bai1.Model.Account;

@Service
public class UserDoctorService {
    private final UserRepository userRepository;

    @Autowired
    public UserDoctorService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User findbyphone(String phone) {
        return userRepository.findByPhoneNumber(phone);
    }

    public User findbyid(Long id) {
        return userRepository.findByUserId(id);
    }

    public User findByAccount(Account account) {
        return userRepository.findByAccount(account);
    }
}
