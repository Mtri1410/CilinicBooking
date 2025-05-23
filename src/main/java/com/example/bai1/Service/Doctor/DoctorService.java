package com.example.bai1.Service.Doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bai1.Model.Account;
import com.example.bai1.Model.Doctor;
import com.example.bai1.Repository.Doctor.DoctorRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public Doctor getinfo(Account account) {
        return doctorRepository.findByAccount(account);
    }

    public Doctor findbydoctorid(Long doctorId) {
        return doctorRepository.findByDoctorId(doctorId);
    }

    public void save(Doctor doctor) {
        doctorRepository.save(doctor);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .filter(doctor -> Doctor.Status.APPROVED == doctor.getStatus())
                .collect(Collectors.toList());
    }

    public List<Doctor> findBySpecialty(String specialty) {
        return doctorRepository.findAll().stream()
                .filter(doctor -> Doctor.Status.APPROVED == doctor.getStatus())
                .filter(doctor -> doctor.getSpecialty() != null && doctor.getSpecialty().equalsIgnoreCase(specialty))
                .collect(Collectors.toList());
    }

    public List<Doctor> searchDoctors(String searchTerm) {
        String searchLower = searchTerm.toLowerCase();
        return doctorRepository.findAll().stream()
                .filter(doctor -> Doctor.Status.APPROVED == doctor.getStatus())
                .filter(doctor -> 
                    (doctor.getFullname() != null && doctor.getFullname().toLowerCase().contains(searchLower)) ||
                    (doctor.getSpecialty() != null && doctor.getSpecialty().toLowerCase().contains(searchLower)) ||
                    (doctor.getAddress() != null && doctor.getAddress().toLowerCase().contains(searchLower))
                )
                .collect(Collectors.toList());
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id).orElse(null);
    }

}
