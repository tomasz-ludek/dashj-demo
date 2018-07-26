package org.dash.dashj.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.dash.dashj.demo.ui.PeerListFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, new PeerListFragment())
                    .commit();
        }
    }
}
