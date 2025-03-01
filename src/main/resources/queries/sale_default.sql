INSERT INTO vente (timestamp, codeproduit, vendus, jetes) VALUES (?, ?, ?, ?);

SELECT timestamp, codeproduit, vendus, jetes
FROM vente JOIN produit ON vente.codeproduit = produit.code
WHERE lower(nom) LIKE lower(?);

SELECT * FROM vente WHERE timestamp = ? AND codeproduit = ?;
DELETE FROM vente WHERE timestamp = ? AND codeproduit = ?;