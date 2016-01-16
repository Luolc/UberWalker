package com.luolc.uberwalker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.avos.avoscloud.*;
import com.avos.avospush.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;

import com.luolc.uberwalker.Manager.UserManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String KEY_TITLLE = "key_title";

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private LeftMenuFragment mLeftMenuFragment;
    private Fragment mCurrentFragment;

    private String mTitle;

    private UserManager mUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserManager = new UserManager(this);

        initToolBar();

        // AVAnalytics.trackAppOpened(getIntent());

        PushService.setDefaultPushCallback(this, MainActivity.class);

        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    Log.v(TAG, installationId);
                    // 关联  installationId 到用户表等操作……
                } else {
                    // 保存失败，输出错误信息
                }
            }
        });


               // 授权WebView
        if (!isUserExist()) {
            startActivity(new Intent(this, AuthActivity.class));
        }
        initViews();

        //恢复title
        restoreTitle(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        //查找当前显示的Fragment
        mCurrentFragment = (ContentFragment) fm.findFragmentByTag(mTitle);

        if (mCurrentFragment == null) {
            mCurrentFragment = MainFragment.newInstance(mTitle);
            fm.beginTransaction().add(R.id.content_container, mCurrentFragment, mTitle).commit();
        }

        mLeftMenuFragment = (LeftMenuFragment) fm.findFragmentById(R.id.left_menu_container);
        if (mLeftMenuFragment == null) {
            mLeftMenuFragment = new LeftMenuFragment();
            fm.beginTransaction().add(R.id.left_menu_container, mLeftMenuFragment).commit();
        }

        //隐藏别的Fragment，如果存在的话
        List<Fragment> fragments = fm.getFragments();
        if (fragments != null)

            for (Fragment fragment : fragments) {
                if (fragment == mCurrentFragment || fragment == mLeftMenuFragment) continue;
                fm.beginTransaction().hide(fragment).commit();
            }

        //设置MenuItem的选择回调
        mLeftMenuFragment.setOnMenuItemSelectedListener(new LeftMenuFragment.OnMenuItemSelectedListener() {
            @Override
            public void menuItemSelected(String title) {

                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(title);
                if (fragment == mCurrentFragment) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    return;
                }

                FragmentTransaction transaction = fm.beginTransaction();
                transaction.hide(mCurrentFragment);

                if (fragment == null) {
                    if(title == "修改预算")
                        fragment = BudgetFragment.newInstance(title);
                    else
                        fragment = ContentFragment.newInstance(title);
                    transaction.add(R.id.content_container, fragment, title);
                } else {
                    transaction.show(fragment);
                }
                transaction.commit();

                mCurrentFragment = fragment;
                mTitle = title;
                mToolbar.setTitle(mTitle);
                mDrawerLayout.closeDrawer(Gravity.LEFT);


            }
        });

    }

    private void restoreTitle(Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mTitle = savedInstanceState.getString(KEY_TITLLE);

        if (TextUtils.isEmpty(mTitle)) {
            mTitle = "Uber Walker";
        }

        mToolbar.setTitle(mTitle);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_TITLLE, mTitle);
    }

    private void initToolBar() {

        Toolbar toolbar = mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // Title
        toolbar.setTitle("Toolbar Title");
        setSupportActionBar(toolbar);

        /*
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });*/
    }

    private void initViews() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    private boolean isUserExist() {
        if ("".equals(mUserManager.getUid())) {
            return false;
        }
        return true;
    }
}