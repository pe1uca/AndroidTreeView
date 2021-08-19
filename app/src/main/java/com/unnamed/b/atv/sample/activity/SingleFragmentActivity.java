package com.unnamed.b.atv.sample.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.unnamed.b.atv.sample.R;

/**
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class SingleFragmentActivity extends AppCompatActivity {
    public final static String FRAGMENT_PARAM = "fragment";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_single_fragment);

        Bundle b = getIntent().getExtras();
        Class<?> fragmentClass = (Class<?>) b.get(FRAGMENT_PARAM);
        if (bundle == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, (Class<? extends Fragment>) fragmentClass, b, fragmentClass.getName())
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
