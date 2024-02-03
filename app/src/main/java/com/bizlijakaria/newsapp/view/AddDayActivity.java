package com.bizlijakaria.newsapp.view;

import static com.google.android.material.color.MaterialColors.isColorLight;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bizlijakaria.newsapp.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddDayActivity extends AppCompatActivity {
    ImageView uploadImage;
    EditText shortNote, dateupload;
    Button saveBtn;
    ProgressBar progressBar;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri selectedImageUri = null;

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    public DocumentReference documentReference = FirebaseFirestore.getInstance().collection("img")
            .document();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_day);
        uploadImage = findViewById(R.id.imageupload);
        shortNote = findViewById(R.id.shortnoteAdd);
        dateupload = findViewById(R.id.dateadd);
        saveBtn = findViewById(R.id.newstduyadd);
        progressBar=findViewById(R.id.progress);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // Set status bar color to white
            window.setStatusBarColor(getResources().getColor(R.color.whit));

            // If the status bar color is light, set system UI to dark theme
            if (isColorLight(getResources().getColor(R.color.whit))) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                // If the status bar color is dark, set system UI to light theme
                getWindow().getDecorView().setSystemUiVisibility(0);
            }
        }

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openImagePicker();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                });

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        displaySelectedImage();
                    }
                });

        uploadImage.setOnClickListener(v -> {
            if (checkPermission()) {
                openImagePicker();
            } else {
                requestPermission();
            }
        });

        saveBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            if (selectedImageUri != null) {
                uploadImageToFirebase(selectedImageUri);

            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void displaySelectedImage() {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            uploadImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        // Generate a random UUID as the image file name
        String fileName = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("AllCategory/" + fileName);

        try {
            // Convert the image to bytes
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            // Upload the image to Firebase Storage
            UploadTask uploadTask = imageRef.putBytes(imageData);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Retrieve the download URL of the uploaded image
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    if (uri != null) {
                        String photoUrl = uri.toString();
                        saveImageToFirestore(photoUrl);
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(AddDayActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(AddDayActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveImageToFirestore(String photoUrl) {
        String shortNoteText = shortNote.getText().toString();
        String dateText = dateupload.getText().toString();

        Map<String, Object> newImage = new HashMap<>();
        newImage.put("i", photoUrl);
        newImage.put("date", shortNoteText);
        newImage.put("title", dateText);

        documentReference.set(newImage)
                .addOnSuccessListener(unused -> {
                    progressBar.setVisibility(View.GONE);
                    shortNote.setText("");
                    dateupload.setText("");
                    Toast.makeText(AddDayActivity.this, "Image added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddDayActivity.this, "Failed to add image.", Toast.LENGTH_SHORT).show();
                });
    }
}
