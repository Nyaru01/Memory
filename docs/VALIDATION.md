# Validation

## Vérification automatique

Depuis PowerShell, à la racine :

```powershell
.\tools\build.ps1
```

Le script exécute `:app:testDebugUnitTest`, `:app:assembleRelease` et `:app:lintRelease`. Il restaure les variables d'environnement à la fin, y compris en cas d'échec. Le rapport lint est généré dans `app/build/reports/lint-results-release.html`, le rapport de tests dans `app/build/reports/tests/testDebugUnitTest/index.html` et les captures natives dans `releases/validation/rendered/`.

## Version 8.8 — dernière validation

- APK release 8.8, code 37, compilé par `tools/build.ps1` et livré via `tools/publish-apk.ps1` dans `APK/MemoMondes.apk`, avec la clé fixe. Aucun outil tiers ajouté.
- 43 tests locaux réussis ; 74 contrôles Android de jeu et de mascotte réussis sur l’APK release final. Rapport : `releases/validation/device-checks.txt`. Lint : 0 erreur et 25 avertissements.
- Trois vues natives de mise à jour examinées sur l’émulateur Android 15 : proposition, bandeau et autorisation Android. Captures : `releases/validation/update-panel-8.8.png`, `update-banner-8.8.png` et `update-permission-8.8.png`. Ces aperçus utilisent une version prête simulée ; le bandeau a été capturé pendant l’accueil de chargement.
- Le téléchargement, la reconstruction et la confirmation système ont été testés réellement en 8.7 (voir ci-dessous). La 8.8 conserve ce mécanisme ; le nouveau panneau d’autorisation a été examiné, sans rejouer l’interaction avec les réglages système.
- Release GitHub v8.8 publiée : manifeste public, code 37 et empreinte identique à l’APK final contrôlés. Correctifs 8.6 → 8.8 : 78 991 octets ; 8.7 → 8.8 : 78 990 octets. Chaque reconstruction a été comparée exactement à l’APK signé de 70 958 626 octets. Le script de publication a été corrigé pour parcourir correctement la liste PowerShell des releases.
- Limites : pas de téléphone physique, de TalkBack réel ni de validation de fluidité sur appareil modeste. Les captures de mascotte ne valident pas l’animation complète en temps réel.

## Version 8.7 — validation précédente

- Compilation par `tools/build.ps1`, publication locale via `tools/publish-apk.ps1` et publication GitHub via `tools/publish-github-release.ps1` réussies. Une seule livraison locale : `APK/MemoMondes.apk`, version 8.7, code 36.
- 38 tests locaux réussis : 20 contrôles de jeu, 8 scénarios du correctif (bornes, troncature, taille, intégrité, commandes invalides), 10 contrôles de manifeste sur Android 9 et 15.
- Lint : 0 erreur, 21 avertissements, dont le libellé du bouton natif à déplacer vers les ressources de traduction. Signature APK vérifiée.
- Manifeste public GitHub contrôlé : version 8.7 et empreinte identique à l'APK final.
- Correctif réel 8.6 → 8.7 : 73 918 octets téléchargés pour reconstruire exactement un APK de 70 953 473 octets. Téléchargement HTTPS depuis GitHub, empreintes, package/version/certificat et fournisseur en lecture seule contrôlés sur Android 15. Rapport : `releases/validation/update-network.txt`.
- Test complet sur émulateur : APK signé 8.6 installé, témoin créé dans les préférences, lancement du jeu, préparation automatique de la 8.7 et proposition hors de l'espace parents. Dialogue du jeu et confirmation système Android examinés.
- Installation réalisée en appuyant sur Installer puis Update dans Android, sans réinstallation par ADB de l'APK cible. Version 36 confirmée et témoin de progression conservé. Rapport : `releases/validation/update-install.txt`.
- L'autorisation d'installation depuis MémoMondes a été accordée dans l'émulateur pour ce scénario. Le chemin de demande initiale est implémenté, mais son interaction n'a pas été validée visuellement.
- Après cette mise à jour, 50 contrôles natifs de jeu réussis sur l'APK release réellement installé. Application relancée et accueil examiné ; APK de test retiré.
- Limites : mise à jour testée sur Android 15 uniquement, sans téléphone physique, coupure en milieu de transfert ni stockage plein. Les bornes et suppressions sur erreur sont couvertes localement. La taille d'un futur correctif dépend des fichiers modifiés ; la première installation du mécanisme nécessite un APK complet.

## Version 8.5 — validation précédente

- Fond pastel propre à chaque monde, dégradé et courbe fixe ; shader réutilisé tant que la taille et le monde ne changent pas.
- Boutons d'action avec arrondi commun, couleur corail pour jouer/rejouer et style clair pour album/accueil. Marges et texte de l'histoire corrigés sur l'écran de victoire.
- 20 tests locaux réussis (Android 9 et 15), 50 contrôles instrumentés réussis sur Android 15 ; lint : 0 erreur et 19 avertissements.
- Captures de paire et victoire examinées, dont victoire sur 320 × 616 pixels. Installation de l'APK release et lancement sur l'émulateur réussis. Signature APK vérifiée.
- Livraison unique : version 8.5, code 34, APK/MemoMondes.apk, même clé fixe que 8.4. Aucun ajout d'outil tiers.
- Limites : pas de nouveau contrôle sur téléphone physique, TalkBack réel, son ni fluidité mesurée.

