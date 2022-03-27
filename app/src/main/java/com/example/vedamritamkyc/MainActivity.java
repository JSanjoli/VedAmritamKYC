package com.example.vedamritamkyc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;


public class MainActivity extends AppCompatActivity {

    private Button addKYC;
    private EditText nameField;
    private EditText emailField;
    private EditText contactNumberField;
    private EditText addressField;
    private Context context;
    private Menu exportToCSV;
    private DBHandler dbHandler;
    Bitmap bitmap;
    private ImageView qrCode;
    private QRGEncoder qrgEncoder;
    private String whatsAppNumber = "https://wa.me/+918700434466";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);

        addKYC = findViewById(R.id.addKYCButton);
        nameField = (EditText) findViewById(R.id.name);
        emailField = (EditText) findViewById(R.id.email);
        contactNumberField = (EditText) findViewById(R.id.contact);
        addressField = (EditText) findViewById(R.id.address);
        exportToCSV = findViewById(R.id.exportCSV);
        qrCode = findViewById(R.id.qrCode);
        context = this.getBaseContext();

        dbHandler = new DBHandler(context);
        generateQRCode(qrCode);

        addKYC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (nameField.getText().length() == 0 || contactNumberField.getText().length() == 0) {
                    Toast.makeText(context, "Please add name and contact number", Toast.LENGTH_SHORT).show();
                } else {
                    String name = nameField.getText().toString();
                    String email = emailField != null ? emailField.getText().toString() : "";
                    Long contactNumber = new Long(contactNumberField.getText().toString());
                    String address = addressField != null ? addressField.getText().toString() : "";
                    dbHandler.addNewProspect(name, email, contactNumber, address);
                    Toast.makeText(context, "Information added", Toast.LENGTH_SHORT).show();
                    onClear();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.exportCSV:
                dbHandler.exportDB();
                Toast.makeText(context, "Exported to CSV", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private void generateQRCode(ImageView qrCode) {
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;

        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;

        qrgEncoder = new QRGEncoder(whatsAppNumber, null, QRGContents.Type.TEXT, dimen);
        // getting our qrcode in the form of bitmap.
        bitmap = qrgEncoder.getBitmap();
        qrCode.setImageBitmap(bitmap);
    }

    private void onClear() {
        if (nameField != null)
            nameField.setText(null);
        if (emailField != null)
            emailField.setText(null);
        if (contactNumberField != null)
            contactNumberField.setText(null);
        if (addressField != null)
            addressField.setText(null);
    }
}