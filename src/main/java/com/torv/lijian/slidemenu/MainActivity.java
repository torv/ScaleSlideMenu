package com.torv.lijian.slidemenu;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.torv.lijian.slidemenu.menu.SlideMenu;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private SlideMenu slideMenu;

    private Button mBtnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "onCreate");

        slideMenu = new SlideMenu(this);
        slideMenu.attachToActivity(this);

        mBtnMenu = (Button) findViewById(R.id.btn_open_menu);
        mBtnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slideMenu.isOpened()) {
                    slideMenu.closeMenu();
                } else {
                    slideMenu.openMenu();
                }
            }
        });

    }

}
