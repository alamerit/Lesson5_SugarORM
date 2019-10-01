package geekbrains.ru.lesson5_sugarorm;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class RoomModel {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "login")
    private String login;

    @ColumnInfo(name = "userId")
    private String userId;

    @ColumnInfo(name = "avatar_url")
    private String avatarUrl;

    @Expose
    @ColumnInfo(name = "id")
    private String id;


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }



    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}