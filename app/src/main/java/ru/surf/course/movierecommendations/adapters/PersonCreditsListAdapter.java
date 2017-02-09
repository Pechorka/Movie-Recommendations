package ru.surf.course.movierecommendations.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.Utilities;
import ru.surf.course.movierecommendations.fragments.PersonCreditsFragment;
import ru.surf.course.movierecommendations.models.Actor;
import ru.surf.course.movierecommendations.models.Credit;
import ru.surf.course.movierecommendations.models.CrewMember;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;

/**
 * Created by andrew on 2/9/17.
 */

public class PersonCreditsListAdapter extends RecyclerView.Adapter<PersonCreditsListAdapter.MyViewHolder> {

    private Context mContext;
    private List<Credit> mCredits;
    private static OnItemClickListener mListener;

    public PersonCreditsListAdapter(Context context, List<Credit> credits) {
        mContext = context;
        mCredits = credits;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.person_credits_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Credit credit = mCredits.get(position);
        if (Utilities.checkString(credit.getMedia().getPosterPath()))
            loadImage(credit.getMedia().getPosterPath(), holder.mImageView);

        holder.mHeader.setText(credit.getMedia().getTitle());

        if (credit instanceof Actor)
            holder.mSubHeader.setText(((Actor)credit).getCharacter());
        if (credit instanceof CrewMember)
            holder.mSubHeader.setText(((CrewMember)credit).getJob());
    }

    @Override
    public int getItemCount() {
        return mCredits.size();
    }

    public List<Credit> getCredits() {
        return mCredits;
    }

    public void setCredits(List<Credit> credits) {
        mCredits = credits;
    }

    private void loadImage(String path, ImageView targetView) {
        ImageLoader.putPoster(mContext, path, targetView, ImageLoader.sizes.w300);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CardView mCardView;
        ImageView mImageView;
        TextView mHeader;
        TextView mSubHeader;

        public MyViewHolder(View itemView) {
            super(itemView);

            mCardView = (CardView)itemView.findViewById(R.id.credits_movies_list_item_card);
            mImageView = (ImageView)itemView.findViewById(R.id.credits_movies_list_item_image);
            mHeader = (TextView)itemView.findViewById(R.id.credits_movies_list_item_header);
            mSubHeader = (TextView)itemView.findViewById(R.id.credits_movies_list_item_sub_header);

            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PersonCreditsListAdapter.mListener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public void setListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

}
