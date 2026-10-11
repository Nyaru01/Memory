# Carte vivante — 9.16

Les vingt paysages, les positions des quarante niveaux et les deux entrées de l'accueil sont conservés.

## Lumi et temps

- `mascot/lumi-map-v916.png` : spritesheet RGBA supplémentaire, 4 colonnes × 4 lignes, 1266 × 1242, générée avec imagegen à partir de Lumi existante. Respiration dessinée, clignement, oreilles et saut. L'atlas original reste utilisé pour les six expressions au toucher et les autres écrans.
- `MascotAnimation.sampleMap` choisit les rectangles de frames selon les millisecondes écoulées, indépendamment du nombre d'appels au rendu. Aucun décodage, bitmap redimensionné ou reconstruction dans la boucle ; rectangles préparés au chargement. Une seule pose opaque évite les doubles visages lors des transitions.
- Attente : cycle de 12 secondes, respiration et oreilles, bref clignement, petit saut. Déplacement : quatre poses de saut ; arrivée : quatre poses sur 1,9 seconde. Au toucher : six expressions sur 1,44 seconde. L'ombre de contact accompagne le personnage.
- Pause/resume de GameView gèle les horloges carte, trajet, toucher et refus ; libère les caches de décor ; aucun rendu ni rechargement de carte pendant la pause. Au retour, les horloges sont décalées de la durée d'absence. Les bitmaps déjà publiés au Canvas ne sont pas recyclés.
- Mode calme/réduction Android : pose stable, aucun décor animé, aucun trajet ; le toucher propose une expression statique. Les gestes et la progression restent utilisables.

## Progression et décor

- Après victoire : validation lumineuse du niveau gagné pendant 600 ms, apparition lumineuse du suivant pendant 600 ms, parcours sur 2,6 secondes, célébration de 1,9 seconde puis attente. Ces états sont seulement visuels : AdventureStore reste seul propriétaire des déblocages. Le glissement ou le recentrage permet d'interrompre la mise en scène.
- Traces espacées sur la courbe du sentier, sans ruban continu. Médaillons verrouillés crème/bronze avec cerclage gravé, six détails dorés, numéro contrasté et cadenas. Cibles de 58 dp conservées.
- Papillons et autres créatures isolées : frames existantes et mouvement lent dans deux axes. Eau : reflets et scintillement discret aux points déjà identifiés. Feuilles, lucioles, particules, bulles/neige selon l'univers ; uniquement les sections visibles.
- Dégradé doux sous l'en-tête ; jointure forêt–ferme élargie de 24 % à 42 % d'un panneau à petite résolution, sous la même limite mémoire. Les bandes sont préparées hors du thread UI et mises en cache. Trois paysages maximum (16 MiB), bandes de jointure 2 MiB maximum ; atlas de Lumi supplémentaire environ 6 MiB, décodé une fois.

## Animaux peints : ressources nécessaires

Les animaux des JPEG n'ont pas de calques séparés. Leurs pixels ne sont pas animés. Les petits sprites de `wildlife-atlas.png` sont des personnages ajoutés, distincts de ceux peints.

Pour animer un animal précis du paysage, il faut une version de ce paysage sans cet animal, un atlas transparent de poses du même animal (attente, clignement, mouvement), son point d'ancrage et une ombre séparée. Ces ressources n'ont pas été remplacées ni inventées dans cette livraison.

## Validation

Voir `VALIDATION.md` pour les résultats effectifs et les limites. Les vérifications d'états et captures ne constituent pas une mesure FPS ou une observation enfant/TalkBack/téléphone physique.
