package com.xinnongyun.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.xinnongyun.sharepreference.MySharePreference;


//展示界面
public class ShowActivity extends FragmentActivity {

    public ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏ActionBar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		         WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
        setContentView(R.layout.activity_show);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return PlaceholderFragment.newInstance(i + 1);
            }

            @Override
            public int getCount() {
                return 4;
            }
        });
        
        ExitApplication.getInstance().addActivity(this);
    }

    public void onEnterButtonClicked(View view) {
    	//保存首次登录，并登录
    	MySharePreference.save(ShowActivity.this, MySharePreference.ISFIRSTOPEN, "alreadyopen");
    	Intent intent = new Intent();
    	intent.setClass(ShowActivity.this, MainActivity.class);
    	startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public static class PlaceholderFragment extends Fragment {
        public static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int layoutResourceId = R.layout.fragment_show1;
            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    layoutResourceId = R.layout.fragment_show1;
                    break;
                case 2:
                    layoutResourceId = R.layout.fragment_show2;
                    break;
                case 3:
                    layoutResourceId = R.layout.fragment_show3;
                    break;
                case 4:
                    layoutResourceId = R.layout.fragment_show4;
                    break;
                default:
                    break;
            }
            return inflater.inflate(layoutResourceId, container, false);
        }
    }
}
