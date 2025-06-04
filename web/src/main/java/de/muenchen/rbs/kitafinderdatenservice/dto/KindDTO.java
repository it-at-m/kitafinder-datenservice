package de.muenchen.rbs.kitafinderdatenservice.dto;

import java.time.LocalDateTime;
import java.util.List;

import de.muenchen.rbs.kitafinderdatenservice.domain.Bewerbung;
import de.muenchen.rbs.kitafinderdatenservice.domain.Vertrag;
import lombok.Data;

@Data
public class KindDTO {
	private Integer id;
	private Integer exportId;
	private LocalDateTime timestamp;

	private String vorname;
	private String nachname;
	private String geburtsdatum;

	private List<Bewerbung> bewerbungen;
	private List<Vertrag> vertraege;
}
