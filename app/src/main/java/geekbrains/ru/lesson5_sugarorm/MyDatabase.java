package geekbrains.ru.lesson5_sugarorm;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RoomModel.class}, version = 1, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    public abstract RoomModelDao productDao();
}
