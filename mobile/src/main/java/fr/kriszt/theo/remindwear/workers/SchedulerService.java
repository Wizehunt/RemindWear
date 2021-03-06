package fr.kriszt.theo.remindwear.workers;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;
import fr.kriszt.theo.shared.Constants;

/**
 * Service de planification : peut être appelé via un Intent
 * Reporte une tâche à plus tard (10 minutes)
 * S'utilise avec l'action "Plus Tard" de la notification (équivalent du 'Snooze' pour un réveil)
 */
public class SchedulerService extends Service {

    public static final String TAG = "SHEDULER_SERVICE";

    public static final int POSTPONE_TIME_MINUTES = Constants.POSTPONE_DELAY;

    public static final String TASK_TAG = "TASK_ID";
    private Tasker tasker;

    @Override
    public void onCreate() {
        super.onCreate();
        tasker = Tasker.getInstance(getApplicationContext());
    }


    /**
     * @param intent dont l'extra contient la tâche sérialisée à planifier
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {

            Bundle extras = intent.getExtras();

            if (extras != null) {
                for (String k : extras.keySet()) {
                    Log.w(TAG, "found key " + k + " = " + extras.get(k));
                }

                Task task = (Task) extras.getSerializable(TASK_TAG);
                assert task != null;
                postponeTask(task);


            }

        }

        return super.onStartCommand(intent, flags, startId);
    }

    @android.support.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Reporte une tâche de POSTPONE_TIME_MINUTES minutes
     *
     * @param task une copie (potentiellement par serialisation) de la tâche a reporter
     *             Gère la persistance côté Tasker
     */
    private void postponeTask(Task task) {

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(task.getID());

        Task alteredTask = tasker.getTaskByID(task.getID()); // get actual tasker's Task, not just a copy

        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.MINUTE, POSTPONE_TIME_MINUTES);
        alteredTask.setTimeHour(calendar.get(Calendar.HOUR_OF_DAY));
        alteredTask.setTimeMinutes(calendar.get(Calendar.MINUTE));
        ReminderWorker.scheduleWorker(alteredTask);
        tasker.serializeLists(); // save the changes

    }
}
