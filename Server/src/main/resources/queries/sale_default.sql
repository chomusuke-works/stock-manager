INSERT INTO vente (timestamp, codeproduit, vendus, jetes) VALUES (?, ?, ?, ?);
SELECT * FROM vente;
SELECT * FROM vente WHERE timestamp = ? AND codeproduit = ?;
DELETE FROM vente WHERE timestamp = ? AND codeproduit = ?;