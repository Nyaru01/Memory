# Changements

## 9.5 — 10 octobre 2026

- Correction du plantage au premier accès à Mon aventure avec les animations actives : l'absence de victoire précédente ne déclenche plus un trajet depuis le niveau0 inexistant.
- Tests de première ouverture normale, répétée et dans l'activité Android réelle ; progression et déplacements après victoire conservés.

## 9.4 — 10 octobre 2026

- Carte d'exploration remplacée par vingt paysages peints distincts, chemin organique et repères illustrés ; mêmes quarante étapes et progression conservée.
- Lumi animé sur le chemin : repos, clignements, coucou, réaction et transfert joyeux vers la prochaine étape au retour après victoire.
- Papillons, lucioles, poissons et méduses sur spritesheet ; nuages et particules discrets.
- Défilement avec inertie, en-tête compact, retour au niveau actuel et Continuer accessible.
- Cache de trois sections RGB565, atlas de créatures partagé, arrêt/libération à la pause, mode calme et réduction système respectés.

## 9.3 — 10 octobre 2026

- Carte d'aventure Canvas originale : cinq îles pastel, chemin et 40 niveaux.
- Déblocage séquentiel après victoire, étapes terminées rejouables, difficulté 6/8/10/12 paires.
- Mon aventure sur l'accueil ; Niveau suivant et Voir la carte à la victoire.
- Sauvegarde indépendante de progression et reprise avec le bon numéro de niveau.
- Jeu libre, albums, records et réglages conservés ; aucun SDK ajouté.


## 9.2 — 10 octobre 2026

- Rejouer turquoise avec texte sombre, résultats menthe et étoiles dorées.
- Félicitations de Lumi adaptées à la densité, agrandies et réparties sur deux lignes au besoin.
- Transitions de sprites progressives, atterrissages continus, attente stable ; rafraîchissement de victoire synchronisé avec l'écran.
- Robots rigolos et Jardin miniature : 24 nouvelles illustrations et deux histoires originales (voix française Android).
- 20 mondes en cinq familles repliables ; identifiants et progression des 18 mondes existants conservés.


## 9.1 — 10 octobre 2026

- Chargement : pose initiale tenue 650 ms, un seul salut avec transition douce de 450 ms, puis pose conservée.
- Respiration et balancement plus lents et légers ; démarrage toujours à 1750 ms, mode calme conservé.

## 9.0 — V2 — 10 octobre 2026

- Accueil : carte du monde et progression réelle, difficultés 6/8/10/12 clairement sélectionnées, action Jouer et commandes secondaires lisibles.
- Mondes : grille de 3 ou 2 colonnes selon largeur et police, défilement accessible et sélection conservée.
- Plateau : en-tête compact, statistiques distinctes, grille adaptée sans défilement, cartes lavande et retournements doux.
- Victoire : titre explicite, nouvelles images réelles et confettis de 1,8 s ; Lumi, rejouer, choix de monde, album et histoires conservés.
- Album : première consultation des nouvelles images, dimensions adaptées et correction du choix du monde. Cache des planches limité à deux.
- Accessibilité : police système jusqu’à 135 %, états sélectionnés annoncés, défilement natif et cibles contrôlées.
- Audio : focus Android, interruption correcte et maintien parental annulé à la pause ou au relâchement.
- Aucun effacement de progression, aucune nouvelle dépendance et mêmes versions Android.

## 8.11 — 10 octobre 2026

- Vérification des mises à jour au retour au premier plan et toutes les minutes pendant que le jeu reste ouvert, sans devoir fermer et relancer l'application.
- Proposition automatique différée dans les menus pour ne pas interrompre une partie, une histoire ou le chargement ; bouton d'installation toujours disponible.
- Accueil de Lumi plus expressif : six poses, salut et petit rebond pendant le chargement existant ; mode calme et réduction des animations conservés.

## 8.10 — 10 octobre 2026

- Animations de Lumi ralenties de 50 % : apparition 1,08 s, joie 2,7 s, réaction 1,44 s ; attente plus tranquille.
- Lumi accueille le joueur dans le chargement, avec une mise en page proportionnelle et sans prolonger le délai de démarrage.
- Retournement des cartes adouci : rotation simulée, accélération/ralentissement, légère élévation et reflet doux ; rendu synchronisé aux images Android.
- Durées partagées entre le dessin, les gestes et TalkBack pour éviter l'interruption d'un retournement par une touche rapide. Mode calme conservé.

