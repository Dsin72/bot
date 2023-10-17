package ru.example.telegram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.telegram.model.Test;
import ru.example.telegram.model.User;

public interface JpaTestRepository  extends JpaRepository<Test, Integer> {
}
