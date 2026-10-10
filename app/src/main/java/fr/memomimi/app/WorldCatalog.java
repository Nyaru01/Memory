package fr.memomimi.app;

/** Stable world IDs: append worlds, never reorder persisted progress. */
final class WorldCatalog {
    static final String[] FAMILIES = {"Animaux et nature", "Mers et aventures", "Rêves et magie", "À la maison", "À créer et découvrir"};
    static final int[][] WORLDS = {{0,3,4,8,11,19}, {1,7,10,17}, {2,6,14}, {9,15,16}, {5,12,13,18}};
    static int familyOf(int world) {
        for (int family=0; family<WORLDS.length; family++)
            for (int candidate:WORLDS[family]) if(candidate==world) return family;
        return 0;
    }
    private WorldCatalog() { }
}
