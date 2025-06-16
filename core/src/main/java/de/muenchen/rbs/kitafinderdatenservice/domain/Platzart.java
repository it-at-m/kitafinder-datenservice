package de.muenchen.rbs.kitafinderdatenservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Platzart {
	// Mapping works out of the box, as long as values are in the correct order
	OHNE_ANGABE(0), KRIPPE(1), KINDERGARTEN(2), HORT(3);

	private int sortOrder;
}
