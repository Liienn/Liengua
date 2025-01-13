package com.example.liengua;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ContactActivity extends AppCompatActivity {

    private EditText contactMessageEditText;
    private Button sendButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_form); // Use the updated layout with the back button

        contactMessageEditText = findViewById(R.id.contactMessage);
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.backButton);

        // Handle back button click
        backButton.setOnClickListener(v -> finish());  // This will close the current activity and return to the previous one

        sendButton.setOnClickListener(v -> sendEmail());
    }

    private void sendEmail() {
        String message = contactMessageEditText.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set up the Intent to send the email
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");

        // Set the email subject and body
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"teuwenlien@gmail.com"});  // Replace with your email
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "User Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send Feedback"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email client is installed.", Toast.LENGTH_SHORT).show();
        }
    }
}

