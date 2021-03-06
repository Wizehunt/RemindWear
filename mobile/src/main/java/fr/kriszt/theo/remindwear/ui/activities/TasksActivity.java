package fr.kriszt.theo.remindwear.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.ui.fragments.SportTaskListFragment;
import fr.kriszt.theo.remindwear.ui.fragments.TaskListFragment;

public class TasksActivity extends AppCompatActivity {

    public static final String FRAGMENT_TO_LAUNCH = "fragment_to_launch";
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_tasks:
                    fragment = new TaskListFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_sport:
                    fragment = new SportTaskListFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        loadFragment(new TaskListFragment());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
