# Validation

## Carte nette et rendu accéléré — 9.6 — 10 octobre 2026

- Cause de flou confirmée dans les ressources : ancien atlas1122×1402 réparti en20 cellules, soit environ280pixels par scène. Vingt paysages recréés individuellement avec imagegen : huit972×1619, douze1254×1254 ; JPEG qualité94,17319015octets au total. Planche des20assets inspectée, puis capture1080×1920 dans l'activité attachée examinée : illustration plus détaillée, Lumi, niveaux et Continuer dégagés. Aucun simple agrandissement de l'atlas précédent.
- Cause de lenteur confirmée dans le rendu : GameView imposait LAYER_TYPE_SOFTWARE sur la carte, et redessinait également son ancien fond caché. La carte passe au Canvas GPU de la fenêtre ; JPEG/créatures décodés hors thread UI, préchargement à l'accueil, cache3sections/16MiB. Réduction complémentaire à la largeur native lorsque l'export est nettement plus grand. Les commandes GPU gardent leurs références bitmap sans recycle prématuré. Les autres écrans gardent leur rendu existant.
- Comparaison identique sur Android15 émulateur,1080×1920,densité3 :12glissements injectés dans l'activité réelle et492images enregistrées via Window.FrameMetrics. APK9.5 : durée totale médiane87,18ms,p95118,03ms. Première9.6 GPU :19,56/36,03ms. Avec ombres sans masque de flou :18,30/19,78ms. Enregistrement Canvas : médiane0,52ms,p951,00ms ; isHardwareAccelerated=true confirmé sur le Canvas réel. Rapports et captures dans releases/validation/9.6/.
- Ces durées totales incluent synchronisation, rendu et uploads ;491/492images du dernier profil dépassent16,67ms dans cet environnement d'injection. Ce test confirme une baisse du coût, pas60FPS sur téléphone. Aucun appareil physique, observation vidéo manuelle, test enfant ou parcours TalkBack complet. La cible initialement demandée à l'émulateur était1080×2400, mais sa taille effective était1080×1920 ; les tests de rendu local couvrent séparément1080×2400 et720×1600.
- Compilation finale debug via tools/build.ps1 :112tests unitaires passent (SDK28/35, avec focus audio API23), lint0erreur/25avertissements existants. Nouvelles régressions : dimensions des20assets, pas de redécodage à viewport stable, cache borné, annulation/réouverture pendant le chargement, Canvas GPU sur carte puis retour logiciel au jeu ; à720pixels, les scènes carrées sont chargées à720pixels et le cache reste sous4MiB.
- APK debug installé : PASS881contrôles Android de jeu, victoire, carte, progression et modes réduits. Rapport releases/validation/9.6/device-debug.txt. La synchronisation des tests attend le worker hors du rendu de production ; aucune attente bloquante n'a été ajoutée à l'application. Le profil logiciel isolé à360pixels reste à3,56ms médiane/4,63ms p95 ; il ne sert pas à déduire les FPS de l'activité matérielle.
- APK final9.6/code47 compilé par tools/build.ps1 et livré via tools/publish-apk.ps1 après tests/lint réussis ;96983458octets, signature fixe conservée. Installation finale : PASS881contrôles Android, rapport device-final.txt. Profil final1080×1920 :494images, durée totale médiane18,46ms/p9536,32ms, enregistrement Canvas0,35/1,09ms, Canvas matériel confirmé ; paysages7812936octets + créatures1572516octets. Les durées totales comprennent aussi uploads et synchronisation ; elles restent plus variables que l'enregistrement Canvas, sans garantie60FPS. Capture map-final-9.6.png inspectée. Résolution/densité de l'émulateur rétablies ensuite.
- Release GitHubv9.6 publiée et reconnue latest par l'API. Manifeste public de la release nommée contrôlé : code47 et SHA256eb44dbd41ec635b29463e96a2c71bda65605c870a776c2a7a7064aac3479f3f2 identiques au final. Correctifs8.6 à9.5 reconstruits exactement avant publication ; depuis9.5,17419741octets. Le premier test Android du flux latest a reçu l'ancien manifeste pendant la propagation et n'a pas trouvé de correctif ; répétition requise après propagation.
- Après propagation : manifeste latest ordinaire9.6/code47 obtenu, puis PASS du test réseau Android sur ce même flux.17419741octets téléchargés depuis la base officielle9.5 pour reconstruire exactement96983458octets ; package/version/signature et fournisseur en lecture seule vérifiés. Rapport github-update-9.5-to-9.6.txt. La confirmation système d'installation n'est pas répétée dans ce scénario réseau. Base temporaire retirée ensuite ; une seule livraison finale dans APK/, aucun APK sous releases/ ni à la racine.

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


