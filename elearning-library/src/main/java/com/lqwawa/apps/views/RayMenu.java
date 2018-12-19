package com.lqwawa.apps.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.lqwawa.apps.R;

import java.util.ArrayList;
import java.util.List;

public class RayMenu implements View.OnClickListener {

    public enum MenuType {
        HORIZONTAL,
        VERTICAL
    }

    private static final int ANIMATION_TIME = 50;

    private Context context;
    private FrameLayout frameLayout;
    private View mainItemView;
    private RotateAnimation menuOpenAnimation;
    private RotateAnimation menuCloseAnimation;
    private List<View> horizontalViews;
    private List<View> verticalViews;
    private ObjectAnimator objectAnimators[];
    private int rootItemViewSize;
    private int itemViewSize;
    private int itemViewSizeDiff;
    private int itemViewMargin;
    private float itemViewScale = 1.0f;
    private boolean isItemViewScaleAllowed = true;
    private boolean isMenuOpened;
    private boolean isAutoCloseAfterClick;//shouyi add
    private MenuItemClickListener menuItemClickListener;
    private List<View> horizontalLockedViews;
    private List<View> verticalLockedViews;

    public boolean isMenuOpened() {
        return isMenuOpened;
    }

    public void setMenuOpened(boolean menuOpened) {
        isMenuOpened = menuOpened;
    }

    public RayMenu(Context context, FrameLayout layout) {
        this.context = context;
        horizontalViews = new ArrayList();
        verticalViews = new ArrayList();
        horizontalLockedViews = new ArrayList();
        verticalLockedViews = new ArrayList();
        frameLayout = layout;
        rootItemViewSize = (int) context.getResources().getDimension(R.dimen.ray_menu_root_item_size);
        itemViewSize = (int) context.getResources().getDimension(R.dimen.ray_menu_item_size);
        itemViewSizeDiff = Math.abs(rootItemViewSize - itemViewSize);
        itemViewMargin = (int) context.getResources().getDimension(R.dimen.ray_menu_item_margin);
        openRotation();
        closeRotation();
    }

    public void setMenuItemClickListener(MenuItemClickListener listener) {
        menuItemClickListener = listener;
    }

    public void setItemViewScale(float scale) {
        if (isItemViewScaleAllowed) {
            rootItemViewSize *= scale;
            itemViewSize *= scale;
            itemViewScale = scale;
        }
    }

    public float getItemViewScale() {
        return itemViewScale;
    }

