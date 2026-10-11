package fr.memomimi.app;

import org.junit.Test;
import static org.junit.Assert.*;

public class BoardSizeTest {
    @Test public void eightPairsUseTallPhoneSpaceWithoutShrinkingCompactBoards(){
        BoardLayout board=new BoardLayout();
        board.fit(16,360,700,1,186);
        assertEquals(3,board.columns);assertEquals(6,board.rows);
        assertTrue(board.width>=76*1.06f);
        assertEquals(180,board.cardLeft(15,16)+board.width/2,.01f);
        board.fit(16,320,294,1,170);assertEquals(4,board.columns);assertTrue(board.width>=48);
        board.fit(16,360,578,1,186);assertEquals(4,board.columns);assertTrue(board.width>=79.5f);assertTrue(board.height>=103);
    }
    @Test public void allDeckSizesFitAndKeepDistinctTouchBounds(){
        for(float d:new float[]{1,1.5f,3})for(int count:new int[]{12,16,20,24})for(float h:new float[]{294,450,594,700}){
            BoardLayout b=new BoardLayout();b.fit(count,360*d,h*d,d,170*d);
            for(int i=0;i<count;i++){assertTrue(b.cardLeft(i,count)>=0);assertTrue(b.cardLeft(i,count)+b.width<=360*d+.01f);if(i>0&&i/b.columns==(i-1)/b.columns)assertTrue(b.cardLeft(i,count)>=b.cardLeft(i-1,count)+b.width);}
            assertTrue(b.top+b.rows*b.height+(b.rows-1)*b.gap<= (170+h)*d+.1f);
        }
    }
}