## Finition graphique et voyage de Lumi — 9.7 — 10 octobre 2026

- Vingt illustrations HD conservées ; nouvelle planche wildlife-atlas.png transparente 1254×1254, 1763986octets, seize poses (lapin, oiseau, faon, grenouille), créée avec imagegen intégré. Prompt exact et artefact final : ILLUSTRATION-PROMPTS.md. Aucun SDK ajouté.
- Raccords RGB565 au contour progressif préparés hors du thread UI, chemins et quarante repères partagés avec le déplacement, interface compacte, fin de partie ouvrant la carte puis Continuer. Mêmes clés de progression et règles des quarante étapes.
- Compilation finale via tools/build.ps1 :118tests Robolectric passent, SDK28/35 et scénarios audio23 ;0échec/erreur. Lint release :0erreur,25avertissements existants. Le test de sprite vérifie qu'une seule pose opaque est dessinée, sans mélange des visages. Les raccords ont des bords exacts, un masque variable horizontalement et n'altèrent pas les illustrations.
- Installation de l'APK final signé :PASS922contrôles Android, solo/duel/coopération/calme,40étapes et cache borné. Carte logicielle isolée chaude : médiane4,28ms,p956,20ms ; ce n'est pas le FPS de l'activité. Rapport releases/validation/9.7/device-final.txt.
- PASS37contrôles dans l'activité attachée à1080×1920,densité3 : vraie partie niveau1 terminée par sélection des paires, action de victoire ouvrant la carte, attente du paysage avant départ, voyage1→2, toucher pendant le trajet, poses de célébration à l'arrivée, Continuer lançant2. Ouverture avec17étapes terminées centrée sur18 ; défilement jusqu'aux zones6/13/21/40 sans modifier la progression. Rapport polish-final.txt.
- Captures réellement inspectées : polish-depart.png, polish-arrival.png, polish-zone-level21.png, polish-zone-level40.png et map-profile-9.7.png. Une première inspection révélait une double image due au fondu entre poses ; remplacement par des poses nettes et des transformations continues du corps. Une ouverture froide peut montrer brièvement les panneaux avant que les petites jonctions soient préparées ; les jonctions sont réutilisées ensuite. Les scènes restent des peintures distinctes, reliées visuellement, sans garantir une géographie continue.
- Profil final de l'activité1080×1920 avec ambiance/inertie actives et12glissements injectés :963images, enregistrement Canvas matériel médiane0,62ms,p953,27ms ; durée totale FrameMetrics médiane68,35ms,p95101,97ms,956images dépassent16,67ms. L'émulateur n'atteint donc pas60FPS sur ce scénario ; les durées totales incluent GPU virtuel, synchronisation et uploads. Aucune amélioration de FPS sur téléphone n'est revendiquée à partir de ce chiffre. Rapport profile-final.txt. Cache observé : paysages7812936octets, jonctions1866240octets, deux atlases de créatures3145032octets, plus Lumi partagé. Limites contrôlées :3paysages/16MiB et3jonctions/2MiB ; jamais40grandes images simultanément. Les commandes GPU peuvent conserver temporairement les images évincées.
- APK final9.7/code48 :98753276octets ; SHA256 5b182da6dff800c0d559d0eff1ea4310e5f82e6b93ead027f6f45cc31f4bf1af. Compilation/publication locale via scripts du projet, clé fixe conservée. GitHubv9.7 publiée ; API latest, manifeste nommé et manifeste latest code48 comparés au SHA256 du final.
- Correctifs depuis8.6 à9.6 reconstruits avant publication. Test Android du flux latest ordinaire depuis la base officielle9.6 :PASS,1762349octets téléchargés et reconstruction exacte98753276octets ; package/version/signature et fournisseur en lecture seule vérifiés. Rapport github-update-9.6-to-9.7.txt. La confirmation système d'installation n'est pas répétée par ce test réseau. Base temporaire retirée après le test ; dimensions/densité d'émulateur restaurées.
- Limites : aucune vidéo évaluée manuellement, aucun téléphone physique modeste testé, aucun essai enfant ni parcours TalkBack manuel complet. Les contrôles de scénario/captures vérifient les états et l'interface ; ils ne garantissent pas la fluidité ressentie sur l'appareil de l'utilisateur. Les animaux peints dans les fonds ne sont pas tous animés ; seuls les sprites/effets ajoutés le sont.


