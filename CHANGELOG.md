# Changements

## 9.17 — 11 octobre 2026 — publiée sur GitHub

- Dix-neuf raccords composés avec un atlas de passages supplémentaire ; vingt paysages conservés.
- Panneaux de chapitres locaux, compteur global fixe, jalons de découverte et bouton Lumi.
- Courbes partagées par les traces, Lumi et la caméra ; placements sur les sentiers ajustés et phases gelées hors de la carte.
- Cache limité à quatre segments et leurs raccords, sans modification des formats persistants.
- [Architecture, ressources et limites](docs/MONDE-CONTINU-9.17.md). Contrôles Android sur émulateur uniquement, selon le choix de l'utilisateur.

## 9.16 — 11 octobre 2026 — publiée sur GitHub

- Carte vivante : spritesheet d'attente de Lumi à 16 frames, respiration dessinée, clignement, oreilles et sauts ; six expressions de l'atlas original au toucher.
- Validation, apparition du niveau suivant, trajet animé et célébration ; horloges gelées en arrière-plan, réduction des animations respectée.
- Papillons mobiles et reflets ; médaillons bronze, traces sans ruban, en-tête doux et fondu forêt–ferme élargi. Illustrations, disposition et stockage conservés.
- Détails et besoins graphiques pour les animaux peints : [carte vivante](docs/CARTE-VIVANTE-9.16.md).

## 9.15 — 11 octobre 2026 — publiée sur GitHub

- Carte : pastilles en pierres ovales avec biseau et ombre de contact ; niveau actuel bordé d'or, niveaux terminés marqués et verrouillés plus discrets. Cibles tactiles conservées.
- Chemin sablé translucide, traces espacées, étapes de départ et Pirates repositionnées sur les sentiers. Autres repères, paysages et progression conservés.
- Pieds et ombre de Lumi mieux intégrés ; réactions existantes conservées. Petit retour de refus sur un niveau verrouillé, respectant mode calme et réglages tactiles.
- Commandes translucides, voile d'en-tête plus court et bouton Continuer moins large. Lucioles lentes dans la végétation en remplacement des nuages génériques ; animations réduites respectées.

## 9.14 — 11 octobre 2026 — publiée sur GitHub

- Mon aventure devient la carte principale : paysage du chapitre courant, aperçu des deux étapes, Lumi discrètement animée, monde et niveau, message chaleureux, compteur sur 40 et barre de progression. Bouton de 56 dp ; Jeu libre plus compact.
- Une partie sauvegardée au niveau 1 affiche Continuer même avant la première victoire. Depuis la carte, une partie valide du niveau demandé est reprise sans recréer son jeu ; sauvegardes incohérentes refusées par le validateur existant.
- Descriptions accessibles avec monde, niveau, progression et action d'ouverture de la carte. Mode calme, animations réduites, formats persistants et collections conservés.

## 9.13 — 10 octobre 2026 — livraison locale

- Accueil : marges internes de 12 dp autour des deux boutons principaux, espace entre illustrations et boutons, même taille de texte pour les actions.
- Album, musique et parents suivent les mêmes bords ; écarts réguliers de 12 dp et textes centrés dans leurs conteneurs.
- Suppression du glissement d'entrée des cartes, qui décalait momentanément leur dessin par rapport aux zones tactiles. Mascotte et navigation conservées.
- Aucun changement des formats de stockage ni de la progression.

## 9.12 — 10 octobre 2026 — livraison locale

- Bouton Espace parents visible, maintien explicite de deux secondes ; les petits mouvements dans le bouton ne déclenchent plus un défilement annulant le maintien.
- Bouton Recommencer l’aventure dans les parents, confirmation avec boutons de 52 dp et annulation par Retour Android.
- Seuls la progression d’aventure et sa partie sauvegardée sont réinitialisés après confirmation. Album, badges, records, statistiques, réglages et sauvegarde libre conservés.

## 9.11 — 10 octobre 2026 — livraison locale

