INSERT INTO lot (datereception, codeproduit, quantite, dateexpiration) VALUES (?, ?, ?, ?);
DELETE FROM lot WHERE datereception = ? AND codeproduit = ?;