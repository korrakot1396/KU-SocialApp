package ku.ac.th;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends AppCompatActivity {

    private EditText UserEmail, UserPassword, UserConfirmPassword;
    private Button CreateAccountButton;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        UserEmail = (EditText) findViewById(R.id.register_email);
        UserPassword = (EditText) findViewById(R.id.register_passwprd);
        UserConfirmPassword = (EditText) findViewById(R.id.register_confirm_password);
        CreateAccountButton = (Button) findViewById(R.id.register_create_account);
        loadingBar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            SendUserToMainActivity();
        }

    }
    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void CreateNewAccount() {

        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmpassword = UserConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"กรุณากรอกอีเมลล์",Toast.LENGTH_LONG).show();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"กรุณากรอกรหัสผ่าน",Toast.LENGTH_LONG).show();
        }else if (TextUtils.isEmpty(confirmpassword)){
            Toast.makeText(this,"กรุณากรอกยืนยันรหัสผ่าน",Toast.LENGTH_LONG).show();
        }else if (!password.equals(confirmpassword)){
            Toast.makeText(this,"รหัสผ่านไม่ตรงกัน",Toast.LENGTH_LONG).show();
        }else {

//            loadingBar.setTitle("สร้างบัญชีใหม่");
//            loadingBar.setMessage("กรุณารอสักครู่ บัญชีผู้ใช้กำลังถูกสร้าง...");
//            loadingBar.show();
//            loadingBar.setCanceledOnTouchOutside(true);

            final SweetAlertDialog pDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("บัญชีผู้ใช้กำลังถูกสร้าง...");
            pDialog.setCancelable(true);
            pDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                           if (task.isSuccessful())
                           {
                               SendUserToSetActivity();
                               DynamicToast.makeSuccess( RegisterActivity.this, "คุณลงทะเบียนสำเร็จ").show();
//                               Toast.makeText(RegisterActivity.this,"คุณลงทะเบียนสำเร็จ",Toast.LENGTH_LONG).show();
                               pDialog.dismiss();
                           }else{
                               String message = task.getException().getMessage();
                               DynamicToast.makeError( RegisterActivity.this, "เกิดข้อผิดพลาด"+message).show();
//                               Toast.makeText(RegisterActivity.this,"เกิดข้อผิดพลาด"+message,Toast.LENGTH_LONG).show();
                               pDialog.dismiss();
                           }
                        }
                    });
        }

    }

    private void SendUserToSetActivity() {
        Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
}
