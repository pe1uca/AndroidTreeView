package com.unnamed.b.atv.sample.activity;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.unnamed.b.atv.sample.R;

/**
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class SingleFragmentActivity extends ActionBarActivity {
    public final static String FRAGMENT_PARAM = "fragment";

    public interface ImagePickerListener {
        public void onImagePicked(Uri imageUri);
    }

    private ImagePickerListener imagePickerListener;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_single_fragment);

        Bundle b = getIntent().getExtras();
        Class<?> fragmentClass = (Class<?>) b.get(FRAGMENT_PARAM);
        if (bundle == null) {
            Fragment f = Fragment.instantiate(this, fragmentClass.getName());
            f.setArguments(b);
            getFragmentManager().beginTransaction().replace(R.id.fragment, f, fragmentClass.getName()).commit();
        }

        this.imagePickerListener = null;
    }

    public void setImagePickerListener(ImagePickerListener listener){
        this.imagePickerListener = listener;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (this.imagePickerListener != null) {
                Uri imageUri = data.getParcelableExtra("imageUri");
                this.imagePickerListener.onImagePicked(imageUri);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
