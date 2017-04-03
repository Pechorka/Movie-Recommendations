package ru.surf.course.movierecommendations.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Calendar;
import java.util.List;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.Utilities;
import ru.surf.course.movierecommendations.models.Season;
import ru.surf.course.movierecommendations.tmdbTasks.ImageLoader;

/**
 * Created by andrew on 2/20/17.
 */

public class TvShowSeasonsListAdapter extends
    RecyclerView.Adapter<TvShowSeasonsListAdapter.MyViewHolder> {

  private static TvShowSeasonsListAdapter.OnItemClickListener mListener;
  private Context mContext;
  private List<Season> mSeasons;

  public TvShowSeasonsListAdapter(Context context, List<Season> seasons) {
    mContext = context;
    mSeasons = seasons;
  }

  @Override
  public TvShowSeasonsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext)
        .inflate(R.layout.recycler_item_seasons, parent, false);
    return new TvShowSeasonsListAdapter.MyViewHolder(view);
  }

  @Override
  public void onBindViewHolder(TvShowSeasonsListAdapter.MyViewHolder holder, int position) {
    Season season = mSeasons.get(position);
    if (Utilities.checkString(season.getPosterPath())) {
      loadImage(season.getPosterPath(), holder.mImageView);
    }

    holder.mSeasonNumber.setText(String.valueOf(season.getSeasonNumber()));

    if (season.getAirDate() != null) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(season.getAirDate());
      holder.mAirYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
    } else {
      holder.mSeparator.setVisibility(View.GONE);
    }

    holder.mNumberOfEpisodes.setText(mContext.getResources()
        .getQuantityString(R.plurals.episodes, season.getEpisodeCount(), season.getEpisodeCount()));
  }

  @Override
  public int getItemCount() {
    return mSeasons.size();
  }

  public List<Season> getSeasons() {
    return mSeasons;
  }

  public void setSeasons(List<Season> seasons) {
    mSeasons = seasons;
  }

  private void loadImage(String path, ImageView targetView) {
    ImageLoader.putPoster(mContext, path, targetView, ImageLoader.sizes.w300);
  }

  public void setListener(TvShowSeasonsListAdapter.OnItemClickListener listener) {
    mListener = listener;
  }

  public interface OnItemClickListener {

    void onClick(int position);
  }

  public static class MyViewHolder extends RecyclerView.ViewHolder {

    CardView mCardView;
    ImageView mImageView;
    TextView mSeasonNumber;
    TextView mAirYear;
    TextView mNumberOfEpisodes;
    TextView mSeparator;

    public MyViewHolder(View itemView) {
      super(itemView);

      mCardView = (CardView) itemView.findViewById(R.id.recycler_item_seasons_card);
      mImageView = (ImageView) itemView.findViewById(R.id.recycler_item_seasons_image);
      mSeasonNumber = (TextView) itemView.findViewById(R.id.recycler_item_seasons_number_of_season);
      mAirYear = (TextView) itemView.findViewById(R.id.recycler_item_seasons_air_year);
      mNumberOfEpisodes = (TextView) itemView
          .findViewById(R.id.recycler_item_seasons_number_of_episodes);
      mSeparator = (TextView) itemView.findViewById(R.id.recycler_item_seasons_separator);

      mCardView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          TvShowSeasonsListAdapter.mListener.onClick(getAdapterPosition());
        }
      });
    }
  }

}
