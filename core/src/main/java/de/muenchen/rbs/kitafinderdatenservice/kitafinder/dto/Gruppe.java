package de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto;

import lombok.Data;

@Data
public class Gruppe {
    private int zuordnungId;
    private int gruppeId;
    private String ab;
    private String bis;
    private String gruppenname;
}
