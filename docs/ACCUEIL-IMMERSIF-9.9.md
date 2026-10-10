# Accueil illustré et carte plein écran —9.9

## Carte

Les positions du chemin et des40repères sont conservées. AdventureMap étend désormais la surface de dessin à y=0. Lorsque la première section débute plus bas, AdventureScenery prolonge la même peinture derrière l’en-tête avec un BitmapShader : pixels supérieurs exclus par le recadrage lorsque disponibles, puis miroir à la limite du bitmap. Aucun nouvel atlas de carte ni chargement de section supplémentaire. Le shader réutilise le bitmap du cache et est déréférencé à la pause.

L’ancien bandeau opaque et le début de fondu au pied de l’en-tête sont remplacés par un voile translucide sur toute sa hauteur, qui décroît jusqu’à top+44dp. Les zones tactiles de navigation et Continuer restent inchangées. Le paysage sous le bouton de9.8 est conservé.

## Lancement

Une illustration originale conçue pour l’accueil remplace le seul fond dégradé : clairière fleurie, rivière/pont, ciel et montagnes lointaines, centre libre pour Lumi. Image générée avec imagegen intégré, inspectée puis copiée dans les assets. Ressource941×1672,2465399octets ; prompt exact et mode dans WELCOME-PROMPT-9.9.md. Aucun texte ou personnage peint dans l’image.

WelcomeScreen dessine le bitmap sans déformation, recadré au ratio de l’écran ; lavis doux sur le ciel et la partie inférieure pour les textes. Lumi mesure au plus152dp (au lieu d’environ200dp), posée au milieu du sol ; elle conserve la respiration et le clignement de9.8. Aucun mouvement de caméra ni boucle de pose supplémentaire. Le titre, le nombre de mondes et la progression restent du texte et des formes Canvas nets.

PNG décodé sur le worker déjà utilisé par la carte :RGB565, demi-résolution sur les petits écrans≤550pixels, pleine résolution au-delà. Bitmap conservé inférieur à4MiB, sans ressource chargée pour chacun des40niveaux. Les bitmaps privés obsolètes sont recyclés ; les bitmaps déjà publiés sont déréférencés pour préserver les commandes GPU. Generation token protège la pause/fin de lancement. Ressource libérée en arrivant à l’accueil et à la pause. Si le décodage échoue, le fond lumineux léger reste utilisable ; le lancement ne bloque jamais pour attendre l’image.

Le timer1750ms et les paramètres sonores restent inchangés. Apparition du décor sur260ms lorsqu’il devient disponible, sans fondu en réduction d’animations. La scène est illustrée mais son paysage reste fixe ; le mouvement est celui de Lumi. Les sprite cells existantes256×256 sont conservées, donc pas de nouvelle animation squelettique.

## Validation

Le lancement utilise désormais le Canvas matériel de la fenêtre, comme la carte, pour éviter de repeindre sa grande illustration dans la couche logicielle de la vue. Les autres écrans gardent leur configuration existante.

Voir VALIDATION.md. Contrôles de peinture derrière l’en-tête et au pied de carte aux étapes1/40, illustration réellement décodée, budget mémoire, libération après lancement, scénario victoire/carte/Continue et captures attachées. Pas de téléphone physique ni d’évaluation vidéo manuelle.
