INSERT INTO produit (code, nom, prix, idfournisseur) VALUES (?, ?, ?, ?);
SELECT * FROM produit WHERE code = ?;
DELETE FROM produit WHERE code = ?;
SELECT code, nom, quantite FROM produit JOIN lot ON produit.code = lot.codeproduit WHERE lot.dateexpiration - current_date <= ?;