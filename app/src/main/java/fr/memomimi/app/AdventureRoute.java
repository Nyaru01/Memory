package fr.memomimi.app;

/** Hand-placed landmarks in the existing paintings, measured in the 360x400dp reference crop. */
final class AdventureRoute {
    static final float SECTION=400;
    private static final float[][] POINTS={
        {.53f,125,.43f,244}, {.55f,138,.43f,292}, {.40f,126,.61f,284}, {.46f,135,.61f,285},
        {.39f,150,.62f,294}, {.42f,163,.56f,294}, {.61f,157,.45f,298}, {.55f,133,.66f,292},
        {.64f,160,.40f,294}, {.57f,146,.45f,290}, {.51f,143,.50f,292}, {.64f,152,.40f,292},
        {.45f,150,.50f,295}, {.56f,135,.54f,294}, {.52f,145,.62f,279}, {.67f,145,.52f,292},
        {.56f,148,.43f,292}, {.43f,143,.55f,282}, {.61f,148,.44f,282}, {.56f,147,.56f,292}
    };
    static final String[][] LANDMARKS={
        {"Le grand arbre","Le pont du ruisseau"},{"La grange","Le pont de la ferme"},
        {"La maison de l’escargot","La mare aux grenouilles"},{"Le pont suspendu","La cascade"},
        {"Les œufs de dinosaures","La vallée des dinosaures"},{"Les igloos","Les pingouins"},
        {"La perle","Le récif"},{"Le palais des sirènes","Les tortues"},
        {"Le trésor","Le ponton pirate"},{"La tour de l’horloge","Le pont du village"},
        {"La fusée","La vallée des cristaux"},{"La tour magique","La cascade enchantée"},
        {"Les maisons champignons","Le pont des fées"},{"Le château sucré","Le pont gaufre"},
        {"Le château de cubes","Le pont des jouets"},{"Les maisons citrouilles","Le potager"},
        {"Le viaduc","Les montgolfières"},{"La harpe","Le pont piano"},
        {"Le moulin","L’atelier de poterie"},{"Les maisons robots","Le pont des robots"}
    };
    // Gentle bends follow the illustrated open corridors rather than a repeated zigzag.
    private static final float[] BEND={.10f,.05f,.08f,-.08f,.07f,.04f,-.06f,.04f,-.05f,.07f,-.08f,.05f,.04f,-.07f,.02f,-.04f,.05f,.04f,-.05f,.06f};
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
