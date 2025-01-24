UPDATE etagere SET nom = ?, eststock = ? WHERE id = ?;

SELECT
    produit.code AS codeProduit,
    produit.nom AS nomProduit,
    etagere.id AS idEtagere,
    etagere.nom AS nomEtagere,
    CASE
        WHEN etagere.eststock IS NULL THEN ''
        WHEN etagere.eststock THEN 'stock'
        ELSE
            'magasin'
    END AS secteur
FROM
    produit
        LEFT JOIN produit_etagere ON produit.code = produit_etagere.codeproduit
        LEFT JOIN etagere ON produit_etagere.idetagere = etagere.id;