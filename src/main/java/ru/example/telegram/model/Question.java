package ru.example.telegram.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question extends AbstractBaseEntity {
    @Column(name = "question", nullable = false)
    @NotBlank
    private String question;

    @ManyToOne
    @JoinColumn(name = "test", nullable = false)
    @NotBlank
    private Test test;

    @Column(name = "option1", nullable = false)
    @NotBlank
    private String optionOne;

    @Column(name = "option2", nullable = false)
    @NotBlank
    private String optionTwo;

    @Column(name = "option3", nullable = false)
    @NotBlank
    private String optionThree;

    @Column(name = "option4", nullable = false)
    @NotBlank
    private String optionFour;

    @Column(name = "option5", nullable = false)
    @NotBlank
    private String optionFive;

    @Override
    public String toString() {
        return "Question{" +
                "test='" + test + '\'' +
                "question='" + question + '\'' +
                ", optionOne='" + optionOne + '\'' +
                ", optionTwo='" + optionTwo + '\'' +
                ", optionThree='" + optionThree + '\'' +
                ", optionFour='" + optionFour + '\'' +
                ", optionFive='" + optionFive + '\'' +
                '}';
    }
}

