# Carte d'aventure — 9.3

La référence fournie inspire un chemin reliant des étapes sur des îles. Le dessin est original : côtes arrondies, mer claire, cinq terrains pastel, arbres discrets et Lumi auprès de l'étape actuelle. Aucun élément de Mario n'est repris.

## Parcours

Le bouton Mon aventure est placé avant les réglages de jeu libre sur l'accueil. La carte présente 40 étapes à parcourir dans l'ordre, sur cinq îles correspondant aux familles existantes. Les mondes et illustrations restent ceux de MémoMondes ; chaque monde intervient dans deux étapes. Six paires aux niveaux 1–6, huit aux 7–16, dix aux 17–28 et douze aux 29–40. Il n'y a ni chronomètre obligatoire ni limite d'erreurs.

Au départ, seul le niveau 1 est disponible. Une victoire réelle sur l'étape actuelle débloque la suivante. Les anciennes étapes restent rejouables ; leurs victoires ne permettent pas de sauter des niveaux. Le niveau 40 termine le parcours et reste rejouable. Les étapes futures affichent un cadenas et restent désactivées pour TalkBack. Leur toucher explique quel niveau terminer.

La victoire propose Niveau suivant et Voir la carte. Le bouton Continuer sur la carte rejoint l'étape courante ; l'ouverture centre le défilement autour de celle-ci. Le chemin reste parcourable par glissement et par défilement accessible. Les cibles mesurent 54 dp, le bouton de continuation 52 dp.

## Sauvegarde et architecture

AdventureStore contient la route fixe et le nombre d'étapes validées, dans la clé additive adventure_completed des préférences m. Les anciennes victoires n'attribuent aucune étape rétroactive : chaque enfant commence au niveau 1. Les IDs de mondes et les albums existants restent inchangés.

GameView conserve le numéro de la partie d'aventure dans adventureLevel et saved_adventure_level. startAdventure refuse toute étape verrouillée avant de modifier la partie. Le lancement en jeu libre remet ce numéro à zéro. L'attribution est déclenchée uniquement après découverte de toutes les paires, avec vérification du monde, du nombre de paires et du mode solo. La sauvegarde d'une partie d'aventure est validée à la reprise ; une ancienne sauvegarde sans cette clé reste du jeu libre. Quitter une étape incomplète ne la valide pas.

AdventureMap dessine la carte en Canvas avec Paint, Path et RectF réutilisés. Aucun bitmap de carte ni framework supplémentaire. Le dessin reste statique, compatible avec les préférences de réduction des animations. La remise à zéro parentale des records garde son périmètre et ne supprime pas la progression d'aventure ni les albums.

## Validation

Tests des 40 étapes dans l'ordre, verrouillage et rejeu, partie libre, partie incomplète, reprise, anciennes données, navigation de victoire et trois tailles de carte. Les résultats de compilation, instrumentation Android et captures inspectées sont consignés dans VALIDATION.md.

Limites : route linéaire de 40 étapes, sans embranchements ni éditeur. Pas de synchronisation entre appareils. Pas d'essai auprès d'enfants ou sur téléphone modeste ; les captures ne prouvent pas une expérience gestuelle complète ni un parcours TalkBack manuel complet.
