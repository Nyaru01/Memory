# MémoMondes

Jeu de mémoire Android hors ligne pour les enfants de 5 à 7 ans.

Le dossier source original ayant été perdu, cette version a été reconstruite à partir de l'APK 7.2, puis remise en état de compilation. Les images, musiques et narrations antérieures ont été récupérées de cet APK. Le code Java reconstruit par décompilation peut contenir des noms de variables moins lisibles que le code original.

## Compiler

Pré-requis : JDK 21 et Android SDK avec la plateforme Android 35. Sous Windows :

```powershell
.\gradlew.bat :app:assembleRelease
```

L'APK se trouve dans `app/build/outputs/apk/release/app-release.apk`. Cette version de démonstration est signée avec la clé Android debug présente sur cette machine, ce qui permet la mise à jour de l'APK 7.2 existant. Pour une distribution publique, créer une clé de signature privée et la conserver en lieu sûr.

## Contenu

- 18 mondes, dont Fées, Maison, Légumes et Détective ; 12 images à découvrir par monde.
- Un album réorganisé avec vue des mondes, images et badges.
- 18 histoires narrées, dont les 4 nouvelles produites avec ElevenLabs ; lecture vocale Android de secours.
- 6 musiques, dont 2 compositions originales générées par `tools/generate_music.py`.

Les éléments du compte ElevenLabs ne sont pas inclus dans le projet. Le script `tools/generate_narrations.py` lit la clé depuis la variable d'environnement `ELEVENLABS_API_KEY` si une nouvelle génération est souhaitée.
