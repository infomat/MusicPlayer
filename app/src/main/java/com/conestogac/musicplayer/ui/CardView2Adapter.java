package com.conestogac.musicplayer.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.conestogac.musicplayer.R;
import com.conestogac.musicplayer.model.Genre;
import com.conestogac.musicplayer.model.Song;
import com.conestogac.musicplayer.util.GlideUtil;
import com.conestogac.musicplayer.util.MusicHelper;

import java.io.File;
import java.util.ArrayList;

/**
 * CardView2Adapter
 * Simplified Version of AlbumView Adapter which is used in Genre View
 * Just display Text and Image
 * AUthor: SungJoe Kim
 */
public class CardView2Adapter extends RecyclerView.Adapter<CardView2Adapter.ViewHolder> {
    private final static String TAG = "CardView2Adapter";
    private ArrayList<Integer> _ids = new ArrayList<Integer>();
    private ArrayList<String> firstTitles = new ArrayList<String>();
    private ArrayList<String> albumArts = new ArrayList<String>();
    private ArrayList<Song> songArrayList = new ArrayList<Song>();
    private int viewPagerPos = 0;
    private File file;
    private Context ctxt;

    /**
     * Define ViewHolder for better performance
     * https://developer.android.com/training/improving-layouts/smooth-scrolling.html
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView overflow;
        public ViewHolder(CardView v) {
            super(v);
            cardView=v;
            overflow = (ImageView) v.findViewById(R.id.overflow);
        }
    }

    /**
     * Adapter for Genre
     * @param genreList
     */
    public CardView2Adapter(ArrayList<Genre> genreList){
        for (Genre genre : genreList) {
            this._ids.add(genre.getID());
            this.firstTitles.add(genre.getGenre());
            //todo what image for genre
            this.albumArts.add(null);
        }
        viewPagerPos = SlideViewPagerAdapter.GENRE_VIEW;
    }

    /**
     * onCreateViewHolder inflate layout and return ViewHolder Object
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public CardView2Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_album2, parent, false);
        ctxt = parent.getContext();
        return new ViewHolder(cv);
    }

    /**
     * Bind view holder with viewHolder created on onCreateViewHolder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        final ImageView imageView = (ImageView)cardView.findViewById(R.id.albumArt);

        //if albumart exist, image is loaded by using Glide,
        // otherwise placeholder defined within Glide will be displayed
        if (albumArts.get(position) != null) {
            file = new java.io.File(albumArts.get(position));
            GlideUtil.loadImageWithFilePath(file, imageView);
        }

        imageView.setContentDescription(firstTitles.get(position));
        TextView tvFirstTitle = (TextView)cardView.findViewById(R.id.firstTitle);
        tvFirstTitle.setText(firstTitles.get(position));

        //define onClick listener for 3-dot optio menu
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, _ids.get(position));
            }
        });

        //Set OnClick listener on cardview which will go to Player
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctxt = v.getContext();
                Log.d(TAG, "Click on Cardview "+position);

                // set information
                // Transfer to musicplayer with intent

                Intent gotoMusicPlayer = new Intent(ctxt, PlayerActivity.class);
                View sharedView = imageView;
                String transitionName = ctxt.getString(R.string.albumart);

                switch (viewPagerPos){
                    case SlideViewPagerAdapter.GENRE_VIEW:  //Get Genre
                        songArrayList = MusicHelper.getSongListByGenre(ctxt, _ids.get(position));
                        break;
                }

                //For animation TransitionAnimation is used. Same trasition id for image view of this and player is defined
                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity)ctxt, sharedView, transitionName);
                gotoMusicPlayer.putExtra(PlayerActivity.EXTRA_SONGLIST, songArrayList);
                ctxt.startActivity(gotoMusicPlayer, transitionActivityOptions.toBundle());
            }
        });
    }

    /**
     * Return list count
     * @return title's list count
     */
    @Override
    public int getItemCount() {
        return firstTitles.size();
    }

    /**
     * Showing popup menu when tapping on 3 dots
     * _id is handed for further processing
     */
    private void showPopupMenu(View view, int _id) {
        // inflate menu
        PopupMenu popup = new PopupMenu(ctxt, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(_id));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        //to save index for updating;
        private int _id;

        public MyMenuItemClickListener(int id) {
            this._id = id;
        }


        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_playlist:
                    songArrayList = MusicHelper.getSongListByGenre(ctxt, _id);
                    //to prevent add 0 to songlist
                    if (songArrayList.size() == 0)
                        return true;
                    //set songlist and goto selecting playlist
                    Intent gotoSelectPlaylist = new Intent(ctxt, SelectAndAddToPlayList.class);
                    gotoSelectPlaylist.putExtra(PlayerActivity.EXTRA_SONGLIST, songArrayList);
                    ctxt.startActivity(gotoSelectPlaylist);
                    return true;

                default:
            }
            return false;
        }
    }
}
