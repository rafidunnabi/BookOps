package com.mini.project.repository;

import com.mini.project.entity.BookEntity;
import com.mini.project.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<BookEntity, Long> {

    Optional<BookEntity> findById(Long id);
    Optional<BookEntity> findByIdAndIsDeletedFalse(Long id);

}
