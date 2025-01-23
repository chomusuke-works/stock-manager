UPDATE etagere SET nom = ?, eststock = ? WHERE id = ?;

SELECT
    produit.code, produit.nom, etagere.id , etagere.nom, etagere.eststock
FROM
    produit
        LEFT JOIN produit_etagere ON produit.code = produit_etagere.codeproduit
        LEFT JOIN etagere ON produit_etagere.idetagere = etagere.id;