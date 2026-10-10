# Finition de Mon aventure — 9.7

Les vingt paysages HD de la9.6 sont conservés sans modification. Cette passe travaille la composition Canvas et le voyage de Lumi ; aucun framework supplémentaire.

## Raccords et parcours

- Les anciens cartouches par section et les bandes de brume uniformes disparaissent. Le nom du monde apparaît seulement dans l'en-tête.
- AdventureSeam prépare sur le worker une jonction de96dp environ entre deux paysages proches : prolongement des peintures avec BitmapShader, masque alpha progressif dont le contour varie horizontalement. Les bords du raccord rejoignent les images originales ; ce n'est pas une bande blanche ajoutée sur une coupure. Le résultat RGB565 est réutilisé, sans composition de grand calque à chaque image.
- Les images restent vingt compositions distinctes : ce procédé réalise un raccord visuel organique, pas une nouvelle peinture géographiquement continue ni une route générée automatiquement dans les images.
- AdventureRoute contient40repères placés manuellement dans les illustrations : arbre et pont du ruisseau, grange et pont de ferme, maison de l'escargot et mare, pont suspendu et cascade, etc. Ces repères sont projetés depuis le recadrage de référence360×400dp vers le recadrage réel du téléphone. Les intitulés sont disponibles à l'accessibilité.
- Le chemin utilise les mêmes courbes pour le dessin et le déplacement de Lumi. Fin sentier aux teintes du terrain, bord discret et petites pierres espacées ; le large trait blanc disparaît. Les cibles de niveau restent58dp.

## Interface et Lumi

En-tête de60dp au lieu de86, titre unique et progression sur deux lignes. Bouton Continuer de48dp et largeur maximale272dp, posé devant le paysage ; réserve tactile basse54dp au lieu de66. Caméra centrée sur l'étape jouable à chaque ouverture ordinaire, bouton cible pour y revenir, exploration libre toujours disponible.

À la victoire d'une étape d'aventure, le bouton principal « Avancer avec Lumi » ouvre la carte. Le départ attend la disponibilité des paysages nécessaires, sans bloquer le thread UI ; si le décodage échoue, cette attente est bornée à8secondes. Lumi attend480ms au départ, suit le sentier pendant2600ms, puis célèbre son arrivée pendant1900ms avec ses poses de joie et sept petites étoiles. Continuer lance immédiatement l'étape disponible, même pendant le trajet. Un glissement interrompt le déplacement ; toucher Lumi joue une réaction sans déclencher une partie. Le trajet est consommé une seule fois. Au dernier niveau, Rejouer conserve son fonctionnement ; le jeu libre conserve également Rejouer.

Une seule pose est dessinée à chaque instant pour garder le visage net et opaque. Le rebond, la respiration, la rotation et les déformations du corps sont interpolés en continu, sans fondre deux silhouettes différentes. Lumi utilise les24poses existantes : respiration, clignements, salut, réaction et joie ; ombre au sol adaptée à la hauteur du saut. Les papillons/lucioles/poissons/méduses conservent leurs quatre poses. Une nouvelle planche transparente4×4 ajoute quatre poses par lapin, oiseau, faon et grenouille dans les zones appropriées. Petits reflets lents sur l'eau peinte, feuilles qui dérivent et nuages. Les animaux peints dans les paysages ne sont pas tous individuellement détourés ou animés.

Mode calme et réduction des animations : ambiance et déplacements figés, pas de célébration mobile. Aucun son supplémentaire. Les boutons restent accessibles et utilisables pendant toutes les animations.

## Mémoire et validation

Même rendu Canvas matériel et même worker que la9.6. Au plus3paysages RGB565 /16MiB et3petites jonctions /2MiB en cache, plus deux planches de créatures partagées sous-échantillonnées (environ3MiB) et l'atlas Lumi déjà partagé. Les bitmaps de commandes GPU sont déréférencés sans recyclage prématuré ; les références GPU et allocations transitoires peuvent prolonger leur durée de vie. Les40grands paysages ne sont jamais décodés ensemble.

Les contrôles portent sur les raccords et leurs bords, le placement et l'ordre des40repères aux densités1/2/3, la caméra, l'annulation/reprise du worker, les commandes de victoire et le parcours réel jusqu'à l'étape suivante. Un scénario Android attaché capture départ, trajet, toucher et arrivée après une vraie partie terminée. Le profil de défilement inclut maintenant l'ambiance et l'inertie actives. Voir VALIDATION.md pour les résultats effectivement obtenus et leurs limites.
