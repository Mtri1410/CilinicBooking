package com.example.bai1.Repository.Doctor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bai1.Model.Memberships;
import java.util.List;

@Repository
public interface MembershipsRepository extends JpaRepository<Memberships, Long> {
    Memberships findByMembershipId(Long membershipId);

    List<Memberships> findAll();
}