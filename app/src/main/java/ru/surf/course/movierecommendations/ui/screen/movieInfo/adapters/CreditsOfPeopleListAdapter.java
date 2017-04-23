package ru.surf.course.movierecommendations.ui.screen.movieInfo.adapters;

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
import ru.surf.course.movierecommendations.domain.people.Actor;
import ru.surf.course.movierecommendations.domain.people.Credit;
import ru.surf.course.movierecommendations.domain.people.CrewMember;
import ru.surf.course.movierecommendations.interactor.tmdbTasks.ImageLoader;


public class CreditsOfPeopleListAdapter extends
        RecyclerView.Adapter<CreditsOfPeopleListAdapter.ViewHolder> {

    private static OnItemClickListener listener;
    private List<Credit> mCreditList;
    private Context mContext;

    public CreditsOfPeopleListAdapter(List<Credit> credits, Context context) {
        mCreditList = credits;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_credits_list_item, parent, false);
        return new CreditsOfPeopleListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Credit credit = mCreditList.get(position);
        if (credit.getPerson().getProfilePath() != null && !credit.getPerson().getProfilePath()
                .equals("null")) {
            loadImage(credit.getPerson().getProfilePath(), holder.image);
        }
        holder.header.setText(credit.getPerson().getName());
        if (credit instanceof Actor) {
            holder.subHeader.setText(((Actor) credit).getCharacter());
        } else if (credit instanceof CrewMember) {
            holder.subHeader.setText(((CrewMember) credit).getJob());
        }
    }

    @Override
    public int getItemCount() {
        return mCreditList.size();
    }

    public List<Credit> getCredits() {
        return mCreditList;
    }

    public void setCredits(List<Credit> credits) {
        mCreditList = credits;
    }

    private void loadImage(String path, ImageView targetView) {
        ImageLoader.putPoster(mContext, path, targetView, ImageLoader.sizes.w300);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        CreditsOfPeopleListAdapter.listener = listener;
    }

    public interface OnItemClickListener {

        void onClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView header;
        TextView subHeader;

        public ViewHolder(View itemView) {
            super(itemView);

            image = (CircleImageView) itemView.findViewById(R.id.circle_images_list_image);
            header = (TextView) itemView.findViewById(R.id.circle_images_list_header);
            subHeader = (TextView) itemView.findViewById(R.id.circle_images_list_sub_header);

            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    CreditsOfPeopleListAdapter.listener.onClick(getAdapterPosition());
                }
            });
        }
    }
}
