package com.mini.project.repository;

import com.mini.project.entity.BookEntity;
import com.mini.project.entity.ReservationEntity;
import com.mini.project.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    Optional<ReservationEntity> findByUserAndBookAndIsCancelledFalse(UserEntity userEntity, BookEntity book);


    List<ReservationEntity> findAllByBookAndNotifyAndIsCancelledFalse(BookEntity book, String pending);
}
