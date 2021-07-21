package io.github.devbobos.cameradic.adapter;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.constant.SystemConstant;
import io.github.devbobos.cameradic.model.License;
import io.github.devbobos.cameradic.view.LicenseActivity;
import io.github.devbobos.cameradic.view.LicenseDetailActivity;

/**
 * Created by devbobos on 2018. 9. 2..
 */
public class LicenseAdapter extends RecyclerView.Adapter<LicenseAdapter.LicenseViewHolder>
{
    private LicenseActivity activity;
    private List<License> list;

    public LicenseAdapter(LicenseActivity activity, List<License> list)
    {
        this.activity = activity;
        this.list = list;
    }

    public List<License> getList()
    {
        return list;
    }

    @NonNull
    @Override
    public LicenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(activity).inflate(R.layout.license_item, parent, false);
        return new LicenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LicenseViewHolder holder, final int position)
    {
        holder.textViewTitle.setText(list.get(position).getTitle());
        StringBuilder builder = new StringBuilder();
        builder.append(list.get(position).getAuthor());
        builder.append(" / ");
        builder.append(list.get(position).getDescription());
        holder.textViewDescription.setText(builder.toString());
        holder.linearLayoutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(activity, LicenseDetailActivity.class);
                intent.putExtra(SystemConstant.KEY_TITLE, list.get(position).getTitle());
                intent.putExtra(SystemConstant.KEY_DESCRIPTION, list.get(position).getDescription());
                activity.startActivity(intent);
            }
        });
        holder.textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(activity, LicenseDetailActivity.class);
                intent.putExtra(SystemConstant.KEY_TITLE, list.get(position).getTitle());
                intent.putExtra(SystemConstant.KEY_DESCRIPTION, list.get(position).getDescription());
                activity.startActivity(intent);
            }
        });
        holder.textViewDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(activity, LicenseDetailActivity.class);
                intent.putExtra(SystemConstant.KEY_TITLE, list.get(position).getTitle());
                intent.putExtra(SystemConstant.KEY_DESCRIPTION, list.get(position).getDescription());
                activity.startActivity(intent);
            }
        });
        holder.linearLayoutContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        holder.linearLayoutContainer.setBackgroundColor(holder.colorAvalonDeepBlue);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        holder.linearLayoutContainer.setBackgroundColor(holder.colorAvalonBlue);
                        break;
                }
                return false;
            }
        });
        holder.textViewTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        holder.textViewTitle.setTextColor(holder.colorAvalonYellow);
                        holder.textViewDescription.setTextColor(holder.colorAvalonWhiteActionDown);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        holder.textViewTitle.setTextColor(holder.colorAvalonDeepYellow);
                        holder.textViewDescription.setTextColor(holder.colorAvalonWhite);
                        break;
                }
                return false;
            }
        });
        holder.textViewDescription.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        holder.textViewTitle.setTextColor(holder.colorAvalonYellow);
                        holder.textViewDescription.setTextColor(holder.colorAvalonWhiteActionDown);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        holder.textViewTitle.setTextColor(holder.colorAvalonDeepYellow);
                        holder.textViewDescription.setTextColor(holder.colorAvalonWhite);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    class LicenseViewHolder extends RecyclerView.ViewHolder
    {
        @BindColor(R.color.colorAvalonWhite) int colorAvalonWhite;
        @BindColor(R.color.colorAvalonWhiteActionDown) int colorAvalonWhiteActionDown;
        @BindColor(R.color.colorAvalonYellow) int colorAvalonYellow;
        @BindColor(R.color.colorAvalonDeepYellow) int colorAvalonDeepYellow;
        @BindColor(R.color.colorAvalonBlue) int colorAvalonBlue;
        @BindColor(R.color.colorAvalonDeepBlue) int colorAvalonDeepBlue;
         @BindView(R.id.item_linearlayout_container) LinearLayout linearLayoutContainer;
         @BindView(R.id.item_textview_title) TextView textViewTitle;
         @BindView(R.id.item_textview_description) TextView textViewDescription;
        public LicenseViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
