package de.muenchen.rbs.kitafinderdatenservice.batch.old;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KitafinderDatenBatchResult {
	private int importCount;
	private int eventCount;
	private int errorCount;
}
