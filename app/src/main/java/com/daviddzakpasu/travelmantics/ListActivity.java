package com.daviddzakpasu.travelmantics;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListActivity extends AppCompatActivity {

    private RecyclerView rvDeals;
    private FloatingActionButton fabNewDeal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        rvDeals = findViewById(R.id.rv_deals);
        rvDeals.setHasFixedSize(true);

        fabNewDeal = findViewById(R.id.fab_newDeal);

        fabNewDeal.hide();

        if (FirebaseUtil.isAdmin) {
            fabNewDeal.show();
        } else fabNewDeal.hide();

        rvDeals.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (FirebaseUtil.isAdmin) {
                    if (dy > 0 && fabNewDeal.getVisibility() == View.VISIBLE) {
                        fabNewDeal.hide();
                    } else if (dy < 0 && fabNewDeal.getVisibility() != View.VISIBLE) {
                        fabNewDeal.show();
                    }
                }
            }
        });

        fabNewDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, DealActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
//        MenuItem menuInsert = menu.findItem(R.id.menu_insert);
        /*if (FirebaseUtil.isAdmin == true) {
            fabNewDeal.show();
        }
        else {
            fabNewDeal.hide();
        }*/
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(Build.VERSION.SDK_INT > 11) {
            invalidateOptionsMenu();
//            MenuItem menuInsert = menu.findItem(R.id.menu_insert);
            /*if (FirebaseUtil.isAdmin == true) {
                fabNewDeal.show();
            }
            else {
                fabNewDeal.hide();
            }*/
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.menu_insert:
                Intent intent = new Intent(this, DealActivity.class);
                startActivity(intent);
                return true;*/
            case R.id.menu_logout:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Logout", "User Logged Out");
                                FirebaseUtil.attachListener();
                            }
                        });
                FirebaseUtil.detachListener();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.openFirebaseRef("holidaydeals", this);
        RecyclerView rvDeals = (RecyclerView) findViewById(R.id.rv_deals);
        final DealAdapter adapter = new DealAdapter();
        rvDeals.setAdapter(adapter);

        @SuppressLint("WrongConstant") LinearLayoutManager dealsLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvDeals.setLayoutManager(dealsLayoutManager);
        FirebaseUtil.attachListener();
    }

    public void showMenu() {
        invalidateOptionsMenu();
    }


}