- Retour haptique de confirmation à chaque paire trouvée, selon les réglages de vibrations.
- Contrôles accessibles sur la portion visible des boutons défilants, descriptions de retour corrigées et coucou de bienvenue accessible.
- Bouton « Revoir mon aventure » après les 40 niveaux.
- Lecture des sauvegardes extraite dans SavedGame ; paquet, masques, paires, propriétaires, scores et valeurs invalides vérifiés avant restauration. Format et migration des anciens duels conservés.
- En cas de sauvegarde invalide, contenu brut conservé et proposition de reprise désactivée ; collections, progression et réglages préservés.

## 9.10 — Accueil à deux modes et coucou de Lumi

- Deux cartes illustrées, progression réelle et état de premier voyage ; aventure ouvrant la carte au prochain niveau.
- Configuration Jeu libre séparée, monde et difficulté persistés indépendamment, retours Android cohérents.
- Album partagé, déblocage d’aventure réservé aux niveaux valides ; données conservées.
- Bienvenue de cinq secondes avec huit nouvelles poses de salut, registration au sol, respiration et toucher.
- Canvas matériel sur l’accueil, deux paysages chargés hors UI, ressources bornées ; accessibilité et mode réduit adaptés.


## 9.9 — 10 octobre2026

- Paysage de carte derrière l’en-tête et jusqu’au haut de l’écran, avec un voile translucide dégradé pour conserver la lisibilité.
- Illustration d’accueil dédiée : clairière, fleurs, pont et ciel lumineux ; Lumi plus petite et intégrée au sol.
- Décodage hors UI de l’illustration, cacheRGB565 limité à4MiB, libéré à la fin du lancement et à la pause.
- Même respiration douce et bref clignement, démarrage1750ms et réduction d’animations ; progression inchangée.

## 9.8 — 10 octobre 2026

- Carte illustrée jusqu’au bas de l’écran, sous Continuer et sa marge de sécurité ; fin du parcours cadrée sans bande vide.
- Fondu doux entre l’en-tête et le paysage, sans nouvel asset ni calque bitmap.
- Chargement recomposé : fond lumineux crème/menthe, titre, Lumi au centre, texte et barre discrets ; suppression de la grande carte blanche.
- Accueil de Lumi posé au sol : respiration légère, bref clignement, aucune bascule vers une pose inclinée ni rebond artificiel.
- Même durée de démarrage, mêmes options sonores et réduction des animations ; progression inchangée.

## 9.7 — 10 octobre 2026

- Illustrations HD conservées ; raccords fondus au contour organique, préparés en arrière-plan et mis en cache.
- Quarante repères placés dans les paysages ; sentier de petites pierres aux couleurs du terrain, mêmes courbes pour Lumi.
- En-tête compact, titres uniques, bouton Continuer plus léger avec cible48dp.
- Victoire : Avancer avec Lumi ouvre la carte, trajet vers la prochaine étape et célébration d'arrivée ; Continuer reste utilisable immédiatement.
- Ombre de Lumi, réactions au toucher, seize poses de lapin/oiseau/faon/grenouille et petits reflets sur les rivières.
- Mode calme, réduction système, progression et sauvegardes conservés ; cache toujours limité aux décors proches.

## 9.6 — 10 octobre 2026

- Vingt paysages recréés individuellement en haute résolution, au lieu de vignettes de 280 pixels agrandies ; recadrage sans déformation.
- Carte Canvas accélérée par le GPU ; fond masqué et ombres floutées inutiles supprimés du rendu répété.
- Décodage des paysages et créatures sur un thread dédié, préchargement à l'accueil, cache de trois sections plafonné à 16 MiB ; chargements obsolètes annulés à la pause.
- Repos animé à 30 images/s, défilement et réactions synchronisés avec l'écran. Les 40 niveaux et sauvegardes conservent leurs règles.
- Comparaison instrumentée sur l'activité attachée en 1080 pixels de large, régression des assets HD et des cycles de chargement/libération.

## 9.5 — 10 octobre 2026

- Correction du plantage au premier accès à Mon aventure avec les animations actives : l'absence de victoire précédente ne déclenche plus un trajet depuis le niveau0 inexistant.
- Tests de première ouverture normale, répétée et dans l'activité Android réelle ; progression et déplacements après victoire conservés.

