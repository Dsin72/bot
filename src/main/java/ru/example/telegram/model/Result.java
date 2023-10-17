package ru.example.telegram.model;

import lombok.AllArgsConstructor;
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
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Result extends AbstractBaseEntity {
    @Column(name = "description", nullable = false)
    @NotBlank
    private String description;

    @ManyToOne
    @JoinColumn(name = "test", nullable = false)
    @NotBlank
    private Test test;

    @Column(name = "fromScore", nullable = false)
    @NotNull
    private Integer fromScore;

    @Column(name = "toScore", nullable = false)
    @NotNull
    private Integer toScore;


}
