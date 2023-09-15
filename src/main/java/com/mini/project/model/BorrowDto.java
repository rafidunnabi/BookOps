package com.mini.project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowDto {

    private Long book_id;
    private String title;
    private String author;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
}
