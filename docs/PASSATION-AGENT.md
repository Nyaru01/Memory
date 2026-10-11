# MémoMondes — Passation à un prochain agent

## Finition carte — sources 9.15/code56 — 11 octobre 2026

- [Détails de la passe](POLISH-CARTE-9.15.md) : pierres de niveaux et ombres de contact, chemin sablé, repères de départ et Pirates ajustés, Lumi posée près de la pierre, commandes translucides plus légères et lucioles latérales. Pastilles accessibles de 58 dp ; progression et illustrations conservées.
- Retour sur niveau verrouillé dans GameView/AdventureMap : court mouvement si animations actives, message et haptique selon réglages ; aucun nouveau déblocage. Ombre en deux couches dans VictoryMascot.drawMap, sans changement des états de victoire. Idle, clignements et réactions de l'atlas existant conservés.
- Validation avant publication : 163 tests unitaires, lint zéro erreur/25 avertissements, 40 contrôles carte Android petit écran et 40 HD, captures inspectées. Voir VALIDATION.md pour les deux incidents de configuration/timing de scénario et les limites. Préférences restaurées par le scénario réussi ; viewport remis à320×640/160 et vérifié, émulateur arrêté avant compilation finale.
- La livraison et la publication GitHub de 9.15 sont réalisées ensuite ; le bloc de publication ajouté en tête doit fournir l'empreinte finale, l'URL et la preuve du test réseau.

## Mise à jour 9.14 publiée — 11 octobre 2026

**État actuel : sources sur origin/main et 9.14/code55 publiée**, https://github.com/Nyaru01/Memory/releases/tag/v9.14. Le flux releases/latest/download/update.json ordinaire renvoie bien 9.14/code55. Final unique APK/MemoMondes.apk : 102692884 octets, SHA256 `695d1395f364de8f4d685854086273d41dfd19156dcaaed69dab4eca43d772f9`. L'empreinte change depuis le bloc local ci-dessous à cause de la métadonnée Git embarquée lors de la recompilation ; aucune modification du code applicatif.

- La non-réception signalée venait de la livraison uniquement locale : publication réalisée via publish-github-release.ps1 après build/tests/lint réussis (tests et lint à jour), signature vérifiée avec la clé fixe. Sources 9cab10f envoyées sur main, puis documentation de publication ajoutée.
- 18 correctifs depuis les versions publiques 8.6 à 9.13 générés et reconstruits exactement avant publication ; depuis 9.13 : 117927 octets, depuis 9.10 : 117923. APK public retéléchargé et comparé par SHA256 au final et au manifeste ; release latest vérifiée.
- Test réseau réel Android 15 PASS : depuis la base publique 9.13, téléchargement GitHub HTTPS de 117927 octets, reconstruction exacte de 102692884 octets, package/version/signature et provider lecture seule vérifiés. Rapport releases/validation/9.14/github-update-9.13-to-9.14.txt. Base temporaire retirée de l'émulateur, émulateur arrêté. Pas de nouvelle confirmation système d'installation ni d'essai du téléphone utilisateur.
- Pour recevoir la proposition : connexion Internet, revenir dans le jeu ou le laisser au premier plan. UpdatePoller vérifie au retour (espacement minimal 15 s) puis toutes les 60 s. Les limites visuelles du bloc précédent restent valables.

## Accueil centré sur l'aventure — 9.14 locale — 11 octobre 2026

**État local actuel : 9.14/code55**, APK/MemoMondes.apk, 102692883 octets, SHA256 `a618c50e8ad24de7d8bd326f4c2c72cd42df72f95786ff4b31687c4f2da206a6`. Build/publish-apk réussis, signature vérifiée, clé fixe conservée. GitHub reste sur 9.13 ; aucune publication distante 9.14 effectuée.

- [Structure UI et états](ACCUEIL-AVENTURE-9.14.md) : HomeScreen affiche Mon aventure sur 324 dp, paysage du chapitre et ses deux étapes, Lumi, monde/niveau, phrase enfant, compteur et barre sur 40, bouton 56 dp. Jeu libre secondaire sur 126 dp. Contrôles du bas accessibles par défilement ; mêmes marges régulières.
- GameView.adventureStarted() distingue le premier niveau actif d'une aventure neuve. Le bouton ouvre la carte centrée via openAdventure() ; startAdventure reprend la sauvegarde du niveau demandé via loadGame/SavedGame lorsqu'elle existe, au lieu de recréer les cartes. Une sauvegarde refusée désactive sa reprise et conserve progression/collections. Aucun nouveau format persistant.
- 163 tests unitaires réussis, lint zéro erreur/25 avertissements. Nouveaux scénarios : niveau 1 déjà commencé, cartes et paires conservées à la reprise, chapitre avancé et 40/40. APK final installé sur émulateur Android 15 : 30 contrôles menus petit écran et 30 HD réussis, dont parents/reset ; préférences restaurées par le scénario.
- Captures réelles 320×640 et 1080×1920/densité480 inspectées, plus rendus locaux à plusieurs tailles et états. Taille/densité restaurées, émulateur arrêté. Pas de téléphone, observation enfant, TalkBack manuel ni validation vidéo/fluide des animations. Les anciens 922 contrôles généraux n'ont pas été relancés.

