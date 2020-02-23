package com.unnamed.b.atv.sample.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.unnamed.b.atv.sample.R;

/**
 * Created by Sumeet Patel on 24/02/2020.
 */

public class ImagePickerActivity extends Activity {

    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);
        openGallery();
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri imageUri = null;
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("imageUri", imageUri);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }

        setResult(resultCode, null);
        finish();
    }
}