## Version 8.4 — validation précédente

- 20 tests locaux réussis, aucun échec : 10 scénarios chacun sous Android 9/API 28 et Android 15/API 35, avec Robolectric et Canvas natif.
- Scénarios : solo, duel, coopération, mode calme, reprise, grandes grilles, sélection accessible des cartes, réduction des animations système, annulation de l'appui parental et pause des retours sonores.
- Compilation release réussie ; lint : 0 erreur, 19 avertissements restant principalement sur les icônes historiques et l'orientation portrait.
- Captures du match, des grandes grilles et de la victoire examinées. Ce rendu local ne constitue pas une validation sur téléphone physique ni avec le service TalkBack réel.
- APK final : `APK/MemoMondes.apk`, version 8.4, signé avec la clé fixe `D:/AndroidDev/signing/MemoMondes.keystore`. Certificat identique à la 8.3, différent de la 8.2.
- L'installation neuve a été choisie par l'utilisateur : désinstaller l'ancienne application avant de réinstaller ; sa progression locale pourra être perdue.
- Android Emulator et image Android 35 sur D:. Après le redémarrage effectué par l'utilisateur, WHPX est opérationnel et Android démarre. Le rendu utilise le GPU hôte, Vulkan désactivé et Qt OpenGL desktop pour éviter le plantage du backend logiciel.
- Lancement via Windows PowerShell 5 vérifié : installation automatique de l'APK final réussie, application ouverte et capture de l'accueil examinée.
- Vérifications instrumentées sur Android 15/API 35 : 50 contrôles réussis (solo, duel, coopération, mode calme). Captures de la paire et de la victoire examinées ; pas de validation du son ni de la fluidité en temps réel. Rapport : `releases/validation/device-checks.txt`.

Les résultats historiques ci-dessous décrivent la session précédente, avant ces améliorations.

Sur un émulateur Android dédié déjà démarré :

```powershell
.\tools\check-device.ps1 -Serial emulator-5554
```

Cette commande compile et installe l'application et les vérifications Android natives (sans bibliothèque de test externe). Elle teste les scores, les paires, les erreurs, la propriété en duel, le mode calme, les gestes pendant la dernière paire, la sauvegarde et la transition finale. Les résultats et captures sont dans `releases/validation/`. Ces scénarios modifient les préférences de l'application sur l'émulateur ; le script refuse un appareil physique.

## Scénarios de jeu

1. Solo : retourner une première carte puis sa jumelle. Observer le retournement, la petite impulsion, le halo, la disparition des particules et les coches permanentes.
2. Continuer à jouer pendant une célébration : la nouvelle première carte doit afficher « Trouve sa jumelle ! » et rester sélectionnée.
3. Retourner deux cartes différentes : elles restent visibles pendant le délai parental, puis se referment sans coche.
4. Duel : vérifier les scores et les repères des joueurs, le rejeu après réussite, puis le changement de tour après une erreur.
5. Coopération : vérifier le compteur partagé, les coches et le message de victoire collective.
6. Mode calme : absence d'impulsion, halo et particules ; les repères statiques restent visibles. La victoire arrive après un bref délai.
7. Dernière paire : elle reste visible avant la victoire. Les taps et le bouton retour pendant le délai ne doivent ni relancer une partie ni ouvrir une confirmation.
8. Pause/reprise après une paire normale : retrouver la progression. Pause sur la dernière paire : ne pas proposer ensuite une partie déjà terminée.
9. Grilles de 3, 6, 10, 12 et 15 paires : les particules restent proportionnées aux cartes, l'interface et les images restent lisibles.
10. Vérifier l'album et une histoire après la victoire, puis recommencer une partie.

## Résultat de la session du 10 octobre 2026

- Compilation release, debug et APK de vérification : réussie.
- Lint release : 0 erreur, 21 avertissements (icônes, orientation portrait, accessibilité de la vue personnalisée et version Gradle).
- `git diff --check` : aucune erreur d'espacement après correction.
- APK livré : `APK/MemoMondes.apk` (version 8.3). Ce fichier est remplacé à chaque livraison réussie ; un seul APK final est conservé.
- Vérifications Android : compilées, mais non exécutées. Le pilote d'hyperviseur Windows est absent ; les essais de démarrage de l'émulateur sans accélération n'ont pas abouti. Aucun pilote système n'a été installé. La vérification visuelle des animations reste à faire sur un appareil ou un émulateur fonctionnel.
- JDK, SDK, Gradle, émulateur, images système et fichiers temporaires dans `D:/AndroidDev`. Les fichiers secondaires créés par l'émulateur dans le profil Android par défaut ont été déplacés vers `D:/AndroidDev/android-user/default-home-artifacts`.

## Certificats vérifiés

Les signatures sont valides, mais les empreintes SHA-256 sont différentes :

- APK 8.2 : `2cb94e71d34ceecc7c34cfb193b12c7eeff25f2da45b3a071f9537df04120417`.
- APK 8.3 : `07d349ca3a7002952bdf75068a8dcff47c77409393cf24b11d1d6a986197d0a7`.

La mise à jour directe d'une installation 8.2 nécessite sa clé d'origine. La clé de démonstration 8.3 est conservée sur D: ; la conserver pour les prochaines versions signées de la même façon.

