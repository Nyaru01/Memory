package fr.memomimi.app;

import android.graphics.*;

/** Two illustrated entrances, independent from the free-game configuration. */
final class HomeScreen {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private final Rect source=new Rect();
    private final RectF art=new RectF(),hero=new RectF(),card=new RectF();
    private final Path clip=new Path();
    private volatile Bitmap[] pictures;
    private LinearGradient titleWash;
    private float washTop=-1,washBottom=-1;
    private int key=-1,generation;
    synchronized void request(GameView v){
        int section=(v.adventure.current()-1)/2,pixels=Math.max(256,Math.min(720,Math.round(v.W)));
        int wanted=section*10000+pixels;if(wanted==key)return;key=wanted;int token=++generation;
        v.adventureMap.scenery.executor().execute(()->{
            Bitmap a=AdventureScenery.decode(v.getContext().getApplicationContext(),section,pixels);
            Bitmap b=AdventureScenery.decode(v.getContext().getApplicationContext(),6,pixels);
            synchronized(this){if(token!=generation){if(a!=null)a.recycle();if(b!=null)b.recycle();return;}pictures=new Bitmap[]{a,b};}
            v.postInvalidate();
        });
    }
    synchronized void release(){if(key==-1&&pictures==null)return;generation++;key=-1;pictures=null;}
    int bytes(){Bitmap[] b=pictures;if(b==null)return 0;return (b[0]==null?0:b[0].getAllocationByteCount())+(b[1]==null?0:b[1].getAllocationByteCount());}
    private void painting(Canvas c,RectF area,int index){
        Bitmap[] b=pictures;Bitmap image=b==null?null:b[index];
        paint.setColor(0xffe1efe6);c.drawRect(area,paint);
        if(image!=null){AdventureScenery.crop(image,area.width(),area.height(),source);paint.setColor(Color.WHITE);c.drawBitmap(image,source,area,paint);}
    }
    void draw(Canvas c,GameView v){
        request(v);float d=v.d,top=v.systemTop(),bottom=v.H-v.systemBottom(),w=v.W;
        v.players=1;v.onePlayer.setEmpty();v.coopPlayer.setEmpty();v.twoPlayers.setEmpty();v.mega.setEmpty();
        v.chooseTheme.setEmpty();v.easy.setEmpty();v.medium.setEmpty();v.hard.setEmpty();v.expert.setEmpty();v.play.setEmpty();
        v.homeScrollMax=Math.max(0,600*d-(bottom-top));v.homeScroll=Math.min(v.homeScroll,v.homeScrollMax);
        c.save();c.clipRect(0,top,w,bottom);float y=top+24*d-v.homeScroll;
        v.fitLabel(c,"MémoMondes",w/2,y,30,25,w-32*d,v.ink);
        v.fitLabel(c,"Un voyage, mille découvertes",w/2,y+28*d,13,11,w-32*d,UiStyle.MUTED);y+=48*d;
        // Keep the illustrated cards and their touch targets on the same grid.
        card.set(16*d,y,w-16*d,y+226*d);v.round(c,card,22,UiStyle.SURFACE);
        clip.reset();clip.addRoundRect(card,22*d,22*d,Path.Direction.CW);c.save();c.clipPath(clip);
        art.set(card.left,card.top,card.right,card.top+158*d);painting(c,art,0);
        if(washTop!=art.top||washBottom!=art.bottom){washTop=art.top;washBottom=art.bottom;titleWash=new LinearGradient(0,art.top,0,art.bottom,0xeef8fcf5,0x00f8fcf5,Shader.TileMode.CLAMP);}
        paint.setShader(titleWash);c.drawRect(art,paint);paint.setShader(null);
        int level=v.adventure.current(),done=v.adventure.completed();
        v.fitLabel(c,"MON AVENTURE",card.centerX(),y+22*d,20,17,card.width()-24*d,v.ink);
        v.fitLabel(c,v.worldTitle(AdventureStore.world(level)),card.centerX(),y+47*d,16,13,card.width()-24*d,v.ink);
        hero.set(card.right-116*d,y+57*d,card.right-12*d,y+151*d);
        v.victoryMascot.drawMap(c,hero,GameView.now()-v.navigationAt,-1,v.animationsReduced(),false,-1);
        v.uiScratch.set(card.left+12*d,y+100*d,hero.left-8*d,y+144*d);v.round(c,v.uiScratch,14,0xedfffaf2);
        v.fitLabel(c,done==0?"Ton voyage commence !":done==40?"Les 40 niveaux terminés !":"Niveau "+level+" t’attend",v.uiScratch.centerX(),v.uiScratch.centerY()-9*d,13,10,v.uiScratch.width()-16*d,v.ink);
        v.fitLabel(c,done+" / 40 niveaux",v.uiScratch.centerX(),v.uiScratch.centerY()+9*d,12,10,v.uiScratch.width()-16*d,UiStyle.MUTED);
        v.adventureButton.set(card.left+12*d,card.bottom-60*d,card.right-12*d,card.bottom-12*d);v.round(c,v.adventureButton,17,UiStyle.REPLAY);
        v.fitLabel(c,done==0?"Commencer l’aventure":done==40?"Revoir mon aventure":"Continuer l’aventure",v.adventureButton.centerX(),v.adventureButton.centerY(),17,14,v.adventureButton.width()-24*d,UiStyle.REPLAY_TEXT);
        c.restore();y=card.bottom+12*d;
        card.set(16*d,y,w-16*d,y+142*d);v.round(c,card,22,UiStyle.SURFACE);
        clip.reset();clip.addRoundRect(card,22*d,22*d,Path.Direction.CW);c.save();c.clipPath(clip);
        art.set(card.left,card.top,card.right,card.top+74*d);painting(c,art,1);
        paint.setColor(0xaffffbf5);c.drawRect(art,paint);
        v.fitLabel(c,"JEU LIBRE",card.centerX(),y+22*d,20,17,card.width()-24*d,v.ink);
        v.fitLabel(c,"Choisis ton monde et joue à ton rythme !",card.centerX(),y+50*d,13,10,card.width()-24*d,v.ink);
        v.freePlayButton.set(card.left+12*d,card.bottom-60*d,card.right-12*d,card.bottom-12*d);v.round(c,v.freePlayButton,17,0xffe8def7);
        v.fitLabel(c,"Jouer librement",v.freePlayButton.centerX(),v.freePlayButton.centerY(),17,14,v.freePlayButton.width()-24*d,UiStyle.FREE_PLAY_TEXT);
        c.restore();y=card.bottom+12*d;
        float mid=w/2;v.albumBtn.set(16*d,y,mid-6*d,y+54*d);v.soundBtn.set(mid+6*d,y,w-16*d,y+54*d);
        v.round(c,v.albumBtn,17,UiStyle.SURFACE);v.round(c,v.soundBtn,17,UiStyle.SURFACE);
        v.fitLabel(c,"Mon album",v.albumBtn.centerX(),v.albumBtn.centerY()-10*d,14,12,v.albumBtn.width()-16*d,v.purple);
        v.fitLabel(c,v.albumCount()+" / "+GameView.ALBUM_TOTAL+" images",v.albumBtn.centerX(),v.albumBtn.centerY()+10*d,12,10,v.albumBtn.width()-16*d,UiStyle.MUTED);
        v.fitLabel(c,v.sound?"♪ Musique : oui":"♪ Musique : non",v.soundBtn.centerX(),v.soundBtn.centerY(),13,10,v.soundBtn.width()-10*d,UiStyle.SUCCESS);
        v.parentBtn.set(16*d,y+66*d,w-16*d,y+122*d);v.round(c,v.parentBtn,17,UiStyle.SURFACE);
        v.fitLabel(c,"Espace parents",v.parentBtn.centerX(),v.parentBtn.centerY()-10*d,14,12,v.parentBtn.width()-24*d,v.ink);
        v.fitLabel(c,v.parentHolding?"Continue à maintenir…":"Maintenir 2 secondes",v.parentBtn.centerX(),v.parentBtn.centerY()+10*d,12,10,v.parentBtn.width()-24*d,UiStyle.MUTED);
        c.restore();
        if(v.activityActive&&!v.animationsReduced())v.postInvalidateDelayed(50);
    }
}
