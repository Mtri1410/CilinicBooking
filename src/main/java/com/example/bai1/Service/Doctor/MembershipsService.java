package com.example.bai1.Service.Doctor;

import com.example.bai1.Model.Memberships;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bai1.Repository.Doctor.MembershipsRepository;

import java.util.List;

@Service
public class MembershipsService {
    private final MembershipsRepository membershipsrepository;

    @Autowired
    public MembershipsService(MembershipsRepository mem) {
        membershipsrepository = mem;
    }

    public List<Memberships> getAllMemberships() {
        return membershipsrepository.findAll();
    }

    public Memberships findbyid(Long id) {
        return membershipsrepository.findByMembershipId(id);
    }
}