## Bordures de carte et accueil de Lumi —9.8 —10 octobre2026

- Carte dessine jusqu’au bas du viewport, sous Continuer et les insets. Maximum de défilement adapté pour que la dernière illustration couvre aussi le bas. Fondu de44dp sous l’en-tête, shader mis en cache. Aucun nouvel asset ni SDK.
- WelcomeScreen remplace la carte blanche de chargement : fond crème/menthe, lumière douce, composition du titre et de Lumi, barre de4dp. Même timer1750ms, aucune attente ajoutée ; fond des mondes caché derrière l’accueil non redessiné.
- MascotAnimation au chargement : pose0, bref clignement4 à1050–1190ms ; respiration0,6% sur5200ms, rotation et lift nuls. Registration du dessin fermé et ombre alignée sous les pieds. Réduction d’animations : pose0 et transformations fixes. Victoire et trajet conservent leur timeline.
- Compilation finale via tools/build.ps1, publication locale via tools/publish-apk.ps1 :120tests locaux SDK28/35 (audio23) passent ;0échec/erreur. Lint0erreur,25avertissements existants. Nouvelle régression raster vérifie un pixel de paysage au bas du viewport aux niveaux1/40 et la couleur du fondu sous l’en-tête. Respiration continue et absence de saut/rotation testées toutes les10ms sur1750ms.
- APK final installé sur l’émulateur :PASS922contrôles Android (solo/duel/coopération/calme et progression). Carte logicielle isolée chaude médiane4,66ms,p956,23ms ; cela ne mesure pas les FPS du téléphone. Rapport releases/validation/9.8/device-final.txt.
- PASS18contrôles d’accueil dans l’activité attachée en1080×1920,densité3 :captures à différents âges de lancement, absence de célébration de victoire, séparation des textes/personnage, pieds au sol, mode réduit immobile, sortie vers l’accueil. Rapport welcome-final.txt. Les captures sont des instantanés selon le timing réel de l’émulateur : elles ne garantissent pas une capture de chaque frame de clignement.
- PASS37contrôles de carte attachée : vraie victoire1, voyage vers2 et arrivée, toucher, Continuer, caméra18 et défilement vers40, caches bornés. Rapport polish-final.txt. Dimensions et densité d’émulateur restaurées à320×640/densité160.
- Captures inspectées : welcome-small-debug.png (320px), welcome-650.png et welcome-1030.png (1080px), polish-arrival.png, polish-zone-level40.png. Le fondu et le paysage derrière le bouton sont visibles ; le niveau40 reste au-dessus du centre de la cible Continuer. Les compositions et les40étapes de9.7 sont conservées.
- APK9.8/code49 :98753921octets ; SHA256 4efab6a0036da54d626aaf8c9c885cc07c2ae2c1bf6e68460a860cef3ae8eaed. Clé fixe conservée, un seul APK livré dans APK/MemoMondes.apk.
- Limites :pas de vidéo évaluée manuellement, de téléphone physique ni d’essai enfant. La qualité perçue de l’animation n’est pas revendiquée comme validée sur l’appareil utilisateur. Même atlas256px par pose ; il peut paraître plus doux lorsqu’il est agrandi en haute densité. Le clignement est un changement de dessin distinct, pas une animation squelettique.

