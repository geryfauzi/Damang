package unikom.gery.damang.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import unikom.gery.damang.R;
import unikom.gery.damang.sqlite.ddl.DBHelper;

public class BackupRestoreActivity extends AppCompatActivity implements View.OnClickListener {

    public TaskCompletionSource<DriveId> mOpenItemTaskSource;
    private ImageView btnBack;
    private Button btnMulai;
    private RadioGroup rgPilihan;
    private RadioButton rbPilihan;
    private ProgressDialog alertDialog;

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
        alertDialog = new ProgressDialog(getApplicationContext());
        alertDialog.setTitle("Harap Tunggu...");
        alertDialog.setCancelable(false);
        btnBack.setOnClickListener(this);
        btnMulai.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == btnBack)
            finish();
        else {
            int pilihan = rgPilihan.getCheckedRadioButtonId();
            rbPilihan = findViewById(pilihan);
            if (rbPilihan.getText().equals("Cadangkan")) {

            } else {

            }
        }
    }
}