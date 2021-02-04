package com.homeout.ui.home;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.homeout.*;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    public TextView tvHome, tvAddress;
    public Button btnHome;
    public String homeAddress;
    public ImageView ivHome;
    public outApplication outApplication;
    public TMapView tMapView;
    public double x, y;


    private final String APK ="l7xxfa281c47f54b4b8d866946553f981932";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        tvHome = root.findViewById(R.id.tvHome);
        tvAddress = root.findViewById(R.id.tvAddress);
        btnHome = root.findViewById(R.id.btnHome);
        ivHome = root.findViewById(R.id.ivWhereHome);
        ivHome.setBackgroundResource(R.drawable.wherehome_animation);

        LinearLayout linearLayoutTmap = (LinearLayout)root.findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(getActivity());

        tMapView.setSKTMapApiKey(APK);
        linearLayoutTmap.addView( tMapView );

        outApplication = (outApplication) ((MainActivity)getContext()).getApplication();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        //어플리케이션에서 받아온 위도 경도
        double latitude = outApplication.getNowLatitude();
        double longitude = outApplication.getNowLongitude();

        //xml파일
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("home", MODE_PRIVATE);
        String hLatitude = sharedPreferences.getString("hLatitude","");
        String hLongitude = sharedPreferences.getString("hLongitude","");

        TMapMarkerItem markerItem1 = new TMapMarkerItem();

        if(hLatitude.equals("") && hLongitude.equals("")){

            AnimationDrawable frameAnimation = (AnimationDrawable) ivHome.getBackground();
            frameAnimation.start();

            //현위치로 지도 중심변경
            tMapView.setCenterPoint(longitude,latitude,true);
            tMapView.setZoomLevel(20);

            TMapPoint tMapPoint1 = new TMapPoint(latitude, longitude); //현재 위치

            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.delgray);

            markerItem1.setIcon(bitmap); // 마커 아이콘 지정
            markerItem1.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
            markerItem1.setTMapPoint( tMapPoint1 ); // 마커의 좌표 지정
            tMapView.addMarkerItem("markerItem1", markerItem1); // 지도에 마커 추가

            tvHome.setText("집의 위치를 설정하세요. ");
            GpsToAddress(latitude, longitude);
        }else{
            ivHome.setBackgroundResource(R.drawable.myhome);
            btnHome.setVisibility(getView().GONE);

            //현위치로 지도 중심변경
            tMapView.setCenterPoint(Double.parseDouble(hLongitude), Double.parseDouble(hLatitude),true);
            tMapView.setZoomLevel(20);

            TMapPoint tMapPoint1 = new TMapPoint(Double.parseDouble(hLatitude), Double.parseDouble(hLongitude)); //xml에 있는 집위치

            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.delgray);

            markerItem1.setIcon(bitmap); // 마커 아이콘 지정
            markerItem1.setPosition(0.5f, 1.0f);
            markerItem1.setTMapPoint( tMapPoint1 ); // 마커의 좌표 지정
            tMapView.addMarkerItem("markerItem1", markerItem1); // 지도에 마커 추가

            tvHome.setText("집이 설정되어 있습니다.");
        }

        tMapView.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
            @Override
            public void onLongPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint) {
                 x = tMapPoint.getLatitude();
                 y = tMapPoint.getLongitude();

                TMapMarkerItem home = new TMapMarkerItem();
                TMapPoint tMapPoint2 = new TMapPoint(x, y); //선택한 집위치

                Bitmap bitmap2 = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.delgray);
                markerItem1.setIcon(bitmap2);
                markerItem1.setPosition(0.5f, 1.0f);
                markerItem1.setTMapPoint(tMapPoint2);
                tMapView.addMarkerItem("home", home);

            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(outApplication, ""+homeAddress, Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("OUT").setMessage(" ' "+homeAddress +" ' 가 맞습니까?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("home",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String hLatitude, hLongitude;
                        if(x !=0.0 && y != 0.0){
                            //수동으로 선택한 집위치
                            hLatitude = Double.toString(x);
                            hLongitude = Double.toString(y);
                        }else{
                            //자동으로 선택된 집위치
                            hLatitude = Double.toString(latitude);
                            hLongitude = Double.toString(longitude);
                        }
                        editor.putString("hLatitude",hLatitude);
                        editor.putString("hLongitude",hLongitude);

                        editor.commit();

                        Toast.makeText(outApplication, "성공적으로 설정되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Toast.makeText(outApplication, "새로 고침 후 다시 설정해주세요.", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    //위도, 경도로 주소 출력하는 메소드
    public void GpsToAddress(double Latitude, double Longitude){
        //좌표를 주소 지명으로 변환 getFromLocaiton
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(Latitude, Longitude, 10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("test", "입출력 오류");
        }
        if (list != null) {
            if (list.size() == 0) {
                Log.i("test", "주소찾기 오류");
            } else {
                Log.i("test", "현위치를 찾았습니다");
                //adress 객체
                Address address = list.get(0);

                Log.i("list", "" + list.get(0));
                Log.i("주소", "" + address.getAddressLine(0));
                tvAddress.setText(""+address.getAddressLine(0));
                homeAddress = address.getAddressLine(0);
            }
        }
    }

}