- Publication GitHubv9.8 réussie via tools/publish-github-release.ps1 ; correctifs depuis8.6 à9.7 reconstruits avant publication. API latest et manifestes nommé/latest code49 vérifiés avec le SHA256 final. Test Android du flux latest depuis la base officielle9.7 :PASS,110496octets téléchargés et reconstruction exacte98753921octets, package/version/signature et fournisseur en lecture seule vérifiés. Rapport github-update-9.7-to-9.8.txt. La confirmation système d’installation n’est pas répétée dans le test réseau. Base temporaire retirée ; un seul APK dans APK/, aucun APK à la racine ni dans releases/.

## Paysage derrière l'en-tête et accueil illustré — 9.9 — 10 octobre 2026

- Carte dessinée sur tout le viewport, sous l'en-tête et le bouton Continuer. Prolongement du premier paysage avec son bitmap existant, voile translucide dégradé. Les 40 positions et règles de progression sont conservées ; tests raster des bords supérieur et inférieur aux étapes 1/40.
- Illustration d'accueil originale 941×1672, chargée sur le worker existant en RGB565 (demi-résolution sur petits écrans), budget conservé inférieur à 4 MiB, libération en quittant le lancement et à la pause. Canvas matériel pour cet écran. Lumi plus petite et posée au sol ; respiration lente et clignement existants, aucune nouvelle boucle rapide. Aucun temps d'attente ajouté aux 1750 ms du lancement.
- Compilation finale via tools/build.ps1 : 120 tests unitaires, aucun échec ; lint release : 0 erreur, 25 avertissements existants. Publication locale via tools/publish-apk.ps1 et clé fixe conservée.
- APK final installé : PASS 922 contrôles Android en 320×640/densité160. Carte logicielle isolée chaude : médiane 5,27 ms, p95 6,66 ms ; ce n'est pas le FPS de l'activité. Rapport releases/validation/9.9/android-final.txt.
- PASS 20 contrôles d'accueil attaché en 1080×1920/densité480 : ressource réellement décodée, budget mémoire, positions, sortie de lancement et mode réduit. PASS 37 contrôles de carte : vraie victoire 1, trajet vers 2, toucher/célébration, Continuer, caméra et sections jusqu'au niveau 40, caches bornés. Rapports welcome-final.txt et polish-final.txt. Dimensions/densité restaurées ensuite.
- Captures finales réellement inspectées : welcome-final.png et map-final.png dans releases/validation/9.9 ; nouvelle peinture visible derrière Lumi, paysage visible derrière le haut et sous le bas de la carte. Un premier essai debug simultané à la compilation a raté l'assertion temporelle « Mid-journey visible » après capture ; le scénario final installé a passé sans modifier cette assertion. Il ne s'agissait pas d'une exception du code de l'application.
- APK 9.9/code50 : 101220842 octets, SHA256 311151997cd9751ddb784cdf6782a64cd4a53cc50b5b82fb227a5a1a0389645f.
- Limites : captures statiques inspectées et états automatisés, aucune vidéo évaluée manuellement, aucun téléphone physique testé. Aucun résultat de fluidité ressentie ou de 60 FPS revendiqué. Les poses Lumi restent issues de l'atlas existant ; l'illustration du chargement reste fixe.
- Publication GitHub v9.9 via tools/publish-github-release.ps1 ; correctifs des bases 8.6 à 9.8 reconstruits avant publication. API latest et manifeste latest vérifiés : code 50, SHA256 du final. Test Android depuis la base officielle 9.8 : PASS, 2502728 octets téléchargés, APK reconstruit exact de 101220842 octets, package/version/signature et fournisseur en lecture seule vérifiés. Rapport github-update-9.8-to-9.9.txt. La confirmation système d'installation n'est pas répétée par ce test. Base temporaire retirée ensuite. Un seul APK dans APK/, aucun APK à la racine ni dans releases/.
- Capture de petit écran welcome-small.png également inspectée après les 922 contrôles : illustration visible au lancement, textes et personnage séparés.

## Accueil à deux modes et bienvenue — 9.10 — 10 octobre 2026

