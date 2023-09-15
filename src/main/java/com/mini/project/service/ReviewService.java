package com.mini.project.service;

import com.mini.project.entity.ReviewEntity;
import com.mini.project.exception.BookNotFoundException;
import com.mini.project.exception.ReviewNotFoundException;
import com.mini.project.exception.UserNotFoundException;
import com.mini.project.model.ReviewDto;

import java.util.List;

public interface ReviewService {

    public List<ReviewDto> getBookReviews(Long bookId) throws BookNotFoundException, ReviewNotFoundException;

    public ReviewDto createReview(Long bookId, ReviewDto reviewDto) throws BookNotFoundException, UserNotFoundException;
    public ReviewDto updateReview(Long bookId, Long reviewId, ReviewDto updatedReview) throws UserNotFoundException,ReviewNotFoundException,BookNotFoundException;
    public ReviewEntity deleteReview(Long reviewId, Long bookId) throws ReviewNotFoundException;
}
