package com.welfaretechservices.welfare;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telecom.VideoProfile;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.barteksc.pdfviewer.PDFView;
import com.squareup.picasso.Picasso;
import com.welfaretechservices.welfare.databinding.ActivityPdfLoadBinding;
import com.welfaretechservices.welfare.registration.NewRegistrationFragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utility.APIUrl;

public class PdfLoad extends BaseActivity implements View.OnClickListener {


    private static final int REQUEST_CODE = 1;
    ActivityPdfLoadBinding binding;
    private static final int REQUEST_PHONE_CALL = 1;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pdf_load);
        binding.ok.appBarTitle.setText(getIntent().getStringExtra("title"));
        

    }

   

   

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.mobileNumberOne) {
            phone = binding.termAccess.mobileNumberOne.getText().toString().trim();
            phoneCall();
            return;
        }
         
        if (view.getId() == R.id.mailTwo) {
            startMail(binding.termAccess.mailTwo.getText().toString());
            return;
        }
        if (view.getId() == R.id.backButton) {
            onBackPressed();
            return;
        }
        
    }



    private void startMail(String mail) {
        try {
            String[] recipients = new String[]{mail};// Replace your email id here
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
            emailIntent.setType("text/plain");
            final PackageManager pm = getPackageManager();
            final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
            ResolveInfo best = null;
            for (final ResolveInfo info : matches)
                if (info.activityInfo.packageName.endsWith(".gm") ||
                        info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
            if (best != null)
                emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
            startActivity(emailIntent);
        } catch (Exception e) {
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void phoneCall() {
        if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phone));
            startActivity(callIntent);
        } else {
            Toast.makeText(this, "You don't have permission.", Toast.LENGTH_SHORT).show();
            // The app does not have the CALL_PHONE permission
            // Request the permission
            final String[] PERMISSIONS_STORAGE = {android.Manifest.permission.CALL_PHONE};
            //Asking request Permissions
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 9);

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permissionGranted = false;
        switch (requestCode) {
            case 9:
                permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (permissionGranted) {
            phoneCall();
        } else {
            Toast.makeText(this, "You don't assign permission.", Toast.LENGTH_SHORT).show();
        }
    }
}
