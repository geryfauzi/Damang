package unikom.gery.damang.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unikom.gery.damang.R;
import unikom.gery.damang.api.WebService;
import unikom.gery.damang.api.BaseApi;
import unikom.gery.damang.model.User;
import unikom.gery.damang.response.CheckUser;
import unikom.gery.damang.sqlite.dml.HeartRateHelper;
import unikom.gery.damang.util.SharedPreference;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogin;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInOptions googleSignInOptions;
    private SharedPreference sharedPreference;
    private User user;
    private ProgressDialog progressDialog;
    private HeartRateHelper heartRateHelper;
    private static final String baseUrl = "https://sohibultech.com/damang/";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide Action Bar
        this.getSupportActionBar().hide();
        //Change statusbar color
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.parseColor("#FF8D8A"));
        //
        setContentView(R.layout.activity_login);
        //Google Signin Configuration
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        //Session Initiation
        heartRateHelper = HeartRateHelper.getInstance(getApplicationContext());
        sharedPreference = new SharedPreference(this);
        if (sharedPreference.isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
        //
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Harap Tunggu...");
        progressDialog.setCancelable(false);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent signIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, 1);
    }

    private void setUserLogin(Response<CheckUser> response) {
        sharedPreference.setLoggedIn(true);
        user.setEmail(response.body().getEmail());
        user.setName(response.body().getName());
        user.setDateofBirth(response.body().getDateofBirth());
        user.setGender(response.body().getGender());
        user.setHeight(response.body().getHeight());
        user.setWeight(response.body().getWeight());
        user.setPhoto(response.body().getPhoto());
        sharedPreference.setUser(user);
        heartRateHelper.insertUser(sharedPreference.getUser());
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }

    private void checkEmail(GoogleSignInAccount account) {
        progressDialog.show();
        WebService webService = BaseApi.getRetrofit(baseUrl).create(WebService.class);
        Call<CheckUser> response = webService.checkUser(account.getEmail());
        user = new User();
        response.enqueue(new Callback<CheckUser>() {
            @Override
            public void onResponse(Call<CheckUser> call, Response<CheckUser> response) {
                progressDialog.hide();
                if (response.body().getCode() == 1) {
                    setUserLogin(response);
                } else {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                }
            }

            @Override
            public void onFailure(Call<CheckUser> call, Throwable t) {
                progressDialog.hide();
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    checkEmail(account);
                }
            } catch (ApiException e) {
                Log.d("Error : ", e.toString());
            }
        }
    }
}