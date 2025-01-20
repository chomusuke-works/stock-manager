SELECT nom, lot.dateexpiration, quantite FROM lot JOIN produit ON lot.codeproduit = produit.code WHERE lot.dateexpiration - current_date BETWEEN 0 AND ?;
SELECT nom, lot.dateexpiration, quantite FROM lot JOIN produit ON lot.codeproduit = produit.code WHERE lot.dateexpiration - current_date < 0;
SELECT
    produit.nom, produitsegmentannee.cible - sum(lot.quantite) AS a_commander
FROM
    produit
        JOIN lot ON produit.code = lot.codeproduit
        JOIN produitsegmentannee ON produit.code = produitsegmentannee.codeproduit
        JOIN segmentannee ON produitsegmentannee.idsegment = segmentannee.id
WHERE
    CASE
        WHEN extract(MONTH FROM datedebut) <= extract(MONTH FROM datefin) THEN extract(MONTH FROM now()) BETWEEN extract(MONTH FROM datefin) AND extract(MONTH FROM datedebut)
        ELSE extract(MONTH FROM now()) >= extract(MONTH FROM datedebut) OR extract(MONTH FROM now()) <= extract(MONTH FROM datefin)
        END
GROUP BY
    produit.nom, produitsegmentannee.cible, produitsegmentannee.seuil, segmentannee.priorite
HAVING sum(lot.quantite) <= produitsegmentannee.seuil AND segmentannee.priorite = (SELECT max(segmentannee.priorite) FROM segmentannee);