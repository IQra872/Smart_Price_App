package com.example.acs.myfyp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextProcessing extends AppCompatActivity {

    ImageButton processText;
    ImageView viewImage;
    TextView viewText;
    Uri imageUri;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_processing);

        processText = (ImageButton)findViewById(R.id.processImage);
        viewImage =(ImageView)findViewById(R.id.imageView);
        viewText = (TextView) findViewById(R.id.viewText);

        Intent i = getIntent();
        imageUri = Uri.parse( i.getStringExtra("Image_url"));
        viewImage.setImageURI(imageUri);

        try {
            InputStream pic = getContentResolver().openInputStream(imageUri);
            bitmap = BitmapFactory.decodeStream(pic);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        processText.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if (!textRecognizer.isOperational()) {
                    Toast.makeText(getApplicationContext(), "Detector is not available yet", Toast.LENGTH_LONG);
                }
                Frame frame = new Frame.Builder().setBitmap(BitmapFactory.decodeFile(imageUri.toString())).build();
                final SparseArray<TextBlock> items = textRecognizer.detect(frame);
                if (items.size() != 0) {
                    viewText.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < items.size(); i++) {
                                TextBlock item = items.valueAt(i);
                                stringBuilder.append(item.getValue());

                            }
                            viewText.setText(stringBuilder.toString());
                            viewText.append("\n"+getPriceFromString(stringBuilder.toString()));
                        }
                    });
                }
            }
        });

    }
    private String getPriceFromString(String text) {
        String result;
        Pattern p = Pattern.compile(Pattern.quote("price")+
                        "[.:\\s][\\d]+|[Rr][Ss][.:\\s]*[\\d]+[\\.]*[\\d]*" +
                        "|$[\\d]+[\\.]*[\\d]*|[Uu][Ss][Dd][\\d]+[\\.]*[\\d]*",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(text);
        if (matcher.find()) {
            Log.d("Extracted String", matcher.group().toString());
            result = matcher.group().toString();
            return result;
        }
            else
        return "no match found";
    }


    }




