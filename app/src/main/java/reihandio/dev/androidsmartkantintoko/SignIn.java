package reihandio.dev.androidsmartkantintoko;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;
import reihandio.dev.androidsmartkantintoko.Common.Common;
import reihandio.dev.androidsmartkantintoko.Model.User;

public class SignIn extends AppCompatActivity {

    EditText editPhone, editPassword;
    Button btnSignIn;

    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editPassword=(MaterialEditText)findViewById(R.id.editPassword);
        editPhone=(MaterialEditText)findViewById(R.id.editPhone);
        btnSignIn = (FButton)findViewById(R.id.btnSignIn);

        //Inisasi Firebase
        db = FirebaseDatabase.getInstance();
        users = db.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser(editPhone.getText().toString(),editPassword.getText().toString());
                
            }
        });

    }

    private void signInUser(final String phone, String password) {
        final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
        mDialog.setMessage("Mohon Tunggu");
        mDialog.show();

        final String localPhone = phone;
        final String localPassword = password;
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(localPhone).exists()){
                    mDialog.dismiss();
                    User user = dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);
                    if(Boolean.parseBoolean(user.getIsStaff())){
                        // if IsStaff==True
                        if(user.getPassword().equals(localPassword)){

                            Intent login = new Intent(SignIn.this,Home.class);
                            Common.currentUser = user;
                            startActivity(login);
                            finish();

                        }
                        else
                            Toast.makeText(SignIn.this, "Password Salah!", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(SignIn.this, "Mohon Login dengan Akun Staff Toko", Toast.LENGTH_SHORT).show();
                }
                else{
                    mDialog.dismiss();
                    Toast.makeText(SignIn.this, "User tidak terdaftar di Database", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
