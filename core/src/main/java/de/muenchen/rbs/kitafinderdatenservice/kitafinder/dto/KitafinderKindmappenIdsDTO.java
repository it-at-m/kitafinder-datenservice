package de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto;

import java.util.Collection;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class KitafinderKindmappenIdsDTO extends KitafinderResponseDTO {
	private Collection<Integer> kindMappenIds;
}
