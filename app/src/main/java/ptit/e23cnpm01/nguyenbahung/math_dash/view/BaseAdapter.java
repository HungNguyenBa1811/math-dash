package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// Lớp cha của adapter danh sách: giữ các phần tử, lớp con tạo ViewHolder và gán dữ liệu
public abstract class BaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected List<T> items = new ArrayList<>();

    @NonNull
    @Override
    public abstract VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public int getItemCount() {
        return items.size();
    }
}