## Publication GitHub et sauvegarde des sources — 11 octobre 2026

**État actuel : 9.13/code54 publiée** sur https://github.com/Nyaru01/Memory/releases/tag/v9.13 et reconnue par releases/latest. APK final unique : 102692335 octets, SHA256 `ad7a1664f5509d36762cee7e510271764621f85584a325ca6882929746f972a6`. La recompilation de publication a changé la métadonnée de révision Git embarquée, donc l'empreinte diffère de la livraison locale du 10 octobre sans modification du code applicatif.

- Sources complètes, assets, tests et scripts envoyés sur `origin/main`, https://github.com/Nyaru01/Memory.git. Historique local relié à l'historique distant par fusion sans réécriture ; commit applicatif publié `d3c2cc9` (la documentation de publication est ajoutée ensuite). Branche locale `codex/memomondes-v2` ; `git remote -v` n'est plus vide. Présence de GameView, HomeScreen et build.ps1 vérifiée dans l'arbre public.
- Publication via tools/publish-github-release.ps1 : build/tests/lint réussis (tests et lint à jour), signature vérifiée avec la clé fixe ; 17 correctifs de 8.6 à 9.10 générés et reconstruits exactement avant envoi. Depuis 9.10 : 117318 octets. Release, APK et update.json publiés ; APK public retéléchargé et comparé au final par SHA256 ; manifeste latest ordinaire vérifié code54/empreinte identique.
- Les limites de validation UI du bloc 9.13 ci-dessous restent applicables : aucun nouveau contrôle Android attaché ni essai téléphone/TalkBack. Aucun test réseau Android supplémentaire de mise à jour réalisé lors de cette publication.
- APK, caches, fichiers de compilation, pièces jointes et clé de signature restent exclus du Git. La clé fixe est uniquement sur D:/AndroidDev/signing ; conserver sa sauvegarde séparément. Les APK de vérification et bases des correctifs sont des intermédiaires dans app/build/.

## Mise à jour de reprise — 9.13 locale — 10 octobre 2026

**Ce bloc remplace l'état 9.12 ci-dessous, conservé comme historique.** Sources et final : **9.13 / versionCode 54**, `APK/MemoMondes.apk`, 102692335 octets, SHA256 `504ea37610ba701cbfc54104f8f757a203f8d4422af5eb298b50f64ac4d1d5e9`. Compilé via build.ps1 et livré via publish-apk.ps1 ; signature vérifiée, clé fixe conservée, un seul final. Aucun déploiement GitHub 9.13.

- HomeScreen seulement : bords communs de 16 dp, espaces de 12 dp, boutons principaux contenus avec des marges identiques et séparés des illustrations. Textes album/parents centrés ; même typographie des deux actions principales. Glissement d'entrée retiré pour ne plus décaler les cartes de leurs zones tactiles. Navigation, progression et stockage inchangés.
- 155 tests unitaires sans échec/erreur, lint zéro erreur et 25 avertissements ; rendus Robolectric à 320×480, 360×640 et 412×850 inspectés. Compilation finale réussie ; tests/lint à jour lors de cette compilation.
- Aucun nouveau test Android attaché : deux lancements GPU host bloqués par la mémoire Windows, une tentative swiftshader démarrée puis arrêtée sur erreur d'allocation. Processus de test terminés avant la compilation finale ; aucun changement du script ou de la configuration AVD persistante. Les contrôles Android 9.12 restent historiques. Pas de téléphone, TalkBack manuel, capture de l'activité réelle ou validation de fluidité 9.13. Voir [VALIDATION.md](VALIDATION.md).

## Mise à jour de reprise — 9.12 locale — 10 octobre 2026

