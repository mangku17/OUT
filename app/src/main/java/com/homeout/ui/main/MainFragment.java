package com.homeout.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.homeout.*;
import com.homeout.SQL.SQLiteHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment {

    public String splashMessage;
    public outApplication outApplication;
    public TextView tvMessage, tvGps;
    public Button btnAdd, btnDel;
    public ImageView ivHomeState;
    public RecyclerView recyclerView;
    public MemoAdapter memoAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Memo> arrayList;

    public SQLiteHelper sqLiteHelper;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        tvGps = root.findViewById(R.id.tvGps);
        tvMessage = root.findViewById(R.id.tvMessage);
        btnAdd = root.findViewById(R.id.btnAdd);
        btnDel = root.findViewById(R.id.btnDel);
        btnAdd.setBackgroundResource(R.drawable.addgray);
        btnDel.setBackgroundResource(R.drawable.delgray);
        ivHomeState = root.findViewById(R.id.ivHomeState);

        //SQL 연결
        sqLiteHelper = new SQLiteHelper(getContext());

        //리사이클러뷰 설정
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존 성능 강화
        recyclerView.setLayoutManager(linearLayoutManager);

        //arrayList = new ArrayList<Memo>();
        arrayList = sqLiteHelper.selectAll();

        memoAdapter = new MemoAdapter(arrayList);
        recyclerView.setAdapter(memoAdapter);

        //Splash에서 받아온 집정보
        Intent intent = getActivity().getIntent();
        splashMessage = intent.getStringExtra("myHOME");
        
        //어플리케이션 정의
        outApplication = (outApplication) ((MainActivity)getContext()).getApplication();

        //추가버튼 이벤트
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoAdapter.notifyDataSetChanged();
                CustomDialog customDialog = new CustomDialog(getActivity());
                customDialog.runDialog(arrayList);
                memoAdapter.notifyDataSetChanged();
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("home", MODE_PRIVATE);
        String hLatitude = sharedPreferences.getString("hLatitude","");
        String hLongitude = sharedPreferences.getString("hLongitude","");

        //어플리케이션에서 받아온 위도 경도
        double latitude = outApplication.getNowLatitude();
        double longitude = outApplication.getNowLongitude();
        String state = outApplication.getHomeState();

        //집유무와 집주소 출력
        if(splashMessage.equals("없음")){
            ivHomeState.setImageResource(R.drawable.nohomecut);
            tvMessage.setText("설정된 집의 위치가 없습니다. [메뉴] > [집정보] 에서 추가해주세요.");
            tvGps.setText("위도 " + latitude + ",  경도 " + longitude);

        }else {
            tvGps.setText("위도 " + latitude + ",  경도 " + longitude);
            ivHomeState.setImageResource(R.drawable.homeoutcut);
            tvMessage.setText("외출 중. 잠금화면이 실행됩니다.");

            //좌표를 주소 지명으로 변환 getFromLocaiton
            Geocoder geocoder = new Geocoder(getContext());
            List<Address> list = null;
            try {
                list = geocoder.getFromLocation(latitude, longitude, 10);
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
                }
            }
        }



        //삭제버튼 이벤트
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}