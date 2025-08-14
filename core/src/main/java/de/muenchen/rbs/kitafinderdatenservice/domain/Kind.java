package de.muenchen.rbs.kitafinderdatenservice.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@IdClass(ExportId.class)
public class Kind implements KindExportResult {

	@Id
	private Long id;
	@Id
	private Long exportId;

	private LocalDateTime timestamp;

	private Long masterkindId;

	@OneToMany(mappedBy = "kind", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Bewerbung> bewerbungen = new ArrayList<>();

	@OneToMany(mappedBy = "kind", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Vertrag> vertraege = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "exportId", nullable = false, insertable = false, updatable = false)
	private ExportRun exportRun;

	public KindDatenstand getMasterkind() {
		KindDatenstand masterkind = getKinddaten().stream().filter(k -> k.getId().equals(this.getMasterkindId()))
				.findAny().orElse(null);
		return masterkind;
	}

	public List<KindDatenstand> getKinddaten() {
		List<KindDatenstand> kinddaten = new ArrayList<>();
		if (getBewerbungen() != null) {
			kinddaten.addAll(getBewerbungen());
		}
		if (getVertraege() != null) {
			kinddaten.addAll(getVertraege());
		}
		return kinddaten;
	}

	public ExportId getFullId() {
		return new ExportId(getId(), getExportId());
	}

}