**Ce bloc remplace l'état 9.11 ci-dessous, conservé comme historique.** Sources et final : **9.12 / versionCode 53**, `APK/MemoMondes.apk`, 102692444 octets, SHA256 `93735cbd06b303cb4ac142741040d582f8302e6ad101a9e7eaa09618df306383`. Compilé avec build.ps1, livré via publish-apk.ps1, signature vérifiée et clé fixe conservée. Aucun déploiement GitHub 9.12.

- Espace parents : bouton de 56 dp avec maintien explicite de 2 secondes. Petits mouvements dans le bouton tolérés avant traitement du scroll ; relâchement après ouverture consommé, pour éviter un appui sur un réglage dessous. Appui court/CANCEL/pause restent protégés.
- « Recommencer l'aventure » : confirmation, Annuler et Retour Android ; remet adventure_completed au niveau zéro (prochain niveau 1), retire uniquement une partie sauvegardée d'aventure et son mouvement de carte. Album, badges, records, statistiques, réglages et sauvegarde libre conservés. AdventureStore.reset() possède la mutation persistante ; GameView synchronise son état.
- 155 tests unitaires sans échec/erreur, lint sans erreur (25 avertissements), 30 contrôles Android attachés par taille petit écran et HD, dont vrai maintien et reset confirmé. Les scénarios restaurent les préférences de l'émulateur. Les 922 contrôles généraux cités pour 9.11 n'ont pas été relancés pour 9.12.
- Captures parents petit écran/HD et confirmation petit écran inspectées. La capture de confirmation HD est noire : aucune validation visuelle revendiquée pour cette capture. Aucun essai manuel sur le téléphone utilisateur ni TalkBack manuel.
- Tests et émulateur doivent tourner séparément sur ce PC : une exécution simultanée a épuisé la mémoire Windows/Java. Voir [VALIDATION.md](VALIDATION.md), section 9.12, pour les incidents de fixtures et détails. Émulateur arrêté après restauration de taille/densité, avant compilation finale.

## Mise à jour de reprise — 9.11 locale — 10 octobre 2026

**Ce bloc remplace les références d'état de la photographie 9.10 ci-dessous.** Version actuelle des sources et de `APK/MemoMondes.apk` : **9.11 / versionCode 52**, 102691581 octets, SHA256 `5f0efeeac6d9a14bd1e10430f417b388f057830737528594778b0e3ae94283b1`. Livraison locale via build.ps1/publish-apk.ps1 ; aucune publication GitHub 9.11 ni validation réseau de son correctif dans cette tâche. Les références GitHub et mesures 9.10 plus bas sont historiques.

- Travail autorisé par l'utilisateur : usage/accessibilité pour 5 et 7 ans, sauvegardes incohérentes, extraction ciblée et vibration sur une paire trouvée. Voir [détails et essais manuels à effectuer](USAGE-ACCESSIBILITE-9.11.md).
- `SavedGame` valide la reprise avant de modifier le GameView ; format existant conservé, migration des anciens duels identique. Une reprise invalide désactive `game_active` sans supprimer le contenu brut ni les collections/réglages/progression.
- Retour haptique de confirmation sur correspondance, selon Vibrations et réglages Android. Mode calme conserve le retour tactile si activé. Contraste du bouton Jeu libre 4,92:1 ; deux entrées inchangées. Après 40 niveaux : « Revoir mon aventure ».
- Contrôles virtuels : bienvenue accessible, descriptions de retour et aventure terminée, activation sur la portion visible d'un contrôle après défilement.
- Validation : 147 tests unitaires sans échec/erreur, lint sans erreur (25 avertissements), 922 contrôles Android généraux, 21 contrôles de menus par taille petit écran/HD ; captures d'accueil et de configuration inspectées. Signature APK vérifiée. Consulter la section 9.11 de [VALIDATION.md](VALIDATION.md) pour les détails, incidents de scénario et limites.
- Final release installé avec succès, 25 contrôles de bienvenue passés. L'émulateur s'est arrêté deux fois en fin de session ; aucun résultat revendiqué pour le contrôle supplémentaire des menus en release. Les contrôles de menus petit écran/HD utilisent la build debug des mêmes sources finales. Taille/densité restaurées et vérifiées avant le second arrêt.
- Aucun essai enfant directement observé, TalkBack manuel complet, téléphone physique, ressenti vibratoire ou vidéo évaluée. Les tests automatiques ne remplacent pas ces observations. Aucune suppression de données de téléphone.
- Aucun changement des règles de progression, de l'album, de clé de signature ou de configuration SDK/caches. Git reste local sans remote ; dernier commit à consulter dans `git log`.

## Photographie de livraison précédente — 9.10

