package com.example.vernseaw.goal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;



import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Host extends AppCompatActivity {
    InputStream inputStream = null;

    TabLayout tabLayout;
    Spinner spinnerMatch;
    Spinner spinnerGuest;
    Button hostButton;
    TextView timeView;
    ToggleButton teamToggle;
    AutoCompleteTextView hostAddress;
    EditText remarksHost;
    CheckBox snacksCheck;
    ImageView uploadImg, picture;
    LocationManager locationManager;

    LocationListener locationListener = null;

    String tempPath;


    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyA-yPlj8YrIU1YZAXjSUk25rug9dHKKFBg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Match Spinner
        spinnerMatch = (Spinner) findViewById(R.id.spinnerMatch);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterMatch = ArrayAdapter.createFromResource(this, R.array.football_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterMatch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerMatch.setAdapter(adapterMatch);


        //Guest Spinner
        spinnerGuest = (Spinner) findViewById(R.id.guestSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterGuest = ArrayAdapter.createFromResource(this, R.array.guest_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterGuest.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerGuest.setAdapter(adapterGuest);

        //TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("MATCHES").setIcon(R.drawable.ic_format_list_bulleted_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setText("HOST").setIcon(R.drawable.ic_weekend_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setText("PROFILE").setIcon(R.drawable.ic_person_white_24dp));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tabLayout.getSelectedTabPosition()) {
                    case 0:
                        //do what you want when tab 0 is selected
                        break;
                    case 1:
                        //do what you want when tab 1 is selected
                        break;
                    case 2:
                        //do what you want when tab 2 is selected
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });




        hostButton = (Button) findViewById(R.id.hostButton);
        timeView = (TextView) findViewById(R.id.time);
        teamToggle = (ToggleButton) findViewById(R.id.toggleTeam);
        hostAddress = (AutoCompleteTextView) findViewById(R.id.addressHost);
        remarksHost = (EditText) findViewById(R.id.remarksHost);
        snacksCheck = (CheckBox) findViewById(R.id.snacksHost);
        uploadImg = (ImageView) findViewById(R.id.uploadImg);
        picture = (ImageView) findViewById(R.id.profileImg);

        hostAddress.setTextSize(12);
        remarksHost.setTextSize(12);

        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
        {
            buildAlertMessageNoGps();
        }
        else
        {
            checkLocation();
        }


        hostAddress.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        hostAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) parent.getItemAtPosition(position);
                hostAddress.setText(str);
            }
        });



        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();

            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();

            }
        });

        hostButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                confirmDialog(getApplicationContext());
            }
        });

        hostAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        remarksHost.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });



        spinnerMatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    timeView.setText("22/06/2015 3.45am");
                } else if (position == 1) {
                    timeView.setText("22/06/2015 4.45am");
                } else if (position == 2) {
                    timeView.setText("23/06/2015 3.45am");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }


        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Host.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void checkLocation() {

            final double longtitude;
            final double latitude;

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    Log.v("Longtitude:", String.valueOf(location.getLongitude()));
                    Log.v("Latitude:", String.valueOf(location.getLatitude()));
                    cityName(location.getLatitude(), location.getLongitude());

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }

            });
        }




    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }



    public void cityName(double latitude, double longtitude){
        String cityName = "";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(latitude, longtitude, 1);
            if (addresses.size() > 0) {

                if((hostAddress.getText().toString().equals("")) || hostAddress.getText() == null) {
                    cityName = addresses.get(0).getAddressLine(0);

                    hostAddress.setText(cityName);
                    hostAddress.setTextSize(12);
                }

            }
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_host, menu);
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

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 0);
                }
                else if (items[item].equals("Choose from Library"))
                {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), 1);
                }
                else if (items[item].equals("Cancel"))
                {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

                    bm = BitmapFactory.decodeFile(f.getAbsolutePath(), btmapOptions);

                    // bm = Bitmap.createScaledBitmap(bm, 70, 70, true);
                    picture.setImageBitmap(bm);

                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream fOut = null;
                    File file = new File(path, String.valueOf(System
                            .currentTimeMillis()) + ".jpg");
                    try {
                        fOut = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                        fOut.flush();
                        fOut.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 1) {
                Uri selectedImageUri = data.getData();

                tempPath = getPath(selectedImageUri, this);
                Bitmap bm;
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                picture.setImageBitmap(bm);
            }
        }
    }

    public String getPath(Uri uri, Host activity) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }




    private void confirmDialog(Context context) {

        final AlertDialog alert = new AlertDialog.Builder(this)
                .create();
        alert.setTitle("Confirmation Dialog");
        alert.setMessage("Do you want to host a match ?");
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);

        alert.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String match = spinnerMatch.getSelectedItem().toString();
                        String time = timeView.getText().toString();
                        String noOfGuest = spinnerGuest.getSelectedItem().toString();
                        String selectTeam = teamToggle.getText().toString();
                        String address = hostAddress.getText().toString();
                        String remarks = remarksHost.getText().toString();
                        String snacks = "No";

                        if (snacksCheck.isChecked()) {
                            snacks = "Yes";
                        }

                        if (address.equals("")) {
                            Toast.makeText(getApplicationContext(), "Please enter an address!", Toast.LENGTH_SHORT).show();
                        } else {

                            HttpClient httpClient = new DefaultHttpClient();
                            HttpGet httpGet = new HttpGet("http://www.hellodes.com/mhci/webservice.php?func=2&1=" + URLEncoder.encode(match) + "&2=" + URLEncoder.encode(time) + "&3=" + noOfGuest +
                                    "&4=" + selectTeam + "&5=" + URLEncoder.encode(address) + "&6=" + URLEncoder.encode(remarks) + "&7=" + snacks + "&8=" + tempPath);

                            try {

                                HttpResponse response = httpClient.execute(httpGet);
                                String responseString = new BasicResponseHandler().handleResponse(response);

                                if (responseString != "false") {
                                    if(responseString.equals("Match Hosted"))
                                    {
                                        hostAddress.setText("");
                                        remarksHost.setText("");
                                    }
                                    Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();

                                }

                            } catch (ClientProtocolException e) {

                                e.printStackTrace();
                            } catch (IOException e) {

                                e.printStackTrace();
                            }

                            alert.dismiss();

                            //finish();
                        }

                    }
                });

        alert.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        alert.dismiss();

                    }
                });

        alert.show();
    }


    public void onResume() {
        super.onResume();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
        {
            checkLocation();
        }

    }

    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?input=" + URLEncoder.encode(input, "utf8"));
            sb.append("&components=country:sg");
            sb.append("&key=" + API_KEY);
            sb.append("&sensor=" + "true");


            Log.v("URL", sb.toString());

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e("MalformedURLException", e.toString());
            return resultList;
        } catch (IOException e) {
            Log.e("IOException", e.toString());
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            Log.v("JSON", String.valueOf(jsonResults.toString()));
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());


            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {

        }
        return resultList;
    }



    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList<String> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocoΩmplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }



}








