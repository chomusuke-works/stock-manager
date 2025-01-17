UPDATE etagere SET nom = ?, eststock = ? WHERE id = ?;
SELECT produit.nom, sum(lot.quantite), etagere.nom FROM produit_etagere JOIN produit ON produit_etagere.codeproduit = produit.code JOIN etagere ON produit_etagere.idetagere = etagere.id JOIN lot ON produit.code = lot.codeproduit GROUP BY produit.nom, etagere.nom;