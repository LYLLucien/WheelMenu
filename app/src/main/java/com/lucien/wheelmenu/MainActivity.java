package com.lucien.wheelmenu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.lucienli.wheelmenulib.WheelMenu;

public class MainActivity extends AppCompatActivity {

    private WheelMenu wm_demo;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        wm_demo = (WheelMenu) this.findViewById(R.id.wm_demo);
        wm_demo.setOnMenuClickListener(new WheelMenu.OnMenuClickListener() {
            @Override
            public void onClick(WheelMenu.Position position) {
                switch (position) {
                    case Right:
                        context.startActivity(new Intent(MainActivity.this, RightActivity.class));
                        break;
                    case Bottom:
                        context.startActivity(new Intent(MainActivity.this, BottomActivity.class));
                        break;
                    case Left:
                        context.startActivity(new Intent(MainActivity.this, LeftActivity.class));
                        break;
                    case Top:
                        context.startActivity(new Intent(MainActivity.this, TopActivity.class));
                        break;
                    default://https://github.com/LYLLucien/WheelMenu.git
                        break;
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
