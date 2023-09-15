package com.mini.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mini.project.entity.BookEntity;
import com.mini.project.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Long review_id;
    private Long userId;
    private Long bookId;
    private String bookTitle;
    private String comment;
    private int rating;
}
