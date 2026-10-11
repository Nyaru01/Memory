# Choisis ton monde — finition 9.18

La passe concerne uniquement le catalogue du Jeu libre. WorldCatalog conserve ses cinq familles, ses vingt identifiants et leur répartition. La navigation et la mémorisation restent dans GameView : sélection → configuration des paires → partie, sans écran supplémentaire.

## Présentation

WorldSelector extrait le rendu de drawThemesGrid. Deux colonnes sur téléphone ; trois ou quatre lorsque la largeur utile permet de garder des cartes confortables. Les dimensions dépendent de la surface, de la densité et du facteur de texte du projet, plafonné à1,35. Les titres utilisent deux lignes si nécessaire. Marges16 dp et espaces12 dp.

Chaque carte contient une grande illustration, le titre, le compteur réel « x /12 images » et une barre de collection. La bordure teintée et la coche indiquent seulement le monde sélectionné. La famille contenant ce monde garde un rappel de sélection quand elle est fermée ; son libellé accessible précise le nom du monde sélectionné.

Les familles ont un accent pastel, une vignette et un chevron tournant. Plusieurs peuvent rester ouvertes, comme dans l'implémentation précédente. Toute la ligne est tactile. Lors de l'ouverture, l'ancrage de défilement conserve la ligne visible et laisse une place à la première rangée lorsque la hauteur le permet. Le fond du catalogue est uni et discret.

## Ressources et performance

Les vingt JPEG déjà présents dans adventure/landscapes sont lus par un worker propre au sélecteur, uniquement comme sources d'aperçus. La correspondance vers les identifiants vient de la route existante en lecture seule. Aucune modification du renderer ni des règles Aventure.

Le décodage par région limite les aperçus à320 pixels de large maximum, avec un budget total6 Mio. Aucun nouveau bitmap ou fichier graphique livré. Rectangles, chemin de clipping et peintures réutilisés ; titres découpés et mesurés lors des changements de largeur/facteur de texte, compteurs recalculés uniquement lorsque leur masque change. Des fonds pastel apparaissent brièvement pendant le premier chargement. La vue détachée libère les références et arrête le worker ; aucun bitmap déjà publié au renderer n'est recyclé explicitement.

Les paysages existants suffisent pour cette passe : aucun visuel manquant inventé. Des cadrages dédiés plus grands, si souhaités ultérieurement pour une très grande tablette, pourraient être ajoutés dans ui/world-previews/<identifiant>.webp ; ils ne sont pas nécessaires au fonctionnement actuel.

## Animation et interaction

Ouverture/fermeture190 ms basée sur le temps, chevron et apparition des cartes ; mode calme et préférence système d'animations réduites existante appliqués. Retour visuel pendant l'appui, transition courte vers la configuration au changement de monde. Aucun effet idle répétitif.

Les rectangles des cartes sont désactivés pendant l'expansion afin qu'une carte partiellement visible ne soit pas sélectionnable. Un tap commencé sur une cible qui bouge ou change de dimensions est refusé. Le glissement conserve le mécanisme actuel de suppression du clic à son relâchement. Les rectangles sont reconstruits avec le layout dessiné ; le sélecteur n'utilise pas une grille théorique séparée pour ses taps.

Voir [VALIDATION.md](VALIDATION.md) pour les résultats et les limites réelles. Aucun stockage de collection, règle de déblocage, sauvegarde de partie ou progression Aventure n'a changé.
