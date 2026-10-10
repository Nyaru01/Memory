# Mascotte de victoire — Lumi

## Choix technique

Le jeu est une application Android native Java (API 23 minimum), avec une seule vue Canvas et sans dépendance de rendu externe. Les préférences son, mode calme et réduction système des animations existent déjà.

| Solution | Intérêt | Contraintes ici |
| --- | --- | --- |
| Rive | États et interactions graphiques conçus dans un éditeur | Runtime supplémentaire et fichier .riv à produire ; intégration distincte du Canvas existant |
| Lottie | Animation vectorielle issue d'un outil graphique | Runtime et export JSON requis ; réactions et accessoires demandent une composition adaptée |
| Sprites | Poses illustrées et rendu bitmap simple | Planche de poses, découpes, mémoire bitmap et variantes d'accessoires à gérer |
| Canvas natif retenu | Personnage vectoriel original, animé sans nouvelle dépendance | Animation procédurale plus simple qu'une production artistique avec rig |

Sources : [Rive Android](https://github.com/rive-app/rive-android), [Lottie Android](https://github.com/airbnb/lottie/blob/master/android.md), [optimisation des vues Android](https://developer.android.com/develop/ui/views/layout/custom-views/optimizing-view).

## Architecture

- `VictoryCelebration` gère uniquement le début unique, le message aléatoire, les réactions et la remise à zéro. Aucun Canvas, son ou référence à la vue.
- `VictoryMascot` dessine Lumi et les particules. Paint, Path et rectangles sont conservés ; aucun bitmap, chargement ou objet graphique n'est créé par image. Ce rendu vectoriel sert aussi de solution de remplacement si des sprites définitifs sont ajoutés.
- `GameView` déclenche la célébration à la transition réelle de victoire (compteur complet et délai de dernière paire écoulé). Dessiner ou revenir de l'album/histoire ne relance pas la célébration. Une nouvelle partie la remet à zéro.
- Les six phrases sont centralisées dans `VictoryCelebration`, choisies une fois par victoire, sans modification à chaque image.
- L'accessoire reçoit la couleur et l'identifiant du monde ; trois silhouettes (collier, étoile, petit chapeau) illustrent le point d'extension.

## Comportement

Lumi arrive par le bas avec un petit rebond (650 ms), saute doucement et agite les pattes (1 250 ms), puis respire, cligne des yeux et se balance. Quatorze petites étoiles/confettis au maximum accompagnent les 3,2 premières secondes. Le dessin est limité à la zone sous les scores, pour ne pas passer devant les résultats ni les commandes pendant l'entrée.

Le toucher alterne deux réactions de 850 ms ; les touches rapprochées ne créent aucune file d'animations. Le son réutilise la file audio bornée et respecte `sound`. En mode calme ou si Android désactive les animations, Lumi est statique, les particules sont absentes et le toucher alterne une expression statique avec une annonce accessible. Aucun timer de dessin de victoire n'est entretenu dans ces modes.

En mode animé, rafraîchissement ciblé à environ 30 images/s, uniquement sur l'écran de victoire et tant que l'activité est active. Aucun moteur, service ou thread d'animation supplémentaire. Les boutons Rejouer, Choisir un monde, Album et Histoire restent disponibles dès l'ouverture de l'écran. Lumi a également une action TalkBack.

## Validation

Voir `VALIDATION.md` pour les résultats de la livraison. Les tests de dessin sur émulateur ne constituent pas une validation de fluidité sur un téléphone d'entrée de gamme. Un essai sur matériel modeste et une observation de l'animation complète restent nécessaires pour confirmer ce critère en conditions réelles.


## Refonte 8.9

Le rendu vectoriel décrit ci-dessus a été remplacé par un personnage illustré en spritesheet. Consulter [MASCOTTE-SPRITES.md](MASCOTTE-SPRITES.md) pour l'architecture actuelle et la liste des frames.
