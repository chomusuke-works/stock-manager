SELECT exists(SELECT * FROM vente WHERE datevente = ? AND codeproduit = ?);
UPDATE vente SET vendus = vendus + ?, jetes = jetes + ? WHERE datevente = ? AND codeproduit = ?;