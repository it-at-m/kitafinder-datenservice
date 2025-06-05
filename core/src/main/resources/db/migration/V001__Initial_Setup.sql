CREATE TYPE EXPORT_STATUS AS ENUM ('RUNNING', 'SUCCESS', 'ERROR');

CREATE SEQUENCE EXPORT_ID_SEQUENCE START 1;

CREATE TABLE EXPORT_RUN (
	ID BIGINT,
	START_TIME TIMESTAMP,
	END_TIME TIMESTAMP,
	STATUS EXPORT_STATUS,
	
	PRIMARY KEY(ID)
);

CREATE TABLE KINDMAPPE_ID (
	ID BIGINT
);

CREATE TABLE KIND (
	ID BIGINT,
	EXPORT_ID BIGINT,
	TIMESTAMP TIMESTAMP,
	
	MASTERKIND_ID BIGINT,
	
	PRIMARY KEY(ID, EXPORT_ID),
    CONSTRAINT FK_EXPORT
    	FOREIGN KEY(EXPORT_ID)
        REFERENCES EXPORT_RUN(ID)
);

CREATE TABLE BEWERBUNG (
	KIND_ID BIGINT,
	KIND_EXPORT_ID BIGINT,
	
	ID BIGINT,
	EXPORT_ID BIGINT,
	
	KINDAKTE_ID BIGINT,
	VORNAME VARCHAR(255),
	NACHNAME VARCHAR(255),
	GEBURTSDATUM VARCHAR(255),
	geschlecht VARCHAR(50),
	pflegekind BOOLEAN,
	immunisierung_Masern VARCHAR(50),
	wohnhaft_Bei VARCHAR(50),
	familiensprache VARCHAR(255),
	weitere_Familiensprachen VARCHAR(255),
	koerperliche_Behinderung BOOLEAN,
	geistige_Behinderung BOOLEAN,
	seelische_Behinderung BOOLEAN,
	status VARCHAR(50),
	status_Datum VARCHAR(255),
	status_Grund VARCHAR(255),
	absagegrund VARCHAR(50),
	externe_Id VARCHAR(255),
	exportdatum VARCHAR(255),
	kibig_Id VARCHAR(255),
	anmeldedatum VARCHAR(255),
	voranmeldung_Gueltig_Bis VARCHAR(255),
	erstvorstellung VARCHAR(255),
	persoenlich_Vorgestellt BOOLEAN,
	betreuungswunsch_Ab VARCHAR(255),
	betreuungswunsch_Zeit VARCHAR(50),
	betreuungsform VARCHAR(50),
	prio_Warteliste BIGINT,
	platzart_Id BIGINT,
	anmeldecode VARCHAR(255),
	integrativplatz_Gewuenscht BOOLEAN,
	platzsharing_Gewuenscht BOOLEAN,
	betreuungszeit_Von BIGINT,
	betreuungszeit_Bis BIGINT,
	bemerkungen_Zur_Bewerbung VARCHAR(255),
	wechselkind BOOLEAN,
	wechselkind_Angabe_Eltern BOOLEAN,
	prio_Eltern BIGINT,
	schulbezirk_Id BIGINT,
	jahrgangsstufe_Id BIGINT,
	umzug_In_Schulbezirk_Zum VARCHAR(255),
	gastschulantrag_Gestellt BOOLEAN,
	vertragsbeginn VARCHAR(255),
	erster_Betreuungstag VARCHAR(255),
	kuendigungsdatum VARCHAR(255),
	vertragsende VARCHAR(255),
	vertragsende_Spaetestens VARCHAR(255),
	einschulungstermin VARCHAR(255),
	auswaertig BOOLEAN,
	umzug_Geplant BOOLEAN,
	auswaertig_Seit VARCHAR(255),
	auswaertig_Bis VARCHAR(255),
	auswaertig_Gefoerdert BOOLEAN,
	ausflugsfoerderung_Von VARCHAR(255),
	ausflugsfoerderung_Bis VARCHAR(255),
	but_Id VARCHAR(255),
	but_Verwendungszweck VARCHAR(50),
	kita_Id BIGINT,
	kita_Name VARCHAR(255),
	kita_Kibig_Id VARCHAR(255),
	
	PRIMARY KEY(ID, EXPORT_ID),
    CONSTRAINT FK_EXPORT
    	FOREIGN KEY(KIND_ID, EXPORT_ID)
        REFERENCES KIND(ID, EXPORT_ID)
);

