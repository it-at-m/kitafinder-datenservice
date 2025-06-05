package de.muenchen.rbs.kitafinderdatenservice.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Kind {

	@EmbeddedId
	private ExportId id;

	private LocalDateTime timestamp;

	private Integer masterkindId;

	@OneToMany(mappedBy = "kind", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Bewerbung> bewerbungen;

	@OneToMany(mappedBy = "kind", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Vertrag> vertraege;

	@ManyToOne
	@JoinColumn(name = "exportId", nullable = false, insertable = false, updatable = false)
	private ExportRun exportRun;

	public KindDatenstand getMasterkind() {
		KindDatenstand masterkind = Stream
				.concat(bewerbungen.stream().map(Bewerbung::getDaten), vertraege.stream().map(Vertrag::getDaten))
				.filter(ka -> ka.getId() == this.getMasterkindId()).findAny().orElse(null);
		return masterkind;
	}

}
