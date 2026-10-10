# Plan d'amélioration

Plan préparé et mis en œuvre le 10 octobre 2026 pour la version 8.4. L'état ci-dessous distingue les changements livrés et les validations encore dépendantes du redémarrage ou d'un appareil réel.

## État de réalisation

- [x] Installer une signature fixe et documenter l'installation neuve choisie après la signature incompatible de l'ancien APK.
- [x] Exécuter les tests des modes de jeu, sauvegardes et transitions avec Robolectric sur Android 9 et 15, et revoir les captures du rendu Canvas natif.
- [x] Affiner les particules, synchroniser le son de réussite, ajouter la teinte d'erreur et le retour de pression des boutons.
- [x] Exposer les commandes, cartes et images de l'album à TalkBack ; respecter la réduction des animations Android.
- [x] Passer les temporisations sur une horloge monotone, réutiliser la géométrie et les palettes, stabiliser le cycle de vie des sons.
- [x] Corriger la restauration des grandes grilles, ajouter une icône monochrome et garder lint actif.
- [x] Installer l'émulateur sur D:, activer HypervisorPlatform avec autorisation et préparer le lancement et l'installation automatique du jeu.
- [x] Conserver un seul APK final dans `APK/MemoMondes.apk` après les vérifications réussies.
- [x] Démarrer Android 35 après redémarrage : installation et lancement réussis, 50 contrôles instrumentés passent, captures de la paire et de la victoire examinées.
- [ ] Tester TalkBack avec un service réel et mesurer la fluidité/mémoire sur un appareil modeste. Les tests locaux ne remplacent pas ces contrôles.
- [ ] Poursuivre l'extraction de la logique de `GameView` au fil des prochaines évolutions, sans migration globale dans cette version.

## 1. Valider la version actuelle — priorité haute

- Tester l'APK sur un appareil Android ou un émulateur fonctionnel, avec les scénarios de `VALIDATION.md`.
- Contrôler les paires ordinaires et la dernière paire, les taps rapides, pause/reprise, solo, duel, coopération et mode calme.
- Vérifier le rendu sur petit écran et avec les grilles les plus denses ; conserver des captures des réussites et de la victoire.
- Clarifier la clé de signature à conserver pour les prochaines mises à jour sans perdre la progression.

Critère de fin : aucun blocage ni perte de progression, repères lisibles, vérifications Android exécutées et rendu visuel revu. L'émulateur fonctionne avec WHPX après le redémarrage autorisé ; la fluidité et TalkBack restent à contrôler sur un appareil réel.

## 2. Affiner les effets et le retour au joueur — priorité haute

- Ajuster taille et distance des particules pour éviter de masquer les cartes voisines.
- Harmoniser la durée du retournement, de l'impulsion et du son de réussite.
- Donner un retour doux sur une erreur, sans flash agressif ni effet punitif.
- Ajouter un retour de pression cohérent sur les boutons principaux.
- Vérifier que les couleurs des joueurs restent immédiatement reconnaissables pendant tous les effets.

Critère de fin : une réussite est claire sur toutes les grilles ; aucune animation ne gêne les touches ; le mode calme reste sobre.

## 3. Améliorer lisibilité et accessibilité — priorité moyenne

- Examiner contrastes, textes et zones tactiles sur les écrans de jeu, d'album et de choix des mondes.
- Exposer les cartes et actions de la vue Canvas comme éléments accessibles à TalkBack ; ajouter des libellés utiles et un ordre de navigation.
- Respecter la préférence Android de réduction des animations en complément du mode calme parental.
- Adapter les messages au solo, au duel et à la coopération sans multiplier les annonces vocales.

Critère de fin : navigation utilisable avec TalkBack, commandes faciles à toucher, aucune information transmise uniquement par la couleur.

## 4. Stabiliser et alléger le rendu — priorité moyenne

- Mesurer les temps de dessin et la mémoire sur un appareil modeste avant de modifier le moteur.
- Réutiliser les objets de géométrie et peintures qui sont recréés dans les boucles de dessin.
- Centraliser le temps des animations avec une horloge monotone pour éviter les sauts lors d'un changement d'heure.
- Vérifier les lecteurs audio et les callbacks lors des pauses, reprises et changements d'écran.
- Extraire progressivement la logique des tours et transitions de `GameView` pour la tester indépendamment.

Critère de fin : fluidité mesurée, pas de redraw continu après les animations, pas de fuite de lecteur audio, résultats de jeu inchangés.

## 5. Finir la maintenance et la livraison — priorité basse

- Traiter les avertissements lint pertinents : accessibilité de la vue, icônes adaptatives et compatibilité des écrans.
- Documenter les changements et les résultats de validation à chaque version.
- Compiler avec `tools/build.ps1` : publier seulement après le succès de compilation et lint.
- Livrer toujours `APK/MemoMondes.apk` et remplacer la version précédente ; aucun historique d'APK distribuables.

Critère de fin : un seul APK final, compilation reproductible sur D:, signature stable et compte rendu de validation à jour.

## Ordre d'exécution

Commencer par la validation réelle, puis les effets, la lisibilité et enfin les optimisations fondées sur les mesures. Chaque étape doit produire une version vérifiable avant de passer à la suivante. Toutes les installations et données des outils restent sur D:.