Le build release utilise actuellement la signature de démonstration Android. La clé créée sur D: diffère de celle de l'ancien APK. Ne pas désinstaller une version existante sans sauvegarder sa progression. Cette recommandation historique est remplacée par la conservation de la clé fixe utilisée depuis la 8.3 pour garantir les mises à jour.

## Mascotte de victoire — 8.8 — 10 octobre 2026

- `tools/build.ps1` : 43 tests locaux réussis (24 GameView, 1 cycle de célébration, 8 delta APK, 10 manifeste), sur API 28 et 35 pour GameView.
- Lint release : 0 erreur, 25 avertissements dans le rapport actuel.
- `tools/check-device.ps1 -SkipBuild` sur `emulator-5554` : 74 vérifications réussies, solo, duel, coopération et mode calme. Contrôles supplémentaires : début unique, non-déclenchement avant fin (local), réaction au toucher, commandes disponibles et remise à zéro au rejeu.
- Mesure isolée du rendu Canvas logiciel de la mascotte sur émulateur, 180 images après 30 images de chauffe : médiane 0,48 ms, p95 0,88 ms, maximum 2,03 ms. Il ne s'agit ni d'un profil GPU de l'écran complet, ni d'un résultat sur appareil modeste.
- Inspection visuelle réalisée sur les PNG Android de joie, attente, réaction calme et petit écran 320 × 616, ainsi que sur la capture locale d'attente. Résultats et commandes dégagés. L'animation complète en mouvement n'a pas été observée en temps réel.
- Le premier essai Android a échoué à cause de la mesure de performance qui modifiait la zone tactile du renderer dans le test. Le test redessine maintenant la bonne géométrie après mesure ; la nouvelle exécution est réussie.
- Un encodage CP1252 préexistant dans MainActivity a été converti en UTF-8 sans changement de texte pour corriger les diagnostics du compilateur.
- Réduction système des animations testée localement ; mode calme exécuté sur Android. Sons coupés et file audio réutilisés, aucun nouveau lecteur. TalkBack exposé via le fournisseur existant, mais parcours vocal manuel non réalisé.
- Aucun SDK, cache ou fichier temporaire ajouté sur C:, aucune clé régénérée. Signature fixe conservée sur D:.
- Limites : essai sur téléphone d'entrée de gamme, observation en mouvement et validation avec enfants restent à réaliser. Les accessoires actuels sont trois variantes vectorielles déclinées en couleur ; aucune planche de sprites définitive n'est requise.

Architecture et comparaison Rive/Lottie/sprites : `MASCOTTE.md`.
- APK final 8.8 (code 37) publié par tools/publish-apk.ps1 après validation ; empreinte identique à app-release.apk. Signature vérifiée : certificat fixe SHA-256 07d349ca3a7002952bdf75068a8dcff47c77409393cf24b11d1d6a986197d0a7.
- APK release final installé ensuite sur l'émulateur : 74 vérifications Android réussies également sur cette version ; mesure renderer release médiane 0,25 ms, p95 0,58 ms, maximum 1,54 ms.

## Refonte spritesheets — 8.9 — 10 octobre 2026

- Nouvelle ressource `app/src/main/assets/mascot/lumi-atlas.png` : 1536 × 1024 RGBA, 24 cellules de 256 × 256. Vérification alpha extérieure et présence de pixels opaques dans chacune des 24 cellules. Atlas décodé de 6 291 456 octets (6 Mio).
- `tools/build.ps1` : 48 tests locaux réussis, dont 26 GameView (API 28/35), 3 MascotAnimation, 1 VictoryCelebration et 18 tests de mise à jour. Sélection de chaque frame, cinq états, réduction des mouvements, cycle long, réinitialisation et mémoire contrôlés.
- Lint release : 0 erreur, 25 avertissements existants. `git diff --check` : aucune erreur d'espacement.
- `tools/check-device.ps1 -SkipBuild` : 83 contrôles Android réussis sur emulator-5554, solo/duel/coopération/calme. Atlas embarqué et budget mémoire inclus.
- Captures GPU de la vraie vue attachée à MainActivity : apparition, victoire, idle et toucher. Les captures de victoire, idle et toucher ont été inspectées ; les résultats et boutons restent lisibles. La vue attachée a été touchée puis le bouton Choisir un monde a ouvert l'écran attendu.
- Vidéo de démonstration de 10 secondes enregistrée avec Android screenrecord : `releases/validation/lumi-animation-8.9.mp4`. La vidéo n'a pas été examinée en lecture intégrale ; les captures Android des phases ont été examinées.
- Mesure du renderer Canvas logiciel isolé : debug p95 0,91 ms ; release médiane 0,27 ms, p95 0,63 ms, maximum 1,22 ms sur 180 images après chauffe. Pas une mesure FPS/GPU de l'écran complet ni un essai sur téléphone modeste.
- Release 8.9/code 38 compilée et publiée via tools/publish-apk.ps1 après les validations. APK/MemoMondes.apk identique à app-release.apk. Installation du final puis nouvelle instrumentation : PASS, 83 contrôles. Rapport : `releases/validation/mascot-release-8.9.txt`.
- Signature vérifiée avec le certificat fixe SHA-256 07d349ca3a7002952bdf75068a8dcff47c77409393cf24b11d1d6a986197d0a7. Aucune clé régénérée. Un seul APK dans APK/, aucun APK à la racine ou sous releases/.
- Aucun framework ou runtime tiers ajouté. Images produites par l'outil imagegen intégré, final stocké dans le projet sur D:. SDK, caches de build et fichiers de travail locaux restent sur D:.
- Limites : fluidité sur Android d'entrée de gamme, parcours vocal TalkBack et évaluation par des enfants non réalisés. Les deux images blink ont les yeux fermés ; les fondus avec les poses ouvertes adoucissent la fermeture/réouverture. Les accessoires par monde restent des ornements Canvas séparés ; des overlays illustrés peuvent les remplacer.