## 8.9 — 10 octobre 2026

- Refonte visuelle complète de Lumi : compagnon menthe et lilas avec volumes doux, grands yeux et expressions illustrées.
- Atlas transparent de 24 poses : idle, blink, appear, victory et touch reaction, animé sur Canvas sans bibliothèque supplémentaire.
- Timeline déterministe, fondus de poses, squash/stretch, deux bonds de victoire, respiration et effets de toucher.
- Tests des états, de la mémoire, des modes de jeu et captures de la vue réellement affichée sur Android.

## 8.8 — 10 octobre 2026

- Panneaux de mise à jour et d’autorisation Android harmonisés : cartes crème arrondies, boutons corail/violet et rappel de progression conservée.
- Bandeau compact avec version prête et bouton Installer, accessible hors de l’espace parents.
- README avec liens GitHub, téléchargement actuel, releases, suivi des problèmes et documentation.

- Lumi, mascotte de victoire vectorielle pastel : entrée, joie, attente vivante et réactions au toucher.
- Bulle de félicitations aléatoire, petits confettis et accessoires aux couleurs du monde.
- État indépendant du rendu, respect du son, du mode calme et des animations réduites, sans dépendance supplémentaire.
- Bouton Choisir un monde directement accessible après la victoire, résultats et autres actions conservés.

## 8.7 — 10 octobre 2026

- Bouton de mise à jour corail, accessible dans le jeu et dans une zone réservée pour ne pas masquer les cartes.
- Publication avec génération et validation des correctifs depuis les releases GitHub précédentes.

## 8.6 — 10 octobre 2026

- Vérification GitHub et téléchargement différentiel au lancement, sans APK complet de secours.
- Reconstruction exacte du nouvel APK signé à partir de l'installation courante ; contrôles de taille, empreintes, identité, version et certificat.
- Proposition d'installation hors de l'espace parents ; autorisation et confirmation par Android.
- Jeu utilisable hors ligne, nettoyage des téléchargements interrompus et conservation d'une mise à jour prête.
- Tests du format de correctif, du manifeste et des chemins d'échec.

## 8.5 — 10 octobre 2026

- Fond pastel lié au monde choisi, avec dégradé, courbe douce et motifs discrets.
- Boutons d'action avec un arrondi commun ; rejouer utilise le même corail que jouer, album et accueil partagent le style secondaire clair.
- Espacements et marges de victoire harmonisés ; texte de l'histoire positionné proportionnellement pour rester dans son bouton sur petit écran.

## 8.4 — 10 octobre 2026

- Signature de démonstration fixe sur D: ; installation neuve choisie pour remplacer l'ancienne version à signature différente.
- Particules mieux contenues autour des cartes, son de réussite après le retournement, teinte douce sur une erreur et boutons réactifs à la pression.
- Commandes, cartes et images de l'album exposées à TalkBack, sans révéler les images des cartes cachées.
- Préférence Android de réduction des animations prise en compte.
- Horloge monotone pour les effets, géométrie réutilisée et restauration des colonnes des grandes grilles corrigée.
- File de sons bornée, effets et appui parental annulés à la pause, lecteurs libérés à la destruction.
- Tests Robolectric avec rendu Canvas natif ; aucun ajout de bibliothèque dans l'APK applicatif.
- Émulateur Android 35 installé sur D:, activation Windows autorisée et lanceur `Tester-MemoMondes.cmd` préparé.
- APK final unique : `APK/MemoMondes.apk`, publié uniquement après succès des vérifications.

## 8.3 — 10 octobre 2026

- Célébration des paires avec impulsion, halo et particules qui s'estompent.
- Fond teinté, coche de réussite et message de félicitation ; repères de duel conservés.
- Dernière paire visible avant la victoire, sans sauvegarde accidentelle d'une partie terminée.
- Étoiles de victoire animées à l'entrée et confettis qui disparaissent progressivement.
- Respect du mode calme.
- Ancien APK et métadonnées décompilées archivés ; configuration Gradle nettoyée.
- Documentation et script PowerShell pour garder outils, caches et fichiers temporaires sur D:.
