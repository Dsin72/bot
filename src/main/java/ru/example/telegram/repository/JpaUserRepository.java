package ru.example.telegram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.example.telegram.model.User;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface JpaUserRepository extends JpaRepository<User, Integer> {
    Optional<User> getByChatId(long chatId);
}