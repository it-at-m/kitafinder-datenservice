package de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto;

import java.util.Collection;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class KitafinderExport extends KitafinderResponse {
	private Collection<Kindmappe> kindMappen;
}
