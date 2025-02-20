package com.example.myapp1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {

    private TextView textViewName, textViewGender, textViewAge, textViewDob, textViewMobile, textViewEmail;
    private Button btnMainPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Ensure the XML file name is activity_profile.xml

        // Initialize text views
        textViewName = findViewById(R.id.textViewName);
        textViewGender = findViewById(R.id.textViewGender);
        textViewAge = findViewById(R.id.textViewAge);
        textViewDob = findViewById(R.id.textViewDob);
        textViewMobile = findViewById(R.id.textViewMobile);
        textViewEmail = findViewById(R.id.textViewEmail);

        // Initialize the button
        btnMainPage = findViewById(R.id.button2);

        // Set initial profile data
        setProfileData("Alisher Abilakim Alimzhanuly", "Gender", "Age", "Date_of_birth", "phone_number", "email");

        // Set up the button listener for navigating to MainPage
        btnMainPage.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, MainActivity2.class);
            startActivity(intent);
        });
    }

    // Method to set profile data
    private void setProfileData(String name, String gender, String age, String dob, String mobile, String email) {
        textViewName.setText(name);
        textViewGender.setText(gender);
        textViewAge.setText(age);
        textViewDob.setText(dob);
        textViewMobile.setText(mobile);
        textViewEmail.setText(email);
    }

    // Handle result from the edit profile activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String newName = data.getStringExtra("name");
            String newGender = data.getStringExtra("gender");
            String newAge = data.getStringExtra("age");
            String newDob = data.getStringExtra("dob");
            String newMobile = data.getStringExtra("mobile");
            String newEmail = data.getStringExtra("email");

            setProfileData(
                    newName != null ? newName : textViewName.getText().toString(),
                    newGender != null ? newGender : textViewGender.getText().toString(),
                    newAge != null ? newAge : textViewAge.getText().toString(),
                    newDob != null ? newDob : textViewDob.getText().toString(),
                    newMobile != null ? newMobile : textViewMobile.getText().toString(),
                    newEmail != null ? newEmail : textViewEmail.getText().toString()
            );
        }
    }
}
