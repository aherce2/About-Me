package edu.illinois.cs465.lockedin.activities;

import android.os.Bundle;
import android.text.InputFilter;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import edu.illinois.cs465.lockedin.R;

public class ProfileActivity extends AppCompatActivity {
    private static final int MAX_NAME_LENGTH = 10;

    private CircleImageView profileImage;
    private Button saveProfileButton;
    private EditText editFirstName, editLastName, editEmail;
    private TextView displayFirstName, displayLastName, displayEmail;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);

        initializeViews();
        setupListeners();
        setInputFilters();
    }

    private void initializeViews() {
        profileImage = findViewById(R.id.profile_img);
        saveProfileButton = findViewById(R.id.save_profile);
        editFirstName = findViewById(R.id.edit_name);
        editLastName = findViewById(R.id.edit_last_name);
        editEmail = findViewById(R.id.edit_email);
        displayFirstName = findViewById(R.id.display_name);
        displayLastName = findViewById(R.id.display_last_name);
        displayEmail = findViewById(R.id.display_email);
    }

    private void setupListeners() {
        saveProfileButton.setOnClickListener(v -> toggleEditMode());
        profileImage.setOnClickListener(v -> handleProfileImageClick());
    }

    private void setInputFilters() {
        InputFilter[] filters = new InputFilter[]{new InputFilter.LengthFilter(MAX_NAME_LENGTH)};
        editFirstName.setFilters(filters);
        editLastName.setFilters(filters);
    }

    private void toggleEditMode() {
        if (isEditMode) {
            if (isInputValid()) {
                saveChanges();
            } else {
                showInputError();
            }
        } else {
            enterEditMode();
        }
    }

    private boolean isInputValid() {
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        return !firstName.isEmpty() && !lastName.isEmpty() && isValidEmail(email);
    }

    private boolean isValidEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void saveChanges() {
        String newFirstName = editFirstName.getText().toString().trim();
        String newLastName = editLastName.getText().toString().trim();
        String newEmail = editEmail.getText().toString().trim();
        displayFirstName.setText(newFirstName);
        displayLastName.setText(newLastName);
        displayEmail.setText(newEmail);
        exitEditMode();
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void showInputError() {
        if (editFirstName.getText().toString().trim().isEmpty()) {
            editFirstName.setError("First name cannot be empty");
        }
        if (editLastName.getText().toString().trim().isEmpty()) {
            editLastName.setError("Last name cannot be empty");
        }
        if (!isValidEmail(editEmail.getText().toString().trim())) {
            editEmail.setError("Please enter a valid email address");
        }
        Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show();
    }

    private void enterEditMode() {
        isEditMode = true;
        saveProfileButton.setText(R.string.save_changes);
        setViewVisibility(View.VISIBLE, View.GONE);
        editFirstName.setText(displayFirstName.getText());
        editLastName.setText(displayLastName.getText());
        editEmail.setText(displayEmail.getText());
        editFirstName.requestFocus();
    }

    private void exitEditMode() {
        isEditMode = false;
        saveProfileButton.setText(R.string.edit_profile);
        setViewVisibility(View.GONE, View.VISIBLE);
    }

    private void setViewVisibility(int editTextVisibility, int textViewVisibility) {
        editFirstName.setVisibility(editTextVisibility);
        editLastName.setVisibility(editTextVisibility);
        editEmail.setVisibility(editTextVisibility);
        displayFirstName.setVisibility(textViewVisibility);
        displayLastName.setVisibility(textViewVisibility);
        displayEmail.setVisibility(textViewVisibility);
    }

    private void handleProfileImageClick() {
        // TODO: Implement profile image change functionality
        Toast.makeText(this, "Profile image change not implemented yet", Toast.LENGTH_SHORT).show();
    }
}