## 9.4 — 10 octobre 2026

- Carte d'exploration remplacée par vingt paysages peints distincts, chemin organique et repères illustrés ; mêmes quarante étapes et progression conservée.
- Lumi animé sur le chemin : repos, clignements, coucou, réaction et transfert joyeux vers la prochaine étape au retour après victoire.
- Papillons, lucioles, poissons et méduses sur spritesheet ; nuages et particules discrets.
- Défilement avec inertie, en-tête compact, retour au niveau actuel et Continuer accessible.
- Cache de trois sections RGB565, atlas de créatures partagé, arrêt/libération à la pause, mode calme et réduction système respectés.

## 9.3 — 10 octobre 2026

- Carte d'aventure Canvas originale : cinq îles pastel, chemin et 40 niveaux.
- Déblocage séquentiel après victoire, étapes terminées rejouables, difficulté 6/8/10/12 paires.
- Mon aventure sur l'accueil ; Niveau suivant et Voir la carte à la victoire.
- Sauvegarde indépendante de progression et reprise avec le bon numéro de niveau.
- Jeu libre, albums, records et réglages conservés ; aucun SDK ajouté.


## 9.2 — 10 octobre 2026

- Rejouer turquoise avec texte sombre, résultats menthe et étoiles dorées.
- Félicitations de Lumi adaptées à la densité, agrandies et réparties sur deux lignes au besoin.
- Transitions de sprites progressives, atterrissages continus, attente stable ; rafraîchissement de victoire synchronisé avec l'écran.
- Robots rigolos et Jardin miniature : 24 nouvelles illustrations et deux histoires originales (voix française Android).
- 20 mondes en cinq familles repliables ; identifiants et progression des 18 mondes existants conservés.


## 9.1 — 10 octobre 2026

- Chargement : pose initiale tenue 650 ms, un seul salut avec transition douce de 450 ms, puis pose conservée.
- Respiration et balancement plus lents et légers ; démarrage toujours à 1750 ms, mode calme conservé.

## 9.0 — V2 — 10 octobre 2026

- Accueil : carte du monde et progression réelle, difficultés 6/8/10/12 clairement sélectionnées, action Jouer et commandes secondaires lisibles.
- Mondes : grille de 3 ou 2 colonnes selon largeur et police, défilement accessible et sélection conservée.
- Plateau : en-tête compact, statistiques distinctes, grille adaptée sans défilement, cartes lavande et retournements doux.
- Victoire : titre explicite, nouvelles images réelles et confettis de 1,8 s ; Lumi, rejouer, choix de monde, album et histoires conservés.
- Album : première consultation des nouvelles images, dimensions adaptées et correction du choix du monde. Cache des planches limité à deux.
- Accessibilité : police système jusqu’à 135 %, états sélectionnés annoncés, défilement natif et cibles contrôlées.
- Audio : focus Android, interruption correcte et maintien parental annulé à la pause ou au relâchement.
- Aucun effacement de progression, aucune nouvelle dépendance et mêmes versions Android.

## 8.11 — 10 octobre 2026

- Vérification des mises à jour au retour au premier plan et toutes les minutes pendant que le jeu reste ouvert, sans devoir fermer et relancer l'application.
- Proposition automatique différée dans les menus pour ne pas interrompre une partie, une histoire ou le chargement ; bouton d'installation toujours disponible.
- Accueil de Lumi plus expressif : six poses, salut et petit rebond pendant le chargement existant ; mode calme et réduction des animations conservés.

## 8.10 — 10 octobre 2026

- Animations de Lumi ralenties de 50 % : apparition 1,08 s, joie 2,7 s, réaction 1,44 s ; attente plus tranquille.
- Lumi accueille le joueur dans le chargement, avec une mise en page proportionnelle et sans prolonger le délai de démarrage.
- Retournement des cartes adouci : rotation simulée, accélération/ralentissement, légère élévation et reflet doux ; rendu synchronisé aux images Android.
- Durées partagées entre le dessin, les gestes et TalkBack pour éviter l'interruption d'un retournement par une touche rapide. Mode calme conservé.

