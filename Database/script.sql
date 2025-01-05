CREATE TABLE etagere (
	id SERIAL PRIMARY KEY,
	nom VARCHAR(64) NOT NULL,
	estStock BOOLEAN NOT NULL
);

CREATE TABLE fournisseur (
	id SERIAL PRIMARY KEY,
	nom VARCHAR(64) NOT NULL,
	email VARCHAR(64) NOT NULL,
	frequenceCommande INTEGER NOT NULL
);

CREATE TABLE produit (
	code BIGINT PRIMARY KEY,
	nom VARCHAR(64) NOT NULL,
	prix DECIMAL NOT NULL,
	idFournisseur INTEGER,

	CONSTRAINT fk_idFournisseur 
		FOREIGN KEY (idFournisseur) REFERENCES fournisseur(id)
		ON DELETE SET NULL
		ON UPDATE CASCADE
);

CREATE TABLE vente (
	dateVente DATE,
	codeProduit BIGINT,
	vendus INTEGER,
	jetes INTEGER,

	CONSTRAINT pk_dateVente_codeProduit 
		PRIMARY KEY (dateVente, codeProduit),
	CONSTRAINT fk_codeProduit
		FOREIGN KEY (codeProduit) REFERENCES produit(code)
		ON DELETE RESTRICT
		ON UPDATE CASCADE
);

CREATE TABLE lot (
	dateReception DATE NOT NULL,
	codeProduit BIGINT NOT NULL,
	quantite INTEGER NOT NULL, 
	dateExpiration DATE,

	CONSTRAINT pk_dateReception_codeProduit 
		PRIMARY KEY (dateReception, codeProduit)
);

CREATE TABLE segmentAnnee (
	id SERIAL PRIMARY KEY,
	nom VARCHAR(64) NOT NULL,
	dateDebut DATE NOT NULL,
	dateFin DATE NOT NULL,
	priorite INTEGER NOT NULL
);

CREATE TABLE produitSegmentAnnee (
	codeProduit BIGINT,
	idSegment INTEGER,
	cible INTEGER,
	seuil INTEGER,

	CONSTRAINT pk_codeProduit_idSegment
		PRIMARY KEY (codeProduit, idSegment),
	CONSTRAINT fk_codeProduit
		FOREIGN KEY (codeProduit) REFERENCES produit(code)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	CONSTRAINT fk_idSegment
		FOREIGN KEY (idSegment) REFERENCES segmentAnnee(id)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);

CREATE TABLE produit_etagere (
	codeProduit BIGINT,
	idEtagere INTEGER,

	CONSTRAINT pk_codeProduit_idEtagere
		PRIMARY KEY (codeProduit, idEtagere),
	CONSTRAINT fk_codeProduit
		FOREIGN KEY (codeProduit) REFERENCES produit(code)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	CONSTRAINT fk_idEtagere
		FOREIGN KEY (idEtagere) REFERENCES etagere(id)
		ON DELETE CASCADE
		ON UPDATE CASCADE
)
