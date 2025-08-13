package de.muenchen.rbs.kitafinderdatenservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "KINDDATEN")
@NoArgsConstructor
@IdClass(ExportId.class)
public abstract class KindExportResult {

	@Id
	private Integer id;
	@Id
	private Integer exportId;

}