## 8.9 — 10 octobre 2026

- Refonte visuelle complète de Lumi : compagnon menthe et lilas avec volumes doux, grands yeux et expressions illustrées.
- Atlas transparent de 24 poses : idle, blink, appear, victory et touch reaction, animé sur Canvas sans bibliothèque supplémentaire.
- Timeline déterministe, fondus de poses, squash/stretch, deux bonds de victoire, respiration et effets de toucher.
- Tests des états, de la mémoire, des modes de jeu et captures de la vue réellement affichée sur Android.

## 8.8 — 10 octobre 2026

- Panneaux de mise à jour et d’autorisation Android harmonisés : cartes crème arrondies, boutons corail/violet et rappel de progression conservée.
- Bandeau compact avec version prête et bouton Installer, accessible hors de l’espace parents.
- README avec liens GitHub, téléchargement actuel, releases, suivi des problèmes et documentation.

- Lumi, mascotte de victoire vectorielle pastel : entrée, joie, attente vivante et réactions au toucher.
- Bulle de félicitations aléatoire, petits confettis et accessoires aux couleurs du monde.
- État indépendant du rendu, respect du son, du mode calme et des animations réduites, sans dépendance supplémentaire.
- Bouton Choisir un monde directement accessible après la victoire, résultats et autres actions conservés.

## 8.7 — 10 octobre 2026

- Bouton de mise à jour corail, accessible dans le jeu et dans une zone réservée pour ne pas masquer les cartes.
- Publication avec génération et validation des correctifs depuis les releases GitHub précédentes.

## 8.6 — 10 octobre 2026

- Vérification GitHub et téléchargement différentiel au lancement, sans APK complet de secours.
- Reconstruction exacte du nouvel APK signé à partir de l'installation courante ; contrôles de taille, empreintes, identité, version et certificat.
- Proposition d'installation hors de l'espace parents ; autorisation et confirmation par Android.
- Jeu utilisable hors ligne, nettoyage des téléchargements interrompus et conservation d'une mise à jour prête.
- Tests du format de correctif, du manifeste et des chemins d'échec.

## 8.5 — 10 octobre 2026

- Fond pastel lié au monde choisi, avec dégradé, courbe douce et motifs discrets.
- Boutons d'action avec un arrondi commun ; rejouer utilise le même corail que jouer, album et accueil partagent le style secondaire clair.
- Espacements et marges de victoire harmonisés ; texte de l'histoire positionné proportionnellement pour rester dans son bouton sur petit écran.

## 8.4 — 10 octobre 2026

- Signature de démonstration fixe sur D: ; installation neuve choisie pour remplacer l'ancienne version à signature différente.
- Particules mieux contenues autour des cartes, son de réussite après le retournement, teinte douce sur une erreur et boutons réactifs à la pression.
- Commandes, cartes et images de l'album exposées à TalkBack, sans révéler les images des cartes cachées.
- Préférence Android de réduction des animations prise en compte.
- Horloge monotone pour les effets, géométrie réutilisée et restauration des colonnes des grandes grilles corrigée.
- File de sons bornée, effets et appui parental annulés à la pause, lecteurs libérés à la destruction.
- Tests Robolectric avec rendu Canvas natif ; aucun ajout de bibliothèque dans l'APK applicatif.
- Émulateur Android 35 installé sur D:, activation Windows autorisée et lanceur `Tester-MemoMondes.cmd` préparé.
- APK final unique : `APK/MemoMondes.apk`, publié uniquement après succès des vérifications.

## 8.3 — 10 octobre 2026

- Célébration des paires avec impulsion, halo et particules qui s'estompent.
- Fond teinté, coche de réussite et message de félicitation ; repères de duel conservés.
- Dernière paire visible avant la victoire, sans sauvegarde accidentelle d'une partie terminée.
- Étoiles de victoire animées à l'entrée et confettis qui disparaissent progressivement.
- Respect du mode calme.
- Ancien APK et métadonnées décompilées archivés ; configuration Gradle nettoyée.
- Documentation et script PowerShell pour garder outils, caches et fichiers temporaires sur D:.
