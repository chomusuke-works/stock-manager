INSERT INTO 
    etagere (id, nom, eststock) 
VALUES 
    (2, 'Produits frais', false),
    (8, 'Thé et café', false),
    (9, 'Cosmétiques', false),
    (10, 'Boissons', true),
    (11, 'Boissons', false),
    (12, 'Non-food', false),
    (13, 'Pain', false);


INSERT INTO
    fournisseur (id, nom, email, frequencecommande)
VALUES
    (1, 'kowag', 'kowag@volg.ch', 7),
    (2, 'Boulangerie Hauser', 'boulangeriehauser@bluewin.ch', 1),
    (3, 'Boucherie Nicolier', 'nicolier.kevin.121@gmail.com', 14);

INSERT INTO
    produit (code, nom, prix, idfournisseur)
VALUES
    (7610173018264, 'Morand sirop grenadine 100cl', 5.95, 1),
    (3415900030427, 'Dermophil crème mains', 12.1, 1),
    (7610108024513, 'Elmex double protection', 4.9, 1),
    (7610097196086, 'Michel Bodyguard 33cl', 2.45, 1),
    (7610054220045, 'Balthasar bougie framboise', 5.45, 1),
    (7616700242980, 'Pain bûcheron', 1.8, 2),
    (7616700242981, 'Pain Paillasse', 3.2, 2),
    (7616700242982, 'Pain Paillasse rustique', 3.5, 2),
    (7616700242979, 'Croissant beurre', 1.65, 2);

INSERT INTO 
    produit_etagere (codeproduit, idetagere) 
VALUES 
    (3415900030427, 9),
    (7610108024513, 2),
    (7610173018264, 10),
    (7610097196086, 11),
    (7610097196086, 10),
    (7616700242982, 13),
    (7610054220045, 12),
    (7616700242981, 13),
    (7616700242980, 13),
    (7616700242979, 13);

INSERT INTO 
    lot (datereception, codeproduit, quantite, dateexpiration) 
VALUES 
    ('2024-12-25', 7610173018264, 6, '2026-01-31'),
    ('2025-01-03', 3415900030427, 2, null),
    ('2024-10-25', 7610054220045, 12, null),
    ('2025-01-15', 7610097196086, 4, '2025-06-15'),
    ('2025-01-22', 7610097196086, 48, '2026-01-31'),
    ('2025-01-22', 7610108024513, 16, null),
    ('2025-01-26', 7616700242980, 4, '2025-01-26'),
    ('2025-01-26', 7616700242981, 3, '2025-01-26'),
    ('2025-01-26', 7616700242982, 3, '2025-01-26'),
    ('2025-01-26', 7616700242979, 24, '2025-01-26');

INSERT INTO
    segmentannee (id, nom, datedebut, datefin, priorite)
VALUES
    (1, 'Printemps', '2025-01-01', '2025-03-31', 1),
    (2, 'Eté', '2025-04-01', '2025-06-30', 1),
    (3, 'Automne', '2025-07-01', '2025-09-30', 1),
    (4, 'Hiver', '2025-10-01', '2025-12-31', 1),
    (5, 'Noel', '2025-12-01', '2025-12-31', 2),
    (6, 'Permanent', '2025-01-01', '2025-12-31', 0);

INSERT INTO 
    produitsegmentannee (codeproduit, idsegment, cible, seuil) 
VALUES 
    (3415900030427, 6, 12, 4),
    (3415900030427, 4, 24, 4),
    (7610173018264, 6, 12, 4),
    (7610173018264, 2, 24, 8),
    (7610054220045, 5, 36, 5),
    (7610054220045, 6, 12, 3),
    (7616700242981, 6, 4, 999999),
    (7616700242981, 2, 6, 999999),
    (7616700242981, 4, 3, 999999),
    (7616700242980, 6, 6, 999999);
