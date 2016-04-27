package com.wishlist.wishlist;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

public class NotificationService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String token = AuthHelper.getAuthToken(getApplicationContext());
        HttpClient.sendGetRequest("products?token=" + token, new HttpCallback() {
            @Override
            public void success(JSONObject response) {
                List<Product> newProductList = JSONHelper.parseProducts(response);
                int productCount = AuthHelper.getProductCount(getApplicationContext());
                if (newProductList.size() > productCount) {
                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    PendingIntent resultPendingIntent =
                            PendingIntent.getActivity(
                                    getApplicationContext(),
                                    0,
                                    resultIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.logo)
                                    .setContentTitle("New products wanted!")
                                    .setContentText("Your family members have listed new products they want.");

                    mBuilder.setContentIntent(resultPendingIntent);

                    int mNotificationId = 001;
                    NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mNotifyMgr.notify(mNotificationId, mBuilder.build());
                }
                AuthHelper.saveProductCount(newProductList.size(), getApplicationContext());
            }

            @Override
            public void failure(JSONObject response) {}
        });
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
