package demo.android.navigationdemo.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import demo.android.navigationdemo.R;
import demo.android.navigationdemo.api.ApiFactory;
import demo.android.navigationdemo.api.DataRequest.DataRequest;
import demo.android.navigationdemo.api.DataResponse.DataResponse;
import demo.android.navigationdemo.api.service.MyService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    public String lat = null;
    public String lng = null;
    public String placeName = null;
    DataRequest dataRequest;
    DataResponse dataResponse;
    ProgressDialog loading;
    TextView tv_display_country;
    private EditText et_lat;
    private EditText et_lng;
    private Button btn_search;
    private Button btn_locate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        dataRequest = new DataRequest();
        dataResponse = new DataResponse();

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_lat.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Enter field with valid data", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("abc", "else part");

                    loading = ProgressDialog.show(MainActivity.this, "Fetching Data", "Please wait...", false, false);

                    MyService dataService = ApiFactory.createService(MyService.class);

                    Call<JsonObject> dataRequestCall = dataService.getResult(et_lat.getText().toString());


                    dataRequestCall.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            loading.dismiss();
                            Log.d("Response Recieved", "onResponse Response");

                            if (response.isSuccessful()) {
                                // Log.e("Response body", String.valueOf(response.code()));

                                Log.e("Response", response.body().toString());

                                dataResponse = new Gson().fromJson(response.body(), DataResponse.class);

                                lat = dataResponse.getResults().get(0).getGeometry().getLocation().getLat().toString();

                                lng = dataResponse.getResults().get(0).getGeometry().getLocation().getLng().toString();

                                placeName = dataResponse.getResults().get(0).getAddressComponents().get(0).getLongName();

                                tv_display_country.setText(dataResponse.getResults().get(0).getAddressComponents().get(2).getLongName());

                            } else {
                                showToast("Check your internet connection");
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.d("abc", "onFailure");
                            loading.dismiss();
                        }

                    });
                }
            }
        });

        btn_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap(lat, lng, placeName);
            }
        });
    }

    public void initViews() {
        et_lat = (EditText) findViewById(R.id.et_lat);
        btn_search = (Button) findViewById(R.id.search);
        btn_locate = (Button) findViewById(R.id.locate);
        tv_display_country = (TextView) findViewById(R.id.display_country);
    }

    public void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void showMap(String latm, String lngm, String placem) {

        boolean installedMaps = false;

        PackageManager pkManager = getPackageManager();
        try {
            @SuppressWarnings("unused")
            PackageInfo pkInfo = pkManager.getPackageInfo("com.google.android.apps.maps", 0);
            installedMaps = true;
        } catch (Exception e) {
            e.printStackTrace();
            installedMaps = false;
        }

        if (installedMaps == true) {

            String geocode = "geo:0,0?q=" + latm + ","
                    + lngm + "(" + placem + ")";
            Intent sendLocationToMap = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(geocode));
            startActivity(sendLocationToMap);
        } else if (installedMaps == false) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    MainActivity.this);

            // SET THE ICON
            alertDialogBuilder.setIcon(R.mipmap.ic_launcher);

            // SET THE TITLE
            alertDialogBuilder.setTitle("Google Maps Not Found");

            // SET THE MESSAGE
            alertDialogBuilder
                    .setMessage("Install Google Maps App")
                    .setCancelable(false)
                    .setNeutralButton("Got It",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.dismiss();
                                }
                            });
            // CREATE THE ALERT DIALOG
            AlertDialog alertDialog = alertDialogBuilder.create();

            // SHOW THE ALERT DIALOG
            alertDialog.show();

        }

    }
}