    public View addRootItem(MenuItem menuItem) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.ray_menu_root, frameLayout, false);
        ImageButton button = (ImageButton) view.findViewById(R.id.menu_item_icon);
        button.setImageResource(menuItem.icon);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMenuOpened) {
                    closeMenuItemActions().start();
                    isMenuOpened = false;
                } else {
                    isMenuOpened = true;
                    openMenuItemActions().start();
                }
                if (menuItemClickListener != null) {
                    menuItemClickListener.onMenuItemClick((MenuItem) view.getTag());
                }
            }
        });
        button.setTag(menuItem);

        frameLayout.addView(view);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
        lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp.rightMargin = (int) context.getResources().getDimension(R.dimen.ray_menu_margin);
        lp.bottomMargin = (int) context.getResources().getDimension(R.dimen.ray_menu_margin);
        lp.width = rootItemViewSize;
        lp.height = rootItemViewSize;
        view.setLayoutParams(lp);
        mainItemView = button;

        isItemViewScaleAllowed = false;

        view.setTag(menuItem);
        return view;
    }

    public View addItem(MenuType menuType, MenuItem menuItem) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.ray_menu_item, frameLayout, false);
        ImageButton button = (ImageButton) view.findViewById(R.id.menu_item_icon);
        button.setImageResource(menuItem.icon);
        button.setOnClickListener(this);
        button.setTag(menuItem);

        if (menuType == MenuType.HORIZONTAL) {
            menuItem.index = horizontalViews.size();
            horizontalViews.add(view);
        } else if (menuType == MenuType.VERTICAL) {
            menuItem.index = verticalViews.size();
            verticalViews.add(view);
        }
        frameLayout.addView(view);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
        lp.width = itemViewSize;
        lp.height = itemViewSize;
        lp.rightMargin = (int) context.getResources().getDimension(R.dimen.ray_menu_margin);
        lp.bottomMargin = (int) context.getResources().getDimension(R.dimen.ray_menu_margin);
        if (menuType == MenuType.HORIZONTAL) {
            lp.rightMargin += (itemViewSizeDiff * itemViewScale);
            lp.bottomMargin += (itemViewSizeDiff * itemViewScale / 2);
        } else if (menuType == MenuType.VERTICAL) {
            lp.rightMargin += (itemViewSizeDiff * itemViewScale / 2);
            lp.bottomMargin += (itemViewSizeDiff * itemViewScale);
        }
        lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        view.setLayoutParams(lp);

        isItemViewScaleAllowed = false;

        view.setTag(menuItem);
        return view;
    }

    public void build() {
        setupMenuItemPosition(MenuType.HORIZONTAL);
        setupMenuItemPosition(MenuType.VERTICAL);
        hideMenuItemViews();
    }

    public void open() {
        if (!isMenuOpened) {
            isMenuOpened = true;
            openMenuItemActions().start();
        }
    }

    public void close() {
        if (isMenuOpened) {
            closeMenuItemActions().start();
            isMenuOpened = false;
        }
    }

    public void showOrHideRootItem(boolean bShow){//added by rmpan 
    	if(bShow){ 
    		mainItemView.clearAnimation();
    		mainItemView.setVisibility(View.VISIBLE); 
            for (View view : horizontalViews) {
                view.setVisibility(View.VISIBLE);
            }
            for (View view : verticalViews) {
                view.setVisibility(View.VISIBLE);
            }
    	}else{
    		 mainItemView.clearAnimation();
    		 mainItemView.setVisibility(View.GONE);
            for (View view : horizontalViews) {
                view.setVisibility(View.GONE);
            }
            for (View view : verticalViews) {
                view.setVisibility(View.GONE);
            }
    	}
    }

    public void showAllMenu() {
        mainItemView.setVisibility(View.VISIBLE);

        for (View view : horizontalViews) {
            view.setVisibility(View.VISIBLE);
        }
        for (View view : verticalViews) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public void show() {
        mainItemView.setVisibility(View.VISIBLE);

        for (View view : horizontalViews) {
            view.setVisibility(View.GONE);
        }
        for (View view : verticalViews) {
            view.setVisibility(View.GONE);
        }
    }

    public void hide() {
        mainItemView.clearAnimation();
        mainItemView.setVisibility(View.GONE);

        for (View view : horizontalViews) {
            view.setVisibility(View.GONE);
        }
        for (View view : verticalViews) {
            view.setVisibility(View.GONE);
        }
    }

    private void setupMenuItemPosition(MenuType menuType) {
        List<View> views = null;
        List<View> lockedViews = null;
        if (menuType == MenuType.HORIZONTAL) {
            views = horizontalViews;
            lockedViews = horizontalLockedViews;
        } else if (menuType == MenuType.VERTICAL) {
            views = verticalViews;
            lockedViews = verticalLockedViews;
        }
        if (views == null || views.size() <= 0) {
            return;
        }

        MenuItem menuItem = null;
        for (View view : views) {
            menuItem = (MenuItem) view.getTag();
            if (menuItem.locked) {
                menuItem.lockedIndex = lockedViews.size();
                lockedViews.add(view);
            }
        }

        if (lockedViews.size() <= 0) {
            return;
        }

        FrameLayout.LayoutParams lp = null;
        int margin = 0;
        for (View view : lockedViews) {
            menuItem = (MenuItem) view.getTag();
            lp = (FrameLayout.LayoutParams) view.getLayoutParams();
            margin = (itemViewSizeDiff / 2 + (itemViewSize + itemViewMargin)
                        * (lockedViews.size() - menuItem.lockedIndex));
            if (menuType == MenuType.HORIZONTAL) {
                lp.rightMargin += margin;
            } else if (menuType == MenuType.VERTICAL) {
                lp.bottomMargin += margin;
            }
            view.setLayoutParams(lp);
        }
    }

    public void setAutoCloseAfterClick(boolean value) {
    	isAutoCloseAfterClick = value;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        if (isAutoCloseAfterClick && isMenuOpened) {
            closeMenuItemActions().start();
            isMenuOpened = false;
        }
        if (menuItemClickListener != null) {
            menuItemClickListener.onMenuItemClick((MenuItem) v.getTag());
        }
    }

    /**
     * Set close animation for promoted actions
     */
    private AnimatorSet closeMenuItemActions() {
        if (objectAnimators == null){
            initMenuItemAnimators();
        }

        AnimatorSet animation = new AnimatorSet();
        int i = 0;
        int count = horizontalViews.size();
        int offset = 0;
        for (; i < count; i++) {
            objectAnimators[i] = setCloseAnimation(MenuType.HORIZONTAL,
                    horizontalViews.get(i), i - offset);
        }
        offset = horizontalViews.size();
        count += verticalViews.size();
        for (; i < count; i++) {
            objectAnimators[i] = setCloseAnimation(MenuType.VERTICAL,
                    verticalViews.get(i - offset), i - offset);
        }

        if (objectAnimators.length == 0) {
            objectAnimators = null;
        }

        animation.playTogether(objectAnimators);
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mainItemView.startAnimation(menuCloseAnimation);
                mainItemView.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mainItemView.setClickable(true);
                hideMenuItemViews();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mainItemView.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        return animation;
    }

    private AnimatorSet openMenuItemActions() {
        if (objectAnimators == null){
            initMenuItemAnimators();
        }

        AnimatorSet animation = new AnimatorSet();
        int i = 0;
        int count = horizontalViews.size();
        int offset = 0;
        for (; i < count; i++) {
            objectAnimators[i] = setOpenAnimation(MenuType.HORIZONTAL,
                    horizontalViews.get(i), i - offset);
        }
        offset = horizontalViews.size();
        count += verticalViews.size();
        for (; i < count; i++) {
            objectAnimators[i] = setOpenAnimation(MenuType.VERTICAL,
                    verticalViews.get(i - offset), i - offset);
        }

        if (objectAnimators.length == 0) {
            objectAnimators = null;
        }

        animation.playTogether(objectAnimators);
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) { 
                mainItemView.startAnimation(menuOpenAnimation);
                mainItemView.setClickable(false);
                showMenuItemViews();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mainItemView.setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mainItemView.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        return animation;
    }

    private void initMenuItemAnimators() {
        objectAnimators = new ObjectAnimator[horizontalViews.size() + verticalViews.size()];
    }

    /**
     * Set close animation for single view
     *
     * @param view
     * @param position
     * @return ObjectAnimator
     */
    private ObjectAnimator setCloseAnimation(MenuType menuType, View view, int position) {
        List<View> views = null;
        List<View> lockedViews = null;
        if (menuType == MenuType.HORIZONTAL) {
            views = horizontalViews;
            lockedViews = horizontalLockedViews;
        } else if (menuType == MenuType.VERTICAL) {
            views = verticalViews;
            lockedViews = verticalLockedViews;
        }

        MenuItem menuItem = (MenuItem) view.getTag();
        float offset = (itemViewSize + itemViewMargin) * (views.size() - position);
        if (menuItem != null && menuItem.locked) {
            offset -= (lockedViews.size() - menuItem.lockedIndex);
        }
        ObjectAnimator objectAnimator = null;
        if (menuType == MenuType.HORIZONTAL) {
            objectAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, -offset, 0f);
        } else if (menuType == MenuType.VERTICAL) {
            objectAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, -offset, 0f);
        }
        objectAnimator.setRepeatCount(0);
        objectAnimator.setDuration(ANIMATION_TIME * (views.size() - position));

        return objectAnimator;
    }

    /**
     * Set open animation for single view
     *
     * @param view
     * @param position
     * @return ObjectAnimator
     */
    private ObjectAnimator setOpenAnimation(MenuType menuType, View view, int position) {
        List<View> views = null;
        List<View> lockedViews = null;
        if (menuType == MenuType.HORIZONTAL) {
            views = horizontalViews;
            lockedViews = horizontalLockedViews;
        } else if (menuType == MenuType.VERTICAL) {
            views = verticalViews;
            lockedViews = verticalLockedViews;
        }

        MenuItem menuItem = (MenuItem) view.getTag();
        float offset = (itemViewSize + itemViewMargin) * (views.size() - position);
        if (menuItem != null && menuItem.locked) {
            offset -= (lockedViews.size() - menuItem.lockedIndex);
        }
        ObjectAnimator objectAnimator = null;
        if(menuType == MenuType.HORIZONTAL) {
            objectAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f, -offset);
        } else if (menuType == MenuType.VERTICAL) {
            objectAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, -offset);
        }
        objectAnimator.setRepeatCount(0);
        objectAnimator.setDuration(ANIMATION_TIME * (verticalViews.size() - position));

        return objectAnimator;
    }

    private void hideMenuItemViews() {
        MenuItem menuItem = null;
        for (View view : horizontalViews) {
            menuItem = (MenuItem) view.getTag();
            if (menuItem == null || !menuItem.locked) {
                view.setVisibility(View.GONE);
            }
        }
        for (View view : verticalViews) {
            menuItem = (MenuItem) view.getTag();
            if (menuItem == null || !menuItem.locked) {
                view.setVisibility(View.GONE);
            }
        }
    }

    private void showMenuItemViews() {
        for (View view : horizontalViews) {
            view.setVisibility(View.VISIBLE);
        }
        for (View view : verticalViews) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void openRotation() {
        menuOpenAnimation = new RotateAnimation(0, 45,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        menuOpenAnimation.setFillAfter(true);
        menuOpenAnimation.setFillEnabled(true);
        menuOpenAnimation.setDuration(ANIMATION_TIME);
    }

    private void closeRotation() {
        menuCloseAnimation = new RotateAnimation(45, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        menuCloseAnimation.setFillAfter(true);
        menuCloseAnimation.setFillEnabled(true);
        menuCloseAnimation.setDuration(ANIMATION_TIME);
    }

    public static class MenuItem {
        public int id;
        public int icon;
        int index;
        public boolean locked;
        int lockedIndex;
    }

    public interface MenuItemClickListener {
        public void onMenuItemClick(MenuItem menuItem);
    }

}
