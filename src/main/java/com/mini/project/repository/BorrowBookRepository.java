package com.mini.project.repository;

import com.mini.project.entity.BookEntity;
import com.mini.project.entity.BorrowBookEntity;
import com.mini.project.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BorrowBookRepository extends JpaRepository<BorrowBookEntity, Long> {
    Optional<BorrowBookEntity> findByBookEntityAndReturnDateIsNull(BookEntity bookEntity);
    List<BorrowBookEntity> findByUserEntity_Id(Long userId);
    boolean existsByBookEntityAndReturnDateIsNull(BookEntity bookEntity);

    boolean existsByBookEntityAndIsAvailable(BookEntity bookEntity, boolean isAvailable);
}
