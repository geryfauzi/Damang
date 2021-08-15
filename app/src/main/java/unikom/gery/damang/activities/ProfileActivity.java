package unikom.gery.damang.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import unikom.gery.damang.R;
import unikom.gery.damang.api.WebService;
import unikom.gery.damang.model.User;
import unikom.gery.damang.response.CheckUser;
import unikom.gery.damang.sqlite.dml.HeartRateHelper;
import unikom.gery.damang.util.SharedPreference;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String baseUrl = "https://sohibultech.com/damang/";
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
    private String kelamin = "";
    private ProgressDialog progressDialog;
    private HeartRateHelper heartRateHelper;

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
        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Harap tunggu...");
        heartRateHelper = HeartRateHelper.getInstance(getApplicationContext());
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
        kelamin = sharedPreference.getUser().getGender();
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

    private void updateData() {
        String oldName = sharedPreference.getUser().getName();
        String oldDateOfBirth = sharedPreference.getUser().getDateofBirth();
        String oldGender = sharedPreference.getUser().getGender();
        float oldWeight = sharedPreference.getUser().getWeight();
        float oldHeight = sharedPreference.getUser().getHeight();

        String newName = etName.getText().toString();
        String newDateOfBirth = etTanggalLahir.getText().toString();
        String newGender = kelamin;
        float newWeight = Float.parseFloat(etWeight.getText().toString());
        float newHeight = Float.parseFloat(etHeight.getText().toString());

        if ((oldName.equals(newName)) && (oldDateOfBirth.equals(newDateOfBirth))
                && (oldGender.equals(newGender)) && (oldHeight == newHeight) && (oldWeight == newWeight)) {
            Toast.makeText(getApplicationContext(), "Anda tidak melakukan perubahan data apapun",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        WebService webService = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create())
                .build().create(WebService.class);
        Call<CheckUser> response = webService.updateData(sharedPreference.getUser().getEmail(),
                newName, newDateOfBirth, newGender, newWeight, newHeight);
        progressDialog.show();
        response.enqueue(new Callback<CheckUser>() {
            @Override
            public void onResponse(Call<CheckUser> call, Response<CheckUser> response) {
                progressDialog.dismiss();
                if (response.code() > 0) {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    sharedPreference.setUser(getUserData());
                }else
                    Toast.makeText(getApplicationContext(),response.body().getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<CheckUser> call, Throwable t) {
                progressDialog.dismiss();
            }
        });

    }

    private User getUserData() {
        User user = new User();
        user.setName(etName.getText().toString().trim());
        user.setEmail(etEmail.getText().toString().trim());
        user.setGender(kelamin);
        user.setPhoto(sharedPreference.getUser().getPhoto());
        user.setWeight(Float.parseFloat(etWeight.getText().toString().trim()));
        user.setHeight(Float.parseFloat(etHeight.getText().toString().trim()));
        user.setDateofBirth(etTanggalLahir.getText().toString().trim());
        return user;
    }

    private boolean checkInputForm() {
        boolean status = false;
        if (TextUtils.isEmpty(etEmail.getText().toString().trim()))
            status = true;
        if (TextUtils.isEmpty(etName.getText().toString().trim()))
            status = true;
        if (etTanggalLahir.getText().toString().trim().equals("Tanggal Lahir"))
            status = true;
        if (TextUtils.isEmpty(etHeight.getText().toString().trim()))
            status = true;
        if (TextUtils.isEmpty(etWeight.getText().toString().trim()))
            status = true;
        if (kelamin.equals(""))
            status = true;
        return status;
    }

    @Override
    public void onClick(View view) {
        if (view == btnSimpan) {
            if (checkInputForm())
                Toast.makeText(getApplicationContext(), "Data input tidak boleh kosong!",
                        Toast.LENGTH_SHORT).show();
            else
                updateData();
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