

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);

        // Initialize views
        TextView pageTopTextView = findViewById(R.id.page_top_text_view);
        pageTopTextView.setText("About the com.example.liengua.app");

        ImageButton homeButton = findViewById(R.id.home_button);
        ImageButton menuButton = findViewById(R.id.menu_button);

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(AboutActivity.this, MainActivity.class);
            startActivity(intent);
        });

        String appName = getAppName();
        String versionName = getAppVersion();

        TextView appNameTextView = findViewById(R.id.app_name_text_view);
        appNameTextView.setText("Package name: " + appName);
        TextView versionTextView = findViewById(R.id.version_text_view);
        versionTextView.setText("Version: " + versionName);


        menuButton.setOnClickListener(this::showMenu);
    }

    private String getAppVersion() {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pInfo = pm.getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    private String getAppName() {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pInfo = pm.getPackageInfo(getPackageName(), 0);
            return pInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    private void showMenu(View view) {
        MenuHandler menuHandler = new MenuHandler(this);
        menuHandler.showMenu(view);
    }
}