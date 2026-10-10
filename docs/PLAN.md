# Plan d'amélioration — 10 octobre 2026

Objectif : rendre les réussites plus lisibles et agréables, ranger le projet et rendre sa compilation reproductible sur D:.

- [x] Examiner les sources, les effets, les sauvegardes et les fichiers récupérés de l'APK.
- [x] Ranger l'APK 8.2 et les anciennes signatures dans `releases/archive/` (ignoré par Git).
- [x] Retirer la ressource XML vide, les références Gradle inutilisées et remplacer les constantes décompilées qui bloquent lint.
- [x] Ajouter une impulsion douce, un halo et des particules synchronisés sur la paire, puis une coche permanente.
- [x] Respecter le mode calme et les couleurs de propriété en duel.
- [x] Montrer la dernière paire avant l'écran de victoire et protéger ce délai contre les actions concurrentes et les sauvegardes.
- [x] Ajouter une entrée douce des étoiles de victoire et la disparition progressive des confettis.
- [x] Documenter l'architecture, les outils sur D: et les scénarios de validation.
- [x] Compiler les APK release, debug et de vérification ; lint : 0 erreur, 21 avertissements.
- [x] Vérifier la signature et livrer l'APK 8.3 dans `releases/`.
- [x] Exécuter les vérifications sur émulateur Android 35 après activation autorisée de WHPX et redémarrage : 50 contrôles passent, accueil, paire et victoire examinés. Voir `VALIDATION.md` pour les limites et résultats actuels.

Périmètre conservé : Android natif, jeu hors ligne, illustrations existantes, mondes, narrations et progression. Aucune dépendance applicative supplémentaire.

## Prochaine itération et règle de livraison

Le prochain plan est dans `AMELIORATIONS.md`. À partir de cette demande, l'APK final est toujours `D:\VibeCoding\Appli enfant\APK\MemoMondes.apk`. La compilation remplace ce fichier uniquement après succès et ne conserve aucune ancienne version distribuable. Les mentions de `releases/` ci-dessus décrivent le rangement historique initial.
