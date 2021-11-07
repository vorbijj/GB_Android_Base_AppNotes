package com.example.gb_android_base_appnotes.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gb_android_base_appnotes.R;
import com.example.gb_android_base_appnotes.data.CardNote;
import com.example.gb_android_base_appnotes.data.CardsSource;

import java.text.SimpleDateFormat;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private final static String TAG = "NoteAdapter";
    private CardsSource dataSource;
    private final Fragment fragment;
    private OnItemClickListener itemClickListener;
    private int menuPosition;

    public NoteAdapter(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setDataSource(CardsSource dataSource) {
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int index) {
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

    public int getMenuPosition() {
        return menuPosition;
    }

    public void SetOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int index);
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

            registerContextMenu(itemView);

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });
        }

        private void registerContextMenu(@NonNull View itemView) {
            if (fragment != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        menuPosition = getLayoutPosition();
                        return false;
                    }
                });
                fragment.registerForContextMenu(itemView);
            }
        }

        public void setData(CardNote cardNote) {
            title.setText(cardNote.getTitle());
            dateTime.setText(new SimpleDateFormat("dd-MM-yy").format(cardNote.getDate()));
            like.setChecked(cardNote.isLike());
        }
    }
}
