package ru.surf.course.movierecommendations.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ru.surf.course.movierecommendations.R;

/**
 * Created by sergey on 24.11.16.
 */

public class GridCustomAdapter extends BaseAdapter {

    private Context mContext;
    private String[] name;
    private int[] imageIds;

    public GridCustomAdapter(int[] imageIds, String[] name, Context mContext) {
        this.imageIds = imageIds;
        this.name = name;
        this.mContext = mContext;
    }


    @Override
    public int getCount() {
        return imageIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item;
        TextView textView;
        ImageView imageView;
        LayoutInflater inflater;
        if (convertView == null) {
            inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            item = inflater.inflate(R.layout.grid_element, null);

            imageView = (ImageView) item.findViewById(R.id.grid_image);
            textView = (TextView) item.findViewById(R.id.grid_text);

            Picasso.with(mContext)
                    .load(imageIds[position])
                    .noFade().resize(250, 375)
                    .centerCrop()
                    .into(imageView);
            textView.setText(name[position]);
        } else {
            item = convertView;
        }
        return item;
    }

}
