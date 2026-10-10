# Accueil, jeu libre et bienvenue — 9.10

## Deux parcours

HomeScreen remplace le panneau de configuration par deux cartes illustrées. La première utilise le paysage du chapitre actuel (section calculée à partir du niveau AdventureStore.current), affiche le monde, les niveaux terminés sur 40 et Lumi. État neuf : « Ton voyage commence ! » et « Commencer l’aventure ». État commencé : niveau jouable et « Continuer l’aventure ». État achevé : 40 niveaux terminés, carte toujours accessible pour rejouer. Le bouton ouvre AdventureMap avec son centrage existant.

La seconde utilise le paysage marin existant et ouvre l’écran 8, Jeu libre. Le choix du monde, les quatre difficultés 6/8/10/12 et Jouer sont déplacés ici. La sélection des mondes (écran 4) revient à la configuration, y compris avec Retour Android ; Retour depuis la configuration revient à l’accueil. Le résultat libre permet de rejouer ou de revenir à cette configuration. Les deux cartes ont un bouton d’au moins 48 dp. L’accueil défile sur les petites hauteurs ; la configuration garde son en-tête fixe et fait défiler son contenu.

Album, compteur de collection, musique et espace parents protégé restent sur l’accueil. Les contrôles virtuels d’accessibilité correspondent aux nouveaux écrans. La proposition de reprise d’une partie sauvegardée reste disponible.

## Données et récompenses

Aucune remise à zéro. Les nouvelles préférences free_theme/free_pairs sont initialisées à partir des anciennes préférences lors de la première ouverture de cette version. Elles sont ensuite écrites par remember uniquement depuis la configuration libre/sélecteur de monde ; startAdventure ne les modifie pas. Les anciennes préférences et les sauvegardes de partie restent compatibles.

start conserve sa remise à zéro d’adventureLevel pour une partie libre ; startAdventure rétablit explicitement le niveau après avoir créé le plateau. unlockProgress enrichit le même album dans les deux modes : les premières images du monde jusqu’au nombre de paires sont déverrouillées (union avec les images déjà acquises). Le déblocage de carte exige toujours un niveau d’aventure valide, une partie solo achevée, le monde et la difficulté de ce niveau. Rejouer un ancien niveau ne saute pas d’étapes.

## Graphisme et mémoire

Vraies peintures de la carte existante, aucun nouveau paysage nécessaire. Deux illustrations de l’accueil décodées sur le worker existant, largeur plafonnée à 720 pixels, budget individuel du décodeur partagé. L’accueil utilise le Canvas matériel ; ses bitmaps sont déréférencés lorsqu’on le quitte ou à la pause. Les ressources privées de chargements obsolètes sont recyclées, celles publiées ne le sont pas afin de préserver les commandes GPU en cours.

Lumi sur la carte d’accueil utilise les sprites et l’animation d’attente existants. Apparition des cartes sur 420 ms, décalage de 8 dp ; mode réduit immobile. Aucun moteur externe.

Le lancement dure désormais cinq secondes, à la demande de l’utilisateur. WelcomeMascot lit huit nouvelles poses de coucou alignées, sur alpha, décodées hors UI et enregistrées à la même ligne de sol. Pose tranquille, montée de patte, coucou avec sourire et bref clignement, descente de patte et repos ; respiration à faible amplitude. Toucher Lumi rejoue le salut sans prolonger indéfiniment le lancement. Mode réduit : pose fixe. Cette animation n’utilise jamais l’état de victoire. En cas de ressource indisponible, le rendu de bienvenue antérieur reste disponible. Prompt et provenance dans WELCOME-PROMPT-9.10.md.

## Validation

Voir VALIDATION.md pour les tests réellement effectués et les limites. Les captures fixes ne prouvent pas la fluidité ressentie sur un téléphone physique.
