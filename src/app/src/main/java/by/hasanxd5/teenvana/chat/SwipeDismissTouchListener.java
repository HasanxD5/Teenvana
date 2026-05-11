package by.hasanxd5.teenvana.chat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class SwipeDismissTouchListener implements View.OnTouchListener {
    private final int slop;
    private float downY;
    private final Activity activity;
    private final View view;

    public SwipeDismissTouchListener(Activity activity, View view) {
        this.activity = activity;
        this.view = view;
        this.slop = ViewConfiguration.get(activity).getScaledTouchSlop();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getRawY();
                return false;

            case MotionEvent.ACTION_MOVE:
                float deltaY = event.getRawY() - downY;
                if (Math.abs(deltaY) > slop) {
                    view.setTranslationY(deltaY);
                    float alpha = 1f - (Math.abs(deltaY) / v.getHeight());
                    view.setAlpha(Math.max(0.5f, alpha));
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float upDeltaY = event.getRawY() - downY;
                if (Math.abs(upDeltaY) > (float) v.getHeight() / 4) {
                    view.animate()
                            .translationY(upDeltaY > 0 ? v.getHeight() : -v.getHeight())
                            .alpha(0)
                            .setDuration(200)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    activity.finish();
                                    activity.overridePendingTransition(0, 0);
                                }
                            });
                } else {
                    view.animate().translationY(0).alpha(1f).setDuration(200);
                }
                break;
        }
        return false;
    }
}