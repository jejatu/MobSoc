package com.wishlist.wishlist;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_INT = 13;
    //public static int tabNumber=1;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Bitmap mCurrentPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkIfLoggedIn(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        setUpAlarm(getApplication());

        requestPermissions();
    }

    private boolean hasPermission(String permission)
    {
        int result = getApplicationContext().checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        if (!hasPermission(Manifest.permission.INTERNET) ||
                !hasPermission(Manifest.permission.ACCESS_NETWORK_STATE) ||
                !hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            String[] permissions = new String[3];

            permissions[0] = Manifest.permission.INTERNET;
            permissions[1] = Manifest.permission    .ACCESS_NETWORK_STATE;
            permissions[2] = Manifest.permission.WRITE_EXTERNAL_STORAGE;

            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_INT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_INT: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("Wishlist", "Permission " + i + " granted!");
                    }
                    else {
                        Log.d("Wishlist", "Permission " + i + " denied...");
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       // Toast.makeText(getApplicationContext(), "Main On resume", Toast.LENGTH_SHORT).show();
       checkIfLoggedIn(getApplicationContext());
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
    }

    @Override
    protected void onRestart(){
        super.onRestart();
       // Toast.makeText(getApplicationContext(), " Main On restart", Toast.LENGTH_SHORT).show();
        checkIfLoggedIn(getApplicationContext());
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Toast.makeText(getApplicationContext(), " Back Pressed", Toast.LENGTH_SHORT).show();
        SharedPreferences sp = getApplicationContext().getSharedPreferences("loginSaved", Context.MODE_PRIVATE);
        String username = sp.getString("username", null);
        finish();

    }
    public void checkIfLoggedIn(Context context){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("loginSaved", Context.MODE_PRIVATE);
        String username = sp.getString("username", null);
        if(username == null && getIntent().getBooleanExtra("EXIT", false)){
            finish();

        }else if(username == null){
            Intent intent= new Intent(context,LoginActivity.class );
            startActivity(intent);
        }

    }
    // copied from http://stackoverflow.com/questions/20887270/android-periodically-polling-a-server-and-displaying-response-as-a-notificatio
    public void setUpAlarm(Application context) {
        // Creates a repeating alarm that starts a notification service every 10 minutes
        // The notification service makes a get requests for products to the server
        // If products have increased since last request a notification is created
        int minutes = 10;
        int time = 60000 * minutes;
        Intent intent = new Intent(context, NotificationService.class);
        PendingIntent pending_intent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarm_mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm_mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), time, pending_intent);
    }

    public static int mImageWidth=0;
    public static int mImageHeight=0;
    public static void zoomImageAddProduct(View view){
        // When image is clicked on the add product view it is zoomed
        ImageView imageView=(ImageView) view.findViewById(R.id.thumbnailImageViewAddProduct);
        LinearLayout.LayoutParams parms;
        if(ViewGroup.LayoutParams.MATCH_PARENT!=imageView.getWidth() && mImageWidth==0){
            mImageHeight=imageView.getHeight();
            mImageWidth=imageView.getWidth();
            parms = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        else {
            parms = new LinearLayout.LayoutParams(mImageWidth, mImageHeight);
            parms.gravity= Gravity.CENTER_HORIZONTAL;
            mImageWidth=0;
            mImageHeight=0;
        }

        imageView.setLayoutParams(parms);

    }
    public static int mImageWidthList=0;
    public static int mImageHeightList=0;
    public static void zoomImageProductList(View view){
        // When image is clicked on the main view it is zoomed
        ImageView imageView=(ImageView) view.findViewById(R.id.thumbnailViewImage);
        LinearLayout.LayoutParams parms;

        if(ViewGroup.LayoutParams.MATCH_PARENT!=imageView.getWidth() && mImageWidthList==0){
            mImageHeightList=imageView.getHeight();
            mImageWidthList=imageView.getWidth();

            parms = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 600);
            parms.setMargins(0,130,0,30);

        }else {

            parms = new LinearLayout.LayoutParams(mImageWidthList, mImageHeightList);
            parms.gravity= Gravity.RIGHT;
            parms.setMargins(0,0,0,15);
            mImageWidthList=0;
            mImageHeight=0;
        }


        imageView.setLayoutParams(parms);


    }
    public void addProduct(View view) {
        // This function happens when add product button is pressed
        // Send a post rqeuest with the product information to the server
        EditText input_product_name = (EditText) findViewById(R.id.input_product_name);
        EditText input_product_description = (EditText) findViewById(R.id.input_product_description);
        final String name = input_product_name.getText().toString();
        final String description = input_product_description.getText().toString();

        if (!name.isEmpty() && !description.isEmpty()) {
            final String token = AuthHelper.getAuthToken(getApplicationContext());

            HttpClient.sendPostRequest("products?token=" + token, JSONHelper.createAddProduct(name, description), new HttpCallback() {
                @Override
                public void success(JSONObject response) {
                    Toast.makeText(getApplicationContext(), "Product added!", Toast.LENGTH_SHORT).show();

                    if (mCurrentPhoto != null) {
                        String productId = JSONHelper.parseProductId(response);
                        HttpClient.sendImage("image/" + productId + "?token=" + token, mCurrentPhoto, new HttpCallback() {
                            @Override
                            public void success(JSONObject response) {
                                Toast.makeText(getApplicationContext(), "Image uploaded!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void failure(JSONObject response) {
                                Toast.makeText(getApplicationContext(), "Uploading image failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void failure(JSONObject response) {
                    Toast.makeText(getApplicationContext(), "Adding product failed.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "No name or description for product.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Used to get the product image
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView mImageView=(ImageView) findViewById(R.id.thumbnailImageViewAddProduct);
            mImageView.setImageBitmap(imageBitmap);
            mCurrentPhoto = imageBitmap;
            /*if (mCurrentPhotoPath != null) {
                File imgFile = new File(mCurrentPhotoPath);

                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    ImageView myImage = (ImageView) findViewById(R.id.thumbnailImageViewAddProduct);

                    myImage.setImageBitmap(myBitmap);

                }
            }*/
        }
    }
    public void dispatchTakePictureIntent(View view) {
       /* Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }*/

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    public String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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
        }else if(id==R.id.fabOpenCamera){
            Fragment fragment= PlaceholderFragment.newInstance(2);
           // fragment.inf


            dispatchTakePictureIntent(findViewById(android.R.id.content));
        } else if (id == R.id.Logout) {
            String token = AuthHelper.getAuthToken(getApplicationContext());
            HttpClient.sendPostRequest("logout", JSONHelper.createLogout(token), new HttpCallback() {
                @Override
                public void success(JSONObject response) {}

                @Override
                public void failure(JSONObject response) {}
            });

            SharedPreferences sp = getApplicationContext().getSharedPreferences("loginSaved", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("username");
            editor.commit();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }else if(id==R.id.addMember){
            Intent intent= new Intent(this, MemberRequests.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public void floatingButtonOnClick(View view){
        Fragment addProductFragmemt=PlaceholderFragment.newInstance(2);

        TabHost host = (TabHost) findViewById(android.R.id.tabhost);
        host.setCurrentTab(2);


        Log.d("Test ","Passed");
    }
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        ListView listView;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView=inflater.inflate(R.layout.fragment_main, container, false);

            if(getArguments().getInt(ARG_SECTION_NUMBER)==1){
                rootView=inflater.inflate(R.layout.fragment_main, container, false);
                //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

                //List view work below
                listView = (ListView)rootView.findViewById(R.id.product_listview);
                //Log.d("List size and name", productList.size() + " name = "+ productList.get(2).getProductName() );

                refreshProducts();
                //end list view work
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==2){
                //FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
                rootView=inflater.inflate(R.layout.fragment_add_product, container, false);
            }


            return rootView;
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser) {
                refreshProducts();
            }
        }


        private void refreshProducts() {
            if (listView != null) {
                String token = AuthHelper.getAuthToken(getContext());

                HttpClient.sendGetRequest("products?token=" + token, new HttpCallback() {
                    @Override
                    public void success(JSONObject response) {
                        List<Product> productList = JSONHelper.parseProducts(response);
                        AuthHelper.saveProductCount(productList.size(), getContext());
                        Collections.reverse(productList);

                        listView.setAdapter(new ProductListAdaptor(productList));
                    }

                    @Override
                    public void failure(JSONObject response) {
                        listView.setAdapter(new ProductListAdaptor(Product.productDummyData()));
                    }
                });
            }
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:

                    return "Shopping List";

                case 1:

                    return "Add Product";
            }
            return null;
        }
    }
}
