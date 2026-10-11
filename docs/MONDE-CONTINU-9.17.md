# Carte d'aventure continue — 9.17

Les vingt illustrations et la logique des quarante niveaux sont conservées. Cette passe ajoute des passages composés, des repères de chapitre et un trajet commun à Lumi, aux traces et à la caméra.

## Continuité et découverte

- `AdventurePassage` utilise un atlas transparent supplémentaire de douze accessoires : ponts, arches végétales, passages polaires, marins, cosmiques et autres portes. Les dix-neuf jonctions choisissent un passage naturel ou une entrée scénarisée selon les univers. L'atlas ne remplace aucun paysage.
- `AdventureSeam` compose ces accessoires hors du thread UI dans les raccords organiques déjà présents. Les bords reprennent les peintures voisines ; aucun filtre de flou n'est ajouté. Le raccord reste une composition des paysages existants : les perspectives originales ne sont pas redessinées.
- `AdventureChapters` pose un panneau dans chaque décor, après son entrée, du côté opposé au premier niveau. L'en-tête fixe affiche uniquement Mon aventure et la progression globale. La zone majoritairement visible se calcule indépendamment de la position réelle de Lumi.
- Six jalons signalent des découvertes ou des repères déjà illustrés, dont le trésor. Ils ne donnent aucun objet et ne promettent aucune récompense supplémentaire.
- Le bouton Lumi recentre immédiatement sur le prochain niveau disponible. Son libellé accessible précise le niveau et son monde.

## Trajets et sauvegardes

`AdventureRoute` définit les étapes et les points intermédiaires des sentiers. Les niveaux 6, 7, 11, 12 et 24 sont ajustés sur les rives ou les escaliers visibles. Les changements de zone passent par une sortie, le passage illustré et l'entrée suivante. `AdventureJourney` conserve trente-neuf Path/PathMeasure ; Lumi et les traces échantillonnent exactement ces courbes. La caméra suit leur progression verticale, avec l'horloge et les vraies frames de saut de Lumi existantes.

La victoire conserve validation, déverrouillage, déplacement puis attente. La position réelle se déduit du compteur déjà sauvegardé par AdventureStore : aucun nouveau format ou fichier persistant. Album, records, réglages et parties sauvegardées restent inchangés. Le trajet/reaction est gelé pendant l'absence de la carte et en arrière-plan, puis reprend dans la même vue. Une recréation de l'activité restaure le prochain niveau enregistré, mais pas la phase transitoire exacte d'une animation. Le retour ordinaire sur la carte recentre sur Lumi.

## Cache et rendu

- Quatre segments maximum, viewport et voisins, avec un budget de 16 Mio.
- Trois raccords composés maximum, budget total de 2 Mio.
- Atlas des passages décodé une fois hors du thread UI à demi-résolution, environ 1,57 Mio.
- Courbes, rectangles, peintures et tableaux de positions réutilisés. Aucun redimensionnement de bitmap par frame. Le rectangle temporaire de prolongement du décor a été supprimé.
- Les bitmaps publiés au renderer ne sont pas recyclés explicitement. Les références des caches sont libérées en pause. Les animations réduites restent respectées.

## Validation et limites

Voir [VALIDATION.md](VALIDATION.md) pour les résultats exacts de la compilation, des tests et des deux tailles d'émulateur. Les vingt zones ont été rendues pour contrôler panneaux et courbes ; les dix-neuf raccords et trente-neuf trajets ont des contrôles automatisés. Quatre voyages entre univers et un glissement réel sont inclus dans le scénario Android.

Le choix explicite de l'utilisateur est de continuer uniquement sur émulateur. Les durées de frames Android incluent chargements et captures et ne constituent pas une mesure de fluidité sur téléphone. Aucun parcours enfant, TalkBack manuel ou visionnage vidéo continu n'est annoncé. Les animaux intégrés aux peintures restent fixes ; les ressources nécessaires à leur animation sont décrites dans [CARTE-VIVANTE-9.16.md](CARTE-VIVANTE-9.16.md).
