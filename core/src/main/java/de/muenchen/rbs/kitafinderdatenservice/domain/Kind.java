package de.muenchen.rbs.kitafinderdatenservice.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
public class Kind {

	@Id
	private Integer id;

	@Id
	private Integer exportId;

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
