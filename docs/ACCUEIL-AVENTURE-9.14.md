# Accueil : l'aventure avec Lumi

## Structure retenue

1. En-tête MémoMondes court.
2. Carte principale Mon aventure, 324 dp : titre, monde et niveau, paysage du chapitre courant et aperçu de ses deux étapes. Lumi accompagne l'étape actuelle avec son animation de carte existante. Texte chaleureux, compteur de niveaux terminés sur 40 et barre de progression. Bouton de 56 dp.
3. Carte secondaire Jeu libre, 126 dp : titre plus discret, phrase courte et bouton de 48 dp. Les réglages restent sur l'écran libre.
4. Album, musique et accès parents restent en dessous. Marges extérieures 16 dp, espaces et marges internes 12 dp ; défilement sur les écrans courts.

## États et actions

- Aucune victoire et aucune partie aventure active : « Viens explorer avec Lumi ! » / « Commencer l'aventure » / 0 sur 40.
- Une partie du niveau actuel sauvegardée, même au niveau 1 sans victoire : « Lumi t'attend. On continue ? » / « Continuer l'aventure ». La progression globale compte les niveaux terminés, pas les paires de la partie.
- Voyage en cours : monde et chapitre déduits d'AdventureStore.current(), compteur déduit de completed(). Aucune progression fictive.
- 40 niveaux terminés : « Bravo ! Quel voyage avec Lumi ! » / « Revoir mon aventure », barre pleine.
- Le bouton ouvre la carte centrée sur le niveau actuel via openAdventure(). Depuis la carte, Continuer recharge la partie sauvegardée de ce niveau via SavedGame/loadGame ; sinon démarre ce niveau. Une sauvegarde incohérente est refusée sans toucher aux collections ou à la progression.

## Architecture

HomeScreen possède le dessin et le paysage chargé hors UI. Le petit chemin est un aperçu décoratif du chapitre de deux niveaux, pas une deuxième carte interactive. VictoryMascot.drawMap fournit l'animation discrète existante ; mode calme et animations réduites la neutralisent. GameAccessibility décrit le monde, le niveau, la progression et l'action Ouvrir la carte.

GameView orchestre la navigation et la reprise. AdventureStore et les formats persistants restent inchangés ; aucun réglage libre dans la zone aventure, aucun nouvel asset ou framework embarqué.
