package by.hasanxd5.teenvana.utils;

import android.view.View;
import android.widget.TextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UIUtils {

    /**
     * Helper method to apply window insets for Edge-to-Edge support.
     */
    public static void applySystemInsets(View view) {
        View titleView = view.findViewById(by.hasanxd5.teenvana.R.id.textViewTitle);
        View toolbar = view.findViewById(by.hasanxd5.teenvana.R.id.toolbar);
        View scrollContent = view.findViewById(by.hasanxd5.teenvana.R.id.nestedScrollView);
        View recyclerView = view.findViewById(by.hasanxd5.teenvana.R.id.recyclerViewChats);

        // Store initial paddings
        final int initialTitleTop = titleView != null ? titleView.getPaddingTop() : 0;
        final int initialToolbarTop = toolbar != null ? toolbar.getPaddingTop() : 0;
        final int initialScrollBottom = scrollContent != null ? scrollContent.getPaddingBottom() : 0;
        final int initialRvBottom = recyclerView != null ? recyclerView.getPaddingBottom() : 0;

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            if (titleView != null) {
                int extraTop = titleView instanceof TextView
                    ? (int) (16 * v.getResources().getDisplayMetrics().density) : 0;
                
                titleView.setPaddingRelative(
                        titleView.getPaddingStart(),
                        systemBars.top + initialTitleTop + extraTop,
                        titleView.getPaddingEnd(),
                        titleView.getPaddingBottom());
            }
            
            if (toolbar != null) {
                toolbar.setPaddingRelative(
                        toolbar.getPaddingStart(),
                        systemBars.top + initialToolbarTop,
                        toolbar.getPaddingEnd(),
                        toolbar.getPaddingBottom());
            }

            if (scrollContent != null) {
                scrollContent.setPaddingRelative(
                        scrollContent.getPaddingStart(),
                        scrollContent.getPaddingTop(),
                        scrollContent.getPaddingEnd(),
                        systemBars.bottom + initialScrollBottom);
            }

            if (recyclerView != null) {
                recyclerView.setPaddingRelative(
                        recyclerView.getPaddingStart(),
                        recyclerView.getPaddingTop(),
                        recyclerView.getPaddingEnd(),
                        systemBars.bottom + initialRvBottom);
            }

            return insets;
        });
    }
}
