package unikom.gery.damang.activities;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import unikom.gery.damang.R;
import unikom.gery.damang.util.SharedPreference;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private CircleImageView imgProfile;
    private EditText etName, etWeight, etHeight;
    private TextView etTanggalLahir, etEmail;
    private CardView btnTanggalLahir, cvEmail;
    private Spinner spinnerKelamin;
    private Button btnSimpan;
    private SharedPreference sharedPreference;
    private ImageView btnBack;
    private ArrayAdapter arrayAdapter;
    private Calendar calendar;
    private String kelamin;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide Action Bar
        this.getSupportActionBar().hide();
        //Change statusbar color
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //
        setContentView(R.layout.activity_profile);
        imgProfile = findViewById(R.id.imgProfile);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etTanggalLahir = findViewById(R.id.etTanggalLahir);
        spinnerKelamin = findViewById(R.id.spinnerKelamin);
        btnTanggalLahir = findViewById(R.id.btnTanggalLahir);
        btnSimpan = findViewById(R.id.btnSimpan);
        btnBack = findViewById(R.id.btnBack);
        cvEmail = findViewById(R.id.cardView10);
        sharedPreference = new SharedPreference(getApplicationContext());
        calendar = Calendar.getInstance();
        //
        btnSimpan.setOnClickListener(this);
        btnTanggalLahir.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        spinnerKelamin.setOnItemSelectedListener(this);
        cvEmail.setOnClickListener(this);
        spinnerInitiation();
        setContentToView();
    }

    private void spinnerInitiation() {
        String[] jenisKelamin = {"Laki - Laki", "Perempuan"};
        arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, jenisKelamin);
        spinnerKelamin.setAdapter(arrayAdapter);
    }

    private void setContentToView() {
        etName.setText(sharedPreference.getUser().getName());
        etEmail.setText(sharedPreference.getUser().getEmail());
        int spinnerPosition = arrayAdapter.getPosition(sharedPreference.getUser().getGender());
        spinnerKelamin.setSelection(spinnerPosition);
        Glide.with(getApplicationContext()).load(sharedPreference.getUser().getPhoto()).into(imgProfile);
        etWeight.setText(String.valueOf(sharedPreference.getUser().getWeight()));
        etHeight.setText(String.valueOf(sharedPreference.getUser().getHeight()));
        etTanggalLahir.setText(sharedPreference.getUser().getDateofBirth());
    }

    private void pickDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                etTanggalLahir.setText(dateFormatter.format(newDate.getTime()));
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    @Override
    public void onClick(View view) {
        if (view == btnSimpan) {

        } else if (view == btnTanggalLahir) {
            pickDate();
        } else if (view == btnBack)
            finish();
        else if (view == cvEmail)
            Toast.makeText(getApplicationContext(), "Email tidak bisa diganti karena terhubung ke akun Google", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        kelamin = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}