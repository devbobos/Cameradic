package io.github.devbobos.cameradic.adapter;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.constant.SystemConstant;
import io.github.devbobos.cameradic.helper.DatabaseHelper;
import io.github.devbobos.cameradic.helper.NetworkChecker;
import io.github.devbobos.cameradic.model.History;
import io.github.devbobos.cameradic.model.HistoryDao;
import io.github.devbobos.cameradic.view.ItemDetailActivity;
import io.github.devbobos.cameradic.view.SearchActivity;

/**
 * Created by devbobos on 2018. 9. 1..
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>
{
    private SearchActivity activity;
    private List<History> list;

    public SearchAdapter(SearchActivity activity, List<History> list)
    {
        this.activity = activity;
        this.list = list;
    }

    public List<History> getList()
    {
        return list;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(activity).inflate(R.layout.search_item, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchViewHolder holder, final int position)
    {
        int maxLength = 0;
        if(list.get(position).getLanguageCode() == SystemConstant.LANGUAGE_TYPE_KOREAN)
        {
            maxLength = 10;
        }
        else
        {
            maxLength = 14;
        }
        if(list.get(position).getTitle().length() > maxLength)
        {
            holder.textViewTitle.setTextSize(activity.getResources().getDimension(R.dimen.textsize_subtitle1)/activity.getResources().getDisplayMetrics().density);
        }
        else
        {
            holder.textViewTitle.setTextSize(activity.getResources().getDimension(R.dimen.textsize_h5)/activity.getResources().getDisplayMetrics().density);
        }
        holder.textViewTitle.setText(list.get(position).getTitle());
        holder.textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(activity, ItemDetailActivity.class);
                intent.putExtra(SystemConstant.KEY_TITLE, list.get(position).getTitle());
                intent.putExtra(SystemConstant.KEY_DESCRIPTION, list.get(position).getDescription());
                intent.putExtra(SystemConstant.KEY_LINK, list.get(position).getLink());
                intent.putExtra(SystemConstant.KEY_LANGUAGE, list.get(position).getLanguageCode());
                intent.putExtra(SystemConstant.KEY_THUMBNAIL, list.get(position).getThumbnail());
                activity.startActivity(intent);
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
        holder.textViewDescription.setText(list.get(position).getDescription());
        holder.textViewDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(activity, ItemDetailActivity.class);
                intent.putExtra(SystemConstant.KEY_TITLE, list.get(position).getTitle());
                intent.putExtra(SystemConstant.KEY_DESCRIPTION, list.get(position).getDescription());
                intent.putExtra(SystemConstant.KEY_LINK, list.get(position).getLink());
                intent.putExtra(SystemConstant.KEY_LANGUAGE, list.get(position).getLanguageCode());
                intent.putExtra(SystemConstant.KEY_THUMBNAIL, list.get(position).getThumbnail());
                activity.startActivity(intent);
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
        HistoryDao dao = DatabaseHelper.getInstance(activity).getHistoryDao();
        List<History> findList = dao.queryBuilder()
                .where(HistoryDao.Properties.Link.eq(list.get(position).getLink()))
                .where(HistoryDao.Properties.Description.eq(list.get(position).getDescription()))
                .list();
        if(findList.size()>0)
        {
            History foundOne = findList.get(0);
            if(foundOne.getIsFavorite())
            {
                holder.imageViewFavorite.setImageResource(R.drawable.ic_bookmark_black_24dp);
            }
        }
        else
        {
            holder.imageViewFavorite.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
        }

        holder.imageViewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Date currentDate = new Date();
                HistoryDao dao = DatabaseHelper.getInstance(activity).getHistoryDao();
                List<History> findList = dao.queryBuilder()
                        .where(HistoryDao.Properties.Link.eq(list.get(position).getLink()))
                        .where(HistoryDao.Properties.Description.eq(list.get(position).getDescription()))
                        .list();
                AppCompatImageView view = (AppCompatImageView)v;
                if(findList.size()>0)
                {
                    History foundOne = findList.get(0);
                    if(foundOne.getIsFavorite())
                    {
                        foundOne.setIsFavorite(false);
                        view.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                    }
                    else
                    {
                        foundOne.setIsFavorite(true);
                        view.setImageResource(R.drawable.ic_bookmark_black_24dp);
                    }
                    foundOne.setUpdatedDate(currentDate);
                    dao.update(foundOne);
                }
                else
                {
                    dao.insert(new History(
                            currentDate,
                            currentDate,
                            true,
                            list.get(position).getTitle(),
                            list.get(position).getDescription(),
                            list.get(position).getLink(),
                            list.get(position).getThumbnail(),
                            list.get(position).getLanguageCode()));
                    view.setImageResource(R.drawable.ic_bookmark_black_24dp);
                }
            }
        });
        holder.imageViewFavorite.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        holder.imageViewFavorite.setColorFilter(holder.colorAvalonYellow);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        holder.imageViewFavorite.setColorFilter(holder.colorAvalonDeepYellow);
                        break;
                }
                return false;
            }
        });
        holder.imageViewLink.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        holder.imageViewLink.setColorFilter(holder.colorAvalonYellow);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        holder.imageViewLink.setColorFilter(holder.colorAvalonDeepYellow);
                        break;
                }
                return false;
            }
        });
        if(list.get(position).getLink()!=null)
        {
            holder.imageViewLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(NetworkChecker.getInstance().isNetworkOnline(activity.getApplicationContext()))
                    {
                        holder.imageViewLink.setColorFilter(holder.colorAvalonDeepYellow);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(list.get(position).getLink()));
                        activity.startActivity(i);
                    }
                    else
                    {
                        Snackbar mySnackbar = Snackbar.make(activity.findViewById(R.id.search_linearlayout_container), holder.stringNetworkUnavailable, Snackbar.LENGTH_SHORT);
                        mySnackbar.show();
                    }
                }
            });

        }
        else
        {
            holder.imageViewLink.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder
    {
        @BindColor(R.color.colorAvalonWhite) int colorAvalonWhite;
        @BindColor(R.color.colorAvalonWhiteActionDown) int colorAvalonWhiteActionDown;
        @BindColor(R.color.colorAvalonYellow) int colorAvalonYellow;
        @BindColor(R.color.colorAvalonDeepYellow) int colorAvalonDeepYellow;
         @BindView(R.id.item_textview_title) TextView textViewTitle;
         @BindView(R.id.item_textview_description) TextView textViewDescription;
         @BindView(R.id.item_imageview_favorite) AppCompatImageView imageViewFavorite;
         @BindView(R.id.item_imageview_link) AppCompatImageView imageViewLink;
        @BindString(R.string.common_network_unavailable) String stringNetworkUnavailable;
        public SearchViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