- HomeScreen : deux cartes utilisant de vraies illustrations existantes, chapitre/niveau et progression AdventureStore, état neuf/terminé, Lumi animée. Écran 8 pour Jeu libre, réglages free_theme/free_pairs indépendants, monde via écran 4 avec retour vers 8. Album, musique et protection parents conservés. Java Canvas, pas de dépendance ajoutée.
- Migration des choix libres à partir des anciennes préférences, aucun effacement utilisateur. Tests SDK28/35 : entrée aventure/carte/Retour ; entrée libre/configuration/mondes/Retour ; choix mémorisés après une aventure et recréation du GameView. Victoire réelle d'aventure et victoire libre contrôlées : album commun enrichi dans les deux cas, progression uniquement par le niveau d'aventure valide. Scénarios de sauvegarde/reprise existants conservés.
- WelcomeMascot : nouvel atlas alpha à 4×2 poses, 1465788 octets sur disque ; plafond bitmap 8 MiB. Décodage sur worker et registration des pieds. Lancement de 5000 ms, coucou et smile, respiration, toucher rejouant le salut sans étendre ce timer. Mode réduit immobile. Prompt/provenance dans WELCOME-PROMPT-9.10.md. Peinture d'accueil inférieure à 4 MiB ; deux images de menu bornées par le décodeur partagé, largeur 720 pixels maximum, libération après navigation/pause. Aucun chargement des 40 peintures simultanément.
- Compilation finale via tools/build.ps1 puis tools/publish-apk.ps1 : 129 tests unitaires, 0 échec/erreur ; lint 0 erreur et 25 avertissements existants. Les premiers essais ont identifié des attentes de tests correspondant à l'ancien accueil/écran4 ; mises à jour des scénarios vers le parcours libre écran8. Les tests de production complets ont ensuite passé.
- APK final installé et contrôlé sur émulateur Android15 : PASS 922 contrôles généraux (solo/duel/coopération/calme et progression), PASS 18 contrôles d'accueil attaché (carte/configuration/mondes/partie/Retour, musique, album), PASS 24 contrôles de bienvenue en 320×640/densité160. Rapports android-final.txt, home-final.txt et welcome-final.txt dans releases/validation/9.10. Carte logicielle isolée chaude : médiane 5,62 ms, p95 6,99 ms ; pas une mesure de FPS de l'activité.
- En 1080×1920/densité480 : PASS 18 contrôles d'accueil, PASS 25 contrôles de bienvenue, PASS 37 contrôles de voyage/célébration/Continue/caches/caméra de carte. Rapport welcome-hd.txt inclut la recharge de l'atlas après une sortie automatique : le premier test HD réutilisait trop tôt l'atlas libéré après la capture tardive, corrigé en attendant le décodage hors UI dans le test avant d'évaluer le mode réduit. Aucun changement au timer de production. Dimensions/densité restaurées.
- Captures réellement inspectées : home-small.png, free-small.png, home-hd.png, welcome-wave-small.png, welcome-wave-hd.png. Les rendus unitaires home-910-320/360/412 et free-910-320 ont également été vérifiés sur les tailles pertinentes. La peinture, le salut et la séparation des modes sont visibles ; ces instantanés ne constituent pas une validation vidéo de l'animation.
- APK 9.10/code51 : 102690679 octets, SHA256 aa4db2d69a30957ccedfd78305d94f56e0380100501c073f3f08fe9ccba8f787 ; clé fixe conservée.
- Limites : pas de téléphone physique modeste testé, de vidéo évaluée manuellement, d'essai enfant ni de parcours TalkBack manuel complet. La réduction d'animations et les contrôles virtuels sont testés ; aucun 60 FPS ou résultat de fluidité ressentie sur l'appareil utilisateur revendiqué. Les nouvelles poses de coucou sont huit illustrations successives, avec respiration continue, pas une animation squelettique.
- Publication GitHub v9.10 via tools/publish-github-release.ps1 ; correctifs depuis les bases 8.6 à 9.9 reconstruits avant publication. API latest et manifeste latest vérifiés : code51 et SHA256 du final. Test Android depuis la base officielle 9.9 : PASS, 1519122 octets téléchargés et reconstruction exacte de 102690679 octets, package/version/signature et fournisseur en lecture seule vérifiés. Rapport github-update-9.9-to-9.10.txt. La confirmation système d'installation n'est pas répétée dans ce test. Base temporaire retirée ensuite ; APK/MemoMondes.apk reste le seul APK livré, aucun APK à la racine ni dans releases/.

