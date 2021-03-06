package at.htlgkr.tournamaker.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import at.htlgkr.tournamaker.Classes.Benutzer;
import at.htlgkr.tournamaker.Classes.Hasher;
import at.htlgkr.tournamaker.R;

public class RegisterActivity extends AppCompatActivity
{
    private final int REQUEST_ID_IMAGE_CAPTURE = 100;
    private Bitmap cameraPicture;
    private List<Benutzer> allBenutzer = new ArrayList<>();

    private DatabaseReference firebaseDatabase;
    private StorageReference firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();


        firebaseDatabase = FirebaseDatabase.getInstance().getReference("users");
        firebaseStorage = FirebaseStorage.getInstance().getReference();

        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                allBenutzer.clear();
                Gson gson = new Gson();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String s = (String) ds.getValue();
                    allBenutzer.add(gson.fromJson(s, Benutzer.class));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("OnCancelled", "Cancelled");
            }
        });

        Button camera = findViewById(R.id.camera_button);
        camera.setOnClickListener(v ->
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_ID_IMAGE_CAPTURE);
        });

        Button register = findViewById(R.id.signoff_button);
        register.setOnClickListener(v -> onClickRegister());



        if(ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 420);
        }
    }


    public void onClickRegister()
    {
        String username = ((TextView) findViewById(R.id.tv_username)).getText().toString();
        String password = ((TextView) findViewById(R.id.tv_password)).getText().toString();
        if(!username.isEmpty() || !password.isEmpty())
        {
            Gson gson = new Gson();
            String securedPassword = Hasher.normalToHashedPassword(password);
            Benutzer newBenutzer = new Benutzer(username, securedPassword);


            if(allBenutzer.stream().map(Benutzer::getUsername).filter((u) -> u.equals(newBenutzer.getUsername())).count() <= 0)
            {
                firebaseDatabase.child(newBenutzer.getUsername()).setValue(gson.toJson(newBenutzer));
                firebaseStorage.child(newBenutzer.getUsername()).putFile(Hasher.getImageUri(RegisterActivity.this, cameraPicture));
                allBenutzer.add(newBenutzer);

                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("benutzer", (Serializable) allBenutzer);
                i.putExtra("bundle", extra);
                startActivity(i);
            }
            else
            {
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Username is already taken", Snackbar.LENGTH_SHORT);

                View snackView = snack.getView();
                snackView.setBackgroundColor(ContextCompat.getColor(RegisterActivity.this, R.color.colorPrimary));
                snack.show();
            }
        }
        else
        {
            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Fields are empty", Snackbar.LENGTH_SHORT);

            View snackView = snack.getView();
            snackView.setBackgroundColor(ContextCompat.getColor(RegisterActivity.this, R.color.colorPrimary));
            snack.show();

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ID_IMAGE_CAPTURE)
        {
            if (resultCode == RESULT_OK)
            {
                cameraPicture = (Bitmap) data.getExtras().get("data");

            }
        }
    }
}
