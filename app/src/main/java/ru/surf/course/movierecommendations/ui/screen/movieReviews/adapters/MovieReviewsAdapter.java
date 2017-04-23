package ru.surf.course.movierecommendations.ui.screen.movieReviews.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.Review;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MyViewHolder> {

    private static MovieReviewsAdapter.OnItemClickListener mListener;
    private Context mContext;
    private List<Review> mReviews;

    public MovieReviewsAdapter(Context context, List<Review> reviews) {
        mContext = context;
        mReviews = reviews;
    }

    @Override
    public MovieReviewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_item_review, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewsAdapter.MyViewHolder holder, int position) {
        Review review = mReviews.get(position);
        holder.mAuthor.setText(review.getAuthor());
        holder.mContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public List<Review> getReviews() {
        return mReviews;
    }

    public void setReviews(List<Review> reviews) {
        mReviews = reviews;
    }

    public void setListener(MovieReviewsAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {

        void onClick(int position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View mPlaceholder;
        TextView mAuthor;
        ExpandableTextView mContent;

        public MyViewHolder(View itemView) {
            super(itemView);

            mPlaceholder = itemView.findViewById(R.id.recycler_item_review_placeholder);
            mAuthor = (TextView) itemView.findViewById(R.id.recycler_item_review_author);
            mContent = (ExpandableTextView) itemView.findViewById(R.id.recycler_item_review_content);

            mPlaceholder.setOnClickListener(view -> {
                if (mListener != null) {
                    mListener.onClick(getAdapterPosition());
                }

                mContent.toggle();
            });
        }
    }

}