## Usage, accessibilité et reprise — 9.11 — 10 octobre 2026

- Extraction ciblée : lecture/validation des sauvegardes et migration des propriétaires dans SavedGame. Format SharedPreferences m conservé. Contrôles des cartes, paires complètes, dimensions bornées, masques/propriétaires/scores, tours, compteurs et niveau d'aventure avant affectation au GameView. Tests avec sauvegardes fictives valides, invalides et anciens duels. Une reprise invalide conserve les clés de collection, progression, réglages et le contenu brut de la sauvegarde ; seule la proposition de reprise est désactivée.
- Haptique : une confirmation par paire trouvée ; bascule Vibrations respectée, aucun ajout haptique sur erreur. Test du vrai chemin de correspondance avec SDK28/35, y compris mode calme et vibrations désactivées. Aucun ressenti vibratoire validé sur appareil physique.
- Accessibilité : navigation de résultat/carte, état aventure terminée, coucou de bienvenue et activation au centre visible d'un contrôle partiellement masqué. Test de cette activation sous un en-tête fixe et de cibles de menu >=48dp sur petites tailles. Réduction système des animations et mode calme couverts par la suite existante.
- 147 tests unitaires, aucun échec/erreur après correction du masque erroné de la nouvelle fixture de duel (43 au lieu de 27). Lint : aucune erreur, 25 avertissements. Les 922 contrôles Android généraux passent, solo/duel/coopération/calme, progression, sauvegardes et carte. Rapports dans releases/validation/9.11.
- Menus attachés : 21 contrôles petit écran 320x640/densité160 et 21 contrôles HD 1080x1920/densité480. Première tentative : AssertionError « Back touch target » dans le scénario, avant le premier rendu de configuration. Assertion déplacée après la capture/rendu ; les deux scénarios passent. Les sorties Process crashed initiales sont conservées et ne sont pas comptées comme des validations réussies.
- Mesure de contraste sur couleurs opaques : texte/surface 11,52:1, secondaire/surface 6,37:1, aventure/Jouer 5,97:1, musique/surface 4,97:1. Jeu libre initialement 4,49:1, corrigé à 4,92:1 par léger assombrissement du texte uniquement. Ces mesures ne constituent pas un audit complet de toutes les images, superpositions et états.
- Limites : aucun essai enfant, parcours TalkBack manuel complet, téléphone physique, ressenti vibratoire ou vidéo évaluée manuellement. Parcours à réaliser dans USAGE-ACCESSIBILITE-9.11.md. Aucun test réseau de mise à jour 9.10 vers 9.11, aucune publication GitHub dans cette tâche. Les tests Android utilisent exclusivement l'émulateur dédié ; aucune donnée de téléphone personnel effacée.
- Après la retouche de contraste, les 147 tests unitaires et lint ont été relancés avec succès ; les 21 contrôles de menus par taille ont été relancés et les nouvelles captures accueil petit écran/HD inspectées. Les captures de configuration ont également été inspectées avant cette retouche, qui touche uniquement le texte du bouton accueil. Retour utilisateur : enfants de 5 et 7 ans, mention de la compréhension des boutons ; aucun parcours enfant directement observé.
- Final signé 9.11/code52 livré via build.ps1/publish-apk.ps1 : 102691581 octets, SHA256 5f0efeeac6d9a14bd1e10430f417b388f057830737528594778b0e3ae94283b1. apksigner verify : signature v1/v2 valide, certificat SHA256 07d349ca3a7002952bdf75068a8dcff47c77409393cf24b11d1d6a986197d0a7 ; clé fixe conservée. Avertissements apksigner sur deux métadonnées META-INF non protégées par la signature JAR, rapport conservé. L'émulateur est devenu indisponible avant le dernier contrôle release, puis a été relancé sans effacement de ses données.
- Installation finale release avec `adb install -r` : Success. Bienvenue release : PASS, 25 contrôles attachés, réduction d'animations et arrivée automatique à l'accueil. Taille/densité confirmées restaurées à 320x640/densité160. L'émulateur s'est ensuite arrêté de nouveau ; le contrôle additionnel `home` en release n'a pas produit de résultat (device not found après vérification). Ce contrôle n'est pas revendiqué comme passé ; les 21+21 contrôles des menus précédents portent sur la build debug des mêmes sources finales. Rapport welcome-release-small.txt conservé.

