SELECT exists(SELECT * FROM vente WHERE codeproduit = ? AND datevente = ?);
UPDATE vente SET vendus = vendus + ?, jetes = jetes + ? WHERE codeproduit = ? AND datevente = ?;