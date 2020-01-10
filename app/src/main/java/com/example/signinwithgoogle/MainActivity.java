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
  //      facebookbtn.setReadPermissions(asList("user_status"));
//        eName =(EditText) findViewById(R.id.et_name);
//        eEid=(EditText) findViewById(R.id.et_eid);
         button.setSize(SignInButton.SIZE_STANDARD);
        // eSignin.setSize(SignInButton.SIZE_STANDARD);
        mAuth = FirebaseAuth.getInstance();


//  email = eName.getText().toString();
//  password = eEid.getText().toString();

//  enewUser.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//          createAccount();
//      }
//  });
  eSignin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          signIn();
      }
  });
// Configure Google Sign In for firebase
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Configure sign-in to request the user's ID, email address, and basic
       // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .requestProfile()
//                .requestId()
//                .build();
    //   Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              //  signIn();
//            }
//        });
        //Facebook
      //  LoginManager.getInstance().setReadPermissions(Collections.singletonList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();


        facebookbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, asList("email","public_profile"));
                // Callback registration
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
//public void createAccount(){
//    mAuth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//                    if (task.isSuccessful()) {
//                        // Sign in success, update UI with the signed-in user's information
//                        Log.d("Newusercreated", "createUserWithEmail:success");
//                        FirebaseUser user = mAuth.getCurrentUser();
//                        updateUI(user);
//                    } else {
//                        // If sign in fails, display a message to the user.
//                        Log.w("NewUserFaild", "createUserWithEmail:failure", task.getException());
//                        Toast.makeText(MainActivity.this, "Authentication failed.",
//                                Toast.LENGTH_SHORT).show();
//                        updateUI(null);
//                    }
//
//                    // ...
//                }
//            });
//}

//facebook onactivityresult
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

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseAuthWithGoogle(account);
//            } catch (ApiException e) {
//                // Google Sign In failed, update UI appropriately
//                Log.w("Firbaseloginsuccess", "Google sign in failed", e);
//                // ...
//            }
//        }
//    }

//    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        Log.d("firebaseAuthWithGoogle", "firebaseAuthWithGoogle:" + acct.getId());
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("signInWithCredential", "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w("signInWithCredential", "signInWithCredential:failure", task.getException());
//                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//
//                        // ...
//                    }
//                });
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
     //    Check for existing Google Sign In account, if the user is already signed in
       // the GoogleSignInAccount will be non-null.
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        updateUI(account);
        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
 //   }
//login with google
//    private void signIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            Log.d("Data", String.valueOf(data));
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
//    }
//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//
//            // Signed in successfully, show authenticated UI.
//            updateUI(account);
//        } catch (ApiException e) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w("Taggggg", "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
//        }
//    }
//    public void updateUI(GoogleSignInAccount mgoogleSignInAccount){
//        String name = mgoogleSignInAccount.getGivenName();
//        String disp = mgoogleSignInAccount.getDisplayName();
//        String family = mgoogleSignInAccount.getFamilyName();
//        String eid = mgoogleSignInAccount.getEmail();
//        Intent intent = new Intent(MainActivity.this, WelcomePage.class);
//        intent.putExtra("Name", name);
//        intent.putExtra("Disply", disp);
//        intent.putExtra("Family", family);
//        intent.putExtra("Eid",eid);
//        startActivity(intent);
//
//    }
}