## Accès parents et reset aventure — 9.12 — 10 octobre 2026

- Cause reproduite par scénario : un déplacement vertical de 12 dp, toujours à l'intérieur du bouton parents, était interprété comme un scroll et annulait le maintien. Le maintien est traité avant le défilement ; il continue tant que le doigt reste dans le bouton. Relâchement prématuré, sortie de la zone, ACTION_CANCEL et pause annulent le maintien.
- Bouton parents visible sur l'accueil, 56 dp, libellé « Maintenir 2 secondes ». Ouverture après 2000 ms ; le relâchement après ouverture est consommé pour ne pas activer un réglage placé dessous. Les activations accessibles sont traitées comme de nouveaux gestes.
- Nouveau bouton « Recommencer l'aventure » dans les parents, confirmation avec boutons de 52 dp, Annuler et Retour Android sans modification. Confirmation : adventure_completed=0, suppression de la partie sauvegardée uniquement si elle appartient à l'aventure, remise à zéro du mouvement de carte et du niveau courant du GameView. Album, badges, records, statistiques, réglages et reprise d'une partie libre conservés.
- Premiers essais : fixture du mode calme incohérente avec la vue déjà créée, corrigée ; saturation mémoire Windows (erreur Java 1455 et CoreCLR) quand tests et émulateur tournaient ensemble. Émulateur arrêté sans effacement et validations ensuite séquencées. Le test historique d'appui interrompu supposait 2500 ms ; adapté au nouveau délai explicite de 2000 ms, en conservant la vérification qu'un appui abandonné ne raccourcit pas le suivant.
- Validation finale : 155 tests unitaires SDK28/35, 0 échec/erreur ; lint 0 erreur et 25 avertissements. Scénario Android attaché `home` étendu : PASS, 30 contrôles en 320x640/densité160 et 30 en 1080x1920/densité480. Vrai maintien de 2 secondes avec mouvement de 12 dp, ouverture parents, confirmation, annulation via Retour, retour au niveau 1 et conservation album/records/réglages vérifiés. Préférences de l'émulateur restaurées en fin de scénario.
- Captures parents-small.png, reset-small.png et parents-hd.png inspectées : bouton reset visible, textes lisibles, actions de confirmation de 52 dp. reset-hd.png est noire et n'est pas une validation visuelle de confirmation HD, malgré les assertions d'état réussies. Taille/densité restaurées après le scénario HD, émulateur arrêté avant la compilation finale pour limiter la mémoire.
- Limites : aucun test manuel sur le téléphone de la capture utilisateur, parcours TalkBack manuel ni vidéo évaluée. Pas de publication GitHub ni de test de correctif réseau 9.11 vers 9.12 dans cette tâche. Le reset n'a pas été exécuté sur un appareil personnel.
- APK final 9.12/code53 : 102692444 octets, SHA256 93735cbd06b303cb4ac142741040d582f8302e6ad101a9e7eaa09618df306383. Build final réussi via tools/build.ps1 et livraison via tools/publish-apk.ps1. Signature vérifiée par apksigner (code 0), clé fixe conservée ; les deux avertissements META-INF déjà présents restent signalés. APK/MemoMondes.apk est le seul final. Les 922 contrôles généraux de 9.11 ne sont pas revendiqués comme relancés pour 9.12 ; les 155 tests unitaires et 60 contrôles attachés ci-dessus sont les validations de cette tâche.
