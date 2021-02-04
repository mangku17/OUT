package com.homeout.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.homeout.Lock.LockActivity;
import com.homeout.Lock.ScreenService;
import com.homeout.R;
import com.homeout.SplashActivity;
import com.homeout.distanceCalculation;
import com.homeout.outApplication;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class homeoutForeGround extends Service {

    public String homeoutChanelId = "homeout";
    public com.homeout.outApplication outApplication;
    public String hLatitude, hLongitude;
    public double xDouble, yDouble;
    public distanceCalculation distance;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //어플리케이션 정의
        outApplication = (outApplication) getApplication();
        distance = new distanceCalculation();

        //집정보xml 확인
        SharedPreferences sharedPreferences = getSharedPreferences("home", MODE_PRIVATE);
        hLatitude = sharedPreferences.getString("hLatitude","");
        hLongitude = sharedPreferences.getString("hLongitude","");

        //집정보위치
        if(!hLatitude.equals("") && !hLongitude.equals("")){
            xDouble = Double.parseDouble(hLatitude);
            yDouble =  Double.parseDouble(hLongitude);
        }

        // 오레오버전 이상에서는 알림창을 띄워야 포그라운드 사용 가능
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, homeoutChanelId)
                .setContentTitle("Home OUT Foreground Service")
                .setContentText("실행중")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(false) //true일 경우 알림 리스트에서 클릭하거나 좌우로 드래그하면 안사라짐.
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        Handler handler = new Handler();
        //핸들러로 전달할 runnalbe 객체, 수신 스레드 실행
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //어플리케이션에서 받아온 위도 경도
                double latitude = outApplication.getNowLatitude();
                double longitude = outApplication.getNowLongitude();

                if(xDouble !=0.0 && yDouble !=0.0){
                    //현위치와 집위치 비교 구문
                    if(distance.latitudeCalculation(latitude,xDouble) && distance.longitudeCalculation(longitude, yDouble)){
                        //집일때
                        Log.i("포그라운드 서비스","집이에용!");
                        Log.i("현위치",""+latitude +", "+ longitude);
                        Log.i("집위치",""+xDouble +", "+ yDouble);
                        double ang = xDouble- 0.00044966051;
                        double ang2 = xDouble+ 0.00044966051;
                        Log.i("사잇값", ""+ang+  "<=" + latitude +"<=" + ang2);

                    }
                    else if (!distance.latitudeCalculation(latitude,xDouble) && !distance.longitudeCalculation(longitude, yDouble)){
                        //밖일때
                        Log.i("포그라운드 서비스","집밖이에용!");
                        Intent lockStart = new Intent(getApplicationContext(), ScreenService.class);
                        startService(lockStart);
                    }
                }else{
                    Log.i("포그라운드 서비스", "집이 설정되어 있지않습니다.");
                }

            }
        };

        //새로운 스레드 실행코드, 시간 단위로 로그확인
        class NewRunnable implements Runnable{
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(2000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    handler.post(runnable);
                }
            }
        }
        NewRunnable newRunnable = new NewRunnable();
        Thread thread = new Thread(newRunnable);
        thread.start();

        // start_sticky : 서비스가 죽어도 시스템에서 다시 재생성.
        // start_not_sticky : 서비스가 죽어도 시스템에서 재생성 하지 않음.
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 채널생성 ( 채팅 채널 )
    // 채널은 목적에 맞게 그룹핑 해준다는 느낌으로 만들어서 사용하면 됨
    private void createNotificationChannel() {
        // 오레오버전 이상일 때 알림 채널이 필요함, 따라서 오레오버전 이상인지 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = homeoutChanelId;
            String description = "아 몰라 좀 되라고......";         // 알림 길게 눌렀을 때 설명 설정
            int importance = NotificationManager.IMPORTANCE_DEFAULT;            // 중요도 설정
            NotificationChannel channel = new NotificationChannel("homeout", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
