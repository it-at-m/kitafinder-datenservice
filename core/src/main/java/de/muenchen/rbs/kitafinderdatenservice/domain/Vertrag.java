package de.muenchen.rbs.kitafinderdatenservice.domain;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Vertrag extends KindDatenstand {

	@ElementCollection(targetClass = Vertragsabschnitt.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "VERTRAGSABSCHNITT", joinColumns = { @JoinColumn(name = "kinddatenId"),
			@JoinColumn(name = "exportId") })
	private List<Vertragsabschnitt> verträge;

}