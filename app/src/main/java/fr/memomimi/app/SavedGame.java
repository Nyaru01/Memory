package fr.memomimi.app;

import android.content.SharedPreferences;

/** Validates the existing save format before GameView changes any live state. */
final class SavedGame {
    final int pairs, players, theme, level, turn, score1, score2, moves, found, errors, first;
    final int[] cards, owners;
    final boolean[] matched;
    final boolean hintUsed;

    SavedGame(SharedPreferences p, AdventureStore adventure) {
        pairs=p.getInt("saved_pairs",6); players=p.getInt("saved_players",1);
        theme=p.getInt("saved_theme",0); level=p.getInt("saved_adventure_level",0);
        require(pairs==3 || pairs==6 || pairs==8 || pairs==10 || pairs==12 || pairs==15);
        require(players>=1 && players<=3 && theme>=0 && theme<GameView.WORLD_COUNT);
        require(level==0 || (adventure.unlocked(level) && players==1
                && theme==AdventureStore.world(level) && pairs==AdventureStore.pairs(level)));
        String encoded=p.getString("saved_cards","");
        require(encoded!=null && encoded.length()<=120);
        String[] values=encoded.split(",",-1); require(values.length==pairs*2);
        cards=new int[values.length]; matched=new boolean[values.length]; owners=new int[values.length];
        long mask=p.getLong("saved_matched",0), owner1=p.getLong("saved_owner1",0), owner2=p.getLong("saved_owner2",0);
        long allowed=(1L<<values.length)-1;
        require(((mask|owner1|owner2)&~allowed)==0 && (owner1&owner2)==0);
        require(((owner1|owner2)&~mask)==0 && (players==2 || (owner1|owner2)==0));
        int[] counts=new int[pairs], matchedCounts=new int[pairs], pairOwner=new int[pairs];
        for(int i=0;i<cards.length;i++) {
            int card=Integer.parseInt(values[i]); require(card>=0 && card<pairs);
            cards[i]=card; counts[card]++; matched[i]=(mask&(1L<<i))!=0;
            owners[i]=(owner1&(1L<<i))!=0?1:(owner2&(1L<<i))!=0?2:0;
            if(matched[i]) {
                if(matchedCounts[card]>0) require(pairOwner[card]==owners[i]);
                pairOwner[card]=owners[i]; matchedCounts[card]++;
            }
        }
        int actualFound=0, actual1=0, actual2=0;
        for(int i=0;i<pairs;i++) {
            require(counts[i]==2 && (matchedCounts[i]==0 || matchedCounts[i]==2));
            if(matchedCounts[i]==2) { actualFound++; if(pairOwner[i]==1)actual1++; if(pairOwner[i]==2)actual2++; }
        }
        turn=p.getInt("saved_turn",1); score1=p.getInt("saved_score1",0); score2=p.getInt("saved_score2",0);
        found=p.getInt("saved_found",0); moves=p.getInt("saved_moves",0); errors=p.getInt("saved_errors",0);
        require(turn>=1 && turn<=2 && (players==2 || turn==1));
        require(found==actualFound && found<pairs && moves>=found && errors>=0 && errors<=moves);
        require(score1>=0 && score2>=0 && score1<=pairs && score2<=pairs);
        if(players==2) {
            require(score1+score2==found);
            // Old duel saves had scores but no owner masks. Preserve their migration.
            if((owner1|owner2)!=0) require(actual1==score1 && actual2==score2);
            else {
                int remaining=score1;
                for(int index=0;index<cards.length;index++) if(matched[index] && owners[index]==0) {
                    int owner=remaining-- > 0?1:2;
                    for(int i=index;i<cards.length;i++) if(cards[i]==cards[index]) owners[i]=owner;
                }
            }
        } else require(score1==0 && score2==0);
        int savedFirst=p.getInt("saved_first",-1);
        first=savedFirst>=0 && savedFirst<cards.length && !matched[savedFirst]?savedFirst:-1;
        hintUsed=p.getBoolean("saved_hint_used",false);
    }

    private static void require(boolean valid) {
        if(!valid) throw new IllegalArgumentException("Inconsistent saved game");
    }
}
