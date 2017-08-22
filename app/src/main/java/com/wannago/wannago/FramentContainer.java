package com.wannago.wannago;

import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wannago.wannago.models.EventModel;

public class FramentContainer extends FragmentActivity implements ItemFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frament_container);

        android.app.FragmentManager fragManager = getFragmentManager();
        Fragment fragment = new ItemFragment();
        android.app.FragmentTransaction transaction = fragManager.beginTransaction();
        transaction.replace(R.id.fragment,fragment);
        transaction.commit();
    }

    @Override
    public void onListFragmentInteraction(EventModel item) {

    }
}
