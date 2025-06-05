package edu.illinois.cs465.lockedin;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class TextInputDialogFragment extends DialogFragment {

    private TextInputEditText textInputEditText;
    private OnTextEnteredListener listener;
    private String dialogTitle;

    public static TextInputDialogFragment newInstance(String initialText, String title) {
        TextInputDialogFragment fragment = new TextInputDialogFragment();
        Bundle args = new Bundle();
        args.putString("initialText", initialText);
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnTextEnteredListener {
        void onTextEntered(String text);
    }

    public void setOnTextEnteredListener(OnTextEnteredListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_text_input, null);
        textInputEditText = view.findViewById(R.id.textInputEditText);

        String initialText = getArguments().getString("initialText", "");
        dialogTitle = getArguments().getString("title", "Enter Text");
        textInputEditText.setText(initialText);

        return new MaterialAlertDialogBuilder(requireContext(), R.style.Theme_LockedIn)
                .setView(view)
                .setTitle(dialogTitle)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String enteredText = textInputEditText.getText().toString();
                        if (listener != null) {
                            listener.onTextEntered(enteredText);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
    }


    @Override
    public void onStart() {
        super.onStart();
        textInputEditText.requestFocus();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}