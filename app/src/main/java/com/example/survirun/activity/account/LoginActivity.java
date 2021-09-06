package com.example.survirun.activity.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.survirun.Medel.UserModel;
import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ActivityLoginBinding binding;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    String uid;
    String email;
    String id;
    String pwe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sf = getSharedPreferences("Login", MODE_PRIVATE);
        email = sf.getString("email", "");
        pwe = sf.getString("pwe", "");
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);
        binding.idEdittext.setText(email);
        binding.passwordEdittext.setText(pwe);
        firebaseAuth = FirebaseAuth.getInstance();

        binding.googleLoginButton.setOnClickListener(v -> {
            signIn();
            Log.d("asdf", "1");
        });

        binding.idEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                id = binding.idEdittext.getText().toString();

                if (!pwe.equals("") && !id.equals("")) {
                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btncolor));
                } else {
                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btn));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                id = binding.idEdittext.getText().toString();
                pwe = binding.passwordEdittext.getText().toString();
                if (!pwe.equals("") && !id.equals("")) {
                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btncolor));
                } else {
                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btn));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.passwordEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                pwe = binding.passwordEdittext.getText().toString();
                id = binding.idEdittext.getText().toString();
                if (!pwe.equals("") && !id.equals("")) {
                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btncolor));
                } else {
                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btn));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pwe = binding.passwordEdittext.getText().toString();

                if (!pwe.equals("") && !id.equals("")) {
                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btncolor));

                } else {

                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btn));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //firebaseAuth의 인스턴스를 가져옴
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pwe.equals("") && !id.equals("")) {
                    email = binding.idEdittext.getText().toString().trim();

                    if (email.contains("@") == true) {
                        int idx = email.indexOf("@");
                        id = email.substring(0, idx);
                        pwe = binding.passwordEdittext.getText().toString().trim();
                        firebaseAuth.signInWithEmailAndPassword(email, pwe)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {//성공했을때
                                            login();
                                            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    } else {
                        Toast.makeText(LoginActivity.this, R.string.email_error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, R.string.fill_error, Toast.LENGTH_SHORT).show();
                }

            }


        });

        binding.signUpTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    public void login() {
        SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);    // test 이름의 기본모드 설정
        SharedPreferences.Editor editor = sharedPreferences.edit(); //sharedPreferences를 제어할 editor를 선언
        editor.putString("id", id);
        editor.putString("pwe", pwe);
        editor.putString("email", email);
        editor.commit();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.d("asdf", "2");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException ignored) {
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String uid = user.getUid();
                            email = user.getEmail();
                            int idx = email.indexOf("@");
                            id = email.substring(0, idx);
                            Log.d("asdf", " " + email + id + LoginActivity.this);
                            UserModel userModel = new UserModel();
                            userModel.id = id;
                            userModel.uid = uid;
                            userModel.todayExerciseTime = 0;
                            userModel.todayKm = 0.00;
                            userModel.todayCalorie = 0;
                            databaseReference.child("Userid").child(id).setValue(uid);
                            databaseReference.child("UserProfile").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent = new Intent(LoginActivity.this, SignUpNameActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }

                    }
                });
    }
    public  void newUser(String uid){
        UserModel userModel = new UserModel();
        userModel.id = id;
        userModel.uid = uid;
        userModel.todayExerciseTime = 0;
        userModel.todayKm = 0.00;
        userModel.todayCalorie = 0;
        databaseReference.child("Userid").child(id).setValue(uid);
        databaseReference.child("UserProfile").child(uid).setValue(userModel);
    }

}
