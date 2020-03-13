package in.tushar.vivoslingshot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class imageSling extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    public static final String TAG = "----CameraScreen----";
    ImageView imageView;
    String image,img;
    Bitmap myBitmap, bMapRotate;
    SensorManager sensorManager = null;
    float[] values;
    List list;
    private Socket socketClient;
    String url = "192.168.0.42";
    Boolean isSended=false;
    private RequestQueue queue;
    File imgFile;
    {
        try {
            socketClient = IO.socket("http://192.168.0.42:27015/androidSocket");
            socketClient.connect();
            Log.d(TAG, "http://" + url + ":27015/androidClient");
            socketClient.emit("Connect","");
            socketClient.on("connected", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
//                    JSONObject obj = (JSONObject)args[0];
                    Log.e(TAG,"Received Message : "+args);
                }
            });
        } catch (
                URISyntaxException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Not connect", Toast.LENGTH_SHORT).show();
        }

    }
    private Emitter.Listener onNewMessage = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            try {
                String msg = "message";

                //Toast.makeText(getApplicationContext(),""+msg,Toast.LENGTH_SHORT).show();
            } catch (Exception exp) {
                return;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_sling);
        image = getIntent().getStringExtra("Image");
        imageView = findViewById(R.id.image);
        Log.w(TAG," Image Source : "+image);
//        socketClient.connect();
        queue = Volley.newRequestQueue(this);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        list=sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        Log.e("-----", String.valueOf(sensorManager.getDefaultSensor(1)));
        Log.e("--list--", String.valueOf(list));
        if(list.size()>0){
            sensorManager.registerListener(sel,(Sensor)list.get(0),SensorManager.SENSOR_DELAY_NORMAL);
        }else {
            Toast.makeText(getBaseContext(), "Error: No Accelerometer.", Toast.LENGTH_LONG).show();
        }
        imgFile = new File(Environment.getExternalStorageDirectory(), "/SlingShot/"+image);
        if (imgFile.exists()) {
            Log.e(TAG ,"Image is exist : "+imgFile);
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
            img = convert(myBitmap);
            Log.e(TAG ,">>Base 64"+img);
            sendImageToServer(imgFile);
//            socketClient.emit("image",img);
        }else {
            Log.e(TAG ,"Image is not exist");
        }
    }
    public static String convert(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
    SensorEventListener sel = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            values = event.values;
            if(values[2]>8){
                if(isSended){
//                    sendImageToServer();
                    Log.e(TAG,"Image Sended");
                }else {
                    Log.e(TAG,"Already Sended");
                }
//                if(isConnected){
//                Log.e(TAG," Z :"+values[2]);
//                socketClient.emit("image",bMapRotate);
//                Log.e(TAG,"Sending Image......");
//                /sdcard/SlingShot/SlingShot_20200313_114430.jpg
//                }
            }else{
                Log.e(TAG,"Try to Sending......");
            }
        }
    };

    public void socketConnected(){
        Log.e(TAG,"Connecting........");
        try {
            socketClient = IO.socket("http://"+ url+":27015/androidClient");
            socketClient.connect();
            Log.d(TAG,"http://"+ url+":27015/androidClient");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Not connect", Toast.LENGTH_SHORT).show();
        }
        if(!socketClient.connected()){
            try{
                Emitter.Listener onConnect = new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.d(TAG, "connected " + socketClient.connected());
                    }
                };
                Emitter.Listener onConnectError = new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.d(TAG, "error " + args[0].toString());
                    }
                };
                socketClient.disconnect();
                socketClient.connect();
                socketClient.on(Socket.EVENT_CONNECT_ERROR,onConnectError);
                socketClient.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
                socketClient.on(Socket.EVENT_CONNECT, onConnect);
                socketClient.on(Socket.EVENT_RECONNECT, onConnect);
                socketClient.on(Socket.EVENT_DISCONNECT, onConnectError);
                Log.d(TAG,"Socket Connected"+socketClient);
                socketClient.emit("connect","Ready");
//                isConnected=true;

            }catch (Exception e){
                Log.d(TAG,"Exception :",e);
            }
        }else if(socketClient.connected()) {
            Log.e("--Socket--","is connected");
        }

    }
    public void sendImageToServer(final File image){
        Log.e(TAG,"Sending Image...");
        String url="http://192.168.0.70:8125/api/img";
        //converting image to base64 string

        try{
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Log.e(TAG,"Response : " +s);
                    Toast.makeText(imageSling.this, "Uploaded : " +s, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                    isSended = true;
                    if(s.equals("true")){
                        Toast.makeText(imageSling.this, "Uploaded Successful", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(imageSling.this, "Some error occurred!", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(imageSling.this, "Some error occurred -> "+error, Toast.LENGTH_LONG).show();
                    isSended = true;
                }
            }){
                //adding parameters to send
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("image", img);
                    return parameters;
                }
            };
            queue.add(stringRequest);

        }catch (Exception e){
            e.printStackTrace();
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
