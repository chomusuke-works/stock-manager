SELECT code, nom, prix, idfournisseur, quantite FROM produit JOIN lot ON produit.code = lot.codeproduit WHERE lot.dateexpiration - current_date <= ?;
