package de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto;

import java.util.List;

import lombok.Data;

@Data
public class KindmappeDTO {
	private Integer id;
	private boolean isGefunden;
	private List<KindakteDTO> kindAkten;
	private ErmittlungsDauerDTO ermittlungsDauer;
}
