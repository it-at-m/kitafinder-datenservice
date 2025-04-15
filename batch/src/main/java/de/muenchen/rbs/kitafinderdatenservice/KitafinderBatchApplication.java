package de.muenchen.rbs.kitafinderdatenservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.muenchen.rbs.kitafinderdatenservice.batch.KitafinderExportBatch;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class KitafinderBatchApplication implements CommandLineRunner {

	@Autowired
	private KitafinderExportBatch batch;

	public static void main(String[] args) {
		SpringApplication.run(KitafinderBatchApplication.class, args);
	}

	@Override
	public void run(String... args) {
		batch.loadKitafinderData();
	}

}