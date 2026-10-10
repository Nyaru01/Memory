# Usage enfant, accessibilité et reprise — 9.11

## Changements

- Les deux entrées de l'accueil restent en place. Après les 40 niveaux, le bouton indique « Revoir mon aventure » et sa description accessible annonce l'aventure terminée.
- Le texte du bouton Jeu libre est légèrement assombri pour dépasser un contraste de 4,5:1 avec son fond pastel, y compris quand le texte est réduit sur petit écran.
- La bienvenue expose le coucou de Lumi aux contrôles virtuels. Les retours vers l'accueil et le jeu libre ont des descriptions explicites.
- L'activation accessible d'un bouton partiellement masqué par un en-tête utilise le centre de sa portion visible. Les zones tactiles existantes des menus sont conservées.
- Une paire trouvée déclenche un retour haptique de confirmation (Android 11+), ou un retour bref standard sur les versions antérieures. Le réglage Vibrations et les préférences système sont respectés. Le mode calme conserve ce retour tactile si les vibrations sont activées ; aucune vibration supplémentaire sur une erreur.
- `SavedGame` isole la lecture et la validation du format existant avant mutation du GameView : dimensions, paquet exact, paires complètes, masques, propriétaires, scores, tours, compteurs et niveau d'aventure. Les anciens duels sans masque de propriétaire conservent leur migration dans l'ordre des cartes.
- Une sauvegarde invalide désactive uniquement la proposition de reprise (`game_active=false`). Son contenu reste disponible jusqu'à une nouvelle sauvegarde ; album, records, progression et réglages ne sont pas effacés. Aucune nouvelle clé ni migration de format.

## Parcours à observer avec un enfant

Sur téléphone, avec un adulte présent, sans lui montrer les boutons à l'avance :

1. Trouver comment commencer une aventure, puis revenir à l'accueil.
2. Ouvrir Jeu libre, choisir un monde et six paires, lancer la partie.
3. Trouver deux images identiques et décrire ce qui signale la réussite.
4. Quitter puis reprendre une partie, ouvrir l'album et revenir.

Noter l'âge, le téléphone, les hésitations, les textes incompris et les appuis manqués. Éviter de modifier la difficulté ou la progression sur la seule base d'une supposition.

## Parcours TalkBack à effectuer manuellement

Sur un téléphone de test : bienvenue, accueil, Jeu libre, sélection d'un monde, cartes cachées/retournées/trouvées, aide, confirmation de sortie, reprise, résultat, album et espace parents par action d'appui long. Vérifier l'ordre de focus, les annonces, le défilement, les boutons partiellement visibles et l'absence d'annonce de l'image d'une carte cachée. Refaire les étapes principales avec grande police, mode calme, animations système désactivées et vibrations désactivées.

## Validation et limites

Résultats consignés dans VALIDATION.md après exécution. Les contrôles automatiques du fournisseur d'accessibilité ne remplacent pas une écoute et un parcours TalkBack manuels. Une capture ne valide ni la fluidité ni une vibration ressentie. Aucun essai enfant ou téléphone physique n'est réalisé par ces changements.

Références de conception : [contraste minimal W3C](https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum), [retours haptiques Android](https://developer.android.com/develop/ui/views/haptics/haptic-feedback).
