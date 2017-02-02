package ru.surf.course.movierecommendations.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.models.Actor;
import ru.surf.course.movierecommendations.models.CrewMember;
import ru.surf.course.movierecommendations.models.People;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;

/**
 * Created by andrew on 2/2/17.
 */

public class PeopleListAdapter extends RecyclerView.Adapter<PeopleListAdapter.ViewHolder> {

    private List<People> mPeopleList;
    private Context mContext;

    public PeopleListAdapter(List<People> people, Context context) {
        mPeopleList = people;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_list_item, parent, false);
        return new PeopleListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        People people = mPeopleList.get(position);
        if (people.getProfilePath() != null && !people.getProfilePath().equals("null"))
            loadImage(people.getProfilePath(), holder.image);
        holder.header.setText(people.getName());
        if (people instanceof Actor)
            holder.subHeader.setText(((Actor)people).getCharacter());
        else if (people instanceof CrewMember)
            holder.subHeader.setText(((CrewMember)people).getJob());
    }

    @Override
    public int getItemCount() {
        return mPeopleList.size();
    }

    public List<People> getPeople() {
        return mPeopleList;
    }

    public void setPeople(List<People> mPeople) {
        this.mPeopleList = mPeople;
    }

    private void loadImage(String path, ImageView targetView) {
        ImageLoader.putPoster(mContext, path, targetView, ImageLoader.sizes.w300);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView header;
        TextView subHeader;

        public ViewHolder(View itemView) {
            super(itemView);

            image = (CircleImageView)itemView.findViewById(R.id.circle_images_list_image);
            header = (TextView)itemView.findViewById(R.id.circle_images_list_header);
            subHeader = (TextView)itemView.findViewById(R.id.circle_images_list_sub_header);
        }
    }
}
