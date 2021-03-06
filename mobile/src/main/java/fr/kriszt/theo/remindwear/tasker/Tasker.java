package fr.kriszt.theo.remindwear.tasker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Locale;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.ui.fragments.SportTaskListFragment;
import fr.kriszt.theo.remindwear.workers.ReminderWorker;

public class Tasker {

    private static final String TAG = Tasker.class.getSimpleName();
    public static final String CATEGORY_NONE_TAG = "Aucune";
    public static final String CATEGORY_SPORT_TAG = "Sport";

    @SuppressLint("StaticFieldLeak")
    private static Tasker INSTANCE = null;

    private ArrayList<Category> listCategories = new ArrayList<>();
    private ArrayList<Task> listTasks = new ArrayList<>();
    private ArrayList<SportTask> listSportTasks = new ArrayList<>();
    private Context context;
    private SportTaskListFragment observer = null;

    public static synchronized Tasker getInstance(@Nullable Context context) {
        if (INSTANCE == null) {
            INSTANCE = new Tasker(context);
        }
        return INSTANCE;
    }

    public Tasker(Context context) {
        this.context = context;
        unserializeLists();

        if (getCategoryByName(CATEGORY_NONE_TAG) == null) {
            addCategory(new Category(CATEGORY_NONE_TAG, R.drawable.ic_base_0, 0));
        }
        if (getCategoryByName(CATEGORY_SPORT_TAG) == null) {
            addCategory(new Category(CATEGORY_SPORT_TAG, R.drawable.baseline_directions_run_24, 0));
        }
        serializeLists();
    }

    public ArrayList<Task> getListTasks() {
        return listTasks;
    }

    public void removeTaskByID(int id) {
        int temp = -1;
        for (int i = 0; i < listTasks.size(); i++) {
            if (listTasks.get(i).getID() == id) {
                temp = i;
                break;
            }
        }
        if (temp != -1) {
            listTasks.remove(temp);
        }
    }

    public Boolean addTask(Task t) {

        for (Task x : listTasks) {
            if (x.toString().equals(t.toString())) {
                return false;
            }
        }
        listTasks.add(t);
        ReminderWorker.scheduleWorker(t);
        return true;
    }

    public ArrayList<SportTask> getListSportTasks() {

        return listSportTasks;
    }

    public void removeSportTask(SportTask t) {
        listSportTasks.remove(t);
    }

    public Boolean addSportTask(SportTask t) {
        Log.w(TAG, "addSportTask: Adding Sport Task " + t);
        for (SportTask x : listSportTasks) {
            Log.w(TAG, "addSportTask: comparing " + x.getDataset() + " AND " + t.getDataset());
            if (x.getDataset() != null && x.getDataset().equals(t.getDataset())) {
                Log.w(TAG, "addSportTask: SportTask already exists, skipping...");
                return false;
            }
        }
        listSportTasks.add(t);
        return true;
    }

    public void editCategoryById(int id, Category c) {
        Category cat = getCategoryByID(id);
        cat.setColor(c.getColor());
        cat.setIcon(c.getIcon());
        cat.setName(c.getName());
    }

    public ArrayList<Category> getListCategories() {
        return listCategories;
    }

    public Boolean addCategory(Category c) {
        for (Category x : listCategories) {
            if (x.toString().equals(c.toString())) {
                return false;
            }
        }
        listCategories.add(c);
        return true;
    }

    public void changeWithSaveIsActivatedNotification(Task t) {
        t.setIsActivatedNotification(!t.getIsActivatedNotification());
        ReminderWorker.scheduleWorker(t);
        serializeLists();
    }

    public void serializeLists() {
        serializeList(listCategories, "Category.txt");
        serializeList(listTasks, "Task.txt");
        serializeList(listSportTasks, "SportTask.txt");

        if (observer != null) {
            observer.updateRecyclerView();
        }

    }

