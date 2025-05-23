package com.example.bai1.Service;

import com.example.bai1.Model.Reviews;
import com.example.bai1.Model.Reviews.ReviewStatus;
import com.example.bai1.Repository.ReviewsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewsRepository reviewsRepository;

    @Autowired
    public ReviewService(ReviewsRepository reviewsRepository) {
        this.reviewsRepository = reviewsRepository;
    }

    public List<Reviews> findAllReviews() {
        return reviewsRepository.findAll(); // Consider Pageable for large datasets
    }

    public List<Reviews> findReviewsByFilters(Long doctorId, Integer rating, ReviewStatus status) {
        List<Reviews> reviews = reviewsRepository.findAll(); // In a real app, use Specification or QueryDSL for DB-level filtering

        if (doctorId != null) {
            reviews = reviews.stream()
                             .filter(r -> r.getDoctor() != null && r.getDoctor().getDoctorId().equals(doctorId))
                             .collect(Collectors.toList());
        }

        if (rating != null) {
            reviews = reviews.stream()
                             .filter(r -> r.getRating() != null && r.getRating().equals(rating))
                             .collect(Collectors.toList());
        }

        if (status != null) {
            reviews = reviews.stream()
                             .filter(r -> r.getStatus() == status)
                             .collect(Collectors.toList());
        }
        return reviews;
    }

    public Reviews findReviewById(Long id) {
        return reviewsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + id));
    }

    @Transactional
    public Reviews approveReview(Long id) {
        Reviews review = findReviewById(id);
        review.setStatus(ReviewStatus.APPROVED);
        return reviewsRepository.save(review);
    }

    @Transactional
    public Reviews rejectReview(Long id) {
        Reviews review = findReviewById(id);
        review.setStatus(ReviewStatus.REJECTED);
        return reviewsRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long id) {
        if (!reviewsRepository.existsById(id)) {
            throw new EntityNotFoundException("Review not found with id: " + id);
        }
        reviewsRepository.deleteById(id);
    }

    @Transactional
    public Reviews saveReview(Reviews review) {
        return reviewsRepository.save(review);
    }

    // Add other methods later for filtering
    /*
    public Reviews saveReview(Reviews review) { // For creating/updating reviews if needed from admin
        return reviewsRepository.save(review);
    }
    */
} 