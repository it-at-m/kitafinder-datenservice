package de.muenchen.rbs.kitafinderdatenservice.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class KindDTO {
	private String id;
	private LocalDateTime stand;
	private String kindAkten;
}
