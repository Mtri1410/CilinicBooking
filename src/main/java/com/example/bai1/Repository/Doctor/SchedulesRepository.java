package com.example.bai1.Repository.Doctor;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bai1.Model.Doctor;
import com.example.bai1.Model.Schedules;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SchedulesRepository extends JpaRepository<Schedules, Long> {
    List<Schedules> findByDoctorAndDate(Doctor doctor, LocalDate date, Sort sort);

    Schedules findByScheduleId(Long scheduleId);

    List<Schedules> findByDoctor(Doctor doctor, Sort sort);

}