État vérifié le **10 octobre 2026**, après livraison de **9.10 / versionCode 51**. Ce document est le point d'entrée de reprise ; les documents historiques restent utiles mais leurs descriptions d'anciens écrans ou durées ne doivent pas remplacer l'état présent. Relire les sources et AGENTS.md avant une modification.

## 1. Projet et livraison actuelle

- Workspace : `D:\VibeCoding\Appli enfant` ; shell PowerShell.
- Android Java, activité `fr.memomimi.app.MainActivity`, interface personnalisée `GameView` / Canvas. Aucun framework UI externe, Rive ou Lottie embarqué.
- SDK minimum 23, compilation/cible 35. Dépendances JUnit/Robolectric uniquement pour les tests.
- Sources reconstruites après perte du projet original, à partir de l'APK 7.2. Certains noms courts et commentaires de décompilation sont hérités ; ne pas confondre cette histoire avec un projet Compose/Flutter.
- APK final unique : `APK/MemoMondes.apk`, **102690679 octets**.
- SHA256 de la 9.10 : `aa4db2d69a30957ccedfd78305d94f56e0380100501c073f3f08fe9ccba8f787`.
- Distribution : https://github.com/Nyaru01/Memory/releases/tag/v9.10 ; le flux `releases/latest` a été vérifié sur cette version.
- Branche locale : `codex/memomondes-v2`. Dernier commit applicatif : `f09066f` ; précédents : `df6d10a` (9.9), `aef59aa` (9.8). Le commit de cette passation viendra après ces références.

**Point de récupération essentiel, actualisé le 11 octobre :** le dépôt public contient désormais les sources, assets, tests et scripts ainsi que la documentation ; clonage depuis https://github.com/Nyaru01/Memory.git possible. Le SDK, les caches et la clé fixe ne sont pas dans Git : les rétablir séparément sur D: pour compiler. L'ancien état 9.10 sans sources distantes est historique.

## 2. Contraintes utilisateur à conserver

- Lire [AGENTS.md](../AGENTS.md). Tous les SDK, installations tierces, caches et temporaires de travail doivent rester sur **D:** ; outil principal : `D:\AndroidDev`.
- Compiler avec `tools/build.ps1`. Livrer avec `tools/publish-apk.ps1` ; le build appelle ce script lorsqu'il contient `:app:assembleRelease`.
- Un seul APK final dans `APK/`. Aucun ancien APK final dans la racine ou `releases/`. `app/build/` contient des intermédiaires, y compris les bases nécessaires aux correctifs.
- Conserver la clé **`D:\AndroidDev\signing\MemoMondes.keystore`**. Ne jamais la régénérer automatiquement. Le Gradle existant décrit la configuration de signature ; ne pas publier la clé ou des credentials.
- La signature de 8.2 différait ; une installation neuve avait été autorisée pour ce cas précis. Ce n'est pas une autorisation générale de désinstaller ou d'effacer les données lors des mises à jour actuelles.
- Windows HypervisorPlatform a été activé avec autorisation. Pas de redémarrage automatique de Windows. Émulateur et données sur D:.
- Préserver les sauvegardes, l'album, les records et les réglages. Ne pas utiliser `pm clear` ou une désinstallation comme réparation de routine.
- Le rendu doit rester pastel, doux, arrondi, lisible pour 5–7 ans, avec zones tactiles d'au moins 48 dp. Illustrations réelles pour les paysages ; des formes Canvas seules ne remplacent pas ces peintures.
- Respecter son, mode calme et réduction système des animations. Éviter flashs, fortes oscillations et navigation bloquée par la célébration.
- Rapporter les tests réellement passés et leurs limites ; une capture fixe n'est pas une validation vidéo, ni une preuve de fluidité sur téléphone.

## 3. Parcours et écrans actuels

| ID GameView.screen | Écran | Navigation |
| --- | --- | --- |
| -1 | Bienvenue illustrée | 5 secondes, puis accueil ; toucher Lumi rejoue le salut sans prolonger le timer |
| 0 | Accueil à deux modes | Mon aventure, Jeu libre, album, musique, parents protégés |
| 1 | Partie | Retour ouvre une confirmation ; sauvegarde/reprise existantes |
| 2 | Résultat/victoire | Aventure : carte/voyage ; libre : rejouer ou revenir à la configuration |
| 3 | Parents | Appui long depuis accueil ; réglages, statistiques, tutoriel |
| 4 | Sélection des mondes | Familles dépliables ; retourne à l'écran 8 après sélection ou Retour |
| 5 | Album | Retour vers `albumReturnScreen` ; images, mondes, badges et agrandissement |
| 6 | Histoire | Retour vers résultat ; narration existante |
| 7 | Carte d'aventure | 40 étapes, Continuer, recentrage, Retour accueil |
| 8 | Configuration Jeu libre | Monde illustré, 6/8/10/12 paires, difficulté, Jouer ; Retour accueil |

