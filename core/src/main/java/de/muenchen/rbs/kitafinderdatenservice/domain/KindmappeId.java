package de.muenchen.rbs.kitafinderdatenservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "KINDMAPPE_ID")
@NoArgsConstructor
@AllArgsConstructor
public class KindmappeId {

	@Id
	private Integer id;
}
