package ru.example.telegram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.example.telegram.model.Touch;
import ru.example.telegram.model.User;


public interface JpaTouchRepository extends JpaRepository<Touch, Integer> {
    @Query(nativeQuery = true, value = "SELECT *  FROM touch WHERE type = 'soft' ORDER BY random() LIMIT 1")
    Touch getRandomSoftTouch();

    @Query(nativeQuery = true, value = "SELECT *  FROM touch WHERE type = 'hard' ORDER BY random() LIMIT 1")
    Touch getRandomHardTouch();
}
