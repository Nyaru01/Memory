package fr.memomimi.app;

import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import java.util.ArrayList;
import java.util.List;

/** Virtual controls for the Canvas interface; activation uses the same touch handlers. */
final class GameAccessibility extends AccessibilityNodeProvider {
    static final int CARD_BASE = 100;
    final GameView view;
    int focused = -1;
    int hovered = -1;
    int lastState = Integer.MIN_VALUE;
    final android.graphics.Paint focusPaint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);

    static final class Target {
        final int id;
        final RectF bounds;
        final String label;
        final boolean enabled;
        final boolean longPress;
        Target(int id, RectF bounds, String label, boolean enabled, boolean longPress) {
            this.id = id; this.bounds = bounds; this.label = label;
            this.enabled = enabled; this.longPress = longPress;
        }
    }

    GameAccessibility(GameView view) { this.view = view; }

    private void add(List<Target> result, RectF bounds, String label) {
        if (bounds != null && !bounds.isEmpty()) result.add(new Target(result.size() + 1, bounds, label, true, bounds == view.parentBtn));
    }

    List<Target> targets() {
        int state = view.screen * 100 + view.albumTab + (view.showQuit ? 1000 : 0)
                + (view.showResetConfirm ? 2000 : 0) + (view.enlargedItem >= 0 ? 4000 : 0);
        if (state != lastState) { focused = -1; hovered = -1; lastState = state; }
        List<Target> result = new ArrayList<>();
        if (view.screen < 0) return result;
        if (view.showQuit) {
            add(result, view.quitContinueBtn, "Continuer la partie");
            add(result, view.quitConfirmBtn, "Quitter la partie");
            return result;
        }
        if (view.showResetConfirm) {
            add(result, view.resetCancelBtn, "Annuler");
            add(result, view.resetConfirmBtn, "Effacer les records et statistiques");
            return result;
        }
        if (view.screen == 0 && view.showResumePrompt && view.resumeAvailable) {
            add(result, view.resumeBtn, "Reprendre la partie");
            add(result, view.newGameBtn, "Choisir une nouvelle partie");
            return result;
        }
        switch (view.screen) {
            case 0:
                add(result,view.adventureButton,"Mon aventure, niveau "+view.adventure.current());
                add(result, view.chooseTheme, "Choisir un monde : " + view.themeName(view.theme));
                add(result, view.easy, "6 paires, Facile"+(view.pairs==6 ? ", sélectionné" : "")); add(result, view.medium, "8 paires, Normal"+(view.pairs==8 ? ", sélectionné" : ""));
                add(result, view.hard, "10 paires, Difficile"+(view.pairs==10 ? ", sélectionné" : "")); add(result, view.expert, "12 paires, Expert"+(view.pairs==12 ? ", sélectionné" : ""));
                add(result, view.onePlayer, "Jouer seul"); add(result, view.twoPlayers, "Duel à deux");
                add(result, view.coopPlayer, "Jouer ensemble"); add(result, view.play, "Jouer");
                add(result, view.albumBtn, "Mon album"); add(result, view.soundBtn, "Son : " + (view.sound ? "activé" : "désactivé"));
                add(result, view.parentBtn, "Espace parents, appui long");
                break;
            case 1:
                if (view.pendingWinAt > 0) return result;
                if (view.tutorialActive && view.tutorialStep >= 2) {
                    add(result, view.tutorialDoneBtn, "J'ai compris"); return result;
                }
                add(result, view.homeBtn, "Quitter ou continuer la partie");
                add(result, view.hintBtn, "Afficher une aide");
                if (view.cards != null) for (int i = 0; i < view.cards.length; i++) {
                    boolean matched = view.matched[i];
                    boolean revealed = matched || i == view.first || i == view.second;
                    String label = "Carte " + (i + 1) + ", " + (matched ? "paire retrouvée" : revealed ? "retournée" : "cachée");
                    if (revealed) label += ", image " + (view.cards[i] + 1) + " du monde " + view.themeName(view.theme);
                    if (matched && view.players == 2) label += ", joueur " + view.owners[i];
                    boolean enabled = !matched && i != view.first && view.hideAt <= 0 && view.closeA < 0
                            && GameView.now() >= view.hintUntil
                            && (view.flipIndex < 0 || view.animationsReduced() || GameView.now() - view.flipStart >= CardMotion.OPEN_MS)
                            && (!view.tutorialActive || view.tutorialStep >= 2 || i == view.tutorialTarget());
                    result.add(new Target(CARD_BASE + i, view.cardBounds[i], label, enabled, false));
                }
                break;
            case 2:
                add(result, view.play,view.nextLevelButton.isEmpty() ? "Rejouer" : "Avancer avec Lumi vers le niveau "+view.adventure.current());
                add(result, view.storyBtn, "Écouter une histoire"); add(result, view.albumBtn, "Mon album");
                add(result, view.homeBtn,view.adventureLevel>0 ? "Voir la carte" : "Choisir un monde");
                add(result, view.victoryMascot.bounds, "Lumi, touche pour jouer avec moi");
                break;
            case 3:
                add(result, view.homeBtn, "Retour");
                add(result, view.shortDelay, "Délai court, 0,8 seconde"); add(result, view.normalDelay, "Délai normal, 1,3 seconde");
                add(result, view.longDelay, "Délai long, 2 secondes");
                add(result, view.calmBtn, "Mode calme : " + (view.calm ? "activé" : "désactivé"));
                add(result, view.hapticBtn, "Vibrations : " + (view.haptics ? "activées" : "désactivées"));
                add(result, view.resetBtn, "Effacer records et statistiques"); add(result, view.tutorialResetBtn, "Revoir le tutoriel");
                break;
            case 4:
                add(result, view.homeBtn, "Retour à l'accueil");
                for(int family=0;family<view.familyTiles.length;family++) add(result,view.familyTiles[family],WorldCatalog.FAMILIES[family]+(view.familyOpen[family] ? ", réduire" : ", ouvrir")+", "+WorldCatalog.WORLDS[family].length+" mondes");
                for (int i = 0; i < GameView.WORLD_COUNT; i++) add(result, view.themeTiles[i], view.themeName(i)+(view.theme==i && view.screen==4 ? ", sélectionné" : "")+", "+Integer.bitCount(view.albumMask(i))+" images découvertes");
                break;
            case 5:
                if (view.enlargedItem >= 0) { add(result, view.enlargedClose, "Fermer l'image"); return result; }
                add(result, view.homeBtn, "Retour"); add(result, view.albumPrev, "Monde précédent");
                add(result, view.albumNext, "Monde suivant"); add(result, view.worldsTab, "Mondes");
                add(result, view.imagesTab, "Images"); add(result, view.badgesTab, "Badges");
                if (view.albumTab == 2) for (int i = 0; i < GameView.WORLD_COUNT; i++) add(result, view.themeTiles[i], view.themeName(i)+(view.theme==i && view.screen==4 ? ", sélectionné" : "")+", "+Integer.bitCount(view.albumMask(i))+" images découvertes");
                if (view.albumTab == 0) for (int i = 0; i < 12; i++) {
                    boolean unlocked = (view.albumMask(view.albumPage) & (1 << i)) != 0;
                    result.add(new Target(200 + i, view.albumImageBounds[i], "Image " + (i + 1) + ", "
                            + view.themeName(view.albumPage) + (unlocked ? ", agrandir" : ", à découvrir"), unlocked, false));
                }
                break;
            case 7:
                add(result,view.adventureMap.back,"Accueil et jeu libre");
                 add(result,view.adventureMap.focusButton,"Revenir au niveau actuel");
                 add(result,view.adventureMap.lumi,"Lumi, touche pour un coucou");
                add(result,view.adventureMap.continueButton,"Continuer le niveau "+view.adventure.current());
                for(int level=1;level<=AdventureStore.LEVEL_COUNT;level++) {
                    RectF node=view.adventureMap.nodes[level-1];
                    if(node.bottom<=view.adventureMap.top || node.top>=view.adventureMap.bottom) continue;
                    boolean unlocked=view.adventure.unlocked(level);
                    result.add(new Target(400+level,node,"Niveau "+level+", "+AdventureRoute.landmark(level)+", "+view.worldTitle(AdventureStore.world(level))+", "+AdventureStore.pairs(level)+" paires, "+
                        (level<=view.adventure.completed() ? "terminé" : unlocked ? "disponible" : "verrouillé"),unlocked,false));
                }
                break;
            case 6:
                add(result, view.homeBtn, "Retour"); add(result, view.storyPlayBtn, view.storyPlaying ? "Mettre l'histoire en pause" : "Écouter l'histoire");
                break;
            default: break;
        }
        java.util.Iterator<Target> visible = result.iterator();
        while (visible.hasNext()) {
            Target target = visible.next();
            if (target.bounds.bottom <= view.visibleTop(target.bounds) || target.bounds.top >= view.H-view.systemBottom()) visible.remove();
        }
        return result;
    }

    Target find(int id) { for (Target target : targets()) if (target.id == id) return target; return null; }
    RectF targetAt(float x, float y) {
        for (Target target : targets()) if (target.enabled && target.bounds.contains(x, y)) return target.bounds;
        return null;
    }

    @Override public AccessibilityNodeInfo createAccessibilityNodeInfo(int id) {
        if (id == HOST_VIEW_ID) {
            AccessibilityNodeInfo host = AccessibilityNodeInfo.obtain(view);
            view.onInitializeAccessibilityNodeInfo(host);
            host.setContentDescription("MémoMondes");
            if (view.screen==0 || view.screen==3 || view.screen==4 || view.screen==7 || view.screen==5 && view.albumTab==2) {
                host.setScrollable(true);
                host.addAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD); host.addAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
            }
            for (Target target : targets()) host.addChild(view, target.id);
            return host;
        }
        Target target = find(id);
        if (target == null) return null;
        AccessibilityNodeInfo node = AccessibilityNodeInfo.obtain();
        node.setSource(view, id); node.setParent(view);
        node.setPackageName(view.getContext().getPackageName());
        node.setClassName("android.widget.Button"); node.setContentDescription(target.label);
        node.setEnabled(target.enabled); node.setFocusable(true); node.setVisibleToUser(view.isShown());
        node.setClickable(target.enabled && !target.longPress); node.setLongClickable(target.longPress);
        Rect bounds = new Rect(); target.bounds.roundOut(bounds);
        bounds.top=Math.max(bounds.top,(int)view.visibleTop(target.bounds)); bounds.bottom=Math.min(bounds.bottom,(int)(view.H-view.systemBottom()));
        node.setBoundsInParent(bounds);
        int[] location = new int[2]; view.getLocationOnScreen(location);
        bounds.offset(location[0], location[1]); node.setBoundsInScreen(bounds);
        node.setAccessibilityFocused(focused == id);
        node.addAction(focused == id ? AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS : AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS);
        if (target.enabled) node.addAction(target.longPress ? AccessibilityNodeInfo.ACTION_LONG_CLICK : AccessibilityNodeInfo.ACTION_CLICK);
        return node;
    }

    @Override public boolean performAction(int id, int action, Bundle arguments) {
        if(id==HOST_VIEW_ID && (action==AccessibilityNodeInfo.ACTION_SCROLL_FORWARD || action==AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD))
            return view.scrollPage(action==AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        Target target = find(id);
        if (target == null) return false;
        if (action == AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS) {
            if (focused == id) return false;
            if (focused != -1) send(focused, AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED);
            focused = id; send(id, AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED); view.invalidate(); return true;
        }
        if (action == AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS && focused == id) {
            focused = -1; send(id, AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED); view.invalidate(); return true;
        }
        if (!target.enabled) return false;
        if ((action == AccessibilityNodeInfo.ACTION_CLICK && !target.longPress)
                || (action == AccessibilityNodeInfo.ACTION_LONG_CLICK && target.longPress)) {
            long now = GameView.now();
            MotionEvent event = MotionEvent.obtain(now, now, target.longPress ? MotionEvent.ACTION_DOWN : MotionEvent.ACTION_UP,
                    target.bounds.centerX(), target.bounds.centerY(), 0);
            view.onTouchEvent(event); event.recycle();
            send(id, AccessibilityEvent.TYPE_VIEW_CLICKED); return true;
        }
        return false;
    }

    @Override public AccessibilityNodeInfo findFocus(int focus) {
        return focus == AccessibilityNodeInfo.FOCUS_ACCESSIBILITY && focused != -1 ? createAccessibilityNodeInfo(focused) : null;
    }

    boolean hover(MotionEvent event) {
        AccessibilityManager manager = (AccessibilityManager) view.getContext().getSystemService(android.content.Context.ACCESSIBILITY_SERVICE);
        if (manager == null || !manager.isTouchExplorationEnabled()) return false;
        int action = event.getActionMasked();
        if (action != MotionEvent.ACTION_HOVER_ENTER && action != MotionEvent.ACTION_HOVER_MOVE && action != MotionEvent.ACTION_HOVER_EXIT) return false;
        int next = -1;
        if (action != MotionEvent.ACTION_HOVER_EXIT) for (Target target : targets()) {
            if (target.bounds.contains(event.getX(), event.getY())) { next = target.id; break; }
        }
        if (next != hovered) {
            if (next != -1) send(next, AccessibilityEvent.TYPE_VIEW_HOVER_ENTER);
            if (hovered != -1) send(hovered, AccessibilityEvent.TYPE_VIEW_HOVER_EXIT);
            hovered = next;
        }
        return next != -1 || action == MotionEvent.ACTION_HOVER_EXIT;
    }

    void changed() {
        view.post(() -> {
            if (focused != -1 && find(focused) == null) focused = -1;
            view.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED);
        });
    }

    void drawFocus(android.graphics.Canvas canvas) {
        if (focused == -1) return;
        Target target = find(focused);
        if (target == null) return;
        focusPaint.setColor(view.ink);
        focusPaint.setStyle(android.graphics.Paint.Style.STROKE);
        focusPaint.setStrokeWidth(view.d * 2f);
        canvas.drawRoundRect(target.bounds, view.d * 16f, view.d * 16f, focusPaint);
    }

    private void send(int id, int type) {
        if (view.getParent() == null) return;
        AccessibilityEvent event = AccessibilityEvent.obtain(type);
        event.setSource(view, id); event.setPackageName(view.getContext().getPackageName());
        event.setClassName("android.widget.Button");
        Target target = find(id); if (target != null) event.setContentDescription(target.label);
        view.getParent().requestSendAccessibilityEvent(view, event);
    }
}
