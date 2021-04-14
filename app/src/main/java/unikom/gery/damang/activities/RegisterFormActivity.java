package unikom.gery.damang.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unikom.gery.damang.R;
import unikom.gery.damang.api.Api;
import unikom.gery.damang.api.BaseApi;
import unikom.gery.damang.model.User;
import unikom.gery.damang.response.CheckUser;
import unikom.gery.damang.util.SharedPreference;

public class RegisterFormActivity extends AppCompatActivity implements View.OnClickListener {

    private GoogleSignInAccount account;
    private String email, name, photo;
    private EditText etEmail, etNama, etHeight, etWeight;
    private TextView etTanggalLahir;
    private Button btnRegister;
    private Calendar calendar;
    private User user;
    private SharedPreference sharedPreference;
    private ProgressDialog progressDialog;

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
        setContentView(R.layout.activity_register_form);
        etEmail = findViewById(R.id.etEmail);
        etNama = findViewById(R.id.etName);
        etHeight = findViewById(R.id.etTinggiBadan);
        etWeight = findViewById(R.id.etBeratBadan);
        etTanggalLahir = findViewById(R.id.etTanggalLahir);
        btnRegister = findViewById(R.id.btnRegister);

        account = GoogleSignIn.getLastSignedInAccount(this);
        email = account.getEmail();
        name = account.getDisplayName();
        photo = account.getPhotoUrl().toString();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Harap Tunggu");
        progressDialog.setCancelable(false);

        btnRegister.setOnClickListener(this);
        etTanggalLahir.setOnClickListener(this);
        calendar = Calendar.getInstance();
        sharedPreference = new SharedPreference(this);
        setUserDataToView();
    }

    private void setUserDataToView() {
        etEmail.setText(email);
        etNama.setText(name);
    }

    private void setUserSession(User data) {
        sharedPreference.setLoggedIn(true);
        user.setEmail(data.getEmail());
        user.setName(data.getName());
        user.setDateofBirth(data.getDateofBirth());
        user.setHeight(data.getHeight());
        user.setWeight(data.getHeight());
        user.setPhoto(data.getPhoto());
        sharedPreference.setUser(user);
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private User getUserData() {
        user = new User();
        user.setName(etNama.getText().toString().trim());
        user.setEmail(etEmail.getText().toString().trim());
        user.setPhoto(photo);
        user.setWeight(Float.parseFloat(etWeight.getText().toString().trim()));
        user.setHeight(Float.parseFloat(etHeight.getText().toString().trim()));
        user.setDateofBirth(etTanggalLahir.getText().toString().trim());
        return user;
    }

    private void Regsiter(final User data) {
        progressDialog.show();
        Api api = BaseApi.getRetrofit().create(Api.class);
        Call<CheckUser> response = api.register(data.getEmail(), data.getName(), data.getDateofBirth(), data.getWeight(), data.getHeight(), data.getPhoto());
        response.enqueue(new Callback<CheckUser>() {
            @Override
            public void onResponse(Call<CheckUser> call, Response<CheckUser> response) {
                progressDialog.hide();
                if (response.body().getCode() == 1) {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    setUserSession(data);
                } else
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<CheckUser> call, Throwable t) {
                progressDialog.hide();
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
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
        if (view == btnRegister) {
            Regsiter(getUserData());
        } else if (view == etTanggalLahir) {
            pickDate();
        }
    }
}