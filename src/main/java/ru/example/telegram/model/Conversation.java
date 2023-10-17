package ru.example.telegram.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "conversation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Conversation extends AbstractBaseEntity {
    @Column(name = "question", nullable = false)
    @NotBlank
    private String question;

    @Column(name = "type", nullable = false)
    @NotBlank
    private String type;
}

