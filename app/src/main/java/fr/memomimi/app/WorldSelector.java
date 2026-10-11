package fr.memomimi.app;

import android.graphics.*;
import android.view.MotionEvent;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.*;

/** Presentation only: IDs, preferences and free-play navigation remain in GameView. */
final class WorldSelector {
    static final long OPEN_MS=190;
    static final int MAX_BYTES=6*1024*1024;
    private static final int[] ACCENTS={0xff508968,0xff438aa0,0xff9474b6,0xffbd8867,0xff8b87ae};
    private final Bitmap[] images=new Bitmap[20];
    private final boolean[] requested=new boolean[20];
    private final float[] from=new float[5],amount=new float[5];
    private final String[][] titles=new String[20][2];
    private final String[] collections=new String[20];
    private final int[] masks=new int[20];
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private final Paint type=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF area=new RectF(),bar=new RectF(),downBounds=new RectF();
    private final Rect source=new Rect();
    private final Path clip=new Path();
    private ExecutorService worker;
    private long changedAt=-1;
    private float cachedWidth=-1,cachedFont=-1,anchorTop;
    private int anchor=-1,bytes,downTarget=-1,generation;
    private boolean downTracked,downUnsafe;
    float start,cardHeight,cardWidth,headerHeight;
    int columns;
    WorldSelector(){java.util.Arrays.fill(masks,-1);type.setTypeface(UiStyle.FONT);type.setTextAlign(Paint.Align.CENTER);}
    int columns(GameView v){return Math.max(2,Math.min(4,(int)((v.W/v.d-20)/(v.fontScale()>1.15f?224:196))));}
    synchronized Bitmap image(int world){return images[world];}
    synchronized int bytes(){return bytes;}
    synchronized void request(GameView v,int world){
        if(requested[world])return;requested[world]=true;
        if(worker==null)worker=Executors.newSingleThreadExecutor(r->{Thread t=new Thread(r,"MemoMondes-world-previews");t.setDaemon(true);return t;});
        final int token=generation;worker.execute(()->{
            int section=0;while(AdventureStore.world(section*2+1)!=world)section++;
            Bitmap decoded=null;
            try(InputStream input=v.getContext().getAssets().open(String.format(Locale.ROOT,"adventure/landscapes/world-%02d.jpg",section))){
                BitmapRegionDecoder decoder=BitmapRegionDecoder.newInstance(input,false);
                try{
                    int w=decoder.getWidth(),h=decoder.getHeight(),cropH=Math.min(h,Math.round(w/1.45f));
                    int y=Math.max(0,Math.min(h-cropH,Math.round(h*.38f-cropH/2f)));
                    BitmapFactory.Options options=new BitmapFactory.Options();options.inSampleSize=1;options.inScaled=false;
                    while(w/options.inSampleSize>320)options.inSampleSize*=2;
                    decoded=decoder.decodeRegion(new Rect(0,y,w,y+cropH),options);
                }finally{decoder.recycle();}
            }catch(Exception|OutOfMemoryError ignored){ }
            synchronized(this){if(decoded!=null){if(token==generation&&bytes+decoded.getAllocationByteCount()<=MAX_BYTES){images[world]=decoded;bytes+=decoded.getAllocationByteCount();}else decoded.recycle();}}
            v.postInvalidate();
        });
    }
    void awaitReady() throws Exception {ExecutorService executor; synchronized(this){executor=worker;}if(executor!=null)executor.submit(()->{}).get(20,TimeUnit.SECONDS);}
    synchronized void release(){generation++;java.util.Arrays.fill(images,null);java.util.Arrays.fill(requested,false);bytes=0;if(worker!=null){worker.shutdownNow();worker=null;}}
    private static int blend(int a,int b,float t){return Color.rgb(Math.round(Color.red(a)*(1-t)+Color.red(b)*t),Math.round(Color.green(a)*(1-t)+Color.green(b)*t),Math.round(Color.blue(a)*(1-t)+Color.blue(b)*t));}
    boolean moving(GameView v){return changedAt>=0&&!v.animationsReduced()&&GameView.now()-changedAt<OPEN_MS;}
    void toggle(GameView v,int family){
        update(v);System.arraycopy(amount,0,from,0,5);anchor=family;anchorTop=v.familyTiles[family].top;
        v.familyOpen[family]=!v.familyOpen[family];changedAt=GameView.now();v.pressedTarget=null;
        if(v.familyOpen[family])anchorTop=Math.max(start,Math.min(anchorTop,v.H-v.systemBottom()-headerHeight-cardHeight-12*v.d));
    }
    private void update(GameView v){
        float t=changedAt<0||v.animationsReduced()?1:Math.min(1,(GameView.now()-changedAt)/(float)OPEN_MS);t=1-(1-t)*(1-t)*(1-t);
        for(int i=0;i<5;i++)amount[i]=changedAt<0?(v.familyOpen[i]?1:0):from[i]+((v.familyOpen[i]?1:0)-from[i])*t;
    }
    private void text(Canvas c,String label,float x,float y,float size,int color){type.setTextSize(size);type.setColor(color);c.drawText(label,x,y-(type.ascent()+type.descent())/2,type);}
    private void layout(GameView v){
        columns=columns(v);float d=v.d,f=v.fontScale();start=v.systemTop()+92*d;headerHeight=(50+12*(f-1))*d;
        cardWidth=(v.W-32*d-(columns-1)*12*d)/columns;cardHeight=cardWidth/1.45f+(80+22*(f-1))*d;
        if(cachedWidth==cardWidth&&cachedFont==f*d)return;cachedWidth=cardWidth;cachedFont=f*d;
        type.setTextSize(15*d*f);
        for(int w=0;w<20;w++){
            String title=v.worldTitle(w);titles[w][0]=title;titles[w][1]="";
            if(type.measureText(title)>cardWidth-18*d){int best=-1;float error=Float.MAX_VALUE;
                for(int at=title.indexOf(' ');at>=0;at=title.indexOf(' ',at+1)){float a=type.measureText(title.substring(0,at)),b=type.measureText(title.substring(at+1));float e=Math.max(a,b);if(e<error){error=e;best=at;}}
                if(best>=0){titles[w][0]=title.substring(0,best);titles[w][1]=title.substring(best+1);}
            }
        }
    }
    void draw(Canvas c,GameView v){
        layout(v);update(v);float d=v.d,f=v.fontScale(),bottom=v.H-v.systemBottom(),content=0;
        for(int i=0;i<5;i++)content+=headerHeight+10*d+rows(i)*(cardHeight+12*d)*amount[i];
        v.worldsScrollMax=Math.max(0,content-(bottom-start));
        if(anchor>=0){float offset=0;for(int i=0;i<anchor;i++)offset+=headerHeight+10*d+rows(i)*(cardHeight+12*d)*amount[i];v.worldsScroll=start+offset-anchorTop;}
        v.worldsScroll=Math.max(0,Math.min(v.worldsScroll,v.worldsScrollMax));
        if(!moving(v))anchor=-1;
        v.homeBtn.set(16*d,v.systemTop()+4*d,64*d,v.systemTop()+52*d);v.round(c,v.homeBtn,18,UiStyle.SURFACE);v.label(c,"‹",v.homeBtn.centerX(),v.homeBtn.centerY(),32,v.ink,Paint.Align.CENTER);
        v.fitLabel(c,"Choisis ton monde",(64*d+v.W)/2,v.systemTop()+28*d,22,16,v.W-88*d,v.ink);
        v.fitLabel(c,"Choisis une famille, puis ton monde !",v.W/2,v.systemTop()+70*d,14,10,v.W-32*d,UiStyle.MUTED);
        for(RectF tile:v.themeTiles)tile.setEmpty();float y=start-v.worldsScroll;
        c.save();c.clipRect(0,start,v.W,bottom);
        for(int family=0;family<5;family++){
            RectF header=v.familyTiles[family];header.set(16*d,y,v.W-16*d,y+headerHeight);int accent=ACCENTS[family];
            v.round(c,header,16,blend(accent,Color.WHITE,.91f));
            area.set(header.left,header.top+10*d,header.left+4*d,header.bottom-10*d);v.round(c,area,2,accent);
            int cover=WorldCatalog.WORLDS[family][0];request(v,cover);area.set(header.left+10*d,header.top+8*d,header.left+44*d,header.bottom-8*d);picture(c,cover,area,7*d,accent);
            String familyTitle=WorldCatalog.FAMILIES[family];
            type.setTextSize(15*d*f);float size=Math.min(15*d*f,15*d*f*(header.width()-92*d)/Math.max(1,type.measureText(familyTitle)));
            text(c,familyTitle,header.centerX()+6*d,header.centerY(),size,UiStyle.TEXT);
            if(!v.familyOpen[family]&&WorldCatalog.familyOf(v.theme)==family){paint.setColor(v.themeColor(v.theme));c.drawCircle(header.left+40*d,header.top+8*d,7*d,paint);text(c,"✓",header.left+40*d,header.top+8*d,10*d,Color.WHITE);}
            float x=header.right-19*d,cy=header.centerY();c.save();c.rotate(180*amount[family],x,cy);paint.setColor(accent);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(2*d);paint.setStrokeCap(Paint.Cap.ROUND);c.drawLine(x-5*d,cy-2*d,x,cy+3*d,paint);c.drawLine(x,cy+3*d,x+5*d,cy-2*d,paint);paint.setStyle(Paint.Style.FILL);c.restore();
            y+=headerHeight+10*d;float expanded=rows(family)*(cardHeight+12*d)*amount[family];
            if(expanded>0){c.save();c.clipRect(16*d,y,v.W-16*d,y+expanded);int[] worlds=WorldCatalog.WORLDS[family];
                for(int index=0;index<worlds.length;index++){
                    int w=worlds[index];float x0=16*d+index%columns*(cardWidth+12*d),y0=y+index/columns*(cardHeight+12*d);
                    area.set(x0,y0,x0+cardWidth,y0+cardHeight);
                    if(v.familyOpen[family]&&!moving(v)&&y0+cardHeight<=y+expanded+.5f)v.themeTiles[w].set(area);
                    request(v,w);if(area.bottom>start&&area.top<bottom)card(c,v,w,area,amount[family]);
                }c.restore();}
            y+=expanded;
        }c.restore();if(moving(v)&&v.activityActive)v.postInvalidateOnAnimation();else if(changedAt>=0&&!moving(v)){changedAt=-1;v.accessible.changed();if(v.activityActive)v.postInvalidateOnAnimation();}
    }
    private int rows(int family){return (WorldCatalog.WORLDS[family].length+columns-1)/columns;}
    private void picture(Canvas c,int world,RectF bounds,float radius,int accent){
        Bitmap bitmap=image(world);paint.setColor(blend(accent,Color.WHITE,.85f));c.drawRoundRect(bounds,radius,radius,paint);
        if(bitmap==null)return;float ratio=bounds.width()/bounds.height();int w=bitmap.getWidth(),h=bitmap.getHeight();
        if(w/(float)h>ratio){int crop=Math.round(h*ratio);source.set((w-crop)/2,0,(w+crop)/2,h);}else{int crop=Math.round(w/ratio);source.set(0,(h-crop)/2,w,(h+crop)/2);}
        clip.reset();clip.addRoundRect(bounds,radius,radius,Path.Direction.CW);c.save();c.clipPath(clip);c.drawBitmap(bitmap,source,bounds,paint);c.restore();
    }
    private void card(Canvas c,GameView v,int world,RectF bounds,float opacity){
        float d=v.d,f=v.fontScale();int accent=v.themeColor(world);boolean selected=v.theme==world;
        c.save();if(opacity<1)c.translate(0,6*d*(1-opacity));
        bar.set(bounds.left,bounds.top+3*d,bounds.right,bounds.bottom+3*d);v.round(c,bar,18,0x14645574);
        v.round(c,bounds,18,selected||v.pressedTarget==v.themeTiles[world]?blend(accent,Color.WHITE,.9f):UiStyle.SURFACE);
        // Separate rectangle: picture must not mutate the card geometry.
        bar.set(bounds.left+5*d,bounds.top+5*d,bounds.right-5*d,bounds.top+cardWidth/1.45f);picture(c,world,bar,14*d,accent);
        float titleY=bar.bottom+14*d*f;text(c,titles[world][0],bounds.centerX(),titleY,15*d*f,UiStyle.TEXT);
        if(!titles[world][1].isEmpty())text(c,titles[world][1],bounds.centerX(),titleY+17*d*f,15*d*f,UiStyle.TEXT);
        int mask=v.albumMask(world);if(masks[world]!=mask){masks[world]=mask;collections[world]=Integer.bitCount(mask)+" / "+GameView.IMAGES_PER_WORLD+" images";}
        text(c,collections[world],bounds.centerX(),bounds.bottom-23*d*f,11*d*f,UiStyle.MUTED);
        bar.set(bounds.left+12*d,bounds.bottom-11*d,bounds.right-12*d,bounds.bottom-7*d);paint.setColor(0xffe4e2e9);c.drawRoundRect(bar,2*d,2*d,paint);bar.right=bar.left+bar.width()*Integer.bitCount(mask)/GameView.IMAGES_PER_WORLD;paint.setColor(accent);c.drawRoundRect(bar,2*d,2*d,paint);
        if(selected){paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(3*d);c.drawRoundRect(bounds,18*d,18*d,paint);paint.setStyle(Paint.Style.FILL);c.drawCircle(bounds.right-17*d,bounds.top+17*d,11*d,paint);text(c,"✓",bounds.right-17*d,bounds.top+17*d,14*d,Color.WHITE);}
        c.restore();
    }
    /** Reject releases whose target moved under the finger or was unavailable during expansion. */
    boolean rejectTouch(GameView v,MotionEvent event){
        int action=event.getActionMasked();if(action==MotionEvent.ACTION_CANCEL){downTracked=false;return false;}
        if(action==MotionEvent.ACTION_DOWN){downTracked=true;downUnsafe=moving(v);downTarget=target(v,event.getX(),event.getY());downBounds.set(bounds(v,downTarget));}
        if(action==MotionEvent.ACTION_UP&&downTracked){downTracked=false;int current=target(v,event.getX(),event.getY());return downUnsafe||moving(v)||downTarget!=current||!downBounds.equals(bounds(v,current));}
        return action==MotionEvent.ACTION_UP&&moving(v);
    }
    private int target(GameView v,float x,float y){if(v.homeBtn.contains(x,y))return 99;if(y<start||y>v.H-v.systemBottom())return -1;for(int i=0;i<5;i++)if(v.familyTiles[i].contains(x,y))return 20+i;for(int i=0;i<20;i++)if(v.themeTiles[i].contains(x,y))return i;return -1;}
    private RectF bounds(GameView v,int id){return id==99?v.homeBtn:id>=20?v.familyTiles[id-20]:id>=0?v.themeTiles[id]:downBounds;}
}
