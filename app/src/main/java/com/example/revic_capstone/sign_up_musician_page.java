package com.example.revic_capstone;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class sign_up_musician_page extends AppCompatActivity {

    private String category;

    private EditText et_username, et_password_signup,
            et_confirmPassword;
    private TextView tv_signIn, tv_terms, textView;
    private Button btn_signUp;
    private CheckBox checkBox_terms;

    private FirebaseAuth fAuth;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_musician_page);

        category = getIntent().getStringExtra("category");

        setRef();
        clickListener();

        textView.setText("Sign up (" + category + ")");
    }

    private void clickListener() {

        tv_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AlertDialog.Builder alert = new AlertDialog.Builder(sign_up_musician_page.this);
//                alert.setTitle("ServiceHub");
//
//                WebView webView = new WebView(sign_up_musician_page.this);
//                webView.loadUrl("file:///android_asset/" + termsHtml);
//                webView.setWebViewClient(new WebViewClient() {
//                    @Override
//                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                        view.loadUrl(url);
//
//                        return true;
//                    }
//                });
//
//                alert.setView(webView);
//                alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
//                    }
//                });
//                alert.show();
            }
        });

        tv_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(sign_up_musician_page.this, login_page.class);
                startActivity(intent);
            }
        });

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signUpUser();

            }
        });

    }

    private void signUpUser() {

        String username = et_username.getText().toString();
        String password = et_password_signup.getText().toString();
        String confirmPass = et_confirmPassword.getText().toString();


        if (TextUtils.isEmpty(username) )
        {
            et_username.setError("This field is required");
        }
        else if ( !Patterns.EMAIL_ADDRESS.matcher(username).matches())
        {
            et_username.setError("Incorrect Email Format");
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
        }
        else if (password.length() < 8)
        {
            Toast.makeText(this, "Password must be 8 or more characters", Toast.LENGTH_LONG).show();

        }
        else if (!isValidPassword(password))
        {
            new SweetAlertDialog(sign_up_musician_page.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error!.")
                    .setContentText("Please choose a stronger password. Try a mix of letters, numbers, and symbols.")
                    .show();

        }
        else if (TextUtils.isEmpty(confirmPass))
        {
            Toast.makeText(this, "Please confirm the password", Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(confirmPass))
        {
            Toast.makeText(this, "Password did not match", Toast.LENGTH_SHORT).show();
        }
        else if(!checkBox_terms.isChecked())
        {
            Toast.makeText(this, "Must agree with the Terms and Conditions to proceed", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(category.equals("Restaurant"))
            {
                Intent intent = new Intent(sign_up_musician_page.this, sign_up2_restaurant_page.class);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                intent.putExtra("category", category);
                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(sign_up_musician_page.this, sign_up2_musician_page.class);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                intent.putExtra("category", category);
                startActivity(intent);
            }

        }
    }

    private static boolean isValidPassword(String password) {


        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=?!#$%&()*+,./])"
                + "(?=\\S+$).{8,15}$";


        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);

        return m.matches();
    }

    private void setRef() {

        tv_signIn = findViewById(R.id.tv_signIn);
        tv_terms = findViewById(R.id.tv_terms);
        textView = findViewById(R.id.textView);

        et_username = findViewById(R.id.et_username);
        et_password_signup = findViewById(R.id.et_password_signup);
        et_confirmPassword = findViewById(R.id.et_confirmPassword);

        checkBox_terms = findViewById(R.id.checkBox_terms);

        btn_signUp = findViewById(R.id.btn_signUp);

        fAuth = FirebaseAuth.getInstance();
    }
}