# Carte d'exploration illustrée — 9.7

La finition9.7 conserve les vingt illustrations ci-dessous et ajoute des raccords organiques, des repères manuels, une interface compacte et le voyage de Lumi à la victoire. Détails et limites : [FINITIONS-CARTE-9.7.md](FINITIONS-CARTE-9.7.md).

## Direction artistique et ressources

Les anciens sprites de cartes et icônes n'étaient pas des paysages. Le parcours Canvas schématique de la 9.3 est remplacé par vingt scènes originales peintes : animaux, ferme, jardin miniature, jungle, dinosaures, banquise, océan, sirènes, pirates, détective, espace, magie, fées, gourmandises, maison, légumes, véhicules, musique, métiers et robots. Chaque scène dessert deux niveaux. Les ponts, cascades, maisons, palais, ateliers et autres repères sont dans les illustrations, et non simulés par des ronds Canvas.

Ressources de rendu : vingt JPEG qualité94 indépendants, `app/src/main/assets/adventure/landscapes/world-00.jpg` à `world-19.jpg`. Les huit premiers font 972 × 1619 pixels, les douze suivants 1254 × 1254. Chaque environnement a été recréé avec du détail résolu, pas agrandi depuis les anciennes vignettes de 280 pixels. Le rendu recadre au centre en conservant les proportions. L'ancien worlds-atlas.jpg est conservé comme référence artistique ; il n'est plus décodé par la carte. `creatures-atlas.png` reste un spritesheet 4 × 4 avec quatre poses pour chaque papillon, luciole, poisson et méduse. Lumi conserve ses vraies poses, clignements, saluts et sauts.

Images créées avec l'outil imagegen intégré puis copiées dans le projet ; aucune génération au lancement de l'application. Le PNG de travail du paysage est sur D:/AndroidDev/tmp, le JPEG final est dans les assets.

Prompts finaux (résumé fidèle) : paysage storybook peint, chaleureux, formes rondes, enfants 5–7 ans, atlas strict 4 × 5 dans l'ordre des vingt mondes ci-dessus, repères illustrés aux côtés, corridor central pour le chemin, brume aux raccords, aucune lettre ni interface. Créatures : atlas transparent strict 4 × 4, papillon pastel battant des ailes, luciole dorée, poisson turquoise nageant, méduse rose pulsante ; quatre poses alignées par créature, padding et fond transparent.

## Architecture et animation

- AdventureStore et ses clés sont inchangés : même route, quarante étapes, mêmes règles de sauvegarde et déblocage.
- AdventureMap : composition Canvas, sentier Bézier avec petites pierres, grandes cibles 58 dp, boutons48dp, hit testing et position caméra. AdventureRoute place les40étapes près des points d'intérêt peints et partage les courbes avec Lumi.
- AdventureScenery : décodage JPEG RGB565 sur un worker unique, résolution choisie selon la largeur native de l'écran, cache de trois sections / 16 MiB maximum. Les sections visibles sont prioritaires ; une section suivante est préchargée si la capacité le permet. Aucun accès aux fichiers ni attente du worker dans le rendu. Les générations et l'identité du worker empêchent un ancien chargement de publier après libération. Les bitmaps déjà dessinés sont déréférencés sans recycle explicite pour respecter les références des display lists GPU.
- AdventureDecor : décodage hors thread UI sur le même worker, atlas partagé sous-échantillonné, quatre poses avec transitions temporelles, trajectoires discrètes. Publication atomique après initialisation des rectangles. Atmosphère Canvas : nuages, feuilles, étoiles ou bulles ; elle complète les illustrations.
- AdventureSeam : raccords de petite hauteur calculés hors thread UI, masque alpha organique et cache de3jonctions limité à2MiB ; peintures d'origine conservées.
- AdventureMotion : horloge monotone, départ480ms, transfert2600ms, célébration d'arrivée1900ms, réaction et vitesse d'inertie. Aucun droit d'écrire la progression. La notification de victoire n'est émise que si AdventureStore a effectivement validé une nouvelle étape.
- VictoryMascot.drawMap : rendu des poses de l'atlas avec respiration, clignements, salut périodique et saut joyeux en déplacement.

Le retour à la carte après une victoire joue une seule fois le transfert entre l'ancienne étape et l'actuelle, le long de la même courbe que le chemin. Une ouverture ordinaire cadre immédiatement le niveau actuel. Le premier cadrage utilise la vraie taille à la première image. Un geste interrompt le transfert sans bloquer la navigation. Continuer lance l'étape actuelle, même pendant une animation. Le bouton cible revient à l'étape actuelle après exploration.

La carte se parcourt par glissement avec inertie amortie, ou actions accessibles de défilement. Le mode calme et la réduction système figent les effets ambiants et les poses, suppriment transfert et inertie. Aucun son ajouté. Animation suspendue lorsque l'activité est inactive ; les caches et le VelocityTracker sont libérés à la pause/détachement. Les anciens albums, records, parties et le jeu libre restent compatibles.

GameView utilise LAYER_TYPE_NONE pour que Mon aventure soit dessiné sur le Canvas matériel de la fenêtre Android. Les autres écrans conservent leur rendu logiciel existant. Le fond des mondes caché derrière la carte n'est plus redessiné. Les boutons utilisent une ombre décalée discrète, sans masque flouté recalculé à chaque image. Au repos : invalidation toutes les 33 ms ; glissement, inertie, transfert et réaction de Lumi : vsync. Les JPEG et les créatures sont préchargés à l'accueil et au chargement. Budget de bitmaps conservés en9.7 :16MiB paysages +2MiB jonctions + environ3MiB pour les deux atlases de créatures + atlas Lumi partagé ; les images encore référencées par des commandes GPU peuvent vivre temporairement plus longtemps.

## Limites et validation

La carte reste un parcours linéaire de quarante niveaux ; les repères étoilés et créatures sont des surprises visuelles, sans nouvelle récompense ni règle cachée. Les scènes sont distinctes et raccordées en9.7 par de petites jonctions au masque organique préparées hors UI, sans nouvelle peinture géographiquement continue entre les vingt compositions. Les mesures de rendu sur émulateur ne prouvent pas les FPS d'un téléphone modeste. Aucun essai enfant ni parcours TalkBack manuel complet. Voir VALIDATION.md pour les tests et captures réellement effectués.

Les prompts finaux exacts et les dimensions réellement reçues sont conservés dans [ILLUSTRATION-PROMPTS.md](ILLUSTRATION-PROMPTS.md).