Direction artistique, liste des frames, architecture et prompt final : `MASCOTTE-SPRITES.md`.
## Publication GitHub 8.9

- Release v8.9 publiée et vérifiée comme dernière release publique. Manifeste public versionCode 38 et SHA-256 identique à APK/MemoMondes.apk.
- Correctifs depuis 8.6, 8.7 et 8.8 reconstruits à l'identique avant publication. Correctif depuis la 8.8 officielle : 2 419 142 octets.
- Test Android contre les assets publics GitHub, avec l'APK officiel 8.8 comme base : téléchargement HTTPS réussi, reconstruction exacte de 73 310 610 octets et contrôles package/version/signature réussis. Rapport : releases/validation/github-update-8.8-to-8.9.txt. Confirmation d'installation Android non répétée dans ce test.

## Chargement et fluidité — 8.10 — 10 octobre 2026

- Lumi : timeline ralentie à 2/3 de la vitesse 8.9 (durées +50 %) ; apparition 1080 ms, joie 2700 ms, toucher 1440 ms, attente 8100 ms. Réemploi du même atlas sur l'accueil de chargement, sans célébration ni allocation de bitmap supplémentaire.
- Chargement : disposition proportionnelle sur petit écran, personnage, titre, sous-titre, badge et barre. Délai de démarrage inchangé (1750 ms). En animations réduites, pose statique et rafraîchissement de barre espacé.
- Cartes : ouverture 280 ms, fermeture 320 ms, largeur en cosinus d'une interpolation smoothstep, léger soulèvement et reflet blanc à faible opacité. Aucun shader ni bitmap ajouté. Dessin synchronisé par postInvalidateOnAnimation.
- Durées partagées par CardMotion entre le rendu, les gestes et le fournisseur TalkBack ; une touche à 200 ms ne remplace pas un retournement en cours, une touche après 280 ms est acceptée. En mode réduit, sélection immédiate. Délai parental des erreurs conservé.
- `tools/build.ps1` : 55 tests locaux réussis (GameView API 28/35, timeline, géométrie de retournement, mises à jour). Vérifications du chargement sans victoire, fermeture/ouverture, gestes rapides et restauration de Paint.
- Android emulator-5554 : 86 contrôles réussis, solo/duel/coopération/calme et chargement de la vue réellement attachée. PNG du chargement live et du retournement à 170 ms inspectés, ainsi que le chargement local 320 × 616. Textes, résultats et commandes dégagés.
- Mesure isolée du renderer de mascotte sur émulateur : médiane 0,39 ms, p95 0,82 ms, maximum 1,26 ms (180 images). Aucune mesure de FPS complet des cartes ni essai sur téléphone modeste.
- Lint : 0 erreur, 25 avertissements existants. La fluidité complète en mouvement n'a pas été observée manuellement ; seules les captures et le fonctionnement Android ont été vérifiés.
- Une copie de l'APK de base de test récupérée automatiquement par check-device a été retirée de releases/validation ; les bases nécessaires aux correctifs restent exclusivement dans app/build/update-bases.
- APK final 8.10/code39 installé sur l'émulateur : 86 contrôles réussis ; renderer isolé p95 0,64 ms. Signature fixe vérifiée par le script de publication.
- Release GitHub v8.10 publiée. Manifeste public latest/download vérifié (code39, empreinte correspondant au final). Un bref délai de propagation a d'abord renvoyé le manifeste précédent ; nouvelle lecture réussie.
- Test Android contre le flux public latest avec base 8.9 : téléchargement de 81 453 octets, reconstruction exacte de 73 311 148 octets et signature/package/version vérifiés. Correctifs 8.8, 8.7 et 8.6 reconstruits avant publication. Confirmation d'installation système non répétée dans ce test réseau.

## Accueil et mises à jour — 8.11 — 10 octobre 2026

- 58 tests locaux réussis : états de mascotte et accueil, modes de jeu API 28/35, cartes et mises à jour. UpdatePoller testé sur trois minutes au premier plan, pause, reprise et retours rapides ; aucun callback supplémentaire en arrière-plan.
- Instrumentation Android : 88 contrôles réussis, incluant la non-interruption d'une partie par une proposition automatique de mise à jour et deux captures de l'accueil réellement affiché.
- Captures loading-lumi-salut-8.11.png et loading-lumi-rebond-8.11.png inspectées : poses distinctes, salut puis bond, texte et barre de progression dégagés. La continuité de l'animation n'a pas été observée manuellement en vidéo.
- Renderer de mascotte isolé, 180 images sur émulateur : médiane 0,45 ms, p95 0,87 ms, maximum 1,44 ms. Aucun essai sur téléphone Android modeste ni mesure de FPS de l'écran entier.
- Lint : 0 erreur, 25 avertissements existants. Aucun SDK, framework UI ou bitmap supplémentaire.
- APK final 8.11/code40 signé avec la clé fixe, publié via tools/publish-apk.ps1 puis GitHub v8.11. Manifeste public vérifié : version et empreinte SHA-256 identiques au final.
- Installation de l'APK final sur emulator-5554 puis 88 contrôles réussis. Renderer isolé release : médiane 0,30 ms, p95 0,88 ms, maximum 2,87 ms. Rapport : releases/validation/mascot-update-release-8.11.txt.
- Test Android contre latest/download public, avec base officielle 8.10 : correctif de 82 749 octets téléchargé, APK de 73 312 477 octets reconstruit à l'identique ; package/version/signature vérifiés. Rapport : releases/validation/github-update-8.10-to-8.11.txt. La confirmation système d'installation n'a pas été répétée dans ce test réseau.
- Correctifs depuis 8.6, 8.7, 8.8, 8.9 et 8.10 reconstruits avant publication. Base de test retirée du stockage externe de l'émulateur. Un seul APK final sous APK/ ; aucun APK à la racine ni sous releases/.

