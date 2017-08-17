package com.atsoft.dhinamorukural;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PopUpService extends Service {
    public PopUpService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