L'accueil n'est plus un panneau de réglages. La grande carte Mon aventure affiche le chapitre actuel et les niveaux terminés sur 40. État neuf : « Ton voyage commence ! » / « Commencer l'aventure ». En cours : prochain niveau et « Continuer l'aventure ». Après 40 : état terminé, carte encore accessible pour rejouer.

Le bouton aventure appelle `openAdventure()`, qui utilise `AdventureStore.current()` et le centrage existant. Le bouton libre appelle `openFreePlay()`. La sélection des mondes utilise `worldReturnScreen=8`. Les réglages ne doivent pas revenir sur l'accueil.

## 4. Sources à modifier selon le besoin

Toutes les classes applicatives sont dans `app/src/main/java/fr/memomimi/app/`.

| Composant | Responsabilité |
| --- | --- |
| MainActivity | Cycle de vie, Retour Android, panneau de mise à jour |
| GameView | Orchestration des écrans/gestes, start, startAdventure, unlockProgress, sauvegarde, médias |
| SavedGame | Lecture/validation complète du format de reprise existant avant affectation au GameView, migration des anciens propriétaires en duel |
| HomeScreen | Composition des deux cartes illustrées, chargement de deux peintures, footer, marges et alignement des contrôles |
| WelcomeScreen | Clairière de lancement, textes, barre, chargement/libération des ressources |
| WelcomeMascot | Nouvel atlas de coucou, sélection de poses, pieds enregistrés, respiration ; durée 5000 ms |
| VictoryCelebration | État de victoire et réaction, indépendant du rendu |
| VictoryMascot / MascotAnimation / SpritePose | Atlas historique, victoire, toucher, attente et Lumi sur la carte ; choix d'une pose nette |
| AdventureStore | Route stable, 40 niveaux, verrouillage séquentiel, niveau actuel |
| AdventureMap / AdventureRoute | Layout, chemin, repères, scroll/inertie, touch, focus/caméra |
| AdventureMotion | Victoire → départ → trajet → arrivée ; consommation unique du déblocage |
| AdventureScenery / AdventureSeam | Décodage worker, cache borné, peinture proportionnelle, raccords, prolongement haut |
| AdventureDecor | Créatures/effets ambiants selon les zones |
| GameModel / BoardLayout / CardMotion | Paquet, géométrie commune aux gestes/dessin/accessibilité, retournement |
| ProgressStore / WorldCatalog | Collection, statistiques/badges et familles/20 mondes |
| GameAccessibility | Boutons virtuels Canvas, mêmes actions que le tactile, descriptions et scroll |
| AudioFocus / StoryLibrary / NewWorldStories | Focus audio, histoires/narrations et nouveaux contenus |
| UpdatePoller / UpdateManager / ApkDelta / UpdateFileProvider | Vérification, téléchargement différentiel, reconstruction, installation Android |

La carte, l'accueil et le lancement utilisent le Canvas matériel de la fenêtre (`LAYER_TYPE_NONE`). Les autres écrans gardent leur configuration de rendu existante. Ne pas changer toutes les couches sans vérifier les effets et performances.

## 5. Données et invariants de progression

Stockage local : SharedPreferences **`m`**. Les formats existants sont conservés, avec ajouts de clés.

- `adventure_completed` : niveaux terminés, borné 0–40. Prochain jouable = min(40, terminés+1).
- `adventureLevel` : état du GameView ; 0 signifie partie libre. `start()` le remet à 0 ; `startAdventure(level)` crée le plateau puis rétablit et sauvegarde le niveau.
- Déblocage : uniquement le niveau immédiatement suivant, partie solo terminée, monde et nombre de paires exactement ceux de ce niveau. Rejouer un ancien niveau ne saute pas d'étapes.
- `free_theme` / `free_pairs` : choix libres persistants, initialisés une seule fois depuis les anciennes clés `theme` / `pairs`. `remember()` les écrit depuis les écrans 8/4 lorsque `adventureLevel==0`. Une aventure ne doit pas les écraser.
- `saved_adventure_level`, `game_active`, `saved_cards`, `saved_matched`, scores/tours/monde/difficulté : sauvegarde/reprise du plateau. Vérification des valeurs avant reprise.
- `album_<monde>` : masque des images déjà obtenues. **Les deux modes enrichissent le même album** ; les premières images jusqu'au nombre de paires sont ajoutées par union, sans retirer les anciennes.
- `album_seen_<monde>` : suivi des images vues, ajouté sans remise à zéro. 20 mondes × 12 images = 240.
- `best_<paires>`, statistiques/badges, `sound`, `calm`, haptics, `tutorial_done` : conserver les clés existantes.

