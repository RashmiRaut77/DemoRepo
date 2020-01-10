package com.example.signinwithgoogle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;

import static java.util.Arrays.*;

public class MainActivity extends AppCompatActivity {
    SignInButton button;
    SignInButton eSignin;
    private FirebaseAuth mAuth;
    EditText eName, eEid;
    Button facebookbtn;
    String email, password;
    private static final int RC_SIGN_IN = 1 ;
   GoogleSignInClient mGoogleSignInClient;
    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (SignInButton) findViewById(R.id.textgoogle);
        eSignin =(SignInButton) findViewById(R.id.btn_signIn);
        facebookbtn =(Button) findViewById(R.id.btn_facebook);
         callbackManager = CallbackManager.Factory.create();
         button.setSize(SignInButton.SIZE_STANDARD);
        mAuth = FirebaseAuth.getInstance();


  email = eName.getText().toString();
  password = eEid.getText().toString();

  button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

      }
  });
  eSignin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          signIn();
      }
  });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();


        facebookbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, asList("email","public_profile"));
                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                GraphRequest request;
                                request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.i("TAGGG", "onCompleted: " + object.toString());
                                        Log.i("TAGGG", "onCompleted: " + response.getJSONObject().toString());
                                        updateFbUI(object);
                                    }
                                });
                                Bundle bundle = new Bundle();
                                bundle.putString("fields","id,email,name,first_name,last_name");
                                request.setParameters(bundle);
                                request.executeAsync();
                            }

                            @Override
                            public void onCancel() {
                                // App code
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                // App code
                            }
                        });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
   public void signIn(){
       Intent signInIntent = mGoogleSignInClient.getSignInIntent();
       startActivityForResult(signInIntent, RC_SIGN_IN);
   }
    public void updateFbUI(JSONObject object){
        String name = null;
        LoginManager.getInstance().logOut();
        try {
            name = object.getString("name");
            String disp = object.getString("first_name");
            String family = object.getString("last_name");
            String eid = object.getString("id");
            Intent intent = new Intent(MainActivity.this, WelcomePage.class);
            intent.putExtra("Name", name);
            intent.putExtra("Disply", disp);
            intent.putExtra("Family", family);
            intent.putExtra("Eid",eid);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
   public void updateUI(FirebaseUser user){
       String name = user.getPhoneNumber();
        String disp = user.getDisplayName();
        String family = user.getProviderId();
        String eid = user.getEmail();
        Intent intent = new Intent(MainActivity.this, WelcomePage.class);
        intent.putExtra("Name", name);
        intent.putExtra("Disply", disp);
        intent.putExtra("Family", family);
        intent.putExtra("Eid",eid);
        startActivity(intent);

   }

}
