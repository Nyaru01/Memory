package fr.memomimi.app;

/** Geometry shared by rendering, gestures and accessibility. Never changes the deck. */
final class BoardLayout {
    int columns, rows;
    float left, top, width, height, gap;

    void fit(int count, float availableWidth, float availableHeight, float density, float startY) {
        gap = (count >= 20 ? 6 : UiStyle.GAP)*density;
        int preferred = count <= 12 ? 3 : 4;
        columns = preferred;
        float best = -Float.MAX_VALUE;
        for (int candidate = 3; candidate <= 6; candidate++) {
            int candidateRows = (count+candidate-1)/candidate;
            float w = (availableWidth-2*UiStyle.MARGIN*density-(candidate-1)*gap)/candidate;
            float h = (availableHeight-(candidateRows-1)*gap)/candidateRows;
            float size = Math.min(w, h/1.12f);
            float score = size/density - (candidate*candidateRows-count)*2 - (candidate == preferred ? 0 : 3);
            if (score > best) { best = score; columns = candidate; }
        }
        rows = (count+columns-1)/columns;
        width = Math.max(1, Math.min((availableWidth-2*UiStyle.MARGIN*density-(columns-1)*gap)/columns,
                (availableHeight-(rows-1)*gap)/rows/1.12f));
        height = width*1.12f;
        left = (availableWidth-columns*width-(columns-1)*gap)/2;
        top = startY + (availableHeight-rows*height-(rows-1)*gap)/2;
    }
}