## V2 — 9.0 — 10 octobre 2026

- Cahier des charges fourni lu intégralement, audit de la stack, des 18 mondes, assets, règles et préférences `m`. Point de retour 752f398 avant modifications. Rapport de conception et limites : V2.md.
- Compilation via tools/build.ps1 : 75 tests locaux réussis (32 GameView API 28/35, 16 V2Acceptance API 28/35, 1 AudioFocus API 23, 26 autres tests de mises à jour/timeline). Lint : 0 erreur, 25 avertissements existants. Une incompatibilité removeIf avec API 23 détectée par lint a été corrigée par un itérateur, sans relever minSdk.
- Couverture V2 : 6/8/10/12 paires, 320×480 / 360×640 / 412×850 / 600×900, police 100/130 %, densités 0,75/1/1,5/2/3 pour la géométrie. Cartes >=48 dp dans les tailles retenues ; limites extrêmes documentées.
- Huit parties parfaites sur les quatre difficultés, rejouer, statistiques exactes et record non réécrit en cas d'égalité. Navigation monde 18, glissement sans sélection, album monde choisi, nouveaux masques de consultation, cache <=2 planches.
- Test du focus audio sur API 23 : perte coupe le son, gain permet reprise et aucun gain tardif après pause ne relance le son. Maintien parental réellement attaché à une activité : interruption puis nouveau maintien n'utilise pas l'ancien délai.
- Upgrade réel sur emulator-5554 : données écrites dans la 8.11/code40, installation signée de la V2/code41 sans désinstallation, monde17/difficulté12/album63/record19/son/calme conservés. Rapports v2-upgrade-seed.txt et v2-upgrade-proof.txt.
- Première instrumentation V2 : PASS 769 contrôles (modes solo/duel/coopération/calme, toutes difficultés sur trois tailles, 18 illustrations présentes, cibles et navigation). Renderer isolé Lumi : médiane 0,45 ms, p95 0,98 ms, maximum 1,20 ms (180 images, émulateur). Ces mesures ne sont pas des FPS de l'écran entier.
- Captures Robolectric et Android inspectées : accueil, monde, grille12 sur petit écran, parents et victoire. Avant réel 8.11 : v2-before-home/worlds/board12.png. Les détails des captures de la version finale et de sa publication sont ajoutés ci-dessous après leur réalisation.
- Aucun essai auprès d'enfants, sur matériel modeste, en confort à une main ou TalkBack manuel complet. Les images inspectées ne prouvent pas à elles seules la fluidité continue des animations. Android 23 vérifié pour le focus et la compatibilité lint, sans émulateur API23 complet.
- Vérification finale debug après la correction du maintien parental : PASS 769 contrôles. Renderer isolé : médiane 0,44 ms, p95 0,87 ms, maximum 1,57 ms.
- Captures de l'activité Android 9.0 obtenues par instrumentation : v2-after-home.png, v2-after-worlds.png, v2-after-board12.png. Réglage système font_scale=1.3 appliqué pour v2-after-font130-*.png, puis valeur initiale restaurée. Captures inspectées : textes et boutons dégagés, plateau entier visible et illustrations conservées. Rapports v2-after.txt et v2-after-font130.txt.
- APK final 9.0/code41 compilé avec tools/build.ps1 et publié localement par tools/publish-apk.ps1 après tests et lint réussis. Même clé fixe, jamais régénérée. Installation du final sur l'émulateur : PASS 769 contrôles, renderer Lumi médiane 0,29 ms / p95 0,66 ms / max 1,11 ms. Rapport v2-final-release.txt.
- GitHub v9.0 vérifiée comme dernière release. Manifeste public code41 et SHA-256 identique à APK/MemoMondes.apk. Correctifs depuis 8.6 à 8.11 reconstruits avant publication.
- Test Android contre le flux public latest avec base officielle 8.11 : 87 252 octets téléchargés et reconstruction exacte de 73 316 842 octets ; package/version/signature vérifiés, fournisseur en lecture seule. Rapport github-update-8.11-to-9.0.txt. Confirmation du système d'installation non répétée dans ce test réseau.
- Base APK de test supprimée du stockage externe après test réseau. Un seul APK final local sous APK/ ; aucun APK dans releases/ ni à la racine. Les bases et compilations restent des intermédiaires sous app/build/.

## Chargement ralenti — 9.1 — 10 octobre 2026

