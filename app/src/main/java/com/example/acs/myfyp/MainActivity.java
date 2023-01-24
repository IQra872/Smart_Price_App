package com.example.acs.myfyp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    //SurfaceView cameraView;
    TextView textView;
    ImageView imageView;
    Button select, take;
    ImageButton process;
    private static final int PICK_IMAGE = 100;
    Uri image_URI;
    // CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // inialize all the views
        process = (ImageButton) findViewById(R.id.process);
        textView = (TextView) findViewById(R.id.view_text);
        imageView = (ImageView) findViewById(R.id.image);
        select = (Button) findViewById(R.id.select);
        take = (Button) findViewById(R.id.take);

        // set listener of take photo button
        take.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

        //set listener of select photo button
        select.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openGallery(); // call openGallery function
                    }
                });

        //set listener of button which will process the image ie extract text from it
        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if (!textRecognizer.isOperational()) {
                    Toast.makeText(getApplicationContext(), "Detector is not available yet", Toast.LENGTH_LONG);
                }
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                final SparseArray<TextBlock> items = textRecognizer.detect(frame);
                if (items.size() != 0) {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < items.size(); i++) {
                                TextBlock item = items.valueAt(i);
                                stringBuilder.append(item.getValue());

                            }
                            textView.setText(stringBuilder.toString());
                            textView.append("\n" + getPriceFromString(stringBuilder.toString()));
                        }
                    });
                }
            }
        });


    }


    private void openGallery() {

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            image_URI = data.getData();
            imageView.setImageURI(image_URI);
            try {
                InputStream pic = getContentResolver().openInputStream(image_URI);
                bitmap = BitmapFactory.decodeStream(pic);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPriceFromString(String text) {
        String result;
        Pattern p = Pattern.compile(Pattern.quote("price")+
                        "[.:\\s][\\d]+|[Rr][Ss][.:\\s]*[\\d]+[\\.]*[\\d]*" +
                        "|$[\\d]+[\\.]*[\\d]*|[Uu][Ss][Dd][\\d]+[\\.]*[\\d]*",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(text);
        if (matcher.find()){
           Log.d("Extracted String", matcher.group());
        result = matcher.group().toString();
        return result;}
        else
        {
            return "match not found";
        }
    }
}







/*
        cameraView = (SurfaceView) findViewById(R.id.image_camera);
        textView = (TextView) findViewById(R.id.view_text);
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Toast.makeText(getApplicationContext(), "Detector is not available yet", Toast.LENGTH_LONG);
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(),textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                        cameraSource.stop();
                }
            });
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {

                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if(items.size() != 0){
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i=0; i<items.size(); i++){
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                textView.setText(stringBuilder.toString());
                            }
                        });
                    }

                }
            });
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case RequestCameraPermissionID:
            {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    return;
                }
                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/

