package de.muenchen.rbs.kitafinderdatenservice.domain;

import java.time.LocalDate;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Vertragsabschnitt {
	private int id;
	private LocalDate ab;
	private LocalDate bis;
	private int betreuungszeitId;
	private int verpflegungId;
	private Boolean koerperlicheBehinderung;
	private Boolean geistigeBehinderung;
	private Boolean seelischeBehinderung;
	private int bringAbholzeitId;
}
