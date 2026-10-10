Mises à jour depuis GitHub : vérification et téléchargement automatique du correctif au lancement, sans télécharger de nouveau tout l’APK.

La version signée est reconstruite sur l’appareil à partir des fichiers déjà installés. Taille, SHA-256, identité et certificat sont contrôlés avant installation. Le jeu reste utilisable hors ligne en cas d’échec. Aucun téléchargement intégral de secours.

Une fois prête, la mise à jour est proposée directement dans le jeu, hors de l’espace parents. « Plus tard » laisse un bouton d’installation accessible. Android demande son autorisation pour installer ; la progression est conservée lors d’une mise à jour avec la même signature.

**Première installation :** installer une fois `MemoMondes.apk` de cette release. Les anciennes versions 8.5 et antérieures ne contiennent pas le mécanisme de mise à jour. La version 8.2 possède une autre signature et nécessite l’installation neuve déjà signalée.

Validation : 38 tests locaux passent ; correctifs reconstruits à l’identique avant publication. Les nouvelles versions nécessitent une nouvelle release avec `update.json` et les correctifs des bases supportées.
