package com.mycrudapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentsList extends AppCompatActivity {

    private RecyclerView studentRecyclerView;
    private StudentsAdapter studentsAdapter;
    private List<StudentModel> studentsList;
    private FloatingActionButton floatingButton;
    private final String fetchApi = "http://10.0.2.2/myCrudAppAPI/fetchStudent.php";
    private final String deleteStudentApi = "http://10.0.2.2/myCrudAppAPI/deleteStudent.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_students_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerView
        studentRecyclerView = findViewById(R.id.studentRecyclerView);
        studentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        studentsList = new ArrayList<>();

        // Load students from API
        loadStudentsFromAPI();

        // Set up adapter
        studentsAdapter = new StudentsAdapter(studentsList, this);
        studentRecyclerView.setAdapter(studentsAdapter);

        // Initialize FAB
        floatingButton = findViewById(R.id.floatingButton);
        floatingButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsList.this, AddStudent.class);
            startActivity(intent);
        });

        studentsAdapter.setOnDeleteClickListener(this::deleteStudent);
    }

    private void loadStudentsFromAPI() {
        StringRequest request = new StringRequest(Request.Method.GET, fetchApi,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            JSONArray data = jsonResponse.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject student = data.getJSONObject(i);
                                String name = student.getString("name");
                                String studentID = student.getString("studentID");
                                String address = student.getString("address");
                                String email = student.getString("email");
                                String phoneNumber = student.getString("phoneNumber");
                                String course = student.getString("course");
                                studentsList.add(new StudentModel(name, studentID, address, email, phoneNumber, course));
                            }
                            studentsAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No students found.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("JSONError", "Parsing error: " + e.getMessage());
                        Toast.makeText(this, "Error parsing data.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("VolleyError", "Request error: " + error.toString());
                    Toast.makeText(this, "Error fetching data.", Toast.LENGTH_SHORT).show();
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void deleteStudent(String studentID) {
        StringRequest request = new StringRequest(Request.Method.POST, deleteStudentApi,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            Toast.makeText(this, "Student deleted successfully!", Toast.LENGTH_SHORT).show();
                            studentsList.clear();
                            loadStudentsFromAPI();
                        } else {
                            Toast.makeText(this, "Failed to delete student.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("JSONError", "Parsing error: " + e.getMessage());
                        Toast.makeText(this, "Error parsing response.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("VolleyError", "Request error: " + error.toString());
                    Toast.makeText(this, "Error deleting student.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("studentID", studentID);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}