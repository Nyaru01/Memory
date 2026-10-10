# Lumi — refonte Canvas + spritesheets (8.9)

## Proposition visuelle réalisée

Lumi est maintenant une créature fantastique moelleuse, entre un petit dragon et un compagnon de conte : corps menthe, ventre et museau crème, oreilles lilas souples, petites cornes pêche, grands yeux brillants et joues roses. Les volumes ombrés, les changements de silhouette et les expressions illustrées remplacent le personnage géométrique plat de 8.8.

Les images sont originales et générées avec l'outil imagegen intégré, puis détourées par le même outil. L'atlas de production est `app/src/main/assets/mascot/lumi-atlas.png` : PNG RGBA 1536 × 1024, grille 6 × 4, cellules 256 × 256. Les pixels extérieurs sont transparents. Chaque pose est réellement illustrée ; les transformations Canvas complètent ces poses.

## Animations et frames

Indices ci-dessous à partir de zéro, de gauche à droite puis de haut en bas.

| État | Frames | Contenu | Durée |
| --- | --- | --- | --- |
| idle | 0–3 | sourire, poitrine relevée, inclinaison gauche, curiosité à droite | cycle 5,4 s, longues pauses entre poses |
| blink | 4–5 | sourire avec yeux fermés ; ouverture/fermeture adoucies par fondu | 300 ms dans le cycle idle |
| appear | 6–11 | accroupissement, étirement, envol surpris, sourire en l'air, atterrissage écrasé, installation | 720 ms, 120 ms par pose |
| victory | 12–17 | anticipation excitée, bras levés, saluts alternés, rire yeux fermés, réception, fierté | 2 bonds de 900 ms, 150 ms par pose |
| touch reaction | 18–23 | surprise, joue touchée, rire, petit bisou, saut avec langue sortie, câlin au ventre | 960 ms, 160 ms par pose |

Les réactions alternent le sens du balancement. Aucune réaction ne s'empile : un nouveau toucher est accepté à la fin des 960 ms. En attente, respiration, léger balancement, changements de pose et clignement évitent l'impression d'une image figée. Les transitions d'images sont fondues sur 40–120 ms ; les transformations restent calculées à chaque image Canvas.

## Intégration

- `VictoryCelebration` : état de victoire, phrase tirée une fois et instant de réaction. Le rendu n'incrémente ni score ni récompense.
- `MascotAnimation` : sélection déterministe des états et des frames, fondu entre poses, déplacement, rotation et squash/stretch. Horloge monotone ; aucun timer propre, thread ou objet créé par image.
- `VictoryMascot` : atlas décodé une seule fois à la création de la vue ; rectangles source conservés, dessin `Canvas.drawBitmap` d'une ou deux frames, ombre, glow, étoiles/confettis et petit pop au toucher. Solution de secours statique si l'atlas est absent.
- `GameView` : conserve le déclenchement unique, l'ordre dernière paire → victoire, la zone dédiée et les boutons. Rafraîchissement de victoire à environ 30 images/s uniquement lorsque l'activité est active.

Le bitmap représente 6 Mio décodés, pour 24 poses. Aucun bitmap par frame, aucune nouvelle bibliothèque dans l'application, aucun chargement dans `onDraw`. Les rectangles acceptent une feuille de dimensions différentes avec la même grille. Une future planche peut remplacer le PNG en conservant l'ordre des poses. Les accessoires restent des superpositions Canvas séparées, pour éviter de dupliquer l'atlas pour chaque monde.

Le mode calme et la désactivation Android des animations montrent une pose ouverte statique. Au toucher, une autre expression est affichée sans rebond ni particules ; le son réutilise la préférence et la file audio existantes. Les boutons Rejouer et Choisir un monde, l'histoire et l'album restent utilisables.

## Spécification artistique et prompt final

La génération demandait une grille exacte 6 × 4, le même personnage complet dans chaque cellule, des poses distinctes selon le tableau, un rendu de jouet/conte en volume, une palette menthe/crème/lilas/pêche, des yeux expressifs, aucune typographie, aucun accessoire ni effet incrusté.

Prompt de correction final envoyé à l'outil intégré :

> Edit target: the complete sprite sheet. Background extraction ONLY. Preserve exactly this character identity, all 24 poses, their positions, grid geometry, image dimensions and ALL expressive faces. Remove the ENTIRE pastel blurred gradient background behind all sprites, including gaps between bodies, arms, ears, tails and cells. Deliver a genuinely transparent PNG with alpha zero everywhere outside the 24 character silhouettes, no fake transparency, no checkerboard baked into pixels, NO opaque background, no ground shadows. Keep the mint/cream/lilac character colors and softly shaded 3D toy rendering intact. This is a 6 columns x 4 rows sprite atlas for Android Canvas drawBitmap; opaque rectangular backgrounds make it unusable. Strictly no rearranging, no typography, no resizing characters, no additional objects.

## Validation

Les contrôles incluent l'intégration d'un atlas réel, le budget mémoire, toutes les frames d'apparition/victoire/réaction, le clignement, une minute de timeline, les modes de jeu, la navigation et la réduction des animations. Les captures de la vue Android attachée complètent les rendus de test hors écran. Résultats et limites de la livraison dans `VALIDATION.md`.

Un atlas de 6 poses par action avec fondus ne remplace pas une animation dessinée frame par frame à 30 poses/s. L'identité, les expressions et les poses constituent ici une vraie refonte ; la fluidité sur matériel d'entrée de gamme reste à confirmer sur un téléphone réel.


## Ajustements 8.10

La timeline est jouée à deux tiers de sa vitesse précédente (durées multipliées par 1,5) : apparition 1080 ms, victoire 2700 ms, toucher 1440 ms, cycle idle 8100 ms. Le chargement réutilise l'atlas et le dessin de sprite en mode attente, sans état de victoire ni bulle de félicitations. Le démarrage reste de 1750 ms. Aucun second bitmap n'est créé.

## Accueil 8.11

Séquence dédiée de six poses, indices 0, 1, 19, 22, 23, 0, environ 290 ms par pose avec fondus de 90 ms. Un petit bond accompagne la quatrième pose. Cette séquence tient dans le chargement existant de 1750 ms ; elle ne déclenche pas la célébration de victoire. Pose fixe en mode calme et en animations réduites.

## Accueil ralenti — 9.1

La séquence de chargement tient la pose 0 pendant 650 ms, effectue un seul fondu smoothstep de 450 ms vers la pose 19 (salut), puis conserve cette pose. La respiration est limitée à 1,2 % avec période de 7,5 s environ ; le balancement à 1,2 degré avec période de 8,8 s environ. Aucun saut ni retour cyclique entre poses pendant le chargement. Délai total de démarrage inchangé : 1750 ms. Le mode calme utilise toujours la pose fixe 0.
