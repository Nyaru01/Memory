# Mises à jour GitHub

Le dépôt public utilisé est https://github.com/Nyaru01/Memory. Le jeu cherche `releases/latest/download/update.json` une fois à chaque création de l'activité, sur un thread dédié. Il ne contient aucun jeton GitHub et n'envoie aucune progression au serveur. Une connexion sert seulement à consulter le manifeste et télécharger le correctif.

## Utilisation

Installer une fois l'APK 8.6 ou suivant pour activer le mécanisme ; les anciennes versions n'ont aucun code permettant une mise à jour automatique. La signature fixe reste celle des versions 8.3 et suivantes. Une mise à jour avec cette signature garde les préférences et l'album. La version 8.2 nécessite toujours une installation neuve à cause de sa signature différente.

Une mise à jour disponible se télécharge automatiquement au lancement. Une fois prête, la boîte « Installer / Plus tard » apparaît directement dans le jeu. Si elle est différée, le bouton « Installer la mise à jour » reste visible, hors de l'espace parents, dans une zone réservée sous le jeu. Android demande l'autorisation d'installer depuis MémoMondes et la confirmation finale d'installation. Une installation silencieuse n'est pas utilisée.

## Correctifs

`tools/apk_delta.py` compare les données compressées des entrées ZIP des deux APK signés. Les données inchangées sont copiées depuis l'APK déjà installé ; seuls les segments nouveaux sont transportés. Le flux gzip MMD1 contient une taille cible et des commandes COPY (offset, longueur), DATA (longueur, octets) et END. Les entêtes ZIP, table centrale et signature de l'APK cible sont reproduits exactement. Le générateur reconstruit indépendamment chaque correctif et compare tous les octets avec l'APK cible avant publication.

`update.json` contient `schema=1`, `packageName`, `versionCode`, `versionName`, `minSdk`, `apkSize`, `apkSha256` et une liste `patches`. Chaque correctif contient l'empreinte SHA-256 de sa base exacte, son URL, sa taille et son empreinte. Le numéro de version seul ne suffit pas : deux APK compilés différemment peuvent avoir le même numéro.

Le client limite le manifeste à 256 Ko, le correctif à 64 Mio et l'APK reconstruit à 256 Mio. Il vérifie taille, empreintes, bornes de copie, version supérieure, identité de package et certificat identique à l'installation courante. L'installateur Android contrôle à son tour la signature. Les URL initiales sont limitées aux assets HTTPS de ce dépôt ; seules les redirections HTTPS GitHub sont acceptées. Le fournisseur de fichier est privé, en lecture seule, et n'expose que l'APK prêt via une autorisation temporaire.

Les fichiers transitoires et l'APK prêt sont dans le stockage privé `noBackupFilesDir/updates`. Le correctif est supprimé après préparation ; l'APK prêt est conservé jusqu'à installation ou invalidation. Un arrêt de l'activité annule le travail ; un correctif incomplet repart au lancement suivant. Une mise à jour déjà prête reste proposée même hors ligne.

## Publier la version suivante

1. Augmenter `versionCode` et `versionName` dans `app/build.gradle` ; conserver la clé fixe sur D:.
2. Écrire les notes de la nouvelle version.
3. Exécuter :

```powershell
.\tools\publish-github-release.ps1 -Notes .\tools\release-notes-VERSION.md
```

Le script utilise Python standard installé sur D:, compile avec `tools/build.ps1`, passe les tests et lint, vérifie la signature, récupère les APK des releases publiques précédentes dans `app/build/update-bases/`, génère les correctifs, puis crée une release brouillon. Il utilise le compte GitHub déjà connecté par Git Credential Manager, sans afficher ni stocker son jeton. Il publie la release seulement après l'envoi de tous les assets. En cas d'échec d'envoi, le brouillon reste à examiner ; l'ancienne release publiée reste la dernière. Réparer ce brouillon avant une nouvelle tentative de même version.

Le manifeste cible reste associé à la version signée. Ne pas modifier un APK déjà publié ni sa clé. Les correctifs couvrent les releases stables parmi les 100 dernières releases retournées par GitHub. Une base locale additionnelle peut être donnée avec `-BaseApk`, seulement dans un dossier d'intermédiaires sur D:. Ne pas publier de clé, de cache, ni d'APK intermédiaire de test.

Un seul APK final local reste dans `APK/MemoMondes.apk`. GitHub conserve les releases précédentes : leurs APK servent à générer les correctifs pour les utilisateurs qui n'ont pas encore mis à jour. Ce sont les archives distantes, pas des copies finales dans le dossier local.

## Limites

Sans correctif correspondant à l'empreinte exacte de la base, aucun APK complet n'est téléchargé automatiquement. Le jeu continue à fonctionner et il faut installer manuellement l'APK officiel. Les changements importants d'images et sons augmentent le téléchargement ; un correctif de taille supérieure à l'APK ou à 64 Mio est refusé. L'appareil doit disposer de l'espace pour reconstruire un APK complet localement, même si le téléchargement est petit. GitHub indisponible, réseau coupé et stockage insuffisant n'empêchent pas de jouer.
