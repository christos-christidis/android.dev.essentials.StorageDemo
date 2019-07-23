package com.devessentials.storagedemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static final int CREATE_FILE_REQUEST_CODE = 40;
    private static final int OPEN_FILE_REQUEST_CODE = 41;
    private static final int SAVE_FILE_REQUEST_CODE = 42;

    private EditText mTextView;

    private final Intent newFileIntent = createIntentForNewFile();
    private final Intent openFileIntent = createIntentForPickingFile();
    private final Intent saveFileIntent = createIntentForPickingFile();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.fileText);
    }

    private static Intent createIntentForNewFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "newfile.txt");
        return intent;
    }

    private static Intent createIntentForPickingFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        return intent;
    }

    public void newFile(View view) {
        startActivityForResult(newFileIntent, CREATE_FILE_REQUEST_CODE);
    }

    public void openFile(View view) {
        startActivityForResult(openFileIntent, OPEN_FILE_REQUEST_CODE);
    }

    public void saveFile(View view) {
        startActivityForResult(saveFileIntent, SAVE_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) return;

        if (requestCode == CREATE_FILE_REQUEST_CODE) {
            mTextView.setText("");
        } else if (requestCode == OPEN_FILE_REQUEST_CODE) {
            Uri uri = data.getData();
            readFromFile(uri);
        } else if (requestCode == SAVE_FILE_REQUEST_CODE) {
            Uri uri = data.getData();
            writeToFile(uri);
        }
    }

    private void readFromFile(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return;

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                stringBuilder.append(currentLine).append("\n");
            }

            inputStream.close();

            mTextView.setText(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFile(Uri uri) {
        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w");
            if (pfd == null) return;

            FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
            String textContent = mTextView.getText().toString();
            fileOutputStream.write(textContent.getBytes());

            fileOutputStream.close();
            pfd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
