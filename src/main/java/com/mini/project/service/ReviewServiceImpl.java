package com.mini.project.service;

import com.mini.project.entity.BookEntity;
import com.mini.project.entity.ReviewEntity;
import com.mini.project.entity.UserEntity;
import com.mini.project.exception.BookNotFoundException;
import com.mini.project.exception.ReviewNotFoundException;
import com.mini.project.exception.UserNotFoundException;
import com.mini.project.model.ReviewDto;
import com.mini.project.repository.BookRepository;
import com.mini.project.repository.ReviewRepository;
import com.mini.project.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService{
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ReviewDto> getBookReviews(Long bookId) throws BookNotFoundException, ReviewNotFoundException{

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> user = userRepository.findByEmail(authentication.getName());
        Long userId = user.get().getId();

        Optional<BookEntity> bookOptional = bookRepository.findByIdAndIsDeletedFalse(bookId);

        if (bookOptional.isEmpty()) {
            throw new BookNotFoundException("Book not found with ID: " + bookId);
        }

        BookEntity book = bookOptional.get();
        List<ReviewEntity> reviewEntities = reviewRepository.findByBookId(bookId);

        if (reviewEntities.isEmpty()) {
            throw new ReviewNotFoundException("Reviews not found for book with ID: " + bookId);
        }

        List<ReviewDto> reviewDtos = reviewEntities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        reviewDtos.forEach(reviewDto -> {
            reviewDto.setBookId(bookId);
            reviewDto.setBookTitle(book.getTitle());
            reviewDto.setUserId(userId);
        });

        return reviewDtos;
    }

    public ReviewDto createReview(Long bookId, ReviewDto reviewDto) throws BookNotFoundException, UserNotFoundException{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> user = userRepository.findByEmail(authentication.getName());
        Long userId = user.get().getId();
        BookEntity book = bookRepository.findByIdAndIsDeletedFalse(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));


        ReviewEntity review = new ReviewEntity();
        review.setBook(book);
        review.setUser(userEntity);
        review.setComment(reviewDto.getComment());
        review.setRating(reviewDto.getRating());



        ReviewEntity savedReview = reviewRepository.save(review);

        ReviewDto responseReviewDto = convertToDto(savedReview);
        responseReviewDto.setBookId(bookId);
        responseReviewDto.setBookTitle(book.getTitle());
        responseReviewDto.setUserId(userId);

        return responseReviewDto;
    }

    public ReviewDto updateReview(Long bookId, Long reviewId, ReviewDto updatedReview) throws UserNotFoundException,ReviewNotFoundException, BookNotFoundException{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> user = userRepository.findByEmail(authentication.getName());
        Long userId = user.get().getId();

        ReviewEntity existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with ID: " + reviewId));

        if (!existingReview.getUser().getId().equals(userId)) {
            throw new UserNotFoundException("You do not have any review to update.");
        }
        Optional<BookEntity> bookOptional = bookRepository.findByIdAndIsDeletedFalse(bookId);
        if (bookOptional.isEmpty()) {
            throw new BookNotFoundException("Book not found with ID: " + bookId);
        }
        BookEntity book = bookOptional.get();
        if (!existingReview.getBook().getId().equals(bookId)) {
            throw new ReviewNotFoundException("Review with ID: " + reviewId + " is not associated with the specified book.");
        }
        existingReview.setComment(updatedReview.getComment());
        existingReview.setRating(updatedReview.getRating());

        ReviewDto responseReviewDto = convertToDto(existingReview);
        responseReviewDto.setBookId(bookId);
        responseReviewDto.setBookTitle(book.getTitle());
        responseReviewDto.setUserId(userId);

        return responseReviewDto;
    }

    public ReviewEntity deleteReview(Long reviewId, Long bookId) throws ReviewNotFoundException{

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> user = userRepository.findByEmail(authentication.getName());
        Long userId = user.get().getId();

        ReviewEntity existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with ID: " + reviewId));

        if (!existingReview.getUser().getId().equals(Long.valueOf(userId))) {
            throw new AccessDeniedException("You do not have permission to delete this review or you are not the author of this review.");
        }
        if (!existingReview.getBook().getId().equals(Long.valueOf(bookId))) {
            throw new AccessDeniedException("The review does not match the specified book.");
        }
        ReviewEntity deletedReview = existingReview;
        reviewRepository.deleteById(reviewId);
        return deletedReview;
    }

    public ReviewDto convertToDto(ReviewEntity reviewEntity) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setReview_id(reviewEntity.getReview_id());
        reviewDto.setComment(reviewEntity.getComment());
        reviewDto.setRating(reviewEntity.getRating());
        return reviewDto;
    }
}