Ne pas dupliquer les règles d'album ou de déblocage dans le renderer des nouveaux menus. Lire `unlockProgress()` et les tests AdventureTest avant une modification de victoire. Les modes duel/coopération restent dans le moteur et la suite de régression, même si l'accueil actuel propose ces deux entrées solo.

## 6. Images, animation et mémoire

- `app/src/main/assets/adventure/landscapes/world-00.jpg` à `world-19.jpg` : 20 grandes peintures. **L'index est celui de la route/section, pas directement l'ID du monde.** Chapitre du niveau = `(level-1)/2`, puis `AdventureStore.world(level)` donne le monde du plateau.
- Carte : 3 paysages / budget 16 MiB ; 3 raccords / 2 MiB ; deux atlases de créatures partagés, environ 3 MiB. Ne jamais charger 40 grandes peintures simultanément.
- `AdventureScenery` décode hors UI en RGB565, proportion conservée par recadrage ; génération protège les callbacks après release. `extendTop()` réutilise le premier bitmap et ses pixels supérieurs, puis miroir à sa limite. Un voile translucide dégradé protège la lecture du header. Paysage jusqu'en bas, sous Continuer.
- Les peintures de sections sont distinctes et raccordées ; ne pas promettre une géographie peinte continue. Les titres de section redondants ont été retirés ; ne pas les réintroduire.
- HomeScreen : deux peintures existantes, section courante et décor marin, largeur plafonnée à 720 px ; worker partagé, références libérées en quittant le menu/à la pause. Apparition de 420 ms avec décalage de 8 dp ; Lumi en attente discrète.
- `mascot/lumi-atlas.png` : atlas historique 6×4, 24 poses, cellules 256 px. Pour victoire, trajet et mascotte du menu.
- `mascot/lumi-welcome-v910.png` : huit nouvelles poses de coucou, grille 4×2, alpha ; 1465788 octets sur disque, bitmap limité à 8 MiB. WelcomeMascot enregistre le sol à partir de l'alpha pour éviter un déplacement des pieds entre poses.
- `welcome/clearing-v99.png` : clairière dédiée, 941×1672, 2465399 octets ; RGB565, demi-résolution sur petits écrans, inférieur à 4 MiB conservés.
- Bienvenue : pose tranquille, patte levée, salut/sourire/clignement, retour au repos, respiration. Toucher rejoue le coucou ; pas d'état de victoire ni son ajouté. Réduction : pose fixe. Fallback ancien renderer si le nouvel atlas manque.
- Ne pas mélanger les visages de deux poses transparentes : la précédente tentative de fondu donnait une double image. SpritePose choisit une pose nette ; les transformations corporelles sont temporelles.
- **Ne pas recycler un bitmap déjà publié au renderer/GPU** : une commande peut encore le référencer. Déréférencer les ressources publiées ; recycler seulement les décodages privés abandonnés. Le crash de carte corrigé précédemment rend ce point important.
- Assets créés via imagegen intégré et copiés dans le projet, sans dépendance d'exécution. Prompts : [accueil 9.9](WELCOME-PROMPT-9.9.md), [coucou 9.10](WELCOME-PROMPT-9.10.md), [paysages](ILLUSTRATION-PROMPTS.md).

## 7. Compiler et tester

Commandes depuis la racine du workspace, en PowerShell. Pas d'installation d'un autre SDK/JDK sur C:.

```powershell
# Tests unitaires + release + lint ; copie finale via publish-apk.ps1.
& tools/build.ps1

# Construire aussi les contrôles Android.
& tools/build.ps1 -Tasks ':app:testDebugUnitTest', ':app:assembleRelease', ':app:assembleDebugAndroidTest', ':app:lintRelease'

# Démarrer l'émulateur dédié si nécessaire ; peut installer/lancer le final.
& tools/start-emulator.ps1 -Headless
& tools/install-on-emulator.ps1

$memoAdb = 'D:/AndroidDev/sdk/platform-tools/adb.exe'
& $memoAdb -s emulator-5554 install -r APK/MemoMondes.apk
& $memoAdb -s emulator-5554 install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
& $memoAdb -s emulator-5554 shell am instrument -w fr.memomimi.app.test/fr.memomimi.app.VisualEffectsChecks
& $memoAdb -s emulator-5554 shell am instrument -w -e mode home fr.memomimi.app.test/fr.memomimi.app.VisualEffectsChecks
& $memoAdb -s emulator-5554 shell am instrument -w -e mode welcome fr.memomimi.app.test/fr.memomimi.app.VisualEffectsChecks
& $memoAdb -s emulator-5554 shell am instrument -w -e mode mappolish fr.memomimi.app.test/fr.memomimi.app.VisualEffectsChecks
```

