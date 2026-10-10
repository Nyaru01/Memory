# Règles du projet

- Pour reprendre le projet, lire d'abord [docs/PASSATION-AGENT.md](docs/PASSATION-AGENT.md) : état actuel, architecture, données, commandes et limites de validation. Vérifier ses références dans les sources avant de les considérer comme actuelles.

- Toutes les installations tierces, SDK, caches et fichiers temporaires de travail doivent être sur D:. Utiliser `tools/build.ps1` pour compiler.
- Livrer toujours l'APK final dans `D:\VibeCoding\Appli enfant\APK\MemoMondes.apk`.
- Ne conserver qu'un APK final dans `APK/`, sans versions anciennes dans `releases/` ou à la racine. Les APK générés pour compilation et tests dans `app/build/` sont des intermédiaires.
- Publier via `tools/publish-apk.ps1` après une compilation et une validation réussies. En cas d'échec, conserver le dernier APK final existant.
- Documenter les validations effectuées et les limites réelles ; ne pas annoncer une validation visuelle si elle n'a pas été réalisée.
- Conserver `D:\AndroidDev\signing\MemoMondes.keystore` : la clé est désormais fixe. Ne jamais la régénérer automatiquement.
- Le remplacement par une installation neuve a été autorisé après constat de signature différente de la version 8.2.
- Le composant Windows HypervisorPlatform a été activé avec autorisation explicite. Ne pas redémarrer Windows automatiquement. L'émulateur et ses données doivent rester sur D:.
