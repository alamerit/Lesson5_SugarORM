package geekbrains.ru.lesson5_sugarorm;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public interface RestAPI {
        @GET("users")
        Single<List<RoomModel>> loadUsers();
    }
    private TextView mInfoTextView;
    private ProgressBar progressBar;
    Button btnLoad;
    Button btnSaveAllSugar;
    Button btnSelectAllSugar;
    Button btnDeleteAllSugar;
    RestAPI restAPI;
    List<RoomModel> modelList = new ArrayList<>();
    Retrofit retrofit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInfoTextView = findViewById(R.id.tvLoad);
        progressBar =  findViewById(R.id.progressBar);
        btnLoad =  findViewById(R.id.btnLoad);
        btnSaveAllSugar =  findViewById(R.id.btnSaveAllSugar);
        btnSelectAllSugar =  findViewById(R.id.btnSelectAllSugar);
        btnDeleteAllSugar =  findViewById(R.id.btnDeleteAllSugar);
        Button btnSaveAllRoom =  findViewById(R.id.btnSaveAllRoom);
        Button btnSelectAllRoom =  findViewById(R.id.btnSelectAllRoom);
        Button btnDeleteAllRoom =  findViewById(R.id.btnDeleteAllRoom);
        btnLoad.setOnClickListener(this);
        btnSaveAllSugar.setOnClickListener(this);
        btnSelectAllSugar.setOnClickListener(this);
        btnDeleteAllSugar.setOnClickListener(this);
        btnSaveAllRoom.setOnClickListener(this);
        btnSelectAllRoom.setOnClickListener(this);
        btnDeleteAllRoom.setOnClickListener(this);
        SugarContext.init(this);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/") // Обратить внимание на слеш в базовом адресе
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        restAPI = retrofit.create(RestAPI.class);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
    }
    private DisposableSingleObserver<Bundle> CreateObserver() {
        return new DisposableSingleObserver<Bundle>() {
            @Override
            protected void onStart() {
                super.onStart();
                progressBar.setVisibility(View.VISIBLE);
                mInfoTextView.setText("");
            }
            @Override
            public void onSuccess(@NonNull Bundle bundle) {
                progressBar.setVisibility(View.GONE);
                mInfoTextView.append("количество = " + bundle.getInt("count") +
                        "\n милисекунд = " + bundle.getLong("msek"));
            }
            @Override
            public void onError(@NonNull Throwable e) {
                progressBar.setVisibility(View.GONE);
                mInfoTextView.setText("ошибка БД: " + e.getMessage());
            }
        };
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoad:
                mInfoTextView.setText("");
                // Подготовили вызов на сервер
                Single<List<RoomModel>> call = restAPI.loadUsers().
                        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
                if (checkConnection()) return;
                // Запускаем
                progressBar.setVisibility(View.VISIBLE);
                downloadOneUrl(call);
                break;
            case R.id.btnSaveAllSugar:
                Single<Bundle> singleSaveAll = Single.create(new SingleOnSubscribe<Bundle>() {
                    @Override
                    public void subscribe(@NonNull SingleEmitter<Bundle> emitter) throws Exception {
                        try {
                            String curLogin = "";
                            String curUserID = "";
                            String curAvatarUrl = "";
                            Date first = new Date();
                            for (RoomModel curItem : modelList) {
                                curLogin = curItem.getLogin();
                                curUserID = curItem.getId();
                                curAvatarUrl = curItem.getAvatarUrl();
                                SugarModel sugarModel = new SugarModel(curLogin, curUserID, curAvatarUrl);
                                sugarModel.save();
                            }
                            Date second = new Date();
                            List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("count", tempList.size());
                            bundle.putLong("msek", second.getTime() - first.getTime());
                            emitter.onSuccess(bundle);
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleSaveAll.subscribeWith(CreateObserver());
                break;
            case R.id.btnSelectAllSugar:
                Single<Bundle> singleSelectAll = Single.create(new SingleOnSubscribe<Bundle>() {
                    @Override
                    public void subscribe(@NonNull SingleEmitter<Bundle> emitter) throws Exception {
                        try {
                            Date first = new Date();
                            List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
                            Date second = new Date();
                            Bundle bundle = new Bundle();
                            bundle.putInt("count", tempList.size());
                            bundle.putLong("msek", second.getTime() - first.getTime());
                            emitter.onSuccess(bundle);
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleSelectAll.subscribeWith(CreateObserver());
                break;
            case R.id.btnDeleteAllSugar:
                Single<Bundle> singleDeleteAll = Single.create(new SingleOnSubscribe<Bundle>() {
                    @Override
                    public void subscribe(@NonNull SingleEmitter<Bundle> emitter) throws Exception {
                        try {
                            List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
                            Date first = new Date();
                            SugarModel.deleteAll(SugarModel.class);
                            Date second = new Date();
                            Bundle bundle = new Bundle();
                            bundle.putInt("count", tempList.size());
                            bundle.putLong("msek", second.getTime() - first.getTime());
                            emitter.onSuccess(bundle);
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleDeleteAll.subscribeWith(CreateObserver());
                break;
            case R.id.btnSaveAllRoom:
                Single<Bundle> singleSaveAllRoom = Single.create(new SingleOnSubscribe<Bundle>() {
                    @Override
                    public void subscribe(@NonNull SingleEmitter<Bundle> emitter) throws Exception {
                        String curLogin = "";
                        String curUserID = "";
                        String curAvatarUrl = "";
                        Date first = new Date();
                        List<RoomModel> roomModelList = new ArrayList<>();
                        RoomModel roomModel = new RoomModel();
                        for (RoomModel curItem : modelList) {
                            curLogin = curItem.getLogin();
                            curUserID = curItem.getId();
                            curAvatarUrl = curItem.getAvatarUrl();
                            roomModel.setLogin(curLogin);
                            roomModel.setAvatarUrl(curAvatarUrl);
                            roomModel.setUserId(curUserID);
                            roomModelList.add(roomModel);

                            OrmApp.get().getDB().productDao().insertAll(roomModelList);
                        }
                        Date second = new Date();
                        Bundle bundle = new Bundle();
                        List<RoomModel> tempList = OrmApp.get().getDB().productDao().getAll();
                        bundle.putInt("count", tempList.size());
                        bundle.putLong("msek", second.getTime() - first.getTime());
                        emitter.onSuccess(bundle);
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleSaveAllRoom.subscribeWith(CreateObserver());
                break;
            case R.id.btnSelectAllRoom:
                Single<Bundle> singleSelectAllRoom = Single.create(new SingleOnSubscribe<Bundle>() {
                    @Override
                    public void subscribe(@NonNull SingleEmitter<Bundle> emitter) throws Exception {
                        try {
                            Date first = new Date();
                            List<RoomModel> products = OrmApp.get().getDB().productDao().getAll();
                            Date second = new Date();
                            Bundle bundle = new Bundle();
                            bundle.putInt("count", products.size());
                            bundle.putLong("msek", second.getTime() - first.getTime());
                            emitter.onSuccess(bundle);
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleSelectAllRoom.subscribeWith(CreateObserver());
                break;
            case R.id.btnDeleteAllRoom:
                Single<Bundle> singleDeleteAllRoom = Single.create(new SingleOnSubscribe<Bundle>() {
                    @Override
                    public void subscribe(@NonNull SingleEmitter<Bundle> emitter) throws Exception {
                        try {
                            List<RoomModel> products = OrmApp.get().getDB().productDao().getAll();
                            Date first = new Date();
                            OrmApp.get().getDB().productDao().deleteAll();
                            Date second = new Date();
                            Bundle bundle = new Bundle();
                            bundle.putInt("count", products.size());
                            bundle.putLong("msek", second.getTime() - first.getTime());
                            emitter.onSuccess(bundle);
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleDeleteAllRoom.subscribeWith(CreateObserver());
                break;
        }
    }

    private boolean checkConnection() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isConnected()) {
            Toast.makeText(this, R.string.getInternet, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void downloadOneUrl(Single<List<RoomModel>> call) {
        call.subscribe(new DisposableSingleObserver<List<RoomModel>>() {
            @Override
            protected void onStart() {
                super.onStart();
                progressBar.setVisibility(View.VISIBLE);
                mInfoTextView.setText("");
            }

            @Override
            public void onSuccess(List<RoomModel> retrofitModels) {
                mInfoTextView.append(getString(R.string.size) + retrofitModels.size()+
                        "\n-----------------");
                for (RoomModel curModel : retrofitModels) {
                    modelList.add(curModel);
                    mInfoTextView.append(
                            "\nLogin = " + curModel.getLogin() +
                                    "\nId = " + curModel.getId() +
                                    "\nURI = " + curModel.getAvatarUrl() +
                                    "\n-----------------");
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
                System.out.println(e.getMessage());
                mInfoTextView.setText(getString(R.string.onFailure ) + e.getMessage());
                System.out.println(e.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
