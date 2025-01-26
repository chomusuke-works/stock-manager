SELECT nom, lot.dateexpiration, quantite
FROM lot JOIN produit ON lot.codeproduit = produit.code
WHERE lot.dateexpiration IS NOT NULL
    AND lot.dateexpiration - current_date BETWEEN 0 AND ?;

SELECT nom, lot.dateexpiration, quantite
FROM lot JOIN produit ON lot.codeproduit = produit.code
WHERE lot.dateexpiration IS NOT NULL
  AND lot.dateexpiration - current_date < 0;

SELECT * FROM commande;