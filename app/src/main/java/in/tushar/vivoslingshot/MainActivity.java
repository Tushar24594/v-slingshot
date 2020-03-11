package in.tushar.vivoslingshot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView nameText, phoneText;
    EditText name, phone;
    Button submit;
    Typeface regular;
    boolean doubleBackToExitPressedOnce = false;
    CSV csv = new CSV();
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