- Accueil : pose initiale conservée 650 ms, transition douce de 450 ms vers un seul salut, puis pose maintenue. Aucun cycle de poses ni bond ; respiration limitée à 1,2 % sur environ 7,5 s, balancement limité à 1,2 degré sur environ 8,8 s. Durée de démarrage inchangée : 1750 ms. Animations de victoire inchangées ; mode réduit statique.
- Compilation debug et lint via tools/build.ps1 réussie : 75 tests locaux, 0 erreur lint et 25 avertissements existants. Test de timeline : transition monotone, pose stable jusqu'à 20 s, pas de bond et amplitudes limitées.
- Émulateur Android : PASS 769 contrôles (solo, duel, coopération, calme). Renderer isolé : médiane 0,46 ms, p95 0,96 ms, maximum 2,31 ms sur 180 images ; aucune mesure de FPS complet ou de téléphone modeste.
- Captures loading-lumi-pose-9.1.png et loading-lumi-salut-9.1.png issues de l'activité attachée ; salut inspecté, textes et barre dégagés. La continuité de l'animation n'a pas été observée manuellement en vidéo.
- APK final 9.1/code42 compilé et publié par tools/publish-apk.ps1, signature fixe vérifiée. Installation du final sur l'émulateur : PASS 769 contrôles. Renderer isolé : médiane 0,29 ms / p95 1,07 ms / maximum 1,99 ms. Rapport loading-release-9.1.txt.
- Release GitHub v9.1 publiée ; manifeste public de la version vérifié (code42, SHA-256 identique au final). Correctifs depuis 8.6 à 9.0 reconstruits avant publication. Un premier test latest sur Android a encore obtenu le flux précédent durant la propagation et n'a donc pas trouvé de correctif pour la base 9.0 ; nouvelle vérification après propagation ci-dessous.
- Après propagation du flux et relance du processus de test suite à une erreur DNS transitoire : PASS du test Android latest avec base officielle 9.0. Correctif de 87 139 octets téléchargé, APK de 73 316 759 octets reconstruit à l'identique ; package/version/signature vérifiés, fournisseur en lecture seule. Rapport github-update-9.0-to-9.1.txt. Confirmation système d'installation non répétée dans ce test réseau.
- Base temporaire supprimée du stockage externe. Un seul APK final dans APK/, aucun APK à la racine ou sous releases/.

## Victoire et mondes — 9.2 — 10 octobre 2026

- Plan et décisions : VICTOIRE-MONDES-9.2.md. Rejouer turquoise avec texte sombre, résultats menthe et étoile dorée ; bulle de Lumi en 18 dp × police système, avec deux lignes au besoin. Capture native dense 1080×1920, densité 3 et police 130 %, inspectée ; texte et commandes dégagés.
- Timeline : fondus smoothstep sur toute la durée des poses, sauts à vitesse verticale nulle à l'atterrissage, silhouette stable au repos, clignements et respiration conservés. Invalidation synchronisée avec l'écran ; aucun débit de 60 FPS garanti sur téléphone. Chargement lent 9.1 conservé.
- Deux planches originales 4×3 créées avec imagegen puis inspectées. Les douze illustrations de chaque monde ont aussi été dessinées avec drawAnimalFrom sur Android et inspectées dans v92-new-cards-18/19.png. Robot et jardin ont leurs IDs 18/19, albums et histoires ; anciennes clés inchangées. Narration des deux histoires par la voix Android existante, sans nouveau MP3 ni écoute manuelle validée.
- 80 tests locaux réussis : couverture des 20 IDs une fois chacun, familles fermées non sélectionnables, navigation vers robot, deux nouvelles parties parfaites de douze paires puis rejouer, albums indépendants et total 240, anciennes préférences conservées, transitions continues aux atterrissages. Tests V2 API 28/35 et focus audio API23 conservés.
- Une compilation de test a d'abord révélé un nom de méthode incorrect dans le nouveau scénario ; corrigé en jouant réellement les douze paires. Une instrumentation répétée a révélé un scénario ancien qui cherchait le monde 17 sans ouvrir sa famille ; préparation corrigée. Ces erreurs appartenaient aux scénarios et ne sont pas déclarées comme validations réussies.
- Première série Android : PASS 778 contrôles (solo/duel/coopération/calme). Captures de l'activité attachée v92-victory-readable.png, v92-worlds-robots.png et grilles robots/jardin inspectées. Renderer isolé sur 180 images : médiane 0,50 ms, p95 1,13 ms, maximum 1,60 ms. Aucun FPS complet ni essai sur téléphone modeste.
- La continuité des animations n'a pas été observée manuellement en vidéo ; les captures et tests temporels ne remplacent pas ce contrôle. Les preuves finales et de publication sont ajoutées ci-dessous après exécution.
- Vérification finale debug après correction du scénario de familles : PASS 780 contrôles ; renderer isolé médiane 0,45 ms, p95 1,13 ms, maximum 1,63 ms. Lint : 0 erreur, 25 avertissements existants. Les 80 tests locaux ont été relancés avec la dernière timeline et la capture dense.
- APK final 9.2/code43 compilé via tools/build.ps1 et publié localement via tools/publish-apk.ps1 ; signature fixe vérifiée. Installation du final : PASS 780 contrôles ; renderer Lumi médiane 0,22 ms, p95 0,76 ms, maximum 2,49 ms. Rapport v92-final-release.txt.
- GitHub v9.2 publiée et vérifiée comme dernière release ; manifeste public code43 et SHA-256 identique au final. Correctifs depuis 8.6 à 9.1 reconstruits avant publication.
- Test Android contre latest avec base officielle 9.1 : PASS ; téléchargement de 3 589 314 octets et reconstruction exacte de l'APK de 77 015 255 octets. Package/version/signature et fournisseur en lecture seule vérifiés. Rapport github-update-9.1-to-9.2.txt. Confirmation d'installation du système non répétée dans ce test réseau.
- Base de test retirée du stockage externe. Un seul APK final sous APK/ ; aucun APK à la racine ni dans releases/.

