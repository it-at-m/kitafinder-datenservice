package de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto;

import java.util.Collection;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class KitafinderKindmappenIds extends KitafinderResponse {
	private Collection<Integer> kindMappenIds;
}