CREATE TABLE VERTRAG (
	KIND_ID BIGINT,
	KIND_EXPORT_ID BIGINT,
	
	ID BIGINT,
	EXPORT_ID BIGINT,
	
	KINDAKTE_ID BIGINT,
	VORNAME VARCHAR(255),
	NACHNAME VARCHAR(255),
	GEBURTSDATUM VARCHAR(255),
	geschlecht VARCHAR(50),
	pflegekind BOOLEAN,
	immunisierung_Masern VARCHAR(50),
	wohnhaft_Bei VARCHAR(50),
	familiensprache VARCHAR(255),
	weitere_Familiensprachen VARCHAR(255),
	koerperliche_Behinderung BOOLEAN,
	geistige_Behinderung BOOLEAN,
	seelische_Behinderung BOOLEAN,
	status VARCHAR(50),
	status_Datum VARCHAR(255),
	status_Grund VARCHAR(255),
	absagegrund VARCHAR(50),
	externe_Id VARCHAR(255),
	exportdatum VARCHAR(255),
	kibig_Id VARCHAR(255),
	anmeldedatum VARCHAR(255),
	voranmeldung_Gueltig_Bis VARCHAR(255),
	erstvorstellung VARCHAR(255),
	persoenlich_Vorgestellt BOOLEAN,
	betreuungswunsch_Ab VARCHAR(255),
	betreuungswunsch_Zeit VARCHAR(50),
	betreuungsform VARCHAR(50),
	prio_Warteliste BIGINT,
	platzart_Id BIGINT,
	anmeldecode VARCHAR(255),
	integrativplatz_Gewuenscht BOOLEAN,
	platzsharing_Gewuenscht BOOLEAN,
	betreuungszeit_Von BIGINT,
	betreuungszeit_Bis BIGINT,
	bemerkungen_Zur_Bewerbung VARCHAR(255),
	wechselkind BOOLEAN,
	wechselkind_Angabe_Eltern BOOLEAN,
	prio_Eltern BIGINT,
	schulbezirk_Id BIGINT,
	jahrgangsstufe_Id BIGINT,
	umzug_In_Schulbezirk_Zum VARCHAR(255),
	gastschulantrag_Gestellt BOOLEAN,
	vertragsbeginn VARCHAR(255),
	erster_Betreuungstag VARCHAR(255),
	kuendigungsdatum VARCHAR(255),
	vertragsende VARCHAR(255),
	vertragsende_Spaetestens VARCHAR(255),
	einschulungstermin VARCHAR(255),
	auswaertig BOOLEAN,
	umzug_Geplant BOOLEAN,
	auswaertig_Seit VARCHAR(255),
	auswaertig_Bis VARCHAR(255),
	auswaertig_Gefoerdert BOOLEAN,
	ausflugsfoerderung_Von VARCHAR(255),
	ausflugsfoerderung_Bis VARCHAR(255),
	but_Id VARCHAR(255),
	but_Verwendungszweck VARCHAR(50),
	kita_Id BIGINT,
	kita_Name VARCHAR(255),
	kita_Kibig_Id VARCHAR(255),
	
	PRIMARY KEY(ID, EXPORT_ID),
    CONSTRAINT FK_EXPORT
    	FOREIGN KEY(KIND_ID, EXPORT_ID)
        REFERENCES KIND(ID, EXPORT_ID)
);

CREATE TABLE EXPORT_ERROR (
	ID BIGINT,
	EXPORT_ID BIGINT,
	TIMESTAMP TIMESTAMP,
	ERROR_MESSAGE VARCHAR(2000),
	
	PRIMARY KEY(ID, EXPORT_ID),
    CONSTRAINT FK_EXPORT
    	FOREIGN KEY(EXPORT_ID)
        REFERENCES EXPORT_RUN(ID)
);

CREATE OR REPLACE VIEW KIND_AKTUELL AS
	SELECT DISTINCT ON (ID) *
	FROM KIND
	ORDER BY ID, TIMESTAMP DESC;