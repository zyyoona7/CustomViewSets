package com.zyyoona7.customviewsets;

import android.os.Bundle;
import android.widget.Button;

import com.zyyoona7.customviewsets.basic_operation.BasicOperationActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    @BindView(R.id.btn_basic_operation)
    Button mBtnBasicOp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_main;
    }

    @OnClick(R.id.btn_basic_operation)
    void basicOpClick() {
        goTo(BasicOperationActivity.class);
    }

}
