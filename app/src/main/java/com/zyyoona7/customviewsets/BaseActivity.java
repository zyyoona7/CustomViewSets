package com.zyyoona7.customviewsets;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by zyyoona7 on 2016/8/9.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Unbinder mUnbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContentViewID() != 0) {
            setContentView(getContentViewID());
        }
        mUnbinder= ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mUnbinder!=null){
            mUnbinder.unbind();
        }
    }

    protected void goTo(Class clazz){
        startActivity(new Intent(this,clazz));
    }

    protected abstract int getContentViewID();
}
