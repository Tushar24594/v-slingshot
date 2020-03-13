package in.tushar.vivoslingshot;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 100;
    TextView nameText, phoneText;
    EditText name, phone;
    Button submit;
    Typeface regular;
    boolean doubleBackToExitPressedOnce = false;
    CSV csv = new CSV();
    private String[] neededPermissions = new String[]{CAMERA, WRITE_EXTERNAL_STORAGE};
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    boolean result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        regular = Typeface.createFromAsset(getAssets(),"fonts/AvenirNextLTPro-Regular.otf");
        nameText = findViewById(R.id.nameText);
        nameText.setTypeface(regular);
        phoneText = findViewById(R.id.phoneText);
        phoneText.setTypeface(regular);
        name = findViewById(R.id.name);
        name.setTypeface(regular);
        phone = findViewById(R.id.phone);
        phone.setTypeface(regular);
        submit = findViewById(R.id.submit);
        submit.setTypeface(regular);
        checkCameraPermission();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit.setScaleX((float)0.9);
                submit.setScaleY((float)0.9);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        submit.setScaleX((float)1.0);
                        submit.setScaleY((float)1.0);
                        if(name.getText().toString().trim().isEmpty()){
                            name.setError("Please enter your name");
                            name.requestFocus();
                            return;
                        }
//                        else if(phone.getText().toString().trim().length()<=10){
//                            phone.setError("Please enter your correct number");
//                            phone.requestFocus();
//                            return;
//                        }
                        else{
                                csv.saveDatatoCSV(name.getText().toString().trim(),phone.getText().toString().trim());
                                Intent camera = new Intent(getApplicationContext(),camera.class);
                                startActivity(camera);
                                finish();
                        }
                    }
                },300);

            }
        });
    }
    private final int MY_PERMISSIONS_REQUEST_USE_CAMERA = 0x00AF;
    private void checkCameraPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED) {
            Log.d("<<<<","Permission not available requesting permission");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_USE_CAMERA);
            int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);

            }
        } else {
            Log.d("<<<<","Permission has already granted");

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_USE_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("<<<<","permission was granted! Do your stuff");
                    int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);

                    }
                } else {
                    Log.d("<<<<","permission denied! Disable the function related with permission.");
                }
                return;
            }
        }
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
