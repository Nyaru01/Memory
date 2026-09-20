package fr.memomimi.app;

import java.util.Random;

/* JADX INFO: loaded from: classes.dex */
final class GameModel {
    static int columns(int i) {
        if (i == 15) {
            return 5;
        }
        return i <= 6 ? 3 : 4;
    }

    GameModel() {
    }

    static int[] shuffledDeck(int i, Random random) {
        int i2 = i * 2;
        int[] iArr = new int[i2];
        for (int i3 = 0; i3 < i2; i3++) {
            iArr[i3] = i3 / 2;
        }
        for (int i4 = i2 - 1; i4 > 0; i4--) {
            int iNextInt = random.nextInt(i4 + 1);
            int i5 = iArr[i4];
            iArr[i4] = iArr[iNextInt];
            iArr[iNextInt] = i5;
        }
        return iArr;
    }

    static int ownedPairs(int[] iArr, int i) {
        if (iArr == null) {
            return 0;
        }
        int i2 = 0;
        for (int i3 : iArr) {
            if (i3 == i) {
                i2++;
            }
        }
        return i2 / 2;
    }
}
