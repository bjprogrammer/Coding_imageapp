package com.bobby.coding.main;

import android.app.ActivityOptions;
import android.app.SharedElementCallback;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.bobby.coding.R;
import com.bobby.coding.detail.DetailActivity;
import com.bobby.coding.model.Upload;
import com.bobby.coding.upload.UploadActivity;
import com.bobby.coding.utils.Constants;
import com.bobby.coding.utils.PreCachingLayoutManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

//Uses Recyclerview to display artist list to users
public class MainFragment extends Fragment {
    private RecyclerView list;
    private MainAdapter adapter;
    public List<Upload> data= new ArrayList<>();
    private Parcelable mListState;
    private PreCachingLayoutManager layoutManager;
    private DatabaseReference mDatabase;
    FloatingActionButton fab;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public MainFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                } else {
                    // User is signed out
                    mAuth.signInAnonymously()
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) { }
                            });
                }
            }
        };


        setRetainInstance(true);
        if(savedInstanceState != null)
            mListState = savedInstanceState.getParcelable("list");
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_main, container, false);
        renderView(v);
        init();
        return v;
    }

    private void renderView(View v) {
        list = v.findViewById(R.id.list);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);

        //Configuring customized Toast messages
        Toasty.Config.getInstance()
                .setErrorColor(getResources().getColor(R.color.colorPrimaryDark))
                .setSuccessColor(getResources().getColor(R.color.colorPrimaryDark))
                .setTextColor(getResources().getColor(R.color.white))
                .tintIcon(true)
                .setTextSize(18)
                .apply();
    }

    private void init() {
        layoutManager = new PreCachingLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setExtraLayoutSpace(getScreenHeight(getActivity()));
        GridLayoutManager gl=new GridLayoutManager(getActivity(),((MainActivity)getActivity()).count);
        list.setLayoutManager(gl);
        list.setItemAnimator(new DefaultItemAnimator());
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(getContext(), UploadActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        //adding an event listener to fetch values
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (getActivity() != null)
                {
                    ((MainActivity) getActivity()).showWait();

                    data.clear();
                //iterating through all the values in database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    data.add(upload);
                }

                if (data.size() > 0) {
                    adapter = new MainAdapter(getContext(), data,
                            new MainAdapter.OnItemClickListener() {
                                @Override
                                public void onClick(Upload Item, View v) {
                                    setIntent(Item, v);
                                }
                            });

                    AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
                    ScaleInAnimationAdapter scaleadapter = new ScaleInAnimationAdapter(alphaAdapter);
                    scaleadapter.setDuration(100);
                    list.setAdapter(scaleadapter);
                    adapter.notifyDataSetChanged();
                } else {
                    ((MainActivity) getActivity()).setMessage();

                }
                ((MainActivity) getActivity()).removeWait();
            }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.getMessage();
                ((MainActivity) getActivity()).removeWait();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mListState != null) {
            layoutManager.onRestoreInstanceState(mListState);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mListState = layoutManager.onSaveInstanceState();
        outState.putParcelable("list", mListState);
    }

    //Passes data to next activity
    private void setIntent(Upload item, final View v) {
        final int position=data.indexOf(item);
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("position",position);

        Bundle args = new Bundle();
        args.putSerializable("data",(Serializable)data);
        intent.putExtra("bundle",args);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(),v, "transition"+position);
            setExitSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    // update names and sharedElements

                    sharedElements.put("transition"+position,v);
                }
            });

            startActivity(intent,transitionActivityOptions.toBundle());
        }
        else {
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
    }

    private int getScreenHeight(Context context) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int screenH = outMetrics.heightPixels;

        return screenH;
    }

    public void updateUI(){
        init();
    }

    @Override
    public void onDestroy() {
        list=null;
        data=null;
        mListState=null;
        super.onDestroy();
    }
}
