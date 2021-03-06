package ru.surf.course.movierecommendations.ui.common.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;


public abstract class BindableViewHolder<T> extends RecyclerView.ViewHolder {

    public BindableViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(T data);
}
