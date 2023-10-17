package ru.example.telegram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.example.telegram.model.Conversation;
import ru.example.telegram.model.Touch;

public interface JpaConversationRepository extends JpaRepository<Conversation, Integer> {
    @Query(nativeQuery = true, value = "SELECT *  FROM conversation ORDER BY random() LIMIT 1")
    Conversation getRandomQuestion();

}
