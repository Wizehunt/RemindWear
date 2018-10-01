package fr.kriszt.theo.remindwear.workers;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;
import androidx.work.Worker;
import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.RemindNotification;
import fr.kriszt.theo.remindwear.tasker.Category;
import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;

public class ReminderWorker extends Worker {
    public static final String TAG = "REMINDER_WORKER";
    private static final String CATEGORY_NONE_TAG = "AUCUNE";
    private static String workTag = "REMINDER_WORK";
    private static final String TASK_ID_KEY = "UUID";

    @NonNull
    @Override
    public Result doWork() {
        int taskID = this.getInputData().getInt(TASK_ID_KEY, -1);
        Log.w(TAG, "doWork: taskID : " + taskID);


        Task task = Tasker.getInstance(getApplicationContext()).getTaskByID(taskID);

        if (task != null) {
            Log.w(TAG, "doWork: Tâche identifiée : " + task.getName());
            new RemindNotification(task, getApplicationContext()).show(null);


            if (false /* task.isRecurrent() */){
                // TODO : vérifier si replanification nécessaire (tâche récurrente)
            }
            return Result.SUCCESS;

        }else {
            Log.w(TAG, "doWork: identifiant de tâche inconnu : " + taskID);
        }

        return Result.FAILURE;
        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
    }

    /**
     * (Re)Planifie la notification d'une tâche
     * @param task
     */
    public static void scheduleWorker(Task task) {
        Log.w(TAG, "scheduleWorker: Scheduling task " + task.getName());

        if (task.getWorkID() != null){ // La tâche est déjà planifiée, annuler le job pour le reprogrammer derrière
            Log.w(TAG, "Task is already scheduled :  rescheduling");
            WorkManager.getInstance().cancelWorkById(task.getWorkID());
        }

        long remainingSeconds = task.getDuration(TimeUnit.SECONDS) * -1; // dateDiff donne une valeur négative si dans le futur
//        remainingSeconds /= 10; // accélérer les tests
        Log.w(TAG, "scheduleWorker: la tâche "+ task.getName() + " commencera dans " + remainingSeconds + " secondes");

        Data inputData = new Data.Builder().putInt(TASK_ID_KEY, task.getID()).build();
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(remainingSeconds, TimeUnit.SECONDS)
                .addTag(getCategoryTag(task.getCategory())) // catégoriser le work par Category (de la tâche)
                .setInputData(inputData)
                .build();


        WorkManager.getInstance().enqueue(work);
        task.setWorkID(work.getId());
    }

    public static LiveData<WorkStatus> getWorkStatus(Task task){
        UUID workId = task.getWorkID();
        LiveData<WorkStatus> workStatusLiveData = WorkManager.getInstance().getStatusById( workId );
        return workStatusLiveData;
    }

    private static String getCategoryTag(Category category){

        String categoryTag = category == null ? CATEGORY_NONE_TAG : category.getName();
        return workTag + "_" + categoryTag;
    }

    /**
     * Annule toutes les plannifications appartenant à la catégorie donnée
     * @param category
     * @return targetsNumber le nombre d'executions déplanifiées
     */
    public static int unScheduleCategory(@NonNull Category category){
        String categoryTag = getCategoryTag(category);

        int targetsNumber = WorkManager.getInstance().getStatusesByTag(categoryTag).getValue().size();
        WorkManager.getInstance().cancelAllWorkByTag(categoryTag);
        return targetsNumber;
    }

    public static void unScheduleAll(){
        WorkManager.getInstance().cancelAllWork();
    }


}
