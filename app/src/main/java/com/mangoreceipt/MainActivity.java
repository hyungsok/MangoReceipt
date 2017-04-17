package com.mangoreceipt;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseUser;
import com.mangoreceipt.protocol.Receipt;
import com.mangoreceipt.protocol.ReceiptService;
import com.mangoreceipt.protocol.ServiceGenerator;
import com.mangoreceipt.utils.FilePath;
import com.mangoreceipt.utils.SimpleDividerItemDecoration;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.user_image)
    ImageView mUserImage;

    ImageView mNaviHeaderImage;
    TextView mNaviHeaderName;
    TextView mNaviHeaderEmail;

    public static final int PICK_IMAGE = 100;

    private LruBitmapPool mLruBitmapPool = new LruBitmapPool(100);
    private ReceiptViewAdapter mReceiptViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Realm.init(this);

        startActivity(new Intent(this, MainSplashScreen.class));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mReceiptViewAdapter = new ReceiptViewAdapter(this);
        mRecyclerView.setAdapter(mReceiptViewAdapter);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));

        initFloatingActionButtons();
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        mNaviHeaderImage = (ImageView) header.findViewById(R.id.navi_header_image);
        mNaviHeaderName = (TextView) header.findViewById(R.id.navi_header_name);
        mNaviHeaderEmail = (TextView) header.findViewById(R.id.navi_header_email);
    }

    @Override
    protected void updateFirebaseUser(FirebaseUser firebaseUser) {
        Log.i(LOG_TAG, "++ updateFirebaseUser() user : " + firebaseUser);
        if (firebaseUser != null) {
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(mNaviHeaderImage);
            mNaviHeaderName.setText(firebaseUser.getDisplayName());
            mNaviHeaderEmail.setText(firebaseUser.getEmail());
            mToolbar.setTitle(firebaseUser.getDisplayName());
            Glide.with(this).load(firebaseUser.getPhotoUrl())
                    .bitmapTransform(new CropCircleTransformation(mLruBitmapPool))
                    .into(mUserImage);
        } else {
            mNaviHeaderImage.setImageResource(android.R.drawable.sym_def_app_icon);
            mNaviHeaderName.setText("로그인이 필요합니다.");
            mNaviHeaderEmail.setText("");
            mToolbar.setTitle("");
            Glide.with(this).load(R.drawable.ic_account_circle_white_24dp)
                    .bitmapTransform(new BlurTransformation(this, mLruBitmapPool),
                            new CropCircleTransformation(mLruBitmapPool));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(LOG_TAG, "++ onActivityResult() requestCode : " + requestCode + ", resultCode : " + resultCode);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            uploadFile(data.getData());
        }

    }

    private void initFloatingActionButtons() {
        FloatingActionButton fabEtc = (FloatingActionButton) findViewById(R.id.fab_etc);
        FloatingActionButton fabFood = (FloatingActionButton) findViewById(R.id.fab_food);
        FloatingActionButton fabMarking = (FloatingActionButton) findViewById(R.id.fab_marketing);
        FloatingActionButton fabTaxi = (FloatingActionButton) findViewById(R.id.fab_taxi);
        final FloatingActionMenu fam = (FloatingActionMenu) findViewById(R.id.fab_menu);

        fam.setOnMenuToggleListener(opened -> {
            if (opened) {
            } else {
            }
        });

        fabEtc.setOnClickListener((view -> {

            fam.close(true);
        }
        ));
        fabFood.setOnClickListener((view -> {
            showImagePicker();
            fam.close(true);
        }
        ));
        fabMarking.setOnClickListener((view -> {
            showImagePicker();
            fam.close(true);
        }
        ));
        fabTaxi.setOnClickListener((view -> {
            showImagePicker();
            fam.close(true);
        }
        ));

        fam.setOnClickListener(view -> {
            if (fam.isOpened()) {
                fam.close(true);
            }
        });
    }

    private void showImagePicker() {
        showProgressDialog();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            signOut();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.i(LOG_TAG, "++ onNavigationItemSelected() id : " + id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void uploadFile(Uri fileUri) {
        Log.i(LOG_TAG, "++ uploadFile() fileUri : " + fileUri);
        showProgressDialog();
        // create upload service client
        ReceiptService service = ServiceGenerator.createService(ReceiptService.class);

        File file = new File(FilePath.getPath(this, fileUri));
        Log.i(LOG_TAG, "\t >> file : " + file);
        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("receiptImg", file.getName(), requestFile);

        Call<Receipt> call = service.postImage(body);
        call.enqueue(new Callback<Receipt>() {
            @Override
            public void onResponse(Call<Receipt> call, Response<Receipt> response) {
                Log.i(LOG_TAG, "++ onResponse() response : " + response);
                Receipt receipt = response.body();
                Log.i(LOG_TAG, "\t receipt : " + receipt);
                ReceiptDetailFragment.show(getSupportFragmentManager(), receipt);
                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<Receipt> call, Throwable t) {
                t.printStackTrace();
                hideProgressDialog();
            }
        });
    }
}
