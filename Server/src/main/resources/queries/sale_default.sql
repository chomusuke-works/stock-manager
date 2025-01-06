INSERT INTO vente (datevente, codeproduit, vendus, jetes) VALUES (?, ?, ?, ?);
SELECT * FROM vente WHERE datevente = ? AND codeproduit = ?;
DELETE FROM vente WHERE datevente = ? AND codeproduit = ?;