    @SuppressWarnings("unchecked")
    private void serializeList(ArrayList<?> list, String name) {
        try {
            FileOutputStream fos = context.openFileOutput(name, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(list);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unserializeLists() {
        unserializeListCategories();
        unserializeListTasks();
        unserializeListSportTasks();
    }

    @SuppressWarnings("unchecked")
    private void unserializeListCategories() {
        ArrayList<Category> list = new ArrayList<>();

        try {
            FileInputStream fis = context.openFileInput("Category.txt");
            ObjectInputStream is = new ObjectInputStream(fis);
            list = (ArrayList<Category>) is.readObject();
            is.close();
            fis.close();

        } catch (Exception e) {
            context.fileList();
            serializeLists();
            try {
                FileInputStream fis = context.openFileInput("Category.txt");
                ObjectInputStream is = new ObjectInputStream(fis);
                list = (ArrayList<Category>) is.readObject();
                is.close();
                fis.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            e.printStackTrace();
        }
        listCategories = list;
    }

    @SuppressWarnings("unchecked")
    private void unserializeListTasks() {
        ArrayList<Task> list = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput("Task.txt");
            ObjectInputStream is = new ObjectInputStream(fis);
            list = (ArrayList<Task>) is.readObject();
            is.close();
            fis.close();

        } catch (Exception e) {
            serializeLists();
            try {
                FileInputStream fis = context.openFileInput("Task.txt");
                ObjectInputStream is = new ObjectInputStream(fis);
                list = (ArrayList<Task>) is.readObject();
                is.close();
                fis.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            e.printStackTrace();
        }
        listTasks = list;
    }

    @SuppressWarnings("unchecked")
    private void unserializeListSportTasks() {
        ArrayList<SportTask> list = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput("SportTask.txt");
            ObjectInputStream is = new ObjectInputStream(fis);
            list = (ArrayList<SportTask>) is.readObject();
            is.close();
            fis.close();

        } catch (Exception e) {
            serializeLists();
            try {
                FileInputStream fis = context.openFileInput("SportTask.txt");
                ObjectInputStream is = new ObjectInputStream(fis);
                list = (ArrayList<SportTask>) is.readObject();
                is.close();
                fis.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            e.printStackTrace();
        }
        listSportTasks = list;
    }

    /**
     * Retire les tâches passées, car elles deviennet superflues
     */
    public void garbageCollectOld() {
        unserializeLists();
        ArrayList<Integer> deletes = new ArrayList<>();
        Calendar now = new GregorianCalendar();
        for (int i = 0; i < listTasks.size(); i++) {
            if (listTasks.get(i).getDateDeb() != null && listTasks.get(i).getNextDate().compareTo(now) < 0) {
                deletes.add(listTasks.get(i).getID());
            }
        }
        for (Integer i : deletes) {
            removeTaskByID(i);
        }

        ArrayList<SportTask> stDeletes = new ArrayList<>();
        for (SportTask st : listSportTasks) {
            if (st == null || st.getDataset() == null) {
                stDeletes.add(st);
            }
        }

        for (SportTask st : stDeletes) {
            removeSportTask(st);
        }

        serializeLists();
    }

    public void sort(final Boolean growing) {
        unserializeLists();
        Collections.sort(listTasks, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                int res = 1;
                if (growing) {
                    res *= -1;
                }
                Calendar o1deb = o1.getNextDate();
                Calendar o2deb = o2.getNextDate();

                if (o1deb.after(o2deb)) {
                    return res * -1;
                }

                if (o1deb.before(o2deb)) {
                    return res;
                }
                return 0;
            }
        });
        serializeLists();
    }

    public void sportSort(final Boolean growing) {
        unserializeLists();
        Collections.sort(listSportTasks, new Comparator<SportTask>() {
            @Override
            public int compare(SportTask o1, SportTask o2) {
                int res = 1;
                if (growing) {
                    res *= -1;
                }
                Calendar o1deb = o1.getFirstDate();
                Calendar o2deb = o2.getFirstDate();
                if (o1deb == null || o2deb == null) return 0;

                if (o1deb.after(o2deb)) {
                    return res * -1;
                }

                if (o1deb.before(o2deb)) {
                    return res;
                }
                return 0;
            }
        });
        serializeLists();
    }

    public ArrayList<Task> filter(String seq, Boolean growing) {
        ArrayList<Task> res = new ArrayList<>();
        sort(growing);
        seq = seq.toUpperCase();
        for (Task t : listTasks) {
            SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
            String dateFormated = format.format(t.getNextDate().getTime());
            if (t.getName().toUpperCase().contains(seq)) {
                res.add(t);
            } else if (t.getCategory().getName().toUpperCase().contains(seq)) {
                res.add(t);
            } else if (t.getDescription().toUpperCase().contains(seq)) {
                res.add(t);
            } else if (dateFormated.toUpperCase().contains(seq)) {
                res.add(t);
            }
        }
        return res;
    }

    public ArrayList<SportTask> sportFilter(String seq, Boolean growing) {
        ArrayList<SportTask> res = new ArrayList<>();
        sort(growing);
        seq = seq.toUpperCase();
        for (SportTask t : listSportTasks) {
            SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
            String dateFormated = format.format(t.getFirstDate().getTime());

            if (t.getName() != null && t.getName().toUpperCase().contains(seq)) {
                res.add(t);
            } else if (t.getCategory() != null && t.getCategory().getName().toUpperCase().contains(seq)) {
                res.add(t);
            } else if (t.getDescription() != null && t.getDescription().toUpperCase().contains(seq)) {
                res.add(t);
            } else if (dateFormated.toUpperCase().contains(seq)) {
                res.add(t);
            }
        }
        return res;
    }

    public Task getTaskByID(int id) {
        for (Task t : listTasks) {
            if (t.getID() == id) {
                return t;
            }
        }
        return null;
    }

    public SportTask getSportTaskByID(Integer taskId) {

        for (SportTask t : listSportTasks) {
            if (t != null && t.getID() != null && t.getID().equals(taskId)) {
                return t;
            }
        }
        return null;
    }


    public Category getCategoryByID(int id) {
        for (Category c : listCategories) {
            if (c.getID() == id) {
                return c;
            }
        }
        return null;
    }

    /**
     * Cherche une catégorie pas son nom
     * Cherche d'abord en strict, puis essaye en ignorant la casse et en matchant singulier/pluriel
     * @param catName le nom de la catégorie à réchercher
     * @return la catégorie correspondate ou null si non trouvée
     */
    public Category getCategoryByName(String catName) {
        if (catName == null) {
            return null;
        } else {
            catName = catName.toLowerCase();
        }

        for (Category c : listCategories) {
            String name = c.getName().toLowerCase();

            if (name.endsWith("s")) {
                name = name.substring(0, name.length() - 1);
            }

            if (catName.endsWith("s")) {
                catName = catName.substring(0, catName.length() - 1);
            }

            if (name.equals(catName)) return c;

        }
        return null;
    }

    public void addObserver(SportTaskListFragment sportTaskListFragment) {
        this.observer = sportTaskListFragment;
    }

}
