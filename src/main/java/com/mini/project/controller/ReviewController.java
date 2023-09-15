package com.mini.project.controller;

import com.mini.project.entity.ReviewEntity;
import com.mini.project.exception.BookNotFoundException;
import com.mini.project.exception.ReviewNotFoundException;
import com.mini.project.exception.UserNotFoundException;
import com.mini.project.model.ReviewDto;
import com.mini.project.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.util.List;

@RestController
@RequestMapping("/books/{bookId}/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("")
    public ResponseEntity<List<ReviewDto>> getBookReviews(@PathVariable Long bookId) throws BookNotFoundException, ReviewNotFoundException{
        List<ReviewDto> reviews = reviewService.getBookReviews(bookId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/create")
    public ResponseEntity<ReviewDto> createReview(
            @PathVariable Long bookId,
            @RequestBody ReviewDto reviewDto) throws BookNotFoundException, UserNotFoundException {
        ReviewDto createdReview = reviewService.createReview(bookId, reviewDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @PutMapping("/{reviewId}/update")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable Long bookId,
            @PathVariable Long reviewId,
            @RequestBody ReviewDto updatedReview) throws UserNotFoundException,ReviewNotFoundException, BookNotFoundException {
        ReviewDto updated = reviewService.updateReview(bookId, reviewId, updatedReview);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{reviewId}/delete")
    public ResponseEntity<String> deleteReview(
            @PathVariable Long bookId,
            @PathVariable Long reviewId) throws ReviewNotFoundException{
        ReviewEntity deletedReview = reviewService.deleteReview(reviewId, bookId);
        String message = "Review with ID: " + reviewId + " is deleted.";
        return ResponseEntity.ok(message);
    }
}
