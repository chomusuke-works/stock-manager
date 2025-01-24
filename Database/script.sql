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
);



CREATE OR REPLACE FUNCTION deductSales()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
DECLARE
    plusAncienLot lot%ROWTYPE;
    aSoustraire INTEGER;
BEGIN
    aSoustraire := NEW.vendus + NEW.jetes;

    LOOP
        IF aSoustraire = 0 THEN EXIT;
        END IF;

        SELECT * INTO plusAncienLot
        FROM lot
        WHERE lot.codeproduit = NEW.codeproduit AND quantite > 0
        ORDER BY lot.dateexpiration
        LIMIT 1;

        plusAncienLot.quantite = plusAncienLot.quantite - aSoustraire;
        UPDATE lot
        SET quantite = plusAncienLot.quantite
        WHERE lot.codeproduit = plusAncienLot.codeproduit AND
            lot.datereception = plusAncienLot.datereception;
        IF plusAncienLot.quantite < 0 THEN
            -- If there are still units to deduct
            aSoustraire := abs(plusAncienLot.quantite);
        ELSE aSoustraire := 0;
        end if;
    END LOOP;

    DELETE FROM lot WHERE quantite <= 0;

    RETURN NEW;
END
$$;

CREATE TRIGGER registerSale BEFORE INSERT ON vente
    FOR EACH ROW EXECUTE FUNCTION deductSales();



CREATE VIEW produit_etagere_extended AS
SELECT
    produit.code AS codeProduit,
    produit.nom AS nomProduit,
    etagere.id AS idEtagere,
    etagere.nom AS nomEtagere,
    CASE
        WHEN etagere.eststock IS NULL THEN ''
        WHEN etagere.eststock THEN 'stock'
        ELSE
            'magasin'
        END AS secteur
FROM
    produit
        LEFT JOIN produit_etagere ON produit.code = produit_etagere.codeproduit
        LEFT JOIN etagere ON produit_etagere.idetagere = etagere.id;