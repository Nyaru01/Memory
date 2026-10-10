# Accueil et bordures de carte —9.8

La capture utilisateur présente les anciens cartouches Animaux/Ferme et le chemin blanc ; la9.7 les a déjà remplacés. Cette passe prolonge les illustrations existantes sous le bouton, sans modifier les quarante étapes.

## Carte

AdventureMap dessine jusqu’à H, y compris derrière Continuer et la marge basse. Le maximum de défilement place la fin de la dernière illustration au bas du viewport ; les niveaux restent sélectionnés dans la zone sûre au-dessus du bouton. L’en-tête conserve sa surface claire ; un LinearGradient de44dp, mis en cache, raccorde cette surface au paysage. Aucune composition bitmap supplémentaire par image, aucune nouvelle grande ressource. Le chargement et les caches de9.7 restent bornés.

## Accueil

WelcomeScreen est un renderer Canvas dédié. Deux shaders réutilisés après changement de taille créent un fond crème/menthe et une lumière diffuse ; suppression du panneau blanc à ombre, du badge en pilule et de la grosse barre. Marque en haut, personnage central, vingt mondes et barre de4dp en bas. Les proportions utilisent la hauteur disponible hors insets et le texte reste ajusté à la largeur.

L’atlas Lumi1536×1024 et ses cellules256×256 sont conservés. Le chargement utilise la pose neutre0 et la pose yeux fermés4 pendant140ms (1050–1190ms dans un cycle de6200ms). Respiration sur5200ms, amplitude verticale0,6%, compensation horizontale0,21% ; aucun saut, aucune rotation. La pose fermée reçoit un petit ajustement de registration pour retrouver les mêmes pieds. L’ombre se trouve réellement sous les pieds, à la base du sprite. Apparition douce sur240ms, inactive en réduction d’animations. Pas de salut utilisant une pose de réaction sans frames intermédiaires.

Le timer de lancement reste1750ms ; le mode calme/réduction système conserve la pose neutre sans respiration ni fondu. Aucun son ajouté. Les timelines de victoire et de voyage ne sont pas changées. Le fond des mondes caché derrière l’accueil n’est plus recalculé.

## Validation et limites

Voir VALIDATION.md pour les résultats réels. Captures contrôlées sur petit écran et en haute densité, vérifications de la couverture basse aux étapes1/40, réduction d’animations et arrivée sur l’accueil. Aucune vidéo évaluée manuellement ni téléphone physique testé. Il s’agit d’animation de poses et de transformations Canvas, pas d’un squelette articulé ; le bref changement vers les yeux fermés reste une image distincte. Les illustrations de carte restent des compositions distinctes avec les jonctions de9.7.