Ces scénarios modifient des préférences/parties : utiliser **l'émulateur dédié**, jamais le téléphone personnel comme fixture. HomeChecks restaure ses préférences ; la suite générale prépare volontairement de la progression. Des modes anciens de seed/upgrade vérifient des codes précis (9.3/9.4, etc.) : ne pas les lancer au hasard sur la dernière version.

Ne pas croire le seul code de sortie PowerShell/ADB : vérifier le texte **PASS**, absence de FAIL/« Process crashed », et logcat si nécessaire. Une AssertionError dans une lambda runOnMainSync peut tuer l'instrumentation malgré un retour shell 0.

Les captures PNG en HD prennent du temps : faire les assertions temporelles avant l'encodage. À la fin d'un lancement, les assets sont libérés ; un scénario qui réouvre la bienvenue doit attendre leur décodage avant de lire le nouvel atlas. Ce sont des attentes de test, pas des délais à ajouter au thread UI de production.

Pour vérifier une haute densité : `wm size 1080x1920`, `wm density 480`, puis **`wm size reset` et `wm density reset`** après les tests. État de référence restauré : 320×640, densité160, Android15, AVD MemoMondes35, emulator-5554.

Rapports unitaires : `app/build/test-results/testDebugUnitTest/` ; lint : `app/build/reports/lint-results-release.xml`. Captures/rapports locaux : `releases/validation/`, ignorés par Git. Source des scénarios : `app/src/androidTest/java/fr/memomimi/app/`.

## 8. Publier et vérifier une mise à jour

1. Incrémenter **versionCode ET versionName** dans app/build.gradle ; créer les notes dans tools/. Ne jamais réécrire un APK déjà publié sous la même version.
2. Compiler et valider le final signé avant publication. Si un contrôle échoue, ne pas distribuer cette tentative ; conserver le dernier final valide.
3. Utiliser le pipeline existant, sans poster manuellement un manifeste partiel :

```powershell
& tools/publish-github-release.ps1 -Notes tools/release-notes-<nouvelle-version>.md
```

Le script recompilé/validé prépare `app/build/github-release-<version>/`, télécharge les bases dans `app/build/update-bases/`, génère/reconstruit les deltas, charge les assets en draft puis rend la release publique/latest. Une tentative échouée peut laisser le dossier/draft : l'examiner avant de relancer, ne pas supprimer aveuglément une release ou des sauvegardes.

4. Vérifier API GitHub latest, `update.json` latest, versionCode, taille et SHA256 du final. Le tag ou un HTTP200 ne suffisent pas.
5. Tester le flux Android avec une base officielle précédente : pousser cette base dans `/sdcard/Android/data/fr.memomimi.app/files/update-base.apk`, lancer l'instrumentation `-e mode network`, vérifier reconstruction exacte/version/signature, puis retirer uniquement ce fichier de test.
6. Mettre à jour README/CHANGELOG/docs et publier leur contenu via :

```powershell
$env:TEMP='D:/AndroidDev/tmp'; $env:TMP=$env:TEMP
$env:PYTHONPYCACHEPREFIX='D:/AndroidDev/tmp/python-cache'
& 'D:/AndroidDev/python/python.exe' -X utf8 tools/update-github-docs.py
```

Ce script publie README, CHANGELOG et les Markdown de docs sur GitHub main. Il ne publie ni sources, ni AGENTS.md, ni clé. Les scripts utilisent Git Credential Manager ; ne jamais afficher les credentials récupérés. Faire ensuite un commit local explicite des fichiers de la tâche.

En cas de problème de propriétaire Git, utiliser `git -c safe.directory='D:/VibeCoding/Appli enfant' ...` par commande. Les commits locaux précédents utilisent l'identité Codex / codex@openai.com via options de commande, sans changer toute la configuration utilisateur.

## 9. Fonctionnement de l'updater

Depuis 8.11 : vérification au retour au premier plan et toutes les 60 secondes ; intervalle minimal 15 secondes entre demandes. Callback retiré à la pause, worker fermé à la destruction. Correctif compatible avec SHA256 exact de la base, téléchargement borné en arrière-plan, reconstruction locale et signature/identité vérifiées.

