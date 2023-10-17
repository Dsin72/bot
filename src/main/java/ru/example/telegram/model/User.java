package ru.example.telegram.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.example.telegram.bot.State;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "chat_id", name = "users_unique_chatid_idx")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractBaseEntity {
    @Column(name = "chat_id", unique = true, nullable = false)
    @NotNull
    private Long chatId;

    @Column(name = "userName", unique = true, nullable = false)
    @NotBlank
    private String userName;

    @Column(name = "currentTestScore", nullable = false)
    @NotNull
    private Integer currentTestScore;

    @Column(name = "currentTest", nullable = false)
    @NotNull
    private Integer currentTest;

    @Column(name = "currentTestQuestion", nullable = false)
    @NotNull
    private Integer currentTestQuestion;

    @Column(name = "bot_state", nullable = false)
    @NotBlank
    private State botState;

    public User(long chatId, String userName) {
        this.chatId = chatId;
        this.userName = userName;
        this.currentTestScore = 0;
        this.currentTest = 0;
        this.currentTestQuestion = 0;
        this.botState = State.START;
    }
}
