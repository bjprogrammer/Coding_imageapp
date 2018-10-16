package com.bobby.coding.detail;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;;

import com.bobby.coding.R;
import com.bobby.coding.model.Upload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DetailActivity extends AppCompatActivity implements ImageFragment.Listener {
    private ViewPager viewPager;
    public int imageno;
    public List<Upload> data;
    public ImageFragment[]  images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.excludeTarget(R.id.applayout, true);
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);

            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }
        fetchData();
        renderView();
        setupViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void renderView() {
        setContentView(R.layout.activity_article);
        viewPager =  findViewById(R.id.viewpager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewPager.setTransitionName("transition" + imageno);
        }
    }

    private void init(){
        final ViewPager.OnPageChangeListener pageChangeListener =new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
              imageno=new Random().nextInt(images.length);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        };

        viewPager.addOnPageChangeListener( pageChangeListener);
    }

    //Fetching artist info from previous activity and from shared preference
    private void fetchData() {
         int  position = getIntent().getIntExtra("position",0);
         Bundle args = getIntent().getBundleExtra("bundle");
         data= (ArrayList<Upload>) args.getSerializable("data");
         images = new ImageFragment[data.size()];
         imageno=position;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                // update names and sharedElements
                sharedElements.put("transition"+imageno, viewPager);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for(int i=0;i<data.size();i++){
            ImageFragment fragment=new ImageFragment();
            adapter.addFragment(fragment);
            images[i]= fragment;
        }
        viewPager.setAdapter(adapter);
    }

    @Override
    public int ImageSelected() {
        return imageno;
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        viewPager=null;
        images=null;
        data=null;
        super.onDestroy();
    }
}
