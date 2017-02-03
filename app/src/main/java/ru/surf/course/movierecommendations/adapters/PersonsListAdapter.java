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
import ru.surf.course.movierecommendations.models.Person;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;

/**
 * Created by andrew on 2/2/17.
 */

public class PersonsListAdapter extends RecyclerView.Adapter<PersonsListAdapter.ViewHolder> {

    private List<Person> mPersonList;
    private Context mContext;

    public PersonsListAdapter(List<Person> person, Context context) {
        mPersonList = person;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_list_item, parent, false);
        return new PersonsListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Person person = mPersonList.get(position);
        if (person.getProfilePath() != null && !person.getProfilePath().equals("null"))
            loadImage(person.getProfilePath(), holder.image);
        holder.header.setText(person.getName());
        if (person instanceof Actor)
            holder.subHeader.setText(((Actor) person).getCharacter());
        else if (person instanceof CrewMember)
            holder.subHeader.setText(((CrewMember) person).getJob());
    }

    @Override
    public int getItemCount() {
        return mPersonList.size();
    }

    public List<Person> getPersons() {
        return mPersonList;
    }

    public void setPersons(List<Person> mPerson) {
        this.mPersonList = mPerson;
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
