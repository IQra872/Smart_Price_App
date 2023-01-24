package com.example.acs.myfyp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;


/**
 * Created by ACS on 3/3/2018.
 */

public class ImageFragment extends Fragment {


    ImageButton processText;
    ImageView viewImage;
    TextView viewText;
    Uri imageUri;
    Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.activity_text_processing,container,true);
        processText = (ImageButton) view.findViewById(R.id.processImage);
        viewImage =(ImageView) view.findViewById(R.id.imageView);
        viewText = (TextView) view.findViewById(R.id.viewText);
        processText.setBackgroundColor(Color.TRANSPARENT);
        String bbb = getArguments().getString("Image");
         bitmap = BitmapFactory.decodeFile(bbb);
        viewImage.setImageBitmap(bitmap);




        processText.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity().getApplicationContext()).build();
                if (!textRecognizer.isOperational()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Detector is not available yet", Toast.LENGTH_LONG);
                }
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                final SparseArray<TextBlock> items = textRecognizer.detect(frame);
                if (items.size() != 0) {
                    viewText.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < items.size(); i++) {
                                TextBlock item = items.valueAt(i);
                                stringBuilder.append(item.getValue());
                                stringBuilder.append("\n");
                            }

                            viewText.setText( getCurrencyFromString(stringBuilder.toString()));
                        }
                    });
                }
            }
        });


        return   super.onCreateView(inflater, container, savedInstanceState);
    }

    private String getCurrencyFromString(String string) {

        Pattern p = Pattern.compile("[rR][sS][.: ][0-9]*([.][0-9])*",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(string);
        Set<String> currency = new HashSet<String>();
        while (matcher.find()) {

            Log.d(TAG, "getCurrencyFromString " + currency);
        }
return currency.toString();
    }
}
