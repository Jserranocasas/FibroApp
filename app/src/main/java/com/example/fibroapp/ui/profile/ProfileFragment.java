package com.example.fibroapp.ui.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.fibroapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class ProfileFragment extends Fragment {
    public static int CAMERA_REQUEST_CODE = 10001;

    StorageReference mStorageImagesReference;
    DatabaseReference mDatabaseUserReference;

    EditText name, email;
    ImageView image;
    Button btnChoose;
    Button btnSave;

    String pathTofile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        name = root.findViewById(R.id.name_edit_profile);
        email = root.findViewById(R.id.email_edit_profile);
        image = root.findViewById(R.id.image_edit_profile);
        btnChoose = root.findViewById(R.id.btn_choose_profile);
        btnSave = root.findViewById(R.id.btn_save_profile);

        mStorageImagesReference = FirebaseStorage.getInstance().getReference().child("images/");
        mDatabaseUserReference = FirebaseDatabase.getInstance().getReference().child("users");

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setText();
        addOnClick();
    }

    private void setText(){
        //Set name edit
        name.setText(Profile.profile.getName());

        //Set email edit
        email.setText(Profile.profile.getEmail());

        //Set photo edit
        Glide.with(getActivity())
                .load(Profile.profile.getImagePath())
                .apply(RequestOptions.circleCropTransform())
                .into(image);
    }

    private void addOnClick(){
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Permissions
                if (Build.VERSION.SDK_INT >= 23) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                }

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getActivity().getPackageManager()) != null){
                    File photoFile = createPhotoFile();

                    if (photoFile != null){
                        pathTofile = photoFile.getAbsolutePath();
                        Uri photoUri = FileProvider.getUriForFile(getContext(), "com.example.fibroapp.fileprovider", photoFile);
                        i.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(i, 1);
                    }
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty()){
                    name.setError(getResources().getString(R.string.enter_name_valid));
                    name.requestFocus();
                }else{
                    Snackbar.make(getView(), getResources().getString(R.string.saving_data), Snackbar.LENGTH_SHORT).show();
                    if (pathTofile != null && !pathTofile.equals("")){
                        uploadImage();
                    }
                    else {
                        saveDataUser(name.getText().toString(), email.getText().toString(), Profile.profile.getImagePath(), Profile.getInstance().getTypeLogin());
                    }
                }
            }
        });
    }

    private void uploadImage(){
        Uri uriImage = Uri.fromFile(new File(pathTofile));

        StorageReference fileReference = mStorageImagesReference.child(System.currentTimeMillis() + ".jpg");
        fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri url = uriTask.getResult();

                String nameString = name.getText().toString();
                String emailString = email.getText().toString();
                String imagePathString = url.toString();

                saveDataUser(nameString, emailString, imagePathString, Profile.getInstance().getTypeLogin());
            }
        });
    }

    private void saveDataUser(String name, String email, String imagePath, String typelogin){
        Map<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("name", name);
        data.put("image", imagePath);
        data.put("typelogin", typelogin);

        mDatabaseUserReference.child(FirebaseAuth.getInstance().getUid()).setValue(data);

        Snackbar.make(getView(), getResources().getString(R.string.data_saved), Snackbar.LENGTH_SHORT).show();
    }

    private File createPhotoFile(){
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name, ".jpg", storageDir); //.png if there is problem
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == 1){
                Bitmap bitmap = BitmapFactory.decodeFile(pathTofile);

                Glide.with(getActivity())
                        .load(bitmap)
                        .apply(RequestOptions.circleCropTransform())
                        .into(image);
            }
        }else{
            pathTofile = null;
        }
    }
}
