package fr.memomimi.app;

import android.graphics.*;

/** Two illustrated entrances, independent from the free-game configuration. */
final class HomeScreen {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private final Rect source=new Rect();
    private final RectF art=new RectF(),hero=new RectF(),card=new RectF();
    private final Path clip=new Path();
    private final Path previewTrail=new Path();
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
        v.homeScrollMax=Math.max(0,682*d-(bottom-top));v.homeScroll=Math.min(v.homeScroll,v.homeScrollMax);
        c.save();c.clipRect(0,top,w,bottom);float y=top+24*d-v.homeScroll;
        v.fitLabel(c,"MémoMondes",w/2,y,30,25,w-32*d,v.ink);
        v.fitLabel(c,"Un voyage, mille découvertes",w/2,y+28*d,13,11,w-32*d,UiStyle.MUTED);y+=48*d;
        // Keep the illustrated cards and their touch targets on the same grid.
        card.set(16*d,y,w-16*d,y+324*d);v.round(c,card,22,UiStyle.SURFACE);
        clip.reset();clip.addRoundRect(card,22*d,22*d,Path.Direction.CW);c.save();c.clipPath(clip);
        art.set(card.left,card.top,card.right,card.top+184*d);painting(c,art,0);
        if(washTop!=art.top||washBottom!=art.bottom){washTop=art.top;washBottom=art.bottom;titleWash=new LinearGradient(0,art.top,0,art.bottom,0xeef8fcf5,0x00f8fcf5,Shader.TileMode.CLAMP);}
        paint.setShader(titleWash);c.drawRect(art,paint);paint.setShader(null);
        int level=v.adventure.current(),done=v.adventure.completed();
        v.fitLabel(c,"Mon aventure",card.centerX(),y+25*d,24,21,card.width()-24*d,v.ink);
        v.fitLabel(c,v.worldTitle(AdventureStore.world(level))+" · Niveau "+level,card.centerX(),y+53*d,16,13,card.width()-24*d,v.ink);
        drawPreview(c,v,level,done,y);
        v.fitLabel(c,done==40?"Bravo ! Quel voyage avec Lumi !":v.adventureStarted()?"Lumi t’attend. On continue ?":"Viens explorer avec Lumi !",card.centerX(),y+202*d,15,12,card.width()-24*d,v.ink);
        v.fitLabel(c,done+" / 40 niveaux",card.centerX(),y+226*d,13,11,card.width()-24*d,UiStyle.MUTED);
        v.uiScratch.set(card.left+20*d,y+241*d,card.right-20*d,y+247*d);v.round(c,v.uiScratch,3,0xffe0e9e2);
        if(done>0){v.uiScratch.right=v.uiScratch.left+v.uiScratch.width()*done/40f;v.round(c,v.uiScratch,3,UiStyle.SUCCESS);}
        v.adventureButton.set(card.left+12*d,card.bottom-68*d,card.right-12*d,card.bottom-12*d);v.round(c,v.adventureButton,17,UiStyle.REPLAY);
        v.fitLabel(c,done==40?"Revoir mon aventure":v.adventureStarted()?"Continuer l’aventure":"Commencer l’aventure",v.adventureButton.centerX(),v.adventureButton.centerY(),18,14,v.adventureButton.width()-24*d,UiStyle.REPLAY_TEXT);
        c.restore();y=card.bottom+12*d;
        card.set(16*d,y,w-16*d,y+126*d);v.round(c,card,22,UiStyle.SURFACE);
        clip.reset();clip.addRoundRect(card,22*d,22*d,Path.Direction.CW);c.save();c.clipPath(clip);
        art.set(card.left,card.top,card.right,card.top+58*d);painting(c,art,1);
        paint.setColor(0xaffffbf5);c.drawRect(art,paint);
        v.fitLabel(c,"Jeu libre",card.centerX(),y+18*d,17,15,card.width()-24*d,v.ink);
        v.fitLabel(c,"À ton rythme, dans ton monde",card.centerX(),y+42*d,12,10,card.width()-24*d,v.ink);
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

    /** The same two-level chapter and landscape as the map, without extra touch targets. */
    private void drawPreview(Canvas c,GameView v,int level,int done,float y){
        float d=v.d,left=card.left+52*d,right=card.right-52*d,nodeY=y+157*d;
        previewTrail.reset();previewTrail.moveTo(left,nodeY);
        previewTrail.cubicTo(left+30*d,y+108*d,right-30*d,y+108*d,right,nodeY);
        paint.setStyle(Paint.Style.STROKE);paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(8*d);paint.setColor(0x70675536);c.drawPath(previewTrail,paint);
        paint.setStrokeWidth(5*d);paint.setColor(0xffead7aa);c.drawPath(previewTrail,paint);
        paint.setStyle(Paint.Style.FILL);
        int first=((level-1)/2)*2+1;
        for(int i=0;i<2;i++){
            int step=first+i;float x=i==0?left:right;
            paint.setColor(step<=done?UiStyle.SUCCESS:step==level?0xff8163bd:0xfff8f1e7);
            c.drawCircle(x,nodeY,20*d,paint);
            v.label(c,String.valueOf(step),x,nodeY,16,step<=done||step==level?Color.WHITE:v.ink,Paint.Align.CENTER);
        }
        float x=level==first?left:right;
        hero.set(x-43*d,y+65*d,x+43*d,y+139*d);
        v.victoryMascot.drawMap(c,hero,GameView.now()-v.navigationAt,-1,v.animationsReduced(),false,-1);
    }
}
