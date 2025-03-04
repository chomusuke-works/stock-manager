# Stock Manager (Server)

Le dossier `dist` contient plusieurs fichiers:
- `.env` les variables d'environnement correspondant aux 
  identifiants pour la base de données
- `docker-compose.yml` sert à démarrer la stack
- `Dockerfile` permet la création de l'image du service backend
- `first-launch.sh` un script qui permet d'installer l'infrastructure 
  pour la première fois, avec des données d'exemple incluses
- `script.sql` le script définissant la base de données
- `sample_content.sql` remplit la base de données de données d'exemple
- `stockmanager.jar` l'exécutable du serveur, utilisé par `Dockerfile`

Avant de lancer l'infrastructure, il convient de modifier le fichier `.env`
et d'y indiquer un nom d'utilisateur, un mot de passe ainsi que le nom de la base de données.

## Marche à suivre
1. Télécharger la dernière release du projet sur https://github.com/chomusuke-works/BDR2024_stock-manager/.
2. Dézipper le fichier téléchargé dans le répertoire de votre choix.
3. Exécuter le script `first-launch`

**Sur Windows:**
```bash
$ ./first-launch.bat
```

**Sur Linux:**
```bash
$ chmod u+x first-launch.sh
$ ./first-launch.sh
```

Tester en tapant `localhost/api/products/all` dans votre navigateur. Cela devrait afficher une liste de produits.
