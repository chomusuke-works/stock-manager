INSERT INTO produit_etagere (codeproduit, idetagere) VALUES (?, ?);
SELECT * FROM produit_etagere;
SELECT * FROM produit_etagere WHERE codeproduit = ? OR idetagere = ?;
DELETE FROM produit_etagere WHERE codeproduit = ? AND idetagere = ?;