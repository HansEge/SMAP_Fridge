package smap_f18_24.smap_fridge.Service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import smap_f18_24.smap_fridge.R;


public class ServiceUpdater extends Service {

    //Used for binding service to activity
    private final IBinder mBinder = new ServiceBinder();

    public static final String BROADCAST_UPDATER_RESULT = "smap_f18_24.smap_fridge.Service.BROADCAST_BACKGROUND_SERVICE_RESULT";
    public static final String EXTRA_TASK_RESULT = "task_result";

    //context shit
    Context context;

    public void setContext(Context c)
    {
        context = c;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    notificationBuilder();

                    try {
                        Thread.sleep(2000); //Update stuff every xx ms
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();




        Log.d("NOTI_FROM_SERVICE", "Notification from service");
        //TODO Update stuff

        return START_STICKY;
    }



    //Make notification displaying the time when updating.
    @TargetApi(26)
    void notificationBuilder()
    {
        //For API version < 26
        if (Build.VERSION.SDK_INT < 26) {
            Log.d("API<26","bobby olsen");
            notificationBuilder_PRE26();
            return;
        }

        LocalDateTime time2 = LocalDateTime.now();

        NotificationChannel channel_1 = new NotificationChannel("CHANNEL_1","Fridge Stuff", NotificationManager.IMPORTANCE_HIGH);
        channel_1.setDescription("Notification for alerting user of changes");

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.createNotificationChannel(channel_1);

        Notification updateNotification =
                new Notification.Builder(this,"CHANNEL_1")
                .setContentTitle("Stuff was updated!")
                .setContentText("Mathias lugtede kl " + time2.format(DateTimeFormatter.ISO_LOCAL_TIME))
                .setSmallIcon(R.drawable.stinus_face)
                .build();
        startForeground(123,updateNotification);
    }


    //For API < 26
    void notificationBuilder_PRE26()
    {
        Calendar time = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        Notification updateNotification =
                new NotificationCompat.Builder(this,"Channel_X")
                .setContentTitle("Stuff was Updated")
                .setContentText("Mathias lugtede kl " + sdf.format(time.getTime()))
                .setSmallIcon(R.drawable.stinus_face)
                .build();
        startForeground(123,updateNotification);
    }


    //Not used atm
    public void broadcastResult(String result)
    {
        Intent broadcastIntent = new Intent();

        //TODO what needs to be broadcasted??

        broadcastIntent.setAction(BROADCAST_UPDATER_RESULT);
        broadcastIntent.putExtra(EXTRA_TASK_RESULT,result);

        LocalBroadcastManager BCManager = LocalBroadcastManager.getInstance(context);

        if(BCManager.sendBroadcast(broadcastIntent))
        {
            Log.d("BROADCAST_SEND","Succes on sending broadcast");
        }
    }



    //Used for binding service
    public class ServiceBinder extends Binder {
        public ServiceUpdater getService(){
            return ServiceUpdater.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}

