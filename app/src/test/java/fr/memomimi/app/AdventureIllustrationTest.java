package fr.memomimi.app;

import android.graphics.*;
import org.junit.*;
import static org.junit.Assert.*;

/** Meaningful renderer regressions in addition to the full progression scenarios. */
public class AdventureIllustrationTest extends AdventureTest {
    @Test public void illustratedTilesRemainBoundedDuringWholeRouteAndReopen() {
        view.openAdventure();
        for(int level=1;level<=40;level++) {
            view.adventureMap.focus(level,488,1);render(level==13?"adventure-illustrated-ocean":level==21?"adventure-illustrated-space":null,360,640);
            assertTrue(view.adventureMap.scenery.retainedTiles()>0);
            assertTrue(view.adventureMap.scenery.retainedTiles()<=3);
            assertTrue(view.adventureMap.scenery.retainedBytes()<=6*1024*1024);
        }
        assertEquals(0,view.adventure.completed());
        view.adventureMap.release();assertEquals(0,view.adventureMap.scenery.retainedBytes());
        view.openAdventure();render("adventure-illustrated-start",360,640);assertTrue(view.adventureMap.scenery.retainedTiles()>0);assertTrue(view.adventureMap.decor.bytes()>0);
    }
    @Test public void travelOccursOnceAndCalmModeSnapsToCurrent() {
        AdventureMotion m=new AdventureMotion();m.completed(1);m.open(2,1000,false);
        assertEquals(1,m.from);assertTrue(m.travelling(1000));assertEquals(0,m.progress(1000),0);
        assertEquals(.5,m.progress(2100),.001);assertEquals(1,m.progress(3200),0);assertFalse(m.travelling(3200));
        m.open(2,4000,false);assertFalse(m.travelling(4000));
        m.completed(2);m.open(3,5000,true);assertEquals(3,m.from);assertFalse(m.travelling(5000));
        m.completed(4);m.open(6,6000,false);assertEquals(6,m.from);
    }
    @Test public void focusButtonAndMascotReactionDoNotLaunchOrUnlock() {
        view.openAdventure();render(null,360,640);
        click(view.adventureMap.lumi);assertEquals(7,view.screen);assertTrue(view.adventureMap.motion.reactionAt>=0);
        view.adventureMap.scrollBy(2000);render(null,360,640);click(view.adventureMap.focusButton);render(null,360,640);
        assertTrue(view.adventureMap.nodes[0].centerY()>=view.adventureMap.top);
        assertTrue(view.adventureMap.nodes[0].centerY()<=view.adventureMap.bottom);assertEquals(0,view.adventure.completed());
        assertTrue(view.accessible.targets().stream().anyMatch(t->t.label.equals("Revenir au niveau actuel")));
    }
}