Le panneau Installer/Plus tard apparaît dans les menus ; une partie ou une histoire n'est pas interrompue. Android garde sa confirmation finale et son autorisation d'installation : **pas de hot reload ni installation silencieuse promise**. Le jeu reste hors ligne lorsque GitHub/réseau manque. Sans correctif compatible, pas de téléchargement automatique d'un APK complet ; installer manuellement l'officiel. Espace nécessaire pour reconstruire un APK complet, même pour un petit delta. Détails : [MISES_A_JOUR.md](MISES_A_JOUR.md).

## 10. Validation de référence et limites

Dernière version applicative : 9.10.

- **129 tests unitaires**, 0 échec/erreur ; lint 0 erreur, 25 avertissements existants.
- **922 contrôles Android généraux** : solo/duel/coopération/calme, progression, carte, médias et régressions.
- Accueil attaché : **18 contrôles** sur petit écran et HD ; configuration/mondes/partie/Retour, album, musique, choix persistés.
- Bienvenue : **24 contrôles** petit écran ; **25 contrôles** HD après fiabilisation du scénario de recharge d'atlas.
- Carte attachée HD : **37 contrôles** de vraie victoire, départ/voyage/toucher/arrivée, Continuer, caméra et caches jusqu'à40.
- Captures réellement inspectées : `releases/validation/9.10/home-small.png`, `free-small.png`, `home-hd.png`, `welcome-wave-small.png`, `welcome-wave-hd.png`, plus rendus unitaires aux petites tailles. Consulter les rapports .txt du même dossier.
- Mise à jour officielle 9.9 → 9.10 : **PASS**, 1519122 octets téléchargés, APK exact de 102690679 octets, identité/signature/provider lecture seule vérifiés. Confirmation système d'installation non répétée par le scénario réseau.
- Chronique détaillée des essais, échecs de scénarios anciens et corrections : [VALIDATION.md](VALIDATION.md).

Pas de téléphone physique modeste testé, d'essai enfant, de vidéo évaluée manuellement ou de parcours TalkBack manuel complet. Les huit nouvelles poses sont des sprites successifs et une respiration continue, pas un rig squelettique. Les mesures de dessin Canvas isolé ne sont **pas** les FPS du jeu. Un profil de la carte HD sur émulateur en 9.7 n'atteignait pas60FPS ; aucune affirmation contraire sur téléphone n'est fondée sur ces mesures. Tous les animaux peints ne sont pas animés ; seuls les sprites/effets ajoutés bougent.

## 11. Reprendre sans perdre le contexte

1. Lire ce document et AGENTS.md, vérifier Git status avant d'éditer ; ne pas écraser du travail nouveau de l'utilisateur.
2. Vérifier la version/build.gradle, le SHA256 du final et la release latest : les valeurs de cette passation sont une photographie, pas une vérité permanente.
3. Identifier le composant concerné dans le tableau ; garder les règles de progression/album dans leurs propriétaires existants.
4. Faire une modification ciblée, adapter les vrais parcours de tests (écran libre8), compiler et valider avant publication.
5. Mettre à jour cette passation et VALIDATION.md avec les résultats réels, la nouvelle version et les limites. Éviter de présenter un test d'état ou une capture comme une vidéo validée.

À la rédaction, les demandes précédentes sont implémentées et la 9.10 publiée. **Aucun chantier applicatif supplémentaire n'est autorisé par cette seule passation.** Le prochain besoin sera surtout le retour utilisateur sur le nouvel accueil et la fluidité réelle de Lumi ; ne pas annoncer ces retours comme déjà reçus.

## 12. Documents complémentaires

- [Accueil et deux modes 9.10](ACCUEIL-DEUX-MODES-9.10.md)
- [Accueil illustré / haut et bas de carte 9.9](ACCUEIL-IMMERSIF-9.9.md)
- [Carte et progression](AVENTURE.md), [finitions 9.7](FINITIONS-CARTE-9.7.md)
- [V2](V2.md), [mascotte et sprites](MASCOTTE-SPRITES.md)
- [Architecture et historique](ARCHITECTURE.md), [validation](VALIDATION.md), [mises à jour](MISES_A_JOUR.md)
- [Changelog](../CHANGELOG.md), [README](../README.md)

Cette passation est documentaire : elle n'incrémente pas la version, ne recompile pas l'APK et ne prétend pas avoir rerun les contrôles applicatifs. L'état Git, les sources et le SHA256 du final ont été relus lors de sa création.
