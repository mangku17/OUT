package com.homeout.Lock;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.homeout.*;
import com.homeout.SQL.SQLiteHelper;

import java.util.ArrayList;

public class LockActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public MemoAdapter memoAdapter;
    private LinearLayoutManager linearLayoutManager;
    public outApplication outApplication;
    private ArrayList<Memo> arrayList;

    public SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        //액션바 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_lock);

        //SQL 연결
        sqLiteHelper = new SQLiteHelper(this);

        //어플리케이션 정의
        outApplication = (outApplication) getApplication();

        //리사이클러뷰 설정
        recyclerView = (RecyclerView) findViewById(R.id.rcLock);
        linearLayoutManager = new LinearLayoutManager(getApplication());
        recyclerView.setLayoutManager(linearLayoutManager);

        //arrayList = new ArrayList<Memo>();
        arrayList = sqLiteHelper.selectAll();

        memoAdapter = new MemoAdapter(arrayList);
        recyclerView.setAdapter(memoAdapter);

        memoAdapter.notifyDataSetChanged();

    }
}