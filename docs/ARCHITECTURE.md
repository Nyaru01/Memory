# Architecture

État actuel et procédure de reprise : [PASSATION-AGENT.md](PASSATION-AGENT.md). Les sections ci-dessous décrivent aussi des versions historiques. Depuis 9.10, l'écran7 est la carte, l'écran8 la configuration libre, l'accueil0 présente les deux modes et le lancement dure5000ms avec WelcomeMascot dédié ; les descriptions de1750ms ou de configuration sur l'accueil sont historiques.

L'application utilise Java et les API Android, sans bibliothèque applicative externe. `MainActivity` héberge une `GameView` dessinée avec `Canvas`, et transmet les événements de cycle de vie pour sauvegarder une partie et suspendre les médias.

| Source | Rôle |
| --- | --- |
| `MainActivity.java` | Activité, pause/reprise, navigation retour |
| `GameView.java` | Écrans, dessin, gestes, logique des tours, effets et médias |
| `GameModel.java` | Mélange du paquet, colonnes et comptage des paires par joueur |
| `ProgressStore.java` | Collection, badges et progression locale |
| `StoryLibrary.java` | Textes des histoires et noms des illustrations |
| `assets/` | Planches d'illustrations, musiques et narrations locales |

Les écrans sont identifiés dans `GameView` par -1 (introduction), 0 (accueil), 1 (partie), 2 (victoire), 3 (parents), 4 (mondes), 5 (album), 6 (histoire). Les données sont stockées dans les préférences Android `m`. Les formats et clés existants sont conservés.

## Évolutions 8.4

`GameAccessibility` fournit les contrôles virtuels via les API Android natives. Les cartes cachées ne révèlent pas leur image au lecteur d'écran ; une carte retournée est identifiée par son numéro d'image et son monde. Les actions utilisent les mêmes gestionnaires que les gestes tactiles. L'appui long parental reste protégé par son délai. Les images disponibles dans l'album sont agrandissables via une action accessible.

`GameView.now()` utilise `SystemClock.uptimeMillis()` pour les temporisations de l'interface. Les statistiques datées de `ProgressStore` restent indépendantes. Les rectangles des cartes et de l'album ainsi que les palettes des mondes sont réutilisés. Le mode calme et la préférence système d'animations réduites désactivent les effets mobiles ; les coches et repères statiques restent présents.

Une file audio limitée à quatre tâches avec un seul worker remplace la création d'un thread par touche. La pause annule les effets en attente, arrête le son actif et annule l'appui parental. La destruction libère aussi la synthèse vocale et ferme le worker. Le son de réussite est différé jusqu'au retournement de la seconde carte.

Les dépendances JUnit/Robolectric sont limitées aux tests et ne sont pas embarquées dans l'APK.

## Retour visuel d'une paire

`lastA`, `lastB` et `successUntil` identifient la dernière réussite. `MATCH_DURATION_MS` vaut 1 050 ms ; le retournement de la seconde carte se termine avant le départ des particules. L'impulsion est limitée à 4,5 % et revient à la taille normale. Le halo et 12 particules par carte s'estompent avec le temps écoulé, sans tirage aléatoire dans chaque image. Le dessin restaure les transformations du Canvas et l'alpha du Paint.

Les cartes trouvées gardent un fond légèrement teinté et une coche en solo/coopération. Le duel conserve les numéros et couleurs des joueurs. Un message de réussite s'affiche tant qu'aucune nouvelle carte n'est sélectionnée. Le mode calme conserve les repères statiques, sans impulsion, halo ou particules.

Pour la dernière paire, la progression et le record sont enregistrés immédiatement ; la sauvegarde de partie est effacée. `pendingWinAt` laisse la paire visible pendant 1 050 ms (450 ms en mode calme). Les gestes et le retour sont temporairement absorbés. Le prochain dessin déclenche l'écran de victoire et son annonce, sa vibration et son son. Une partie terminée ne peut pas être réenregistrée par `saveGame()` pendant ce délai.

## Maintenance

Les noms courts de variables et certains appels anciens proviennent de la décompilation. Une migration complète vers une nouvelle interface ou une nouvelle architecture dépasserait le périmètre de cette amélioration. Modifier une fonction à la fois et vérifier les modes solo, duel et coopération avant de changer le format de sauvegarde.

Les ressources `public.xml` et les icônes Android sont conservées. Les anciens certificats `META-INF` de l'APK récupéré ne participent pas à la signature d'une nouvelle compilation ; ils sont archivés hors des sources.

## Mises à jour 8.6 / 8.7

- UpdateManager : GitHub Releases public, téléchargement borné en arrière-plan, cache privé hors sauvegarde, contrôles d'identité et installation via Android.
- ApkDelta : format gzip MMD1, copie bornée depuis l'APK installé et reconstruction exacte contrôlée par SHA-256.
- UpdateFileProvider : lecture seule d'un APK prêt via une permission URI temporaire.
- MainActivity : dialogue et bouton d'installation directement dans le jeu ; une marge réserve la place du bouton.
- `tools/apk_delta.py`, `github_release.py`, `publish-github-release.ps1` : génération, reconstruction de validation puis publication des assets. Aucune nouvelle bibliothèque dans l'application.

Voir MISES_A_JOUR.md pour le protocole, les contraintes et les publications suivantes.


## Mascotte 8.8

VictoryCelebration sépare le cycle de victoire du rendu VictoryMascot. GameView orchestre les entrées, sons et préférences. Voir MASCOTTE.md pour les alternatives, les points d'extension et les limites de validation.


## Mascotte 8.9

MascotAnimation sélectionne les cinq états, les cellules d'atlas et les transitions. VictoryMascot conserve un seul bitmap de 6 Mio et rend les poses et effets sur Canvas. VictoryCelebration reste indépendant du rendu. Voir MASCOTTE-SPRITES.md.

## Accueil et vérification continue — 8.11

UpdatePoller programme une vérification au retour au premier plan et toutes les 60 secondes. Un retour rapide attend le délai minimal de 15 secondes depuis la dernière demande. onPause retire le callback ; onDestroy ferme également le worker. UpdateManager conserve son verrou de téléchargement unique et demande de ne pas réutiliser le cache HTTP. Une mise à jour prête reste dans le bandeau pendant une partie, une histoire ou le chargement ; la proposition automatique attend un menu.

MascotAnimation.sampleLoading utilise six poses pendant les 1750 ms de démarrage : redressement, sourire, salut et petit bond, avec de légers fondus, rotation et squash/stretch. Le mode calme ou la réduction système des animations utilise une pose fixe. Le même atlas et le même renderer servent au chargement et à la victoire.

## V2 — 9.0

UiStyle centralise palette, police, rayons et durées. BoardLayout calcule une géométrie de cartes unique pour dessin, gestes et lecteur d'écran. AudioFocus gère les demandes Android et empêche une reprise tardive après pause. GameView reste le moteur des écrans ; GameModel, ProgressStore et les formats de partie sont conservés. Les clés album_seen_<monde> sont additives, sans réinitialisation. Le cache des planches est limité à deux mondes. Voir V2.md pour les règles préservées, critères et compromis sur petites tailles/polices agrandies.
