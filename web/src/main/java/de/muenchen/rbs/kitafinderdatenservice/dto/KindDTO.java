package de.muenchen.rbs.kitafinderdatenservice.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import de.muenchen.rbs.kitafinderdatenservice.domain.KindDatenstand;
import lombok.Data;

@Data
@JsonRootName("kind")
public class KindDTO {
	private Integer id;
	private Integer exportId;
	private LocalDateTime timestamp;
	private Integer masterkindId;

	@JsonUnwrapped
	private KindDatenstand masterkind;
}