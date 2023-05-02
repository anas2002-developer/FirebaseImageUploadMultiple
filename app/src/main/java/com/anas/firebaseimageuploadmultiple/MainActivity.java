package com.anas.firebaseimageuploadmultiple;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView vRV;
    Button btnBrowse;

    Adapter_Image adapter_image;
    StorageReference storageReference;

    List<String> files,status;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vRV=findViewById(R.id.vRV);
        btnBrowse=findViewById(R.id.btnBrowse);

        vRV.setLayoutManager(new LinearLayoutManager(this));
        storageReference= FirebaseStorage.getInstance().getReference();

        files=new ArrayList<>();
        status=new ArrayList<>();

        adapter_image = new Adapter_Image(files,status);
        vRV.setAdapter(adapter_image);

        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(MainActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent i = new Intent(Intent.ACTION_PICK);
                                i.setType("image/*");
                                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                                startActivityForResult(i,100);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                //on reopen of app , again asks for permission
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK && requestCode==100){
            if (data.getClipData()!=null){
                for (int i=0;i<data.getClipData().getItemCount();i++){
                    uri = data.getClipData().getItemAt(i).getUri();
                    String image_name = getFileNameFromUri(uri);
                    files.add(image_name);
                    status.add(i,"loading");
                    adapter_image.notifyDataSetChanged();

                    final int index = i;
                    StorageReference imgUpload_location = storageReference.child("/MutipleImageUpload").child(image_name);
                    imgUpload_location.putFile(uri)
                            .addOnSuccessListener(taskSnapshot -> {
                                Toast.makeText(MainActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                                status.remove(index);
                                status.add(index,"done");
                                adapter_image.notifyDataSetChanged();

                            });
                }
            }
        }
    }

    private String getFileNameFromUri(Uri CurrentUri) {
        String result=null;
        if (CurrentUri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(CurrentUri,null,null,null);
            try {
                if (cursor!=null && cursor.moveToFirst()){
                    result=cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
            finally {
                cursor.close();
            }
        }
        if (result==null){
            result = CurrentUri.getPath();
            int cut = result.lastIndexOf("/");
            if (cut!=-1){
                result = result.substring(cut+1);
            }
        }
        return result;
    }
}