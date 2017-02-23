package ece420.lab6;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;


public class MainActivity extends AppCompatActivity {

    public static int appflag = 0;
    private Button buttonHE;
    private Button buttonGB;
    private Button buttonED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        buttonHE = (Button) findViewById(R.id.buttonHE);
        buttonGB = (Button) findViewById(R.id.buttonGB);
        buttonED = (Button) findViewById(R.id.buttonED);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);}

        buttonHE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appflag = 1;
                startActivity(new Intent(MainActivity.this, HistEq.class));
            }
        });

        buttonGB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appflag = 2;
                startActivity(new Intent(MainActivity.this, HistEq.class));
            }
        });

        buttonED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appflag = 3;
                startActivity(new Intent(MainActivity.this, HistEq.class));
            }
        });

    }
}
