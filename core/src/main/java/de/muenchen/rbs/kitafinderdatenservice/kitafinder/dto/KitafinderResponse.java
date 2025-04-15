package de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto;

import lombok.Data;

@Data
public abstract class KitafinderResponse {
	private String fehlermeldung;
	private String stacktrace;
}
