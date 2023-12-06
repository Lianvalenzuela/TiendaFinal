package com.example.tiendav1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.CaseMap;
import android.nfc.Tag;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthActivity extends AppCompatActivity {

    private Button signInButton;
    private Button mqqt_btn;
    private TextInputEditText emailEditText;
    String TAG ="GoogleSignIn";
    private TextInputEditText passwordEditText;
    private RatingBar rbr1;
    private FirebaseAuth mAuth;
    int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleAuth;
    private Button googlebtn;
    private TextView registro;

    String titulo = "Autenticacion";
    private FirebaseAnalytics mFirebaseanalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(R.string.mi_titulo);

        //firebase analytics
        mFirebaseanalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("message", "Integracion de firebase completa");
        mFirebaseanalytics.logEvent("InitScreen", bundle);
        setContentView(R.layout.activity_auth);

        //firebase auth y Autentificacion de Google
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("145705098903-1971fhf3kvr6lk4remorl6gmj44hsre8.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleAuth = GoogleSignIn.getClient(this, gso);

        //Inicializacion editText y TextView
        emailEditText = (TextInputEditText) findViewById(R.id.email);
        passwordEditText = (TextInputEditText) findViewById(R.id.password);
        registro = (TextView) findViewById(R.id.registro);
        googlebtn = findViewById(R.id.Googlebtn);
        googlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AuthActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        mqqt_btn = findViewById(R.id.mqttbtn);
        mqqt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Toast.makeText(AuthActivity.this, "Handler", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AuthActivity.this, MqttActivity.class);
                    Toast.makeText(AuthActivity.this, "handler23", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                    AuthActivity.this.finish();
            }
        });


        //inicializacion de la barra de calificacion
        rbr1 = (RatingBar) findViewById(R.id.rbr1);

        //Mostrar la siguiente actividad
        signInButton=findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                email = String.valueOf(emailEditText.getText());
                password = String.valueOf(passwordEditText.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(AuthActivity.this, "Ingresar email: ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(AuthActivity.this, "Ingresar contraseña: ", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(AuthActivity.this, "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show();
                                    Intent intentS = new Intent(AuthActivity.this, Comp.class);
                                    startActivity(intentS);
                                }else{
                                    Toast.makeText(AuthActivity.this, "Inicio de sesion fallido", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Resultado devuelto al iniciar el Intent de GoogleSignInApi.getSignInIntent (...);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In fallido, actualizar GUI
                    Log.w(TAG, "Google sign in failed", e);
                }
            } else {
                Log.d(TAG, "Error, login no exitoso:" + task.getException().toString());
                Toast.makeText(this, "Ocurrio un error. " + task.getException().toString(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
    public void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            Intent dashboardActivity = new Intent(AuthActivity.this, Dashboard.class);
                            startActivity(dashboardActivity);
                            AuthActivity.this.finish();
                    //Iniciar DASHBOARD u otra actividad luego del SigIn Exitoso
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });

    }
    private void signIn() {
        Intent signInIntent = mGoogleAuth.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public void calificacion(View v) {
        Toast.makeText(AuthActivity.this, "Has seleccionado un rating de: " + rbr1.getRating() + " estrellas!", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onStart() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){ //si no es null el usuario ya esta logueado
            //mover al usuario al dashboard
            Intent dashboardActivity = new Intent(AuthActivity.this, Dashboard.class);
            startActivity(dashboardActivity);
        }
        super.onStart();
    }

}