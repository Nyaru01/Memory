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
