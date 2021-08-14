package unikom.gery.damang.activities;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.drive.DriveId;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import unikom.gery.damang.R;
import unikom.gery.damang.api.WebService;
import unikom.gery.damang.response.Backup;
import unikom.gery.damang.sqlite.ddl.DBHelper;
import unikom.gery.damang.sqlite.dml.HeartRateHelper;
import unikom.gery.damang.util.SharedPreference;

public class BackupRestoreActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String baseUrl = "https://sohibultech.com/damang/";
    public TaskCompletionSource<DriveId> mOpenItemTaskSource;
    private ImageView btnBack;
    private Button btnMulai;
    private RadioGroup rgPilihan;
    private RadioButton rbPilihan;
    private ProgressDialog alertDialog;
    private HeartRateHelper heartRateHelper;
    private DBHelper dbHelper;
    private String sdPath;
    private String id;
    private SharedPreference sharedPreference;
    private ProgressDialog progressDialog;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                File internal = new File(Environment.DIRECTORY_DOWNLOADS);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    internal = getApplicationContext().getObbDir();
                    String obb = internal.toString().replace("obb/unikom.gery.damang", "data/unikom.gery.damang/files/Download");
                    internal = new File(obb);
                }

                if (internal.canRead()) {
                    File currentDB = new File("/data/data/" + getPackageName() + "/databases/", DBHelper.DATABASE_NAME);
                    File backupDB = new File(internal, sharedPreference.getUser().getEmail() + ".db");

                    if (backupDB.exists()) {
                        FileChannel src = new FileInputStream(backupDB).getChannel();
                        FileChannel dst = new FileOutputStream(currentDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Sukses melakukan restore ! Harap tutup, dan buka kembali aplikasi anda!", Toast.LENGTH_SHORT).show();
                        backupDB.delete();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error, file not exist!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error, cannot read the directory!", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception error) {
                Toast.makeText(getApplicationContext(), "Error : " + error.toString(), Toast.LENGTH_LONG).show();
                Log.d("Tag", "Error : " + error.toString());
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Change statusbar color
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().hide();
        //
        setContentView(R.layout.activity_backup_restore);
        btnBack = findViewById(R.id.btnBack);
        btnMulai = findViewById(R.id.btnMulai);
        rgPilihan = findViewById(R.id.radioGroup);
        progressDialog = new ProgressDialog(BackupRestoreActivity.this);
        progressDialog.setTitle("Harap Tunggu...");
        progressDialog.setCancelable(false);
        sharedPreference = new SharedPreference(getApplicationContext());
        heartRateHelper = HeartRateHelper.getInstance(getApplicationContext());
        dbHelper = new DBHelper(getApplicationContext());
        alertDialog = new ProgressDialog(getApplicationContext());
        alertDialog.setTitle("Harap Tunggu...");
        btnBack.setOnClickListener(this);
        btnMulai.setOnClickListener(this);
        //
        registerReceiver(mReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private String getTodayDate() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(System.currentTimeMillis()));
    }

    private boolean isSDPresent(Context context) {
        File[] storage = ContextCompat.getExternalFilesDirs(context, null);
        if (storage.length > 1 && storage[0] != null && storage[1] != null) {
            sdPath = storage[1].toString();
            return true;
        } else
            return false;
    }

    private void uploadToClound(Uri fileUri) {
        File file = new File(fileUri.getPath());

        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        WebService webService = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build().create(WebService.class);
        Call<Backup> response = webService.backupCloud(fileToUpload, filename, sharedPreference.getUser().getEmail(), getTodayDate());
        response.enqueue(new Callback<Backup>() {
            @Override
            public void onResponse(Call<Backup> call, Response<Backup> response) {
                progressDialog.dismiss();
                String pesan = response.body().getMessage();
                Toast.makeText(getApplicationContext(), pesan, Toast.LENGTH_SHORT).show();
                file.delete();
            }

            @Override
            public void onFailure(Call<Backup> call, Throwable t) {
                progressDialog.dismiss();
                file.delete();
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan saat melakukan backup!", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void restore() {
//
//    }

    private void restore() {
        WebService webService = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build().create(WebService.class);
        Call<Backup> response = webService.restoreCloud(sharedPreference.getUser().getEmail());
        response.enqueue(new Callback<Backup>() {
            @Override
            public void onResponse(Call<Backup> call, Response<Backup> response) {
                if (response.body().getCode() > 0) {
                    try {
                        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(response.body().getPath());
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, sharedPreference.getUser().getEmail() + ".db");
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                        request.setTitle(sharedPreference.getUser().getEmail() + ".db");
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                        Long downloadReference = manager.enqueue(request);
                    } catch (Exception error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), " Error : " + error.toString(), Toast.LENGTH_SHORT).show();
                    }//endcatch
                } else
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Backup> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void backup() {
        try {
            if (!isSDPresent(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "Error, SD Card tidak ditemukan!", Toast.LENGTH_SHORT).show();
                return;
            }

            File sd = new File(sdPath);
            File internal = getApplicationContext().getObbDir();
            if (internal.canWrite()) {
                File currentDB = new File("/data/data/" + getPackageName() + "/databases/", DBHelper.DATABASE_NAME);
                File backupDB = new File(internal, sharedPreference.getUser().getEmail() + ".db");

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    //Method Upload
                    Uri localDB = Uri.fromFile(backupDB);
                    Toast.makeText(getApplicationContext(), backupDB.toString(), Toast.LENGTH_SHORT).show();
                    uploadToClound(localDB);
                } else
                    Toast.makeText(getApplicationContext(), "Database tidak ditemukan!", Toast.LENGTH_SHORT).show();

            } else
                Toast.makeText(getApplicationContext(), "Error, tidak bisa write SD Card!", Toast.LENGTH_SHORT).show();

        } catch (Exception error) {
            Toast.makeText(getApplicationContext(), "Error : " + error.toString(), Toast.LENGTH_LONG).show();
            Log.d("Tag", "Error : " + error.toString());
        }
        //
    }

    private String generateRandomString() {
        String charId = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder newID = new StringBuilder();
        Random random = new Random();
        while (newID.length() < 10) {
            int index = (int) (random.nextFloat() * charId.length());
            newID.append(charId.charAt(index));
        }
        id = newID.toString();
        return newID.toString();
    }


    @Override
    public void onClick(View view) {
        if (view == btnBack)
            finish();
        else {
            int pilihan = rgPilihan.getCheckedRadioButtonId();
            rbPilihan = findViewById(pilihan);
            if (rbPilihan.getText().equals("Cadangkan")) {
                progressDialog.show();
                Toast.makeText(getApplicationContext(), "Sedang melakukan backup", Toast.LENGTH_SHORT).show();
                backup();
            } else {
                progressDialog.show();
                Toast.makeText(getApplicationContext(), "Sedang melakukan restore", Toast.LENGTH_SHORT).show();
                restore();
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

}