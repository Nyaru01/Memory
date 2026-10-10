# Victoire et nouveaux mondes — 9.2

## Plan réalisé

1. Corriger la couleur de Rejouer et la lisibilité du message de Lumi.
2. Lisser les transitions de pose et les atterrissages, synchroniser les invalidations avec l'affichage.
3. Inventer deux mondes complets, chacun avec douze cartes et une histoire.
4. Regrouper les mondes en familles repliables, préserver les identifiants et la progression.
5. Compiler, vérifier sur Android et publier un APK signé avec la clé existante.

## Design et rendu

Rejouer utilise un turquoise pastel (#b2e5db) et un texte sombre (#20574e). Les résultats utilisent aussi un fond menthe ; l'étoile centrale devient dorée. La couleur d'alerte ne sert plus à relancer la partie.

Le message de Lumi était limité à 17 **pixels physiques**, ce qui le rendait minuscule sur les écrans denses. Il utilise maintenant 18 dp multipliés par la préférence de police, avec retour sur deux lignes. La zone de bulle occupe 34 % de l'illustration réservée. Les résultats et boutons restent hors de cette zone. Les chaînes découpées sont réutilisées.

Les fondus de sprites suivent une interpolation smoothstep pendant toute la durée d'une pose, au lieu d'une brève transition suivie d'un arrêt. Les sauts utilisent une courbe dont la vitesse verticale tend vers zéro à l'atterrissage ; les transformations commencent et terminent près du neutre. Au repos, la silhouette reste stable et conserve respiration, balancement et clignements. L'invalidation de victoire utilise postInvalidateOnAnimation au lieu d'un délai fixe de 33 ms. Cela permet de suivre le rafraîchissement de l'écran sans garantir à lui seul 60 FPS sur tout appareil. Le chargement lent de la 9.1 et les préférences de réduction des mouvements restent respectés.

Le moteur reste Java/Canvas et conserve le même atlas de Lumi. Les fondus ne remplacent pas une animation squelettique ni des dessins intermédiaires originaux. Aucun framework ou SDK ajouté.

## Mondes

Les IDs 0 à 17 ne changent pas. Les nouveaux IDs sont ajoutés :

- 18, Robots rigolos : douze compagnons robots aux silhouettes, couleurs et accessoires distincts. Histoire « Bip et le cadeau qui pousse ».
- 19, Jardin miniature : coccinelle, escargot, papillon, abeille, chenille, libellule, fleur, maison champignon, grenouille, hérisson, arrosoir, soleil. Histoire « La fête sous la feuille ».

Les deux planches 4 × 3 sont des illustrations originales générées avec imagegen, inspectées puis intégrées sous app/src/main/assets/robots.png et mini_garden.png. Les albums passent de 216 à 240 images. Les masques existants ne sont ni renumérotés ni réinitialisés. Le cache de planches reste limité à deux ; les badges utilisent le nombre réel de mondes.

WorldCatalog centralise cinq familles. Chaque en-tête est une cible de 48 dp et indique à TalkBack son état ouvrir/réduire. Les tuiles de familles fermées sont vidées pour éviter toute sélection invisible. Le glissement et le défilement accessible restent disponibles.

## Narration et limites

Les histoires originales sont intégrées en texte. Les anciens enregistrements restent intacts ; les nouvelles utilisent le fallback TextToSpeech français déjà présent. Une voix française installée est nécessaire ; il n'y a pas de nouveau MP3 embarqué. Les contrôles de contenu et de navigation ne constituent pas une écoute manuelle de cette voix sur chaque appareil.

Les preuves de compilation, tests, captures Android et publication figurent dans VALIDATION.md. Les captures vérifient la lisibilité et la disposition, pas la fluidité continue observée en vidéo. Aucun essai sur téléphone Android modeste ou auprès d'enfants n'est annoncé.
