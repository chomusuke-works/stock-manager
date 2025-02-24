INSERT INTO produit_etagere (codeproduit, idetagere) VALUES (?, ?);
SELECT * FROM produit_etagere_extended WHERE lower(nomproduit) LIKE lower(?) OR lower(nometagere) LIKE lower(?);
SELECT * FROM produit_etagere_extended WHERE codeproduit = ? AND idetagere = ?;
DELETE FROM produit_etagere WHERE codeproduit = ? AND idetagere = ?;