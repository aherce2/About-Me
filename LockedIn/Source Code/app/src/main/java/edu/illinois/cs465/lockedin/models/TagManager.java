package edu.illinois.cs465.lockedin.models;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.illinois.cs465.lockedin.R;

public class TagManager {
    public static List<String> tagOptions = new ArrayList<>(Arrays.asList("Choose Tag", "CS 465", "CS 374", "CS 461", "Add New Tag"));

    public static List<String> getTagOptions() {
        return tagOptions;
    }

    public static void addTag(String newTag) {
        if (!tagOptions.contains(newTag)) {
            tagOptions.add(tagOptions.size() - 1, newTag); // Add before "Add New Tag"
        }
    }

    public static void showAddTagDialog(Context context, ArrayAdapter<String> adapter, Spinner spinner) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_tag, null);
        TextInputLayout textInputLayout = dialogView.findViewById(R.id.outlinedTextField);
        TextInputEditText input = (TextInputEditText) textInputLayout.getEditText();

        builder.setView(dialogView)
                .setTitle("Add New Tag")
                .setPositiveButton("OK", (dialog, which) -> {
                    String newTag = input.getText().toString().trim();
                    if (!newTag.isEmpty()) {
                        addTag(newTag);
                        adapter.notifyDataSetChanged();
                        spinner.setSelection(tagOptions.indexOf(newTag));
                        Log.d("spinner currently created", newTag);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                    spinner.setSelection(0);
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


}