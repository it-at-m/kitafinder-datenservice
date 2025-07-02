package de.muenchen.rbs.kitafinderdatenservice.domain.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateMapper {

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	public String asString(LocalDate date) {
		return date != null ? formatter.format(date) : null;
	}

	public LocalDate asDate(String date) {
		return date != null && date.length() > 0 ? LocalDate.parse(date, formatter) : null;
	}
}