INSERT INTO vente (datevente, codeproduit, vendus, jetes) VALUES (?, ?, ?, ?);
SELECT * FROM vente WHERE codeproduit = ? AND datevente = ?;
DELETE FROM vente WHERE codeproduit = ? AND datevente = ?;
-- A sale is an accounting entry and cannot be erased