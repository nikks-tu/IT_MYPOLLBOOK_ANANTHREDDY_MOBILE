package com.techuva.new_changes_corporate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.techuva.councellorapp.R;
import com.techuva.new_changes_corporate.dummy.DummyContent;
import com.techuva.new_changes_corporate.models.ComplaintDetailModel;

import java.util.List;

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.ViewHolder> {

    private final List<ComplaintDetailModel> mValues;

    public ComplaintsAdapter(List<ComplaintDetailModel> items) {
        mValues = items;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_complaint, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tv_title.setText(mValues.get(position).getTitle());
        holder.tv_content.setText(mValues.get(position).getComplaint());

        if(!mValues.get(position).getImageUrl().equals(""))
        {
            holder.iv_image.setVisibility(View.VISIBLE);
           // holder.iv_image.setImageDrawable();
        }
        else {
            holder.iv_image.setVisibility(View.GONE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tv_title;
        public final TextView tv_content;
        public ComplaintDetailModel mItem;
        ImageView iv_image;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            iv_image = (ImageView) view.findViewById(R.id.iv_image);
        }

     /*   @Override
        public String toString() {

            return super.toString() + " '" + mContentView.getText() + "'";
        }*/
    }
}