## Carte d'aventure — 9.3 — 10 octobre 2026

- Référence utilisateur examinée ; carte originale Canvas de cinq îles, route linéaire et 40 étapes, sans nouveau SDK ni bitmap de carte. Règles et limites : AVENTURE.md.
- 90 tests locaux réussis, incluant dix scénarios AdventureTest sur API28/35. Deux parcours complets des 40 étapes avec véritables sélections de toutes les paires ; chaque victoire débloque exactement la suivante. Niveaux 0/41 et saut de niveau refusés, anciens niveaux rejouables sans progression supplémentaire.
- Partie libre et partie incomplète ne débloquent pas d'étape. Reprise conserve le niveau ; sauvegarde de niveau verrouillé refusée. Albums et records antérieurs conservés. Boutons de victoire, retour carte et accueil vérifiés.
- Carte contrôlée sur 320×480, 360×640 et 412×850 ; cibles 54 dp, dernier niveau accessible par défilement, étapes verrouillées désactivées pour le fournisseur accessible. Captures AdventureMap natives inspectées : première étape, étape 2 ouverte après victoire et fin de parcours.
- Le premier scénario de retour carte utilisait sa géométrie avant le premier rendu de cet écran ; corrigé en attendant le rendu. La dernière compilation tests/debug/instrumentation/lint via tools/build.ps1 a réussi. Lint : 0 erreur, 25 avertissements existants.
- Mise à jour réelle sur emulator-5554 : données écrites dans la 9.2/code43, puis installation de 9.3/code44 sans désinstallation. Monde17, difficulté12, album63, record19, son coupé et mode calme conservés ; aventure commence à 1. Rapports adventure-upgrade-seed.txt et adventure-upgrade-proof.txt.
- Première instrumentation Android : PASS 787 contrôles solo/duel/coopération/calme et aventure. Victoire réelle du niveau1, niveau2 seul débloqué, bouton suivant, reprise au niveau2 et accès au niveau40 vérifiés. Renderer Lumi isolé : médiane 0,44 ms, p95 0,90 ms, maximum 1,18 ms sur émulateur.
- Aucun essai sur téléphone modeste, auprès d'enfants ou parcours TalkBack manuel complet. Carte statique, préférences de réduction des animations conservées. Les captures inspectées ne constituent pas un test gestuel continu ni une mesure de FPS complet.
- Dernière instrumentation avec capture de l'activité attachée : PASS 788 contrôles. adventure-live-map.png inspectée : étape1 cochée, étape2 ouverte et centrée, cadenas des étapes futures, titre et bouton Continuer dégagés. Renderer isolé médiane 0,48 ms, p95 1,25 ms, maximum 1,79 ms.
- APK final 9.3/code44 compilé via tools/build.ps1 et publié localement via tools/publish-apk.ps1 ; clé fixe et signature vérifiées. Installation du final sur l'émulateur : PASS 788 contrôles ; renderer Lumi isolé médiane 0,24 ms, p95 0,70 ms, maximum 1,16 ms. Rapport adventure-final-release.txt.
- GitHub v9.3 publiée et vérifiée comme dernière release. Manifeste public code44 et SHA-256 identiques à APK/MemoMondes.apk ; correctifs depuis 8.6 à 9.2 reconstruits avant publication. Le premier appel Android au flux latest a encore obtenu le manifeste précédent pendant la propagation et n'a pas trouvé de correctif pour la base 9.2 ; nouvelle vérification après propagation ci-dessous.
- Test Android latest après propagation avec base officielle 9.2 : PASS ; 94 415 octets téléchargés, reconstruction exacte de l'APK de 77 019 630 octets, package/version/signature vérifiés et fournisseur en lecture seule. Rapport github-update-9.2-to-9.3.txt. La confirmation d'installation du système n'a pas été répétée dans ce test réseau.
- Base temporaire supprimée du stockage externe. Un seul APK final sous APK/ ; aucun APK à la racine ni dans releases/.

## Refonte illustrée de Mon aventure — 9.4

