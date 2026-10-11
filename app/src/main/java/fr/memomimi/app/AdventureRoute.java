package fr.memomimi.app;

/** Hand-placed landmarks in the existing paintings, measured in the 360x400dp reference crop. */
final class AdventureRoute {
    static final float SECTION=400;
    private static final float[][] POINTS={
        {.64f,145,.63f,282}, {.55f,138,.43f,292}, {.40f,126,.47f,284}, {.29f,135,.61f,285},
        {.39f,150,.62f,294}, {.37f,163,.22f,294}, {.61f,157,.45f,298}, {.55f,133,.66f,292},
        {.64f,160,.58f,294}, {.57f,146,.45f,290}, {.51f,143,.50f,292}, {.64f,152,.62f,292},
        {.45f,150,.50f,295}, {.56f,135,.54f,294}, {.52f,145,.62f,279}, {.67f,145,.52f,292},
        {.56f,148,.43f,292}, {.43f,143,.55f,282}, {.61f,148,.44f,282}, {.56f,147,.56f,292}
    };
    static final String[][] LANDMARKS={
        {"Près du grand arbre","Le sentier du ruisseau"},{"La grange","Le pont de la ferme"},
        {"La maison de l’escargot","La mare aux grenouilles"},{"Le pont suspendu","La cascade"},
        {"Les œufs de dinosaures","La vallée des dinosaures"},{"Les igloos","Les pingouins"},
        {"La perle","Le récif"},{"Le palais des sirènes","Les tortues"},
        {"Le trésor","Le sentier pirate"},{"La tour de l’horloge","Le pont du village"},
        {"La fusée","La vallée des cristaux"},{"La tour magique","La cascade enchantée"},
        {"Les maisons champignons","Le pont des fées"},{"Le château sucré","Le pont gaufre"},
        {"Le château de cubes","Le pont des jouets"},{"Les maisons citrouilles","Le potager"},
        {"Le viaduc","Les montgolfières"},{"La harpe","Le pont piano"},
        {"Le moulin","L’atelier de poterie"},{"Les maisons robots","Le pont des robots"}
    };
    // Gentle bends follow the illustrated open corridors rather than a repeated zigzag.
    private static final float[] BEND={.10f,.05f,.08f,-.08f,.07f,.04f,-.06f,.04f,-.05f,.07f,-.08f,.05f,.04f,-.07f,.02f,-.04f,.05f,.04f,-.05f,.06f};
    // Surveyed bends through the open path/ice shelves in each reference crop.
    private static final float[][] WALK={
        {.61f,190,.63f,238},{.58f,189,.49f,242},{.44f,180,.55f,237},{.38f,186,.59f,238},
        {.48f,195,.60f,242},{.30f,210,.25f,253},{.53f,210,.46f,252},{.59f,188,.64f,242},
        {.59f,202,.57f,250},{.53f,196,.46f,244},{.50f,195,.49f,245},{.57f,197,.45f,245},
        {.48f,200,.52f,252},{.57f,195,.52f,248},{.56f,192,.60f,237},{.61f,195,.54f,249},
        {.53f,196,.48f,246},{.49f,190,.56f,239},{.43f,176,.36f,245},{.56f,195,.55f,243}
    };
    private static final float[] EXIT={.62f,.49f,.55f,.63f,.58f,.24f,.49f,.62f,.55f,.49f,.49f,.43f,.53f,.50f,.60f,.53f,.46f,.53f,.47f,.55f};
    private static final float[] ENTRY={.62f,.56f,.46f,.29f,.42f,.37f,.60f,.54f,.63f,.57f,.51f,.63f,.45f,.56f,.52f,.66f,.56f,.43f,.60f,.56f};
    static float passageX(int boundary,float w,float d){return (project(boundary-1,EXIT[boundary-1],.5f,w,d,true)+project(boundary,ENTRY[boundary],.5f,w,d,true))*.5f;}
    static void fill(android.graphics.Path path,int level,float w,float d){
        int s=(level-1)/2;float[] points=new float[level%2==1?8:12];
        points[0]=x(level,w,d);points[1]=y(level,w,d);
        if(level%2==1){
            for(int i=0;i<2;i++){points[2+i*2]=project(s,WALK[s][i*2],.5f,w,d,true);points[3+i*2]=s*SECTION*d+project(s,.5f,WALK[s][i*2+1]/SECTION,w,d,false);}
        }else{
            points[2]=project(s,EXIT[s],.5f,w,d,true);points[3]=(s*SECTION+365)*d;
            points[4]=passageX(s+1,w,d);points[5]=(s+1)*SECTION*d;
            points[6]=project(s+1,ENTRY[s+1],.5f,w,d,true);points[7]=((s+1)*SECTION+65)*d;
            points[8]=points[6];points[9]=((s+1)*SECTION+100)*d;
        }
        int end=points.length-2;points[end]=x(level+1,w,d);points[end+1]=y(level+1,w,d);
        path.reset();path.moveTo(points[0],points[1]);
        // Monotonic cubic interpolation: no overshoot into a neighbouring river or building.
        for(int i=0;i<end;i+=2){float gap=points[i+3]-points[i+1];
            path.cubicTo(points[i],points[i+1]+gap*.34f,points[i+2],points[i+3]-gap*.34f,points[i+2],points[i+3]);}
    }
    static float x(int level,float w,float d){return project((level-1)/2,POINTS[(level-1)/2][level%2==1?0:2],.5f,w,d,true);}
    static float y(int level,float w,float d){int s=(level-1)/2;return s*SECTION*d+project(s,.5f,POINTS[s][level%2==1?1:3]/SECTION,w,d,false);}
    /** Project a reference-crop landmark through the actual aspect-preserving bitmap crop. */
    static float project(int section,float x,float y,float w,float d,boolean horizontal){
        float bw=section<8?972:1254,bh=section<8?1619:1254;
        float rw=Math.min(bw,bh*.9f),rh=Math.min(bh,bw/.9f);
        float sx=(bw-rw)/2+x*rw,sy=(bh-rh)/2+y*rh;
        float cw=Math.min(bw,bh*w/(SECTION*d)),ch=Math.min(bh,bw*SECTION*d/w);
        float value=horizontal?(sx-(bw-cw)/2)/cw*w:(sy-(bh-ch)/2)/ch*SECTION*d;
        return horizontal?Math.max(32*d,Math.min(w-32*d,value)):Math.max(65*d,Math.min(SECTION*d-55*d,value));
    }
    static float controlX(int level,float w,float d,boolean second){
        float a=x(level,w,d),b=x(level+1,w,d),bend=BEND[(level-1)/2]*w;
        return Math.max(w*.18f,Math.min(w*.82f,second?b-(b-a)*.28f+bend:a+(b-a)*.28f+bend));
    }
    static String landmark(int level){return LANDMARKS[(level-1)/2][(level-1)%2];}
}
