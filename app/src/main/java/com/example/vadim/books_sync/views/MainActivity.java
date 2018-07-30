package com.example.vadim.books_sync.views;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;

import com.example.vadim.books_sync.R;
import com.example.vadim.books_sync.adapter.MaterialsRecyclerAdapter;
import com.example.vadim.books_sync.dagger.AppModule;
import com.example.vadim.books_sync.dagger.DaggerAppComponent;
import com.example.vadim.books_sync.dagger.RoomModule;
import com.example.vadim.books_sync.dao.MaterialDao;
import com.example.vadim.books_sync.model.Material;
import com.example.vadim.books_sync.presenters.MaterialsUpdaterPresenter;
import com.example.vadim.books_sync.viewPresenters.MaterialsView;
import com.example.vadim.books_sync.views.animations.ImageButtonAnimation;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MaterialsView {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 3;

    @BindView(R.id.btnSync)
    ImageButton syncButton;

    @BindView(R.id.material_list)
    RecyclerView recyclerView;

    @BindView(R.id.inputSearch)
    SearchView inputSearch;

    @Inject
    MaterialDao materialDao;

    @Inject
    MaterialsUpdaterPresenter materialsUpdaterPresenter;

    private MaterialsRecyclerAdapter materialsRecyclerAdapter;

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                REQUEST_WRITE_EXTERNAL_STORAGE);
    
        ButterKnife.bind(this);
        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .injectMainActivity(this);
        materialsUpdaterPresenter.attachView(this);

        createMaterialAdapter();
        final List<Material> materials = materialDao.findAll();
        final LinkedList<Material> materialLinkedList
                = convertToLinkedMaterialList(materials);

        materialsUpdaterPresenter.updateMaterials(materialLinkedList);
        inputSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                materialsRecyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });

        syncButton.setOnClickListener((View e) -> {
            final ImageButtonAnimation animation =
                    new ImageButtonAnimation(syncButton);
            animation.startAnimation();
            final RotateAnimation rotateAnimation = animation.getRotateAnimation();
            rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {}

                @Override
                public void onAnimationRepeat(Animation animation) {
                    materialsUpdaterPresenter.updateMaterials(materialLinkedList);
                    rotateAnimation.setRepeatCount(Animation.ABSOLUTE);
                }
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("state ", "start activity");
        materialsUpdaterPresenter.attachView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("state ", "stop activity");
        materialsUpdaterPresenter.detachView();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void updateMaterials(LinkedList<Material> materials) {
        materialsRecyclerAdapter.setListContent(materials);
        recyclerView.setAdapter(materialsRecyclerAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private LinkedList<Material> convertToLinkedMaterialList(
            List<Material> materials) {
        final LinkedList<Material> newMaterialList = new LinkedList<>();
        for (Material material : materials) {
            newMaterialList.addLast(material);
        }
        return newMaterialList;
    }

    private void createMaterialAdapter() {
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));
        materialsRecyclerAdapter = new MaterialsRecyclerAdapter(this);
        recyclerView.setAdapter(materialsRecyclerAdapter);
    }

}

