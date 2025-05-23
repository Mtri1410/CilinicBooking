package com.example.bai1.Service.Doctor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.bai1.Model.Doctor;
import com.example.bai1.Model.Schedules;
import com.example.bai1.Repository.Doctor.AppointmentsRepository;
import com.example.bai1.Repository.Doctor.SchedulesRepository;

@Service
public class SchedulesService {
    private final SchedulesRepository schedulesRepository;

    @Autowired
    public SchedulesService(SchedulesRepository schedules) {
        schedulesRepository = schedules;
    }

    public List<Schedules> getSchedulesByDoctorAndDate(Doctor doctor, LocalDate date, Sort sort) {
        return schedulesRepository.findByDoctorAndDate(doctor, date, sort);
    }

    public List<Schedules> getSchedulesByDoctor(Doctor doctor, Sort sort) {
        return schedulesRepository.findByDoctor(doctor, sort);
    }

    public Schedules findbyschedulesid(Long id) {
        return schedulesRepository.findByScheduleId(id);
    }

    public void save(Schedules schedules) {
        try {
            schedulesRepository.save(schedules);
        } catch (Exception e) {
            System.out.println("Lỗi khi lưu lịch: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public boolean isSlotAvailable(Doctor doctor, LocalDate date, java.time.LocalTime startTime, java.time.LocalTime endTime) {
        List<Schedules> schedules = schedulesRepository.findByDoctorAndDate(doctor, date, Sort.unsorted());
        for (Schedules s : schedules) {
            if (s.getStartTime().equals(startTime) && s.getEndTime().equals(endTime)
                && s.getStatus() == Schedules.Status.BOOKED) {
                return false;
            }
        }
        return true;
    }
}
