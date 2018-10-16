package com.bobby.coding.main;

/**
 * Created by bobby on 5/1/2018.
 */

import android.animation.Animator;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bobby.coding.BaseApp;
import com.bobby.coding.R;
import com.bobby.coding.model.SettingDatabaseHelper;
import com.bobby.coding.utils.ConnectivityReceiver;
import com.bobby.coding.utils.Constants;
import com.bobby.coding.utils.DatabaseScope;
import com.wang.avi.AVLoadingIndicatorView;
import com.xw.repo.BubbleSeekBar;

import javax.inject.Inject;

import es.dmoral.toasty.Toasty;
public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    @Inject
    public SettingDatabaseHelper dh;

    private AVLoadingIndicatorView progressBar;
    private AlertDialog.Builder alert;
    private Interpolator interpolator;
    private LayoutInflater inflater;
    private View dialogView;
    private AlertDialog dialog;
    private TextView textview, mTitle,text;
    private Button button, b1, b2;
    private Toolbar toolbar;
    private MainFragment  fragment =new MainFragment();
    private boolean flag=false;
    final static String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    IntentFilter intentFilter;
    ConnectivityReceiver receiver;
    private BubbleSeekBar interval;
    public int count;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       ((BaseApp) getApplicationContext()).getDeps().maininject(this);

        renderView();
        init();
    }


    private void renderView() {
        setContentView(R.layout.activity_main);
        progressBar =findViewById(R.id.progress);
        toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        text=findViewById(R.id.textmessage);

        //Adding fragment to main activity
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if( getFragmentManager().findFragmentByTag("search")!=null){
            fragment=(MainFragment)getFragmentManager().findFragmentByTag("search");
        }
        else {
            fragmentTransaction.add(R.id.framelayout, fragment, "search");
        }
        fragmentTransaction.commit();
        fragmentTransaction.addToBackStack(null);
    }

    private void init() {
        mTitle.setTypeface(Constants.montserrat_semiBold);
       // animateRevealShow(mTitle);
        mTitle.setTextColor(getResources().getColor(R.color.transition));

        //Grid per row
        Cursor c1=dh.getalldata();
        c1.moveToNext();
        count=Integer.parseInt(c1.getString(1));

        //Broadcast receiver for checking internet connection
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new ConnectivityReceiver();
        registerReceiver(receiver, intentFilter);

        //Configuring customized Toast messages
        Toasty.Config.getInstance()
                .setErrorColor( getResources().getColor(R.color.colorPrimaryDark) )
                .setSuccessColor(getResources().getColor(R.color.colorPrimaryDark) )
                .setTextColor(getResources().getColor(R.color.white) )
                .tintIcon(true)
                .setTextSize(18)
                .apply();
    }

    public void animateRevealShow(View viewRoot) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
        viewRoot.setVisibility(View.VISIBLE);
        anim.setDuration(1000);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.start();
    }


    public void showWait() {
        progressBar.setVisibility(View.VISIBLE);
    }
    public void removeWait() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logoff, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id =item.getItemId();
        switch (id){
            case R.id.action_logoff:
                alert = renderAlertView(R.layout.exit);
                dialog = alert.create();
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exitApp();

                    }
                });
                dialog.show();
                break;

            case R.id.action_setting:
                alert = renderAlertView(R.layout.setting);
                dialog = alert.create();
                interval.setProgress(count);

                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dh.deletedata(1);
                        dh.insertdata(""+interval.getProgress());
                        count=interval.getProgress();
                        fragment.updateUI();
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setMessage()
    {
        text.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setConnectivityListener(this);
    }

    private void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    //Checking internet flag using broadcast receiver
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(flag!=isConnected)
        {
            if(isConnected){
                Toasty.success(this, "Connected to internet",Toast.LENGTH_SHORT, true).show();
            }
            else
            {
                Toasty.error(getApplicationContext(), "Not connected to internet",Toast.LENGTH_LONG, true).show();
            }
        }
        flag= (isConnected);
    }



    //Rendering alert dialog
    private AlertDialog.Builder renderAlertView(int resource) {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(resource, null);
        alert.setView(dialogView);

        textview = dialogView.findViewById(R.id.textView2);
        button = dialogView.findViewById(R.id.button);
        b1 = dialogView.findViewById(R.id.button2);
        b2 = dialogView.findViewById(R.id.button3);
        interval=dialogView.findViewById(R.id.seekbar);

        return alert;
    }

    //Handling app closure
    private void exitApp() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exitApp();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (receiver != null)
            unregisterReceiver(receiver);

        dh=null;
    }

}
