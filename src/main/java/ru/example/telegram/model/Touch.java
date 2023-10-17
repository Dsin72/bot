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
@Table(name = "touch")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Touch extends AbstractBaseEntity{
    @Column(name = "touch", nullable = false)
    @NotBlank
    private String touch;

    @Column(name = "type", nullable = false)
    @NotBlank
    private String type;
}
