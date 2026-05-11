package by.hasanxd5.teenvana.chat;

import android.net.Uri;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class MediaPickerAdapter extends RecyclerView.Adapter<MediaPickerAdapter.ViewHolder> {
    private final List<String> list;
    private final OnItemClick listener;

    public interface OnItemClick { void onClick(String path); }

    public MediaPickerAdapter(List<String> list, OnItemClick listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
        ImageView img = new ImageView(p.getContext());
        img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new ViewHolder(img);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        String uriString = list.get(pos);
        Uri uri = Uri.parse(uriString);

        Glide.with(h.itemView.getContext())
                .load(uri)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .into((ImageView) h.itemView);

        h.itemView.setOnClickListener(v -> listener.onClick(uriString));
    }

    @Override public int getItemCount() { return list.size(); }
    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull ImageView iv) { super(iv); }
    }
}
