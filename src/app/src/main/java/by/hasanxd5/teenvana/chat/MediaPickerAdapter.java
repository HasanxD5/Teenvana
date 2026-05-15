package by.hasanxd5.teenvana.chat;

import android.graphics.Color;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class MediaPickerAdapter extends RecyclerView.Adapter<MediaPickerAdapter.ViewHolder> {
    private final List<String> list;
    private final List<String> selectedPaths = new ArrayList<>();
    private final OnSelectionChangedListener listener;

    public interface OnSelectionChangedListener {
        void onSelectionChanged(List<String> selectedPaths);
    }

    public MediaPickerAdapter(List<String> list, OnSelectionChangedListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
        FrameLayout frameLayout = new FrameLayout(p.getContext());
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
        
        ImageView img = new ImageView(p.getContext());
        img.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setId(android.R.id.icon);
        
        ImageView overlay = new ImageView(p.getContext());
        overlay.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        overlay.setBackgroundColor(Color.parseColor("#80000000")); // Dark overlay for selection
        overlay.setImageResource(android.R.drawable.checkbox_on_background);
        overlay.setScaleType(ImageView.ScaleType.CENTER);
        overlay.setVisibility(android.view.View.GONE);
        overlay.setId(android.R.id.checkbox);

        frameLayout.addView(img);
        frameLayout.addView(overlay);
        
        return new ViewHolder(frameLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        String uriString = list.get(pos);
        Uri uri = Uri.parse(uriString);

        ImageView img = h.itemView.findViewById(android.R.id.icon);
        ImageView overlay = h.itemView.findViewById(android.R.id.checkbox);

        Glide.with(h.itemView.getContext())
                .load(uri)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .into(img);

        boolean isSelected = selectedPaths.contains(uriString);
        overlay.setVisibility(isSelected ? android.view.View.VISIBLE : android.view.View.GONE);

        h.itemView.setOnClickListener(v -> {
            if (selectedPaths.contains(uriString)) {
                selectedPaths.remove(uriString);
            } else {
                selectedPaths.add(uriString);
            }
            notifyItemChanged(pos);
            listener.onSelectionChanged(selectedPaths);
        });
    }

    @Override public int getItemCount() { return list.size(); }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull android.view.View itemView) { super(itemView); }
    }
}
