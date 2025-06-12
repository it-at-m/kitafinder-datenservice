package de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto;

import lombok.Data;

@Data
public abstract class KitafinderResponseDTO {
	private String fehlermeldung;
	private String stacktrace;
}
