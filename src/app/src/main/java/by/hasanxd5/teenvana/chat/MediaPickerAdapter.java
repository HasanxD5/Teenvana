package by.hasanxd5.teenvana.chat;

import android.graphics.Color;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class MediaPickerAdapter extends RecyclerView.Adapter<MediaPickerAdapter.ViewHolder> {
    private final List<String> list;
    private final List<String> selectedPaths = new ArrayList<>(); // Храним выбранные пути
    private final OnSelectionChangedListener listener;

    // Новый интерфейс для отслеживания выбора
    public interface OnSelectionChangedListener {
        void onSelectionChanged(List<String> selectedPaths);
    }

    public MediaPickerAdapter(List<String> list, OnSelectionChangedListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
        ImageView img = new ImageView(p.getContext());
        // Добавляем небольшие отступы для визуализации рамки выбора
        int padding = 4;
        img.setPadding(padding, padding, padding, padding);
        img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new ViewHolder(img);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        String uriString = list.get(pos);
        ImageView imageView = (ImageView) h.itemView;

        Glide.with(imageView.getContext())
                .load(Uri.parse(uriString))
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(imageView);

        // Визуальное выделение выбранного элемента
        if (selectedPaths.contains(uriString)) {
            imageView.setBackgroundColor(Color.parseColor("#00BFFF")); // Голубая рамка
            imageView.setAlpha(0.7f); // Слегка прозрачный
        } else {
            imageView.setBackgroundColor(Color.TRANSPARENT);
            imageView.setAlpha(1.0f);
        }

        imageView.setOnClickListener(v -> {
            if (selectedPaths.contains(uriString)) {
                selectedPaths.remove(uriString);
            } else {
                selectedPaths.add(uriString);
            }
            notifyItemChanged(pos); // Обновляем только этот элемент
            listener.onSelectionChanged(new ArrayList<>(selectedPaths));
        });
    }

    @Override public int getItemCount() { return list.size(); }
    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull ImageView iv) { super(iv); }
    }
}