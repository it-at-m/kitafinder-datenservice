package de.muenchen.rbs.kitafinderdatenservice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KindakteDTO;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KindmappeDTO;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.SorgeberechtigterDTO;

@Component
public class TestDataUtils {

	private Faker faker = new Faker();
	private DateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy");

	private int currentId = 0;

	public KindmappeDTO fakeKindmappeDTO() {
		return KindmappeDTO.builder().id(Long.valueOf(currentId++)).isGefunden(true).kindAkten(new ArrayList<>()).build();
	}
	
	public KindakteDTO fakeKindakteDTO() {
		return KindakteDTO.builder()
				.id(currentId++)
				.vorname(faker.name().firstName())
				.nachname(faker.name().lastName())
				.geburtsdatum(dateFormat.format(faker.date().birthday(3, 7)))
				.geschlechtId(faker.random().nextInt(0, 3))
				.pflegekind(faker.bool().bool())
				.immunisierungMasernId(faker.random().nextInt(0, 4))
				.wohnhaftBeiId(faker.random().nextInt(0, 2))
				.familiensprache(faker.programmingLanguage().name())
				.koerperlicheBehinderung(faker.bool().bool())
				.geistigeBehinderung(faker.bool().bool())
				.seelischeBehinderung(faker.bool().bool())
				.statusId(faker.random().nextInt(0, 9))
				.statusGrund(faker.backToTheFuture().quote())
				.statusDatum(dateFormat.format(faker.date().past(100, TimeUnit.DAYS)))
				.externeId("K" + currentId++)
				.kibigId("K" + currentId)
				.kibigId(faker.random().nextInt(1620010000, 1620019999).toString())
				.anmeldedatum(dateFormat.format(faker.date().past(350, 100, TimeUnit.DAYS)))
				.voranmeldungGueltigBis(dateFormat.format(faker.date().past(350, 100, TimeUnit.DAYS)))
				.erstvorstellung(dateFormat.format(faker.date().past(350, 100, TimeUnit.DAYS)))
				.betreuungswunschAb(dateFormat.format(faker.date().past(350, 100, TimeUnit.DAYS)))
				.betreuungswunschZeitId(faker.random().nextInt(0, 9))
				.betreuungsformId(faker.random().nextInt(0, 3))
				.platzartId(faker.random().nextInt(0, 3))
				.integrativplatzGewuenscht(faker.bool().bool())
				.platzsharingGewuenscht(faker.bool().bool())
				.betreuungszeitVon(faker.random().nextLong(73080000))
				.betreuungszeitBis(faker.random().nextLong(86340000))
				.wechselkind(faker.bool().bool())
				.wechselkindAngabeEltern(faker.bool().bool())
				.prioEltern(faker.random().nextBoolean() ? 101l : faker.random().nextBoolean() ? 4l : 1l)
				.schulbezirkId(faker.random().nextInt(40))
				.jahrgangsstufeId(faker.random().nextInt(6))
				.gastschulantragGestellt(faker.bool().bool())
				
				.sorgeberechtigter1(null)
				.build();
	}
	
	public SorgeberechtigterDTO getSorgeberechtigter() {
        return SorgeberechtigterDTO.builder()
                .id((long)currentId++)
                .anrede(faker.options().option("Herr", "Frau"))
                .titel(faker.options().option("Dr.", "Prof.", "Dipl.-Ing.", ""))
                .vorname(faker.name().firstName())
                .nachname(faker.name().lastName())
                .strasse(faker.address().streetName())
                .hausnummer(faker.address().buildingNumber())
                .plz(faker.address().zipCode())
                .ort(faker.address().city())
                .land(faker.address().country())
                .telefon1(faker.phoneNumber().phoneNumber())
                .telefon2(faker.phoneNumber().phoneNumber())
                .email(faker.internet().emailAddress())
                .auslaendischeherkunft(faker.bool().bool())
                .nichtDeutschsprachigeHerkunft(faker.bool().bool())
                .alleinErziehend(faker.bool().bool())
                .geplanteAufnahmeBerufstaetigkeit(faker.bool().bool())
                .geplanteAufnahmeBerufstaetigkeitAb(faker.date().past(365, java.util.concurrent.TimeUnit.DAYS).toString())
                .geplantAufnahmeAusbildung(faker.bool().bool())
                .geplantAufnahmeAusbildungAb(faker.date().past(365, java.util.concurrent.TimeUnit.DAYS).toString())
                .geplantAufnahmeStudium(faker.bool().bool())
                .geplantAufnahmeStudiumAb(faker.date().past(365, java.util.concurrent.TimeUnit.DAYS).toString())
                .berufstaetig(faker.bool().bool())
                .erstStudiumAusbildung(faker.bool().bool())
                .wochenarbeitszeit(faker.number().randomDouble(2, 0, 40))
                .wochenarbeitstage((double) faker.number().numberBetween(1, 5))
                .lageDerArbeitszeitId(faker.number().randomNumber())
                .arbeitsuchend(faker.bool().bool())
                .arbeitsuchendSeit(faker.date().past(365, java.util.concurrent.TimeUnit.DAYS).toString())
                .build();
    }

}