- Ressources créées avec imagegen intégré et inspectées : vingt scènes distinctes dans worlds-atlas.jpg (1122 × 1402, 907456 octets), seize poses transparentes dans creatures-atlas.png (1254 × 1254, 1725116 octets). Réutilisation des 24 poses de Lumi. Aucun framework ni bibliothèque de production ajouté.
- Compilation via tools/build.ps1 : 106 tests Robolectric SDK28/35 réussis ; lint release : 0 erreur, 25 avertissements existants. Les scénarios incluent les quarante étapes, le rejeu sans saut, le jeu libre et les sauvegardes, le cadrage avant layout, les boutons et réactions, l'animation de transfert consommée une seule fois, le cache borné et sa réouverture.
- Mise à jour réelle signée : base9.3/code44 → debug9.4/code45, même clé. PASS :17 étapes terminées, partie sauvegardée niveau18, album17=63, record12=19, son coupé et mode calme préservés. Rapports illustrated-upgrade-seed.txt et illustrated-upgrade-proof.txt. Le premier scénario terminait l'instrumentation avant vidage de l'écriture asynchrone de la partie ; ajout d'une barrière commit dans le scénario de préparation et répétition réussie, sans modification de sauvegarde dans l'application.
- Émulateur Android : PASS872 contrôles (solo, duel, coopération, calme, carte). Traversée des vingt scènes, régions RGB565 au plus3 et budget6MiB contrôlés ; raccourci de caméra et réaction de Lumi sans lancement ni déblocage. Rapport device-checks.txt.
- Mesure logicielle Canvas isolée,120 images chaudes : carte médiane3,88ms,p954,66ms. Régions visibles472082octets + créatures1572516octets ≈1,95MiB, en plus de l'atlas Lumi partagé existant. Ne mesure ni FPS de l'activité ni coûts de décodage à froid ni performances téléphone.
- Captures inspectées : adventure-illustrated-start.png, adventure-illustrated-ocean.png, adventure-illustrated-space.png, puis adventure-live-map.png de l'activité réelle. Corrections après inspection : opacité des médaillons et place de Lumi sous le cartouche du monde. Contraste des étapes terminées/disponibles/verrouillées et accès Continuer vérifiés sur captures.
- Limites : pas de vidéo évaluée manuellement pour la fluidité, de test enfant, de test physique sur Android modeste ni de parcours TalkBack manuel complet. Atlas de résolution actuelle approprié aux captures téléphone ; exports plus grands souhaitables pour tablettes. Décors raccordés par brume, pas une peinture continue de8000dp. Repères et créatures sont visuels ; progression linéaire inchangée.
- APK final9.4/code45 :79657069octets, compilation/release/lint et publication via scripts du projet, clé fixe conservée. Installation du final et répétition Android : PASS872 contrôles ; carte chaude médiane3,63ms,p954,11ms, budget bitmap identique. Rapport illustrated-final-release.txt.
- Release GitHubv9.4 publiée. Manifeste de la release nommée vérifié : code45 et SHA256 identiques à l'APK final. Correctifs reconstruits exactement avant publication : depuis9.3 et9.2,2619802octets téléchargés pour un APK79657069octets ; bases8.6 à9.1 également traitées.
- Le flux GitHub latest a d'abord encore fourni update.json9.3/code44 après création de la release9.4 ; le premier scénario réseau n'a pas trouvé de correctif pour une base9.3. Nouvelle vérification après propagation requise ci-dessous.
- Après propagation : PASS scénario Android sur le flux latest ordinaire, téléchargement2619802octets, reconstruction exacte79657069octets, package/code45/signature et fournisseur en lecture seule validés. Rapport github-update-9.3-to-9.4.txt. La confirmation d'installation Android n'est pas répétée dans ce test réseau. Le manifeste latest public code45 a également été obtenu et son SHA256 comparé au final.

## Correction du plantage à la première ouverture — 9.5

- Cause reproduite sur le code9.4 : pendingFrom=0 (aucune victoire), current=1 ; la condition pendingFrom==current-1 créait un trajet0→1. AdventureMap.drawLumi accédait à nodes[-1] et levait ArrayIndexOutOfBoundsException. La réduction des animations masquait ce chemin défaillant, ce qui expliquait le manque de couverture de la première ouverture dans la validation précédente.
- Reproduction avant correction : nouveau test de première ouverture normale, SDK28 et35, deux échecs avec Index-1 out of bounds for length40. Trace conservée dans releases/validation/map-first-open-crash-before.xml. Il s'agit d'une reproduction locale de la condition, pas d'un journal provenant du téléphone de l'utilisateur.
- Correctif limité à AdventureMotion : une étape précédente doit être réelle (>=1), une notification ne peut venir du niveau0, et un trajet animé relie exclusivement deux niveaux consécutifs valides. Première ouverture : Lumi apparaît à1 sans trajet ; après victoire, le trajet1→2 reste testé. Aucune clé ni logique de sauvegarde changée.
- Compilation via tools/build.ps1 :108 tests unitaires SDK28/35 passent, dont le nouveau scénario ; lint release0erreur,25avertissements existants. Les parcours complets40étapes, rejeu, jeu libre et sauvegardes restent validés.
- ÉmulateurAndroid : PASS881contrôles, comprenant trois ouvertures à progression0 avec animations actives et une première ouverture dans l'activité réellement attachée. Capture map-first-open-live-9.5.png inspectée : Lumi au niveau1, Continuer accessible, carte affichée sans fermeture. Rapport map-crash-fixed-device.txt. Aucun téléphone physique connecté ; modèle et version du téléphone demandés, non requis pour corriger la condition reproduite.
- APK final9.5/code46 signé avec la clé fixe, publié via les scripts et installé sur l'émulateur : PASS881contrôles. Rapport map-crash-fixed-final.txt. APK79657107octets, une seule livraison dans APK/MemoMondes.apk.
- GitHubv9.5 publiée, manifeste public code46 et hash final vérifiés. Test Android du flux latest depuis la base officielle9.4 : PASS,99057octets téléchargés, reconstruction exacte79657107octets, package/version/signature et fournisseur en lecture seule validés. Rapport github-update-9.4-to-9.5.txt. Confirmation système d'installation non répétée dans le scénario réseau. Aucune validation sur le téléphone personnel de l'utilisateur annoncée.
