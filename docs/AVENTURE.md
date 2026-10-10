# Carte d'exploration illustrée — 9.4

## Direction artistique et ressources

Les anciens sprites de cartes et icônes n'étaient pas des paysages. Le parcours Canvas schématique de la 9.3 est remplacé par vingt scènes originales peintes : animaux, ferme, jardin miniature, jungle, dinosaures, banquise, océan, sirènes, pirates, détective, espace, magie, fées, gourmandises, maison, légumes, véhicules, musique, métiers et robots. Chaque scène dessert deux niveaux. Les ponts, cascades, maisons, palais, ateliers et autres repères sont dans les illustrations, et non simulés par des ronds Canvas.

Ressources finales : `app/src/main/assets/adventure/worlds-atlas.jpg` (atlas 4 colonnes × 5 lignes, 1122 × 1402 pixels), `creatures-atlas.png` (4 × 4, quatre poses pour chacun des papillon, luciole, poisson, méduse). Le spritesheet de Lumi existant est réutilisé pour ses vraies poses, clignements, saluts et sauts. Les scènes sont étirées modestement au format des sections ; la netteté sur tablette très haute densité peut nécessiter des exports plus grands.

Images créées avec l'outil imagegen intégré puis copiées dans le projet ; aucune génération au lancement de l'application. Le PNG de travail du paysage est sur D:/AndroidDev/tmp, le JPEG final est dans les assets.

Prompts finaux (résumé fidèle) : paysage storybook peint, chaleureux, formes rondes, enfants 5–7 ans, atlas strict 4 × 5 dans l'ordre des vingt mondes ci-dessus, repères illustrés aux côtés, corridor central pour le chemin, brume aux raccords, aucune lettre ni interface. Créatures : atlas transparent strict 4 × 4, papillon pastel battant des ailes, luciole dorée, poisson turquoise nageant, méduse rose pulsante ; quatre poses alignées par créature, padding et fond transparent.

## Architecture et animation

- AdventureStore et ses clés sont inchangés : même route, quarante étapes, mêmes règles de sauvegarde et déblocage.
- AdventureMap : composition Canvas, chemin Bézier, grandes cibles 58 dp, boutons 48/52 dp, hit testing et position caméra.
- AdventureScenery : BitmapRegionDecoder, au plus trois sections RGB565 en cache LRU ; aucun décodage du paysage complet en bitmap. Taille des régions plafonnée pour un futur atlas plus grand.
- AdventureDecor : atlas partagé sous-échantillonné, quatre poses avec transitions temporelles, trajectoires discrètes. Atmosphère Canvas : nuages, feuilles, étoiles ou bulles ; elle complète les illustrations.
- AdventureMotion : horloge monotone, transfert de Lumi durant 2,2 secondes, réaction et vitesse d'inertie. Aucun droit d'écrire la progression. La notification de victoire n'est émise que si AdventureStore a effectivement validé une nouvelle étape.
- VictoryMascot.drawMap : rendu des poses de l'atlas avec respiration, clignements, salut périodique et saut joyeux en déplacement.

Le retour à la carte après une victoire joue une seule fois le transfert entre l'ancienne étape et l'actuelle, le long de la même courbe que le chemin. Une ouverture ordinaire cadre immédiatement le niveau actuel. Le premier cadrage utilise la vraie taille à la première image. Un geste interrompt le transfert sans bloquer la navigation. Continuer lance l'étape actuelle, même pendant une animation. Le bouton cible revient à l'étape actuelle après exploration.

La carte se parcourt par glissement avec inertie amortie, ou actions accessibles de défilement. Le mode calme et la réduction système figent les effets ambiants et les poses, suppriment transfert et inertie. Aucun son ajouté. Animation suspendue lorsque l'activité est inactive ; les caches et le VelocityTracker sont libérés à la pause/détachement. Les anciens albums, records, parties et le jeu libre restent compatibles.

## Limites et validation

La carte reste un parcours linéaire de quarante niveaux ; les repères étoilés et créatures sont des surprises visuelles, sans nouvelle récompense ni règle cachée. Les scènes sont distinctes et raccordées par une brume légère, sans paysage peint continu entre les vingt panneaux. Les mesures de rendu sur émulateur ne prouvent pas les FPS d'un téléphone modeste. Aucun essai enfant ni parcours TalkBack manuel complet. Voir VALIDATION.md pour les tests et captures réellement effectués.

Les prompts finaux exacts et les dimensions réellement reçues sont conservés dans [ILLUSTRATION-PROMPTS.md](ILLUSTRATION-PROMPTS.md).
