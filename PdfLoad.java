package com.welfaretechservices.welfare;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
 

public class PdfLoad extends AppCompatActivity {
   private static final int REQUEST_CODE = 1;
    ActivityPdfLoadBinding binding;
    private static final int REQUEST_PHONE_CALL = 1;
  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pdf_load);
        binding.ok.appBarTitle.setText(getIntent().getStringExtra("title"));
  
        binding.mobileNumberOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone = binding.termAccess.mobileNumberOne.getText().toString().trim();
                phoneCall(phone); 
            }
        });
        
        
            binding.mailTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String mail = binding.termAccess.mailTwo.getText().toString();
                startMail(mail); 
            }
        });
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
    private void phoneCall(String number) {
        if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));
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
