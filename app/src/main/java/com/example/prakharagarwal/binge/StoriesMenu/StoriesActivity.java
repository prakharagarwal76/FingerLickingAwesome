package com.example.prakharagarwal.binge.StoriesMenu;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.prakharagarwal.binge.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class StoriesActivity extends FragmentActivity {


    RecyclerView mRecyclerView;
    MenuAdapter menuAdapter;
    List<Menu> menus;
    SlidingUpPanelLayout slidingUpPanelLayout;
    String ID;
    ArrayList<String> Videos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);
        menus=new ArrayList<Menu>();
        Videos=new ArrayList<String>();

        ID=getIntent().getStringExtra("restaurantID");
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("menu");
        ref.keepSynced(true);


        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getData(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e("error","error");
            }
        });

        slidingUpPanelLayout=(SlidingUpPanelLayout)findViewById(R.id.stories_sliding_up);

       slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
           @Override
           public void onPanelSlide(View panel, float slideOffset) {

           }

           @Override
           public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            if(newState== SlidingUpPanelLayout.PanelState.EXPANDED)
                ((ImageView)findViewById(R.id.stories_heading_image)).setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);


               if(newState== SlidingUpPanelLayout.PanelState.COLLAPSED)
                   ((ImageView)findViewById(R.id.stories_heading_image)).setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);

           }
       });

        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this);

        mRecyclerView=(RecyclerView)findViewById(R.id.menu_fragment_recycler_view);
        menuAdapter= new MenuAdapter(this,menus);
        try{
            mRecyclerView.setAdapter(menuAdapter);
        }catch (NoClassDefFoundError e){

        }
        mRecyclerView.setLayoutManager(linearLayoutManager);


    }

    public ArrayList<String> getVideos() {
        return Videos;
    }

    public void getData(DataSnapshot dataSnapshot) {

                for (DataSnapshot child1 : dataSnapshot.getChildren()) {
                    if(child1.getKey().equals(ID))

                    for (DataSnapshot child2 : child1.getChildren()) {

                        if (child2.getKey()!=null){
                            for (DataSnapshot child3 : child2.getChildren()) {
                                Menu menu=child3.getValue(Menu.class);
                                getVideoStoryURL(menu);
                                menus.add(menu);
                                }

                        }

                    }


            }
        menuAdapter.addAll(menus);

        menuAdapter.notifyDataSetChanged();
        //getVideoStoriesURL(menus);

    }

    private void getVideoStoriesURL(List<Menu> menus) {
        for(int i=0;i<menus.size();i++){
            Menu menu=menus.get(i);
            if(menu.getHas_video()==0){
                Videos.add(menu.getVideo_url());
            }
        }
    }
    private void getVideoStoryURL(Menu menu) {
            if(menu.getHas_video()==0) {
                Videos.add(menu.getVideo_url());
            }
    }



    public SlidingUpPanelLayout getSlidingUpPanelLayout(){
        return slidingUpPanelLayout;
    }

}