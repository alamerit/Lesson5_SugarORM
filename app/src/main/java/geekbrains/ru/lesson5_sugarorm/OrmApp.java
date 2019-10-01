package geekbrains.ru.lesson5_sugarorm;

import android.app.Application;

import androidx.room.Room;

public class OrmApp extends Application {

    private static final String DATABASE_NAME = "DATABASE_USER_GIT2";
    public static MyDatabase database;
    public static OrmApp INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
//        SugarContext.init(this);

        database = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, DATABASE_NAME).build();
        INSTANCE = this;
    }

    public MyDatabase getDB() {
        return database;
    }

    public static OrmApp get() {
        return INSTANCE;
    }
}