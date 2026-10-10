# MémoMondes 9.9 — V2

Jeu de mémoire Android pour les enfants de 5 à 8 ans. Le jeu fonctionne hors ligne ; une connexion sert à vérifier et télécharger les mises à jour GitHub.

Le dossier source original ayant été perdu, cette version a été reconstruite à partir de l'APK 7.2, puis remise en état de compilation. Les images, musiques et narrations antérieures ont été récupérées de cet APK. Le code Java reconstruit par décompilation peut contenir des noms de variables moins lisibles que le code original.

## Télécharger et suivre le projet

- [Dépôt GitHub](https://github.com/Nyaru01/Memory)
- [Dernière version et notes de publication](https://github.com/Nyaru01/Memory/releases/latest)
- [Télécharger l’APK actuel](https://github.com/Nyaru01/Memory/releases/latest/download/MemoMondes.apk)
- [Toutes les versions publiées](https://github.com/Nyaru01/Memory/releases)
- [Signaler un problème](https://github.com/Nyaru01/Memory/issues)
- [Changements détaillés](CHANGELOG.md)
- [Fonctionnement et publication des mises à jour](docs/MISES_A_JOUR.md)

Installer une fois la version 8.6 ou suivante pour activer les mises à jour depuis GitHub. Depuis la 8.11, le jeu vérifie au retour dans l'application et toutes les minutes tant qu'elle reste ouverte. Un correctif compatible est téléchargé en arrière-plan ; un panneau propose « Installer » ou « Plus tard », directement dans le jeu. Une partie en cours n'est pas interrompue par ce panneau : le bouton d'installation reste disponible dans le bandeau. La confirmation finale appartient à Android. Le jeu et la progression restent utilisables hors ligne.

## Carte d’exploration 9.9

La9.9 prolonge aussi le paysage derrière l’en-tête, avec un voile translucide. Le lancement accueille Lumi dans une clairière illustrée dédiée, avec une respiration discrète et un clignement. Voir [l’accueil illustré et la carte plein écran](docs/ACCUEIL-IMMERSIF-9.9.md).

Mon aventure propose 40 niveaux traversant vingt paysages illustrés distincts. Lumi et les petites créatures sont animés ; glissement avec inertie et raccourci vers le niveau actuel. Le niveau 1 est ouvert au départ, puis chaque partie terminée débloque la suivante. Reprise et progression sauvegardées ; jeu libre et albums conservés. Voir [la carte et ses règles](docs/AVENTURE.md).

La 9.6 remplace les petites vignettes par vingt illustrations individuelles de 972 à 1254 pixels de large. La carte utilise le Canvas accéléré par le GPU, avec décodage en arrière-plan et cache limité aux sections proches. Les proportions des illustrations sont conservées.

La9.7 conserve ces paysages et ajoute des raccords organiques, des étapes placées près des repères illustrés, un sentier discret et une interface compacte. « Avancer avec Lumi » montre le trajet vers le niveau débloqué, puis Continuer lance la partie. Voir [les finitions et leurs limites](docs/FINITIONS-CARTE-9.7.md).

## Version V2

Accueil et mondes plus lisibles, plateau adapté aux quatre difficultés, victoire avec Lumi et progression réelle de l’album. Java/Canvas, les 18 mondes originaux, les réglages et les sauvegardes sont conservés. La 9.2 ajoute Robots rigolos et Jardin miniature, et regroupe les 20 mondes en cinq familles repliables. Voir [victoire et mondes 9.2](docs/VICTOIRE-MONDES-9.2.md). Voir [le rapport V2](docs/V2.md) pour les décisions, critères et limites.

## Compiler

Pré-requis : JDK 21, Android SDK 35 et Build Tools 35.0.0. Les outils de cette session sont dans `D:\AndroidDev` : JDK sous `java/`, SDK sous `sdk/`. Sous Windows :

```powershell
.\tools\build.ps1
```

L'APK final se trouve toujours dans `D:\VibeCoding\Appli enfant\APK\MemoMondes.apk`. Après tests Android locaux, compilation et lint réussis, le script remplace ce fichier et retire les autres APK de ce dossier. Si une vérification échoue, le dernier APK final est conservé. Les fichiers dans `app/build/` sont des intermédiaires de compilation et de test.

Le script place les caches Gradle, fichiers temporaires, préférences Android et clé debug sous `D:\AndroidDev`. Il ne modifie pas les variables système. Ne pas utiliser directement le wrapper si l'on veut garantir ces emplacements.

La clé de démonstration est maintenant fixée dans `D:\AndroidDev\signing\MemoMondes.keystore`. Elle correspond à celle de la version 8.3, et diffère de celle de l'ancienne version 8.2. La clé d'origine étant introuvable, une installation neuve a été choisie : désinstaller l'ancienne application avant d'installer la nouvelle, avec perte possible de la progression locale. Conserver la clé fixe pour que les prochaines versions puissent mettre à jour cette nouvelle installation. Changer de clé empêcherait les prochaines mises à jour : conserver cette clé fixe, ne jamais la publier et en garder une sauvegarde privée hors du dépôt.

## Tester ici sur Windows

Les fichiers Android Emulator, le SDK et l'image Android 35 sont dans `D:\AndroidDev`. Le composant Windows `HypervisorPlatform` a été activé avec autorisation ; il est opérationnel après le redémarrage effectué.

Après redémarrage, double-cliquer `Tester-MemoMondes.cmd` à la racine du projet. Le lanceur ouvre l'émulateur `MemoMondes35`, puis installe `APK/MemoMondes.apk` et ouvre le jeu. Les logs sont dans `D:\AndroidDev\emulator-start*.log` et `D:\AndroidDev\emulator-app-install*.log`. Le rendu utilise le GPU hôte et désactive Vulkan pour éviter le plantage observé avec le backend logiciel. Aucun IDE Android Studio n'est nécessaire.

Pour une exécution sans fenêtre : `.\tools\start-emulator.ps1 -Headless`. Le lanceur refuse de démarrer si l'accélération n'est pas disponible.

## Organisation et documentation

- `app/src/main/` : sources Java et ressources de l'application.
- `tools/` : compilation et scripts facultatifs de génération des médias.
- `docs/PLAN.md` : plan exécuté et état des vérifications.
- `docs/AMELIORATIONS.md` : plan de la prochaine itération, par priorité.
- `docs/ARCHITECTURE.md` : écrans, stockage et fonctionnement des effets.
- `docs/VALIDATION.md` : vérifications et parcours à tester.
- `CHANGELOG.md` : changements par version.
- `APK/` : un seul APK final, `MemoMondes.apk` ; dossier ignoré par Git.
- `releases/` : rapports de validation et métadonnées archivées, sans ancien APK.

Les dossiers `.gradle/`, `build/` et `app/build/` contiennent des fichiers générés et sont ignorés par Git. Les signatures issues de la décompilation sont conservées dans `releases/archive/`. Les anciens APK distribuables sont supprimés pour ne conserver que la version finale actuelle.

## Contenu

- Lumi en spritesheet : 24 poses illustrées, cinq états d’animation, squash/stretch, rebonds, expressions et réactions au toucher. Voir [MASCOTTE-SPRITES.md](docs/MASCOTTE-SPRITES.md).


- Paires réussies : impulsion douce, halo, particules et coches permanentes ; mode calme respecté.
- Dernière paire visible avant l'écran de victoire ; étoiles et confettis améliorés.
- Retour de pression des boutons et erreur de paire avec une teinte douce.
- Préférence Android de réduction des animations respectée ; commandes et cartes accessibles via TalkBack.
- Horloge monotone, géométrie des cartes réutilisée et file de sons bornée.

- 20 mondes organisés en cinq familles repliables, dont Robots rigolos et Jardin miniature ; 12 images à découvrir par monde.
- Les illustrations conservent leurs proportions sur les tuiles, les cartes et dans l'album.
- Les vignettes de choix du monde sont détourées, sans rectangle crème autour des personnages ; l'ours blanc et le pompier utilisent des détourages transparents dédiés.
- Un album réorganisé avec vue des mondes, images et badges.
- 18 histoires préenregistrées et deux nouvelles histoires originales lues avec la voix française Android ; lecture vocale de secours pour les autres.
- 6 musiques, dont 2 compositions originales générées par `tools/generate_music.py`.

Les éléments du compte ElevenLabs ne sont pas inclus dans le projet. Le script `tools/generate_narrations.py` lit la clé depuis la variable d'environnement `ELEVENLABS_API_KEY` si une nouvelle génération est souhaitée.

## Mises à jour du jeu

Depuis la version 8.6, les correctifs GitHub se téléchargent au lancement et sont proposés directement dans le jeu, hors de l'espace parents. Android confirme l'installation. Installer une première fois cette version pour activer le mécanisme. Voir [MISES_A_JOUR.md](docs/MISES_A_JOUR.md) pour publier les versions suivantes et connaître les limites.
