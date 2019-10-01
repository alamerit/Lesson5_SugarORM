package geekbrains.ru.lesson5_sugarorm;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RoomModelDao {

    @Query("SELECT * FROM roommodel")
    abstract List<RoomModel> getAll();

    @Query("SELECT * FROM roommodel WHERE userId LIKE :userId LIMIT 1")
    RoomModel findByUserId(String userId);

    @Insert
    void insertAll(List<RoomModel> item);

    @Update
    void update(RoomModel item);

    @Delete
    void delete(RoomModel item);

    @Query("DELETE FROM roommodel")
    void deleteAll();

}