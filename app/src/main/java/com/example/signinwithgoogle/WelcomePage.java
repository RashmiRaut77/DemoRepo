package com.example.signinwithgoogle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

public class WelcomePage extends AppCompatActivity {
EditText eName;
    EditText eDispName;
    EditText eFamilyName;
EditText eEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
     eName =(EditText) findViewById(R.id.editName);
     eEmail =(EditText) findViewById(R.id.editEmail);
        eDispName =(EditText) findViewById(R.id.editDisplyName);
        eFamilyName =(EditText) findViewById(R.id.editfamilyName);

        Intent intent = getIntent();
        String Iname = intent.getStringExtra("name");
        String IDisname = intent.getStringExtra("Disply");
        String IFamname = intent.getStringExtra("family");
        String Ieid = intent.getStringExtra("Eid");

         eName.setText(Iname);
         eEmail.setText(Ieid);
         eDispName.setText(IDisname);
        eFamilyName.setText(IFamname);
    }
}
