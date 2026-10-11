package fr.memomimi.app;

/** Geometry shared by rendering, gestures and accessibility. Never changes the deck. */
final class BoardLayout {
    int columns, rows;
    float left, top, width, height, gap;

    void fit(int count, float availableWidth, float availableHeight, float density, float startY) {
        fit(count,availableWidth,availableHeight,density,startY,false);
    }

    private void fit(int count,float availableWidth,float availableHeight,float density,float startY,boolean compact) {
        gap = (compact ? 4 : count >= 16 ? 6 : UiStyle.GAP)*density;
        float margin=(compact?6:count>=16?12:UiStyle.MARGIN)*density;
        int preferred = count <= 12 ? 3 : 4;
        columns = preferred;
        float best = -Float.MAX_VALUE;
        for (int candidate = 3; candidate <= 6; candidate++) {
            int candidateRows = (count+candidate-1)/candidate;
            float w = (availableWidth-2*margin-(candidate-1)*gap)/candidate;
            float h = (availableHeight-(candidateRows-1)*gap)/candidateRows;
            float size = Math.min(w, h/1.12f);
            float score = size/density - (candidate*candidateRows-count)*2 - (candidate == preferred ? 0 : 3);
            if (score > best) { best = score; columns = candidate; }
        }
        rows = (count+columns-1)/columns;
        // On tall phones, 3 x 6 can make the sixteen cards appreciably larger.
        // Keep the regular 4 x 4 unless the gain is real; never change the deck.
        if(count==16 && columns==4){
            float four=Math.min((availableWidth-2*margin-3*gap)/4,
                    (availableHeight-3*gap)/4/1.12f);
            float three=Math.min((availableWidth-2*margin-2*gap)/3,
                    (availableHeight-5*gap)/6/1.12f);
            if(three>=four*1.06f){columns=3;rows=6;}
        }
        width = Math.max(1, Math.min((availableWidth-2*margin-(columns-1)*gap)/columns,
                (availableHeight-(rows-1)*gap)/rows/1.12f));
        // Dense decks on short screens need tighter spacing to retain 48 dp targets.
        if(!compact && count>=16 && width<UiStyle.TARGET*density){
            fit(count,availableWidth,availableHeight,density,startY,true);
            return;
        }
        // Use spare vertical space on every deck; never reduce the fitted width.
        height = Math.min(width*1.30f,(availableHeight-(rows-1)*gap)/rows);
        left = (availableWidth-columns*width-(columns-1)*gap)/2;
        top = startY + (availableHeight-rows*height-(rows-1)*gap)/2;
    }

    float cardLeft(int index,int count){
        int remaining=count%columns;
        float offset=remaining>0 && index/columns==rows-1?(columns-remaining)*(width+gap)/2:0;
        return left+offset+index%columns*(width+gap);
    }
}
