package com.homeout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int GPS_UTIL_LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int GPS_UTIL_LOCATION_RESOLUTION_REQUEST_CODE = 101;

    public static final int DEFAULT_LOCATION_REQUEST_PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    public static final long DEFAULT_LOCATION_REQUEST_INTERVAL = 5000L;
    public static final long DEFAULT_LOCATION_REQUEST_FAST_INTERVAL = 2000L;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private double longitude, latitude;
    public outApplication outApplication;
    public Intent intent;
    public String hLatitude, hLongitude;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //액션바 설정
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        intent = new Intent(SplashActivity.this, MainActivity.class);
        outApplication = (outApplication) getApplication();

        ImageView ivSplash = findViewById(R.id.ivSplash);
        ivSplash.setBackgroundResource(R.drawable.splash_animation);

        AnimationDrawable frameAnimation = (AnimationDrawable) ivSplash.getBackground();
        frameAnimation.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLocationPermission();

        //집정보xml 확인
        SharedPreferences sharedPreferences = getSharedPreferences("home", MODE_PRIVATE);
        hLatitude = sharedPreferences.getString("hLatitude","");
        hLongitude = sharedPreferences.getString("hLongitude","");
    }

    //위치정보 권한 확인
    private void checkLocationPermission() {
        //ActivityCompat 클래스에서 checkSelfPermission 메소드를 사용해 권한 확인
        int accessLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        // 결과값 확인
        if (accessLocation == PackageManager.PERMISSION_GRANTED) {
            //권한이 부여되었다면, 다음작업 실행
            checkLocationSetting();
        } else {
            //권한이 없다면, requestPermissions 메소드를 통해 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_UTIL_LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GPS_UTIL_LOCATION_PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i])) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        checkLocationSetting();
                    } else {
                        // 위치정보 권한을 거부했을 때
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                        builder.setTitle("위치 권한이 꺼져있습니다.");
                        builder.setMessage("[권한] 설정에서 위치 권한을 허용해야 합니다.");
                        builder.setPositiveButton("설정으로 가기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }).setNegativeButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        //
                        androidx.appcompat.app.AlertDialog alert = builder.create();
                        alert.show();
                    }
                    break;
                }
            }
        }
    }

    // 안드로이드에 위치 서비스가 활성화 되어있는지 확인
    private void checkLocationSetting() {

        // 얻고 싶은 위치정보 객체 생성
        locationRequest = LocationRequest.create();
        //객체 - 우선순위 :: 전력을 효율적으로 사용하며, 위치 정보 수집 최적화
        locationRequest.setPriority(DEFAULT_LOCATION_REQUEST_PRIORITY);
        //객체 - gps가 바뀔때마다 10초 -20초 사이 갱신 (30초 - 1분 갱신) 유튭16분
        locationRequest.setInterval(DEFAULT_LOCATION_REQUEST_INTERVAL);
        locationRequest.setFastestInterval(DEFAULT_LOCATION_REQUEST_FAST_INTERVAL);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).setAlwaysShow(true);
        settingsClient.checkLocationSettings(builder.build())
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        //구글에서 제공하는 api
                        // 위치정보를 제공하는 client 객체 가져올 수 있음
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SplashActivity.this);
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        //원하는 locationRequest 위치정보를 알고 싶고, 결과에 관해 메소드를 실행
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                        new Handler().postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                //딜레이 후 시작할 코드 작성
                                //xml 확인 후 화면전환
                                if(hLatitude.equals("") && hLongitude.equals("")) {
                                    Toast.makeText(SplashActivity.this, "설정된 집 위치가 없습니다.", Toast.LENGTH_SHORT).show();
                                    intent.putExtra("myHOME", "없음");
                                    startActivity(intent);
                                    finish();
                                }else{
                                    intent.putExtra("myHOME", "있음");
                                    startActivity(intent);
                                    finish();
                                }

                            }
                        }, 3000); //3초

                    }
                })
                .addOnFailureListener(SplashActivity.this, new OnFailureListener() {
                    // 보통 위치기반 서비스가 비활성화 되었을때 실행
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            //오류 종류
                            //환경설정에서 설정했을때( 환경설정에서 위치기반  1.)
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(SplashActivity.this, GPS_UTIL_LOCATION_RESOLUTION_REQUEST_CODE);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.w(TAG, "unable to start resolution for result due to " + sie.getLocalizedMessage());
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //환경설정에서 위치 서비스 활성화
        if (requestCode == GPS_UTIL_LOCATION_RESOLUTION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                checkLocationSetting();
            } else {
                finish();
            }
        }
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            longitude = locationResult.getLastLocation().getLongitude();
            latitude = locationResult.getLastLocation().getLatitude();
            //실시간으로 gps를 받기 위해서는 리스너를 활성화 시키면됨.
            //fusedLocationProviderClient.removeLocationUpdates(locationCallback);

            outApplication.setNowLatitude(latitude);
            outApplication.setNowLongitude(longitude);
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            Log.i(TAG, "onLocationAvailability - " + locationAvailability);
        }
    };



}