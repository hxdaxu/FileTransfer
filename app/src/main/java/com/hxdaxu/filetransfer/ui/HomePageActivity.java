package com.hxdaxu.filetransfer.ui;

import android.view.View;
import android.widget.Button;

import com.hxdaxu.filetransfer.R;
import com.hxdaxu.filetransfer.ui.base.BaseActivity;

public class HomePageActivity extends BaseActivity implements View.OnClickListener{

    private Button bt_send;
    private Button bt_receive;

    @Override
    protected int getContentView() {
        return R.layout.activity_home_page;
    }

    @Override
    protected void initView() {
        super.initView();
        bt_send = findViewById(R.id.bt_send);
        bt_receive = findViewById(R.id.bt_receive);

        bt_send.setOnClickListener(this);
        bt_receive.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_receive :
                break;
            case R.id.bt_send :
                break;
        }
    }
}
