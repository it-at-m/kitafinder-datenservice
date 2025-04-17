package de.muenchen.rbs.kitafinderdatenservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.muenchen.rbs.kitafinderdatenservice.batch.KitafinderDatenBatch;
import de.muenchen.rbs.kitafinderdatenservice.batch.KitafinderIdBatch;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class KitafinderBatchApplication implements CommandLineRunner {

	@Autowired
	private KitafinderIdBatch idBatch;
	@Autowired
	private KitafinderDatenBatch datenBatch;

	public static void main(String[] args) {
		SpringApplication.run(KitafinderBatchApplication.class, args);
	}

	@Override
	public void run(String... args) {
		idBatch.loadKitafinderIds();

		datenBatch.loadKitafinderData();

		System.exit(0);
	}

}