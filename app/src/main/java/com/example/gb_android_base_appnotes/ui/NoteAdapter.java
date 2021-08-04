package com.example.gb_android_base_appnotes.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gb_android_base_appnotes.R;
import com.example.gb_android_base_appnotes.data.CardNote;
import com.example.gb_android_base_appnotes.data.CardsSource;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private final static String TAG = "NoteAdapter";
    private CardsSource dataSource;
    private OnItemClickListener itemClickListener;

    public NoteAdapter(CardsSource dataSource) {
        this.dataSource = dataSource;
    }

    @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int index) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        Log.d(TAG, "onCreateViewHolder");
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder viewHolder, int index) {
        viewHolder.setData(dataSource.getNoteData(index));
        Log.d(TAG, "onBindViewHolder");
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public void SetOnItemClickListener(OnItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view , int index);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView dateTime;
        private CheckBox like;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            dateTime = itemView.findViewById(R.id.date_time);
            like = itemView.findViewById(R.id.like);

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });
        }

        public void setData(CardNote cardNote) {
            title.setText(cardNote.getTitle());
            dateTime.setText(cardNote.getDate());
            like.setChecked(cardNote.getLike());
        }
    }
}
