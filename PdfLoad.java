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
        init();

        if (getIntent().getStringExtra("title").equals("Help & Support")) {
            binding.pdfLayout.setVisibility(View.GONE);
            binding.termsAndConditionLay.setVisibility(View.VISIBLE);
            binding.toolbar.setVisibility(View.GONE);
            binding.termAccess.headerAccess.headTitleTv.setText("Help & Support");
            binding.termAccess.headerAccess.headTitleTv.setVisibility(View.VISIBLE);
            getData();


        } else {
            binding.pdfLayout.setVisibility(View.VISIBLE);
            binding.termsAndConditionLay.setVisibility(View.GONE);
            binding.pdfView.fromAsset(getIntent().getStringExtra("pdf"))
                    .enableSwipe(true) // allows to block changing pages using swipe
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                    .password(null)
                    .scrollHandle(null)
                    .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                    .spacing(0)
                    .load();
        }

    }

    private void getData() {
        ShowLoading();
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObj = new JsonObjectRequest(Request.Method.GET, "https://secure.welfarebd.org/api/help_info", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        HideLoading();
                        Log.d("Responsed", String.valueOf(response));

                        String status = response.getString("status");
                        String msg = response.getString("msg");
                        if (status.equals("1")) {
                            JSONObject UserData = response.getJSONObject("help_and_support");
                            String name = UserData.getString("name");
                            String designtion = UserData.getString("designtion");
                            String contact = UserData.getString("contact");
                            String primary_email = UserData.getString("primary_email");
                            String secondary_email = UserData.getString("secondary_email");
                            String primary_phone = UserData.getString("primary_phone");
                            String secondary_phone = UserData.getString("secondary_phone");
                            String primary_mobile = UserData.getString("primary_mobile");
                            String secondary_mobile = UserData.getString("secondary_mobile");
                            String website = UserData.getString("website");
                            String finalWeb = website.substring(8);
                            binding.termAccess.name.setText("" + name);
                            binding.termAccess.mobileNumberOne.setText("" + primary_mobile);
                            binding.termAccess.mobileNumberTwo.setText("" + secondary_mobile);
                            binding.termAccess.contact.setText("" + contact);
                            binding.termAccess.mailOne.setText("" + primary_email);
                            binding.termAccess.mailTwo.setText("" + secondary_email);


                            binding.termAccess.website.setText("" + finalWeb);
                            binding.termAccess.designation.setText("" + designtion);
                            binding.termAccess.telephoneNumber.setText("" + primary_phone);


                        } else {
                            HideLoading();
                            ShowAlert(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        HideLoading();
                        ShowAlert("Invalid Response");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    HideLoading();
                    ShowAlert("Invalid Response");
                }
            }) {
                @Override
                public Map getHeaders() throws AuthFailureError {
                    Map headers = new HashMap();
                    headers = getHeaderAuthorization();
                    Log.d("HeaderData", String.valueOf(headers));
                    return headers;
                }
            };
            jsonObj.setShouldCache(false);
            jsonObj.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsonObj);
        } catch (Exception e) {
            HideLoading();
            ShowAlert("Invalid Response");
        }
    }

    private void init() {
        binding.termAccess.mobileNumberOne.setOnClickListener(this);
        binding.termAccess.mobileNumberTwo.setOnClickListener(this);
        binding.termAccess.contact.setOnClickListener(this);
        binding.termAccess.telephoneNumber.setOnClickListener(this);
        binding.termAccess.mailOne.setOnClickListener(this);
        binding.termAccess.mailTwo.setOnClickListener(this);
        binding.termAccess.website.setOnClickListener(this);
        binding.termAccess.websiteOne.setOnClickListener(this);
        binding.ok.backButton.setOnClickListener(this);
        binding.termAccess.headerAccess.backButton.setOnClickListener(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.mobileNumberOne) {
            phone = binding.termAccess.mobileNumberOne.getText().toString().trim();
            phoneCall();
            return;
        }
        if (view.getId() == R.id.mobileNumberTwo) {
            phone = binding.termAccess.mobileNumberTwo.getText().toString().trim();
            phoneCall();
            return;
        }
        if (view.getId() == R.id.contact) {
            phone = binding.termAccess.contact.getText().toString().trim();
            phoneCall();
            return;
        }
        if (view.getId() == R.id.telephoneNumber) {
            phone = binding.termAccess.telephoneNumber.getText().toString().trim();
            phoneCall();

            return;
        }
        if (view.getId() == R.id.mailOne) {
            startMail(binding.termAccess.mailOne.getText().toString());
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
        if (view.getId() == binding.termAccess.headerAccess.backButton.getId()) {
            onBackPressed();
            return;
        }
        if (view.getId() == R.id.website) {
            Intent intent = new Intent(PdfLoad.this, Webview.class);
            intent.putExtra("url", "https://" + binding.termAccess.website.getText().toString());
            intent.putExtra("title", "Welfare Website");
            startActivity(intent);
            return;
        }
        if (view.getId() == R.id.websiteOne) {
            Intent intent = new Intent(PdfLoad.this, Webview.class);
            intent.putExtra("url", "https://welfarefamily.org");
            intent.putExtra("title", "Welfare Website");
            startActivity(intent);
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