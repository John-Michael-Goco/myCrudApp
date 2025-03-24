package com.mycrudapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddStudent extends AppCompatActivity {

    private Button backBtn, addStudent;
    private final String url = "http://10.0.2.2/myCrudAppAPI/insertStudent.php";
    private TextInputLayout studentNumber, name, address, email, phoneNumber, course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_student);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        backBtn = findViewById(R.id.backBtn);
        addStudent = findViewById(R.id.addStudent);
        studentNumber = findViewById(R.id.studentID);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);
        course = findViewById(R.id.course);

        // Back button listener
        backBtn.setOnClickListener(v -> {
            finish();
        });

        // Register user when the Add Student button is clicked
        addStudent.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        // Get input values
        String studentNumberVal = studentNumber.getEditText().getText().toString().trim();
        String nameVal = name.getEditText().getText().toString().trim();
        String addressVal = address.getEditText().getText().toString().trim();
        String emailVal = email.getEditText().getText().toString().trim();
        String phoneNumberVal = phoneNumber.getEditText().getText().toString().trim();
        String courseVal = course.getEditText().getText().toString().trim();

        // Input validation
        if (studentNumberVal.isEmpty() || nameVal.isEmpty() || addressVal.isEmpty() ||
                emailVal.isEmpty() || phoneNumberVal.isEmpty() || courseVal.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()) {
            Toast.makeText(this, "Invalid email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phoneNumberVal.matches("\\d{10}")) {
            Toast.makeText(this, "Phone number must be 10 digits.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the request to add the student
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if ("success".equals(status)) {
                            Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show();
                            navigateToStudentList();
                        } else if ("exists".equals(status)) {
                            Toast.makeText(this, "Student already registered!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e("RegisterError", "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(this, "Parsing Error. Try again later.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("VolleyError", "Request Error: " + error.toString());
                    Toast.makeText(this, "Request Error. Check your connection.", Toast.LENGTH_LONG).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("studentNumber", studentNumberVal);
                params.put("name", nameVal);
                params.put("address", addressVal);
                params.put("email", emailVal);
                params.put("phoneNumber", phoneNumberVal);
                params.put("course", courseVal);
                return params;
            }
        };

        // Add the request to the Volley queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void navigateToStudentList() {
        Intent intent = new Intent(AddStudent.this, StudentsList.class);
        startActivity(intent);
        finish();
    }
}