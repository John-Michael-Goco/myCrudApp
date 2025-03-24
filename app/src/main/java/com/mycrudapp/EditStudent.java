package com.mycrudapp;

import android.app.AlertDialog;
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
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditStudent extends AppCompatActivity {

    Button backBtn, updateStudent;
    TextInputEditText studentIDInput, nameInput, addressInput, emailInput, phoneInput, courseInput;

    private final String url = "http://10.0.2.2/myCrudAppAPI/updateStudent.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_student);

        String studentID = getIntent().getStringExtra("studentID");
        String name = getIntent().getStringExtra("name");
        String address = getIntent().getStringExtra("address");
        String email = getIntent().getStringExtra("email");
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        String course = getIntent().getStringExtra("course");

        studentIDInput = findViewById(R.id.studentIDInput);
        nameInput = findViewById(R.id.nameInput);
        addressInput = findViewById(R.id.addressInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneNumberInput);
        courseInput = findViewById(R.id.courseInput);

        // Populate Input Fields
        if (studentID != null) studentIDInput.setText(studentID);
        if (name != null) nameInput.setText(name);
        if (address != null) addressInput.setText(address);
        if (email != null) emailInput.setText(email);
        if (phoneNumber != null) phoneInput.setText(phoneNumber);
        if (course != null) courseInput.setText(course);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(EditStudent.this, StudentsList.class);
            startActivity(intent);
        });

        // Update button
        updateStudent = findViewById(R.id.updateStudent);
        updateStudent.setOnClickListener(v -> showUpdateConfirmationDialog(getIntent().getStringExtra("studentID")));
    }

    private void showUpdateConfirmationDialog(String originalStudentID) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Update")
                .setMessage("Are you sure you want to update this student?")
                .setPositiveButton("Yes", (dialog, which) -> updateStudent(originalStudentID))
                .setNegativeButton("No", null)
                .show();
    }

    private void updateStudent(String originalStudentID) {

        // Get input values
        String studentNumberVal = studentIDInput.getText().toString().trim();
        String nameVal = nameInput.getText().toString().trim();
        String addressVal = addressInput.getText().toString().trim();
        String emailVal = emailInput.getText().toString().trim();
        String phoneNumberVal = phoneInput.getText().toString().trim();
        String courseVal = courseInput.getText().toString().trim();

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

        // Create the request to update the student
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if ("success".equals(status)) {
                            Toast.makeText(this, "Student Successfully Updated!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditStudent.this, StudentsList.class);
                            startActivity(intent);
                            finish();
                        } else if ("exists".equals(status)) {
                            Toast.makeText(this, "Student Number already registered!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e("UpdateError", "JSON Parsing Error: " + e.getMessage());
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
                params.put("studentNumberOrig", originalStudentID);
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
}
