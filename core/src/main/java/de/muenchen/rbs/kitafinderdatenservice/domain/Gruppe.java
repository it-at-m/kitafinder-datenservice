package de.muenchen.rbs.kitafinderdatenservice.domain;

import java.time.LocalDate;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Gruppe {
    private int zuordnungId;
    private int gruppeId;
    private LocalDate ab;
    private LocalDate bis;
    private String gruppenname;
}
