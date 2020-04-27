package com.example.hitdaily;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    SetTime setTime = new SetTime();
    String[] year ={"1st","2nd","3rd","4th"};
    String[] branch = {"CSE", "ECE","IT","CE","ME","ChE","EE","AEIE","BT"};
    String[] sec = {"N/A","A","B","C"};
    String[] group = {"A","B"};
    String dept="Hello",section="World",yearString = "year", groupString = "group";
    String subject;
    String teacher;
    String room_no;
    String category;
    Users currentUser;
    ProgressBar progressBar;
    int counter=0;
    boolean nextPressed = false;
    boolean prevPressed = false;
    boolean initialPress = false;
    public static final int PICK_IMAGE_REQUEST=1;
    Uri mImageUri;
    StorageReference storageReference;
    DatabaseReference myDataBase;
    Bitmap bitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        /**
         * Checks for user already logged in or not
         * If null launches LogInPage or else Launches main activity
         **/
        if(firebaseUser == null) {
            launchLogIN();
        }
        else {
           launch();
        }
    }

    /**
     *Launches login page
     */

    public void launchLogIN(){
        setContentView(R.layout.signin);
        try {
            progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
        }catch (NullPointerException e1){
            e1.printStackTrace();
        }
    }

    /**
    * Gets user name, email id and password of the user.
    * Checks for correctness of all the fields.
    * If correct then creates the user,
    * then uploads the user name, email id and password to the database.
    * */
    public void createUser(View view){
        EditText emailID = findViewById(R.id.emailEditText);
        EditText password1 = findViewById(R.id.password);
        EditText password2 = findViewById(R.id.passwordReCheck);
        EditText name = findViewById(R.id.takeNameEditText);
        final Information information = new Information();

        if(emailID.getText().toString().equals("")){
            Toast.makeText(this,"email ID cannot be blank",Toast.LENGTH_LONG).show();
            return;
        }
        if(!emailID.getText().toString().contains("@")&&!(emailID.getText().toString().contains(".com")||emailID.getText().toString().contains(".in")
                ||emailID.getText().toString().contains(".edu"))){
            Toast.makeText(this,"Please enter a valid e-mail id",Toast.LENGTH_LONG).show();
            return;
        }
        if(password1.getText().toString().equals("")){
            Toast.makeText(this,"Password cannot be blank",Toast.LENGTH_LONG).show();
            return;
        }
        if(password1.getText().toString().length()<=5){
            Toast.makeText(this,"Please enter at least 6 digit password",Toast.LENGTH_LONG).show();
            return;
        }
        if(password2.getText().toString().equals("")){
            Toast.makeText(this,"Password didn't match\nPlease re enter",Toast.LENGTH_LONG).show();
            return;
        }
        if(password1.getText().toString().equals(password2.getText().toString())){

            information.name=name.getText().toString();
            information.email=emailID.getText().toString();

            Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();
            firebaseAuth.createUserWithEmailAndPassword(emailID.getText().toString(),password1.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                firebaseUser = firebaseAuth.getCurrentUser();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("User").child(firebaseUser.getUid()).child("info").setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(MainActivity.this,"All set !",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
            setContentView(R.layout.pref);
            setSpinner();
        }
        else{
            Toast.makeText(this,"Password didn't match\nPlease re enter",Toast.LENGTH_LONG).show();
            try {
                password1.setText("");
                password2.setText("");
            }catch (NullPointerException e1){
                e1.printStackTrace();
            }
        }
    }

    /**
    * Gives a alert dialog to user when logout is clicked from the menu item,
    * if yes then launches logout function.
    **/
    public void logout(MenuItem menuItem){
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage("Do you want to logout?");
      builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
              logOut();
              dialog.cancel();
          }
      });
      builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
              dialog.cancel();
          }
      });
      AlertDialog alertDialog = builder.create();
      alertDialog.show();
    }

    /**
    * Logs out the user and launches login page.
    * */
    public void  logOut(){
        firebaseAuth.signOut();
        launchLogIN();
    }


    /**
     * Checks for all field and then login's function through firebase email and password is called
     * */
    public void login(View view){
      final   EditText emailID = findViewById(R.id.emailSignIn);
       final EditText password1 = findViewById(R.id.passwordSignIn);
        if(emailID.getText().toString().equals("")){
            Toast.makeText(this,"email ID cannot be blank",Toast.LENGTH_LONG).show();
            return;
        }
        if(!emailID.getText().toString().contains("@")&&!(emailID.getText().toString().contains(".com")||emailID.getText().toString().contains(".in")
                ||emailID.getText().toString().contains(".edu"))){
            Toast.makeText(this,"Please enter a valid e-mail id",Toast.LENGTH_LONG).show();
            return;
        }
        if(password1.getText().toString().equals("")){
            Toast.makeText(this,"Password cannot be blank",Toast.LENGTH_LONG).show();
            return;
        }
        if(password1.getText().toString().length()<=5){
            Toast.makeText(this,"Please enter at least 6 digit password",Toast.LENGTH_LONG).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(emailID.getText().toString(),password1.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isComplete()) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        launch();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,"Login not successful\nPlease check email and password",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    /**
     * Setup class routine from the current day
     * */
    public void today() {
        TextView subject1 = findViewById(R.id.sub_view);
        TextView subject2 = findViewById(R.id.sub2_view);
        TextView subject3 = findViewById(R.id.sub3_view);
        TextView subject4 = findViewById(R.id.sub4_view);
        TextView subject5 = findViewById(R.id.sub5_view);
        TextView subject6 = findViewById(R.id.sub6_view);
        TextView subject7 = findViewById(R.id.sub7_view);
        TextView subject8 = findViewById(R.id.sub8_view);
        TextView subject9 = findViewById(R.id.sub9_view);
        TextView subject10 = findViewById(R.id.sub10_view);
        TextView teacher1 = findViewById(R.id.teacher_textView);
        TextView teacher2 = findViewById(R.id.teacher2_textView);
        TextView teacher3 = findViewById(R.id.teacher3_textView);
        TextView teacher4 = findViewById(R.id.teacher4_textView);
        TextView teacher5 = findViewById(R.id.teacher5_textView);
        TextView teacher6 = findViewById(R.id.teacher6_textView);
        TextView teacher7 = findViewById(R.id.teacher7_textView);
        TextView teacher8 = findViewById(R.id.teacher8_textView);
        TextView teacher9 = findViewById(R.id.teacher9_textView);
        TextView teacher10 = findViewById(R.id.teacher10_textView);
        TextView room1 = findViewById(R.id.category_textView);
        TextView room2 = findViewById(R.id.category2_textView);
        TextView room3 = findViewById(R.id.category3_textView);
        TextView room4 = findViewById(R.id.category4_textView);
        TextView room5 = findViewById(R.id.category5_textView);
        TextView room6 = findViewById(R.id.category6_textView);
        TextView room7 = findViewById(R.id.category7_textView);
        TextView room8 = findViewById(R.id.category8_textView);
        TextView room9 = findViewById(R.id.category9_textView);
        TextView room10 = findViewById(R.id.category10_textView);
        TextView time1 = findViewById(R.id.roomNO_textView);
        TextView time2 = findViewById(R.id.roomNO2_textView);
        TextView time3 = findViewById(R.id.roomNO3_textView);
        TextView time4 = findViewById(R.id.roomNO4_textView);
        TextView time5 = findViewById(R.id.roomNO5_textView);
        TextView time6 = findViewById(R.id.roomNO6_textView);
        TextView time7 = findViewById(R.id.roomNO7_textView);
        TextView time8 = findViewById(R.id.roomNO8_textView);
        TextView time9 = findViewById(R.id.roomNO9_textView);
        TextView time10 = findViewById(R.id.roomNO10_textView);
        TextView center = findViewById(R.id.todayCenter);
        try {
            room1.setText("");
            room2.setText("");
            room3.setText("");
            room4.setText("");
            room5.setText("");
            room6.setText("");
            room7.setText("");
            room8.setText("");
            room9.setText("");
            room10.setText("");
            teacher1.setText("");
            teacher2.setText("");
            teacher3.setText("");
            teacher4.setText("");
            teacher5.setText("");
            teacher6.setText("");
            teacher7.setText("");
            teacher8.setText("");
            teacher9.setText("");
            teacher10.setText("");
            subject1.setText("");
            subject2.setText("");
            subject3.setText("");
            subject4.setText("");
            subject5.setText("");
            subject6.setText("");
            subject7.setText("");
            subject8.setText("");
            subject9.setText("");
            subject10.setText("");
        }catch (NullPointerException e1){
            e1.printStackTrace();
        }
        TextView day = findViewById(R.id.day_TextView);
        GenerateId gid = new GenerateId();
        if(gid.dayID()==10||gid.dayID()==16)
        {
            try {
                center.setText("NO CLASSES");
                time1.setText("");
                room1.setText("");
                time2.setText("");
                room2.setText("");
                time3.setText("");
                room3.setText("");
                time4.setText("");
                room4.setText("");
                time5.setText("");
                room5.setText("");
                time6.setText("");
                room6.setText("");
                time7.setText("");
                room7.setText("");
                time8.setText("");
                room8.setText("");
                time9.setText("");
                room9.setText("");
                time10.setText("");
                room10.setText("");
            }catch (NullPointerException e1){
                e1.printStackTrace();
            }
        }
        try {
            if (gid.dayID() == 10) {
                day.setText("SUNDAY");
            }
            if (gid.dayID() == 11) {
                day.setText("MONDAY");
            }
            if (gid.dayID() == 12) {
                day.setText("TUESDAY");
            }
            if (gid.dayID() == 13) {
                day.setText("WEDNESDAY");
            }
            if (gid.dayID() == 14) {
                day.setText("THURSDAY");
            }
            if (gid.dayID() == 15) {
                day.setText("FRIDAY");
            }
            if (gid.dayID() == 16) {
                day.setText("SATURDAY");
            }
        }catch (NullPointerException e1){
            e1.printStackTrace();
        }
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mdatabaseReference = databaseReference.child("User").child(firebaseUser.getUid());
        List<Classes> list = new LinkedList<>();
        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    dept = dataSnapshot.child("pref").child("dept").getValue().toString();
                } catch (NullPointerException e) {
                    Toast.makeText(MainActivity.this, "Dept error", Toast.LENGTH_LONG).show();
                }
                try {
                    section = dataSnapshot.child("pref").child("section").getValue().toString();
                } catch (NullPointerException e) {
                    Toast.makeText(MainActivity.this, "Section error", Toast.LENGTH_LONG).show();
                }
                try {
                    yearString = dataSnapshot.child("pref").child("year").getValue().toString();
                } catch (NullPointerException e) {
                    Toast.makeText(MainActivity.this, "Year error", Toast.LENGTH_LONG).show();
                }
                try {
                    groupString = dataSnapshot.child("pref").child("group").getValue().toString();
                } catch (NullPointerException e) {
                    Toast.makeText(MainActivity.this, "Group error", Toast.LENGTH_LONG).show();
                }
                GenerateClassId generateClassId = new GenerateClassId(dept, section, yearString, groupString);
                long deptId = generateClassId.dept(dept);
                long sectionId = generateClassId.section(section);
                long yearId = generateClassId.year(yearString);
                long groupId = generateClassId.group(groupString);
                GenerateId generateId = new GenerateId();
                int time = (generateId.dayID()*100)+10;
                int i = 0;
                Classes classes = new Classes();
                while(i<10){
                    int clac = i;
                    final String id = Long.toString(yearId) + Long.toString(deptId) + Long.toString(sectionId) + Long.toString(groupId) + Long.toString(time);
                    DatabaseReference dbr  = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference mdbr = dbr.child("classes").child(id);
                    int finalTime = time;
                    mdbr.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                classes.subject = dataSnapshot.child("subject").getValue().toString();
                            } catch (NullPointerException e) {
                                try {
                                    if (clac == 0) {
                                        time1.setText("");
                                        room1.setText("");
                                    }
                                    if (clac == 1) {
                                        time2.setText("");
                                        room2.setText("");
                                    }
                                    if (clac == 2) {
                                        time3.setText("");
                                        room3.setText("");
                                    }
                                    if (clac == 3) {
                                        time4.setText("");
                                        room4.setText("");
                                    }
                                    if (clac == 4) {
                                        time5.setText("");
                                        room5.setText("");
                                    }
                                    if (clac == 5) {
                                        time6.setText("");
                                        room6.setText("");
                                    }
                                    if (clac == 6) {
                                        time7.setText("");
                                        room7.setText("");
                                    }
                                    if (clac == 7) {
                                        time8.setText("");
                                        room8.setText("");
                                    }
                                    if (clac == 8) {
                                        time9.setText("");
                                        room9.setText("");
                                    }
                                    if (clac == 9) {
                                        time10.setText("");
                                        room10.setText("");
                                    }
                                    return;
                                } catch (NullPointerException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            try {
                                classes.subject=dataSnapshot.child("subject").getValue().toString();
                            }catch (NullPointerException e){
                                return;
                            }
                            try {
                                classes.teacher=dataSnapshot.child("teacher").getValue().toString();
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                            try {
                                classes.room_no=dataSnapshot.child("room_no").getValue().toString();
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                            try {
                                classes.category=dataSnapshot.child("category").getValue().toString();
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                            list.add(classes);
                            if(clac==0) {
                                try {
                                    subject1.setText(list.get(0).getSubject());
                                    teacher1.setText(list.get(0).getTeacher());
                                    room1.setText((list.get(0).getRoom_no()));
                                }catch (NullPointerException e1){
                                    e1.printStackTrace();
                                }
                            }
                            if(clac==1) {
                                try {
                                    subject2.setText(list.get(0).getSubject());
                                    teacher2.setText(list.get(0).getTeacher());
                                    room2.setText((list.get(0).getRoom_no()));
                                }catch (NullPointerException e1){
                                    e1.printStackTrace();
                                }
                            }
                            if(clac==2) {
                                try {
                                    subject3.setText(list.get(0).getSubject());
                                    teacher3.setText(list.get(0).getTeacher());
                                    room3.setText((list.get(0).getRoom_no()));
                                }catch (NullPointerException e1){
                                    e1.printStackTrace();
                                }
                            }
                            if(clac==3) {
                                try {
                                    subject4.setText(list.get(0).getSubject());
                                    teacher4.setText(list.get(0).getTeacher());
                                    room4.setText((list.get(0).getRoom_no()));
                                }catch (NullPointerException e1){
                                    e1.printStackTrace();
                                }
                            }
                            if(clac==4) {
                                try {
                                    subject5.setText(list.get(0).getSubject());
                                    teacher5.setText(list.get(0).getTeacher());
                                    room5.setText((list.get(0).getRoom_no()));
                                }catch (NullPointerException e1){
                                    e1.printStackTrace();
                                }
                            }
                            if(clac==5) {
                                try {
                                    subject6.setText(list.get(0).getSubject());
                                    teacher6.setText(list.get(0).getTeacher());
                                    room6.setText((list.get(0).getRoom_no()));
                                }catch (NullPointerException e1){
                                    e1.printStackTrace();
                                }
                            }
                            if(clac==6) {
                                try {
                                    subject7.setText(list.get(0).getSubject());
                                    teacher7.setText(list.get(0).getTeacher());
                                    room7.setText((list.get(0).getRoom_no()));
                                }catch (NullPointerException e1){
                                    e1.printStackTrace();
                                }
                            }
                            if(clac==7) {
                                try {
                                    subject8.setText(list.get(0).getSubject());
                                    teacher8.setText(list.get(0).getTeacher());
                                    room8.setText((list.get(0).getRoom_no()));
                                }catch (NullPointerException e1){
                                    e1.printStackTrace();
                                }
                            }
                            if(clac==8) {
                                try {
                                    subject9.setText(list.get(0).getSubject());
                                    teacher9.setText(list.get(0).getTeacher());
                                    room9.setText((list.get(0).getRoom_no()));
                                }catch (NullPointerException e1){
                                    e1.printStackTrace();
                                }
                            }
                            if(clac==9) {
                                try {
                                    subject10.setText(list.get(0).getSubject());
                                    teacher10.setText(list.get(0).getTeacher());
                                    room10.setText((list.get(0).getRoom_no()));
                                }catch (NullPointerException e1){
                                    e1.printStackTrace();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    i++;
                    time++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

        /**
         * Setup class routine to the day user requested.
         * */
        public int nextDay(final int counter){
            int value = 0;
            TextView subject1 = findViewById(R.id.sub_view);
            TextView subject2 = findViewById(R.id.sub2_view);
            TextView subject3 = findViewById(R.id.sub3_view);
            TextView subject4 = findViewById(R.id.sub4_view);
            TextView subject5 = findViewById(R.id.sub5_view);
            TextView subject6 = findViewById(R.id.sub6_view);
            TextView subject7 = findViewById(R.id.sub7_view);
            TextView subject8 = findViewById(R.id.sub8_view);
            TextView subject9 = findViewById(R.id.sub9_view);
            TextView subject10 = findViewById(R.id.sub10_view);
            TextView time1 = findViewById(R.id.roomNO_textView);
            TextView time2 = findViewById(R.id.roomNO2_textView);
            TextView time3 = findViewById(R.id.roomNO3_textView);
            TextView time4 = findViewById(R.id.roomNO4_textView);
            TextView time5 = findViewById(R.id.roomNO5_textView);
            TextView time6 = findViewById(R.id.roomNO6_textView);
            TextView time7 = findViewById(R.id.roomNO7_textView);
            TextView time8 = findViewById(R.id.roomNO8_textView);
            TextView time9 = findViewById(R.id.roomNO9_textView);
            TextView time10 = findViewById(R.id.roomNO10_textView);
            TextView teacher1 = findViewById(R.id.teacher_textView);
            TextView teacher2 = findViewById(R.id.teacher2_textView);
            TextView teacher3 = findViewById(R.id.teacher3_textView);
            TextView teacher4 = findViewById(R.id.teacher4_textView);
            TextView teacher5 = findViewById(R.id.teacher5_textView);
            TextView teacher6 = findViewById(R.id.teacher6_textView);
            TextView teacher7 = findViewById(R.id.teacher7_textView);
            TextView teacher8 = findViewById(R.id.teacher8_textView);
            TextView teacher9 = findViewById(R.id.teacher9_textView);
            TextView teacher10 = findViewById(R.id.teacher10_textView);
            TextView room1 = findViewById(R.id.category_textView);
            TextView room2 = findViewById(R.id.category2_textView);
            TextView room3 = findViewById(R.id.category3_textView);
            TextView room4 = findViewById(R.id.category4_textView);
            TextView room5 = findViewById(R.id.category5_textView);
            TextView room6 = findViewById(R.id.category6_textView);
            TextView room7 = findViewById(R.id.category7_textView);
            TextView room8 = findViewById(R.id.category8_textView);
            TextView room9 = findViewById(R.id.category9_textView);
            TextView room10 = findViewById(R.id.category10_textView);
            TextView center = findViewById(R.id.todayCenter);
            try {
                room1.setText("");
                room2.setText("");
                room3.setText("");
                room4.setText("");
                room5.setText("");
                room6.setText("");
                room7.setText("");
                room8.setText("");
                room9.setText("");
                room10.setText("");
                teacher1.setText("");
                teacher2.setText("");
                teacher3.setText("");
                teacher4.setText("");
                teacher5.setText("");
                teacher6.setText("");
                teacher7.setText("");
                teacher8.setText("");
                teacher9.setText("");
                teacher10.setText("");
                subject1.setText("");
                subject2.setText("");
                subject3.setText("");
                subject4.setText("");
                subject5.setText("");
                subject6.setText("");
                subject7.setText("");
                subject8.setText("");
                subject9.setText("");
                subject10.setText("");
            }catch (NullPointerException e1){
                e1.printStackTrace();
            }
            TextView day = findViewById(R.id.day_TextView);
            GenerateId gid = new GenerateId();
            int dayNo = gid.dayID();
            dayNo=dayNo+counter;
            if(dayNo==10||dayNo==16) {
                try {
                    center.setText("NO CLASSES");
                    time1.setText("");
                    room1.setText("");
                    time2.setText("");
                    room2.setText("");
                    time3.setText("");
                    room3.setText("");
                    time4.setText("");
                    room4.setText("");
                    time5.setText("");
                    room5.setText("");
                    time6.setText("");
                    room6.setText("");
                    time7.setText("");
                    room7.setText("");
                    time8.setText("");
                    room8.setText("");
                    time9.setText("");
                    room9.setText("");
                    time10.setText("");
                    room10.setText("");
                }catch (NullPointerException e1){
                    e1.printStackTrace();
                }
            }
            try {
                if (dayNo / 17 > 0) {
                    return 2;
                }
                if ((dayNo) == 10) {
                    day.setText("SUNDAY");
                    return 1;
                }
                if (dayNo == 11) {
                    day.setText("MONDAY");
                }
                if (dayNo == 12) {
                    day.setText("TUESDAY");
                }
                if (dayNo == 13) {
                    day.setText("WEDNESDAY");
                }
                if (dayNo == 14) {
                    day.setText("THURSDAY");
                }
                if (dayNo == 15) {
                    day.setText("FRIDAY");
                }
                if (dayNo == 16) {
                    day.setText("SATURDAY");
                    return 2;
                }
            }catch (NullPointerException e1){
                e1.printStackTrace();
            }
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference mdatabaseReference = databaseReference.child("User").child(firebaseUser.getUid());
            List<Classes> list = new LinkedList<>();
            mdatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        dept = dataSnapshot.child("pref").child("dept").getValue().toString();
                    } catch (NullPointerException e) {
                        Toast.makeText(MainActivity.this, "Dept error", Toast.LENGTH_LONG).show();
                    }
                    try {
                        section = dataSnapshot.child("pref").child("section").getValue().toString();
                    } catch (NullPointerException e) {
                        Toast.makeText(MainActivity.this, "Section error", Toast.LENGTH_LONG).show();
                    }
                    try {
                        yearString = dataSnapshot.child("pref").child("year").getValue().toString();
                    } catch (NullPointerException e) {
                        Toast.makeText(MainActivity.this, "Year error", Toast.LENGTH_LONG).show();
                    }
                    try {
                        groupString = dataSnapshot.child("pref").child("group").getValue().toString();
                    } catch (NullPointerException e) {
                        Toast.makeText(MainActivity.this, "Group error", Toast.LENGTH_LONG).show();
                    }
                    GenerateClassId generateClassId = new GenerateClassId(dept, section, yearString, groupString);
                    long deptId = generateClassId.dept(dept);
                    long sectionId = generateClassId.section(section);
                    long yearId = generateClassId.year(yearString);
                    long groupId = generateClassId.group(groupString);
                    GenerateId generateId = new GenerateId();
                    int dayNumber = generateId.dayID()+counter;

                    if(dayNumber<10){
                        dayNumber=16;
                    }
                    if(dayNumber>16){
                        dayNumber=10;
                    }
                    int time = (dayNumber*100)+10;
                    int i = 0;
                    Classes classes = new Classes();
                    while(i<10){
                        int clac = i;
                        final String id = Long.toString(yearId) + Long.toString(deptId) + Long.toString(sectionId) + Long.toString(groupId) + Long.toString(time);
                        DatabaseReference dbr  = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference mdbr = dbr.child("classes").child(id);
                        int finalTime = time;
                        mdbr.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    classes.subject = dataSnapshot.child("subject").getValue().toString();
                                } catch (NullPointerException e) {
                                    try {
                                        if (clac == 0) {
                                            time1.setText("");
                                            room1.setText("");
                                        }
                                        if (clac == 1) {
                                            time2.setText("");
                                            room2.setText("");
                                        }
                                        if (clac == 2) {
                                            time3.setText("");
                                            room3.setText("");
                                        }
                                        if (clac == 3) {
                                            time4.setText("");
                                            room4.setText("");
                                        }
                                        if (clac == 4) {
                                            time5.setText("");
                                            room5.setText("");
                                        }
                                        if (clac == 5) {
                                            time6.setText("");
                                            room6.setText("");
                                        }
                                        if (clac == 6) {
                                            time7.setText("");
                                            room7.setText("");
                                        }
                                        if (clac == 7) {
                                            time8.setText("");
                                            room8.setText("");
                                        }
                                        if (clac == 8) {
                                            time9.setText("");
                                            room9.setText("");
                                        }
                                        if (clac == 9) {
                                            time10.setText("");
                                            room10.setText("");
                                        }
                                        return;
                                    } catch (NullPointerException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                                try {
                                    classes.teacher = dataSnapshot.child("teacher").getValue().toString();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    classes.room_no = dataSnapshot.child("room_no").getValue().toString();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    classes.category = dataSnapshot.child("category").getValue().toString();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                                list.add(classes);
                                try {
                                    if (clac == 0) {
                                        subject1.setText(list.get(0).getSubject());
                                        teacher1.setText(list.get(0).getTeacher());
                                        room1.setText((list.get(0).getRoom_no()));
                                    }
                                    if (clac == 1) {
                                        subject2.setText(list.get(0).getSubject());
                                        teacher2.setText(list.get(0).getTeacher());
                                        room2.setText((list.get(0).getRoom_no()));
                                    }
                                    if (clac == 2) {
                                        subject3.setText(list.get(0).getSubject());
                                        teacher3.setText(list.get(0).getTeacher());
                                        room3.setText((list.get(0).getRoom_no()));
                                    }
                                    if (clac == 3) {
                                        subject4.setText(list.get(0).getSubject());
                                        teacher4.setText(list.get(0).getTeacher());
                                        room4.setText((list.get(0).getRoom_no()));
                                    }
                                    if (clac == 4) {
                                        subject5.setText(list.get(0).getSubject());
                                        teacher5.setText(list.get(0).getTeacher());
                                        room5.setText((list.get(0).getRoom_no()));
                                    }
                                    if (clac == 5) {
                                        subject6.setText(list.get(0).getSubject());
                                        teacher6.setText(list.get(0).getTeacher());
                                        room6.setText((list.get(0).getRoom_no()));
                                    }
                                    if (clac == 6) {
                                        subject7.setText(list.get(0).getSubject());
                                        teacher7.setText(list.get(0).getTeacher());
                                        room7.setText((list.get(0).getRoom_no()));
                                    }
                                    if (clac == 7) {
                                        subject8.setText(list.get(0).getSubject());
                                        teacher8.setText(list.get(0).getTeacher());
                                        room8.setText((list.get(0).getRoom_no()));
                                    }
                                    if (clac == 8) {
                                        subject9.setText(list.get(0).getSubject());
                                        teacher9.setText(list.get(0).getTeacher());
                                        room9.setText((list.get(0).getRoom_no()));
                                    }
                                    if (clac == 9) {
                                        subject10.setText(list.get(0).getSubject());
                                        teacher10.setText(list.get(0).getTeacher());
                                        room10.setText((list.get(0).getRoom_no()));
                                    }
                                }catch (NullPointerException e1){
                                    e1.printStackTrace();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        i++;
                        time++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            return value;
        }
        //Has to be implemented for navigation view
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return true;
        }

        /**
         * Directs user to Heritage Institute of Technology's official web site.
         * */
        public void collegeSite(MenuItem menuItem){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.heritageit.edu/"));
            startActivity(intent);
        }

        /**
         * Directs user to Heritage Institute of Technology's official web site's notice section.
         * */
        public void noticeSite(MenuItem menuItem){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.heritageit.edu/Notice.aspx"));
            startActivity(intent);
        }

        /**
         * Directs user to Heritage Institute of Technology's official web site's examination section.
         * */
        public void examinationSite(MenuItem menuItem){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.heritageit.edu/ExamCell.aspx"));
            startActivity(intent);
        }

        /**
         * Directs user to email application to send developer an email.
         * */
        public void toEmail(View view){
            Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto","amansingh8066@gmail.com",null));
            intent.putExtra(Intent.EXTRA_SUBJECT,"Report HIT Daily");
            startActivity(Intent.createChooser(intent,"Choose an email client :"));
        }

        /**
         * Directs user to GitHub code of this application.
         * */
        public void toGitHub(View view){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/amansingh-52/HIT-Daily"));
            startActivity(intent);
        }
    /**
     * Setup routine view and drawerLayout.
     * */
    public void setDayView(){
        setContentView(R.layout.mainfortoday);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        setNameAndEmail();
        setProfileImageDrawer();
    }

    /**
     * Launches routine from today routine.
     * */
    public void todaySetUp(MenuItem menuItem){
        setDayView();
        today();
    }

    /**
     * Takes user to next day routine when next is pressed.
     * */
    public void next(View view){
        setDayView();
        GenerateId gid = new GenerateId();
        if(prevPressed){
            counter+=2;
            prevPressed=false;
        }
        if(!initialPress){
            counter++;
            if(gid.dayID()==16){
                counter=0;
            }
        }
        if(((gid.dayID()+counter)>16)||((gid.dayID()+counter)<10))
        {
            counter=0;
        }
            int value = nextDay(counter);
            if (value == 2) {
                counter = 10 - gid.dayID();
            } else counter++;
            nextPressed=true;
            initialPress=true;
    }

    /**
     * Takes user to previous day routine when prev is pressed.
     * */
    public void prev(View view){
        setDayView();
        GenerateId gid = new GenerateId();
        if(nextPressed){
            counter-=2;
            nextPressed=false;
        }
        if(!initialPress){
            counter--;
        }
        if(((gid.dayID()+counter)>16)||((gid.dayID()+counter)<10))
        {   counter=0;
            return;
        }
            int value = nextDay(counter);
           if (value == 1) {
                counter = 16 - gid.dayID();
            } else counter--;
            prevPressed=true;
            initialPress=true;
        }

        /**
         * Launches help page and setup drawerLayout.
         * */
        public void help(MenuItem menuItem){
            setContentView(R.layout.mainforhelp);
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            drawerLayout = findViewById(R.id.drawer);
            navigationView = findViewById(R.id.navigation_view);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            actionBarDrawerToggle.syncState();
            setNameAndEmail();
            setProfileImageDrawer();
            initialPress=false;
            nextPressed=false;
            prevPressed=false;
        }


    /**
     * Uploads user preference.
     * */
    public void uploadData(Users users){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("User").child(firebaseUser.getUid()).child("pref").setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this,"All set !",Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     *Gives Alert to user he wants to upload profile picture or not,
     * if yes launches choose image from device.
     * */
    public void profilePictureClicked(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to upload image?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_IMAGE_REQUEST);
                dialog.cancel();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    /**
     *Checks for whether image is chosen or not,
     * if yes then compresses the image to 40% of its size.
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
             data != null && data.getData() != null){
            mImageUri =data.getData();

            try {
                storageReference = FirebaseStorage.getInstance().getReference("profile");
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(),mImageUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG,40,byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                uploadImage(byteArray);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *Uploads image to the firebase storage and generates download url and stores download url to firebase database.
     * */
    private  void uploadImage(byte[] bytes){
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        if(mImageUri != null){
            StorageReference storageReference1 = storageReference.child(firebaseUser.getUid()+"."+getFileExtension(mImageUri));
            ProgressBar uploadProgressBar = findViewById(R.id.uploadProgressBar);
            uploadProgressBar.setVisibility(View.VISIBLE);
            UploadTask uploadTask =  storageReference1.putBytes(bytes);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    myDataBase =FirebaseDatabase.getInstance().getReference("User");
                    DatabaseReference userDataBase = myDataBase.child(firebaseUser.getUid()).child("profile image");
                    ProgressBar uploadProgressBar = findViewById(R.id.uploadProgressBar);
                    uploadProgressBar.setVisibility(View.GONE);
                    storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            progressDialog.dismiss();
                            String url = uri.toString();
                            Toast.makeText(MainActivity.this,"Uploaded!",Toast.LENGTH_LONG).show();
                            ImageUpload imageUpload = new ImageUpload("profile url",url);
                            userDataBase.setValue(imageUpload);
                            setProfileImageDrawer();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this,"Unable to upload",Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/ taskSnapshot.getTotalByteCount());
                    progressDialog.show();
                    ProgressBar progressBar = findViewById(R.id.uploadProgressBar);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress((int) progress);
                    progressDialog.setMessage("Uploading image");
                    progressDialog.setCancelable(false);
                }
            });
        }else {
            Toast.makeText(this,"Invalid image",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Sets profile image to the drawer.
     * */
    private void setProfileImageDrawer(){
        myDataBase =FirebaseDatabase.getInstance().getReference("User");
        try {
            DatabaseReference userDataBase = myDataBase.child(firebaseUser.getUid()).child("profile image").child("mImageUri");
            userDataBase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    CircleImageView drawerImage = findViewById(R.id.drawerImage);
                    try {
                        Picasso.get().load(dataSnapshot.getValue().toString()).into(drawerImage);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Sets profile image in profile section.
     * */
    private void setImage(){
        myDataBase =FirebaseDatabase.getInstance().getReference("User");
        try {
            DatabaseReference userDataBase = myDataBase.child(firebaseUser.getUid()).child("profile image").child("mImageUri");
            userDataBase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    CircleImageView drawerImage = findViewById(R.id.p_profileImage);
                    try {
                        Picasso.get().load(dataSnapshot.getValue().toString()).into(drawerImage);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Sets user information.
     * */
    private void setUserInfo(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        final DatabaseReference mdatabaseReference = databaseReference.child("User").child(firebaseUser.getUid());
        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    dept = dataSnapshot.child("pref").child("dept").getValue().toString();
                } catch (NullPointerException e) {
                    Toast.makeText(MainActivity.this, "Dept error", Toast.LENGTH_LONG).show();
                }
                try {
                    section = dataSnapshot.child("pref").child("section").getValue().toString();
                    if(section.equals("N/A")){
                        section="";
                    }
                } catch (NullPointerException e) {
                    Toast.makeText(MainActivity.this, "Section error", Toast.LENGTH_LONG).show();
                }
                try {
                    yearString = dataSnapshot.child("pref").child("year").getValue().toString();
                } catch (NullPointerException e) {
                    Toast.makeText(MainActivity.this, "Year error", Toast.LENGTH_LONG).show();
                }
                try {
                    groupString = dataSnapshot.child("pref").child("group").getValue().toString();
                } catch (NullPointerException e) {
                    Toast.makeText(MainActivity.this, "Group error", Toast.LENGTH_LONG).show();
                }
                try {
                    TextView userInfo = findViewById(R.id.p_UserInfo);
                    userInfo.setText(dept + " -" + section + " \"" + groupString + "\" (" + yearString + ")");
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    /**
     * Gets file extension from URI.
     * */
    private String getFileExtension (Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cR.getType(uri));
    }

    /**
     * Launches home screen
     * */
    public void mainScreen (MenuItem menuItem){
        launch();
    }

    /**
     * Launches sign up page.
     * */
    public void signUp(View view){
        setContentView(R.layout.signup);
    }

    /**
     * Launches sign in when back to sign button is pressed.
     * */
    public void signIn(View view){
        setContentView(R.layout.signin);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Set up home screen and drawerLayout.
     * */
    void launch() {
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        setUpHomeScreen();
        initialPress=false;
        nextPressed=false;
        prevPressed=false;
    }

    /**
     * Launches preference to change it from menu item
     * */
    public void toLogIN(MenuItem item){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to change preference?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setContentView(R.layout.pref);
                setSpinner();
                dialog.cancel();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Saves preferences and launch home screen.
     * */
    public void toHome(View view){
       launch();
       uploadData(currentUser);
    }

    /**
     * Set up home screen.
     * */
    public void setUpHomeScreen() {
        final ProgressBar progressBar1 = findViewById(R.id.nowClassProgressBar);
        final ProgressBar nextProgressBar = findViewById(R.id.nextClassProgressBar);
        progressBar1.setVisibility(View.VISIBLE);
        final TextView textView1 = findViewById(R.id.classNameText);
        final TextView timeLeftTextView = (TextView) findViewById(R.id.nowClassStartTime);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        final TextView time = findViewById(R.id.timeTextView);
        final GenerateId generateId = new GenerateId();
        setProfileImageDrawer();
        final TextView classTextView = (TextView) findViewById(R.id.classNameText);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mdbr = databaseReference.child("notice");
        mdbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TextView notice = findViewById(R.id.notice);
                try {
                    notice.setText(dataSnapshot.getValue().toString());
                }catch (NullPointerException e1){
                    e1.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final DatabaseReference mdatabaseReference = databaseReference.child("User").child(firebaseUser.getUid());
        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    dept = dataSnapshot.child("pref").child("dept").getValue().toString();
                }catch (NullPointerException e){
                    Toast.makeText(MainActivity.this,"Dept error",Toast.LENGTH_LONG).show();
                }
                try {
                    section = dataSnapshot.child("pref").child("section").getValue().toString();
                }catch (NullPointerException e){
                    Toast.makeText(MainActivity.this,"Section error",Toast.LENGTH_LONG).show();
                }
                try{
                    yearString = dataSnapshot.child("pref").child("year").getValue().toString();
                }catch (NullPointerException e){
                    Toast.makeText(MainActivity.this,"Year error",Toast.LENGTH_LONG).show();
                }
                try {
                    groupString = dataSnapshot.child("pref").child("group").getValue().toString();
                }catch (NullPointerException e){
                    Toast.makeText(MainActivity.this,"Group error",Toast.LENGTH_LONG).show();
                }
                progressBar1.setVisibility(View.GONE);
                nextProgressBar.setVisibility(View.GONE);

                TextView classID = (TextView) findViewById(R.id.nowclassId);
                GenerateClassId generateClassId = new GenerateClassId(dept, section, yearString, groupString);
                classTextView.setText(dept + " -" + section + " \"" + groupString + "\" (" + yearString + ")");
                long deptId = generateClassId.dept(dept);
                long sectionId = generateClassId.section(section);
                long yearId = generateClassId.year(yearString);
                long groupId = generateClassId.group(groupString);
                final long timeId = generateId.generate();
                final String id = Long.toString(yearId) + Long.toString(deptId) + Long.toString(sectionId) + Long.toString(groupId) + Long.toString(timeId);
                try {
                    classID.setText("@classID\n" + id);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                final TimeLeftCalculation timeLeftCalculation = new TimeLeftCalculation();
                final TextView currentSubject = (TextView) findViewById(R.id.nowSubject);
                final TextView currentTeacher = (TextView) findViewById(R.id.nowTeacher);
                final TextView currentRoom = (TextView) findViewById(R.id.nowRoom_no);
                final TextView currentCategory = (TextView) findViewById(R.id.nowCategory);
                final TextView nowTextView = (TextView) findViewById(R.id.nowText);
                final TextView nextText = findViewById(R.id.nextText);
                final TextView nextTime = findViewById(R.id.nextClassStartTime);
                final TextView nextRoom = findViewById(R.id.nextRoom_no);
                final TextView nextCategory = findViewById(R.id.nextCategory);
                final TextView nextClass = findViewById(R.id.nextSubject);
                final TextView nextTeacher = findViewById(R.id.nextTeacher);
                timeLeftTextView.setText(timeLeftCalculation.timeLeft());
                try {
                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
                    dbref = dbref.child("classes").child(id);
                    dbref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                subject = Objects.requireNonNull(dataSnapshot.child("subject").getValue()).toString();
                                currentSubject.setText(subject);
                            } catch (NullPointerException e) {
                                if(generateId.dayID() == 10){
                                    try {
                                        currentSubject.setText("");
                                        ConstraintLayout now = findViewById(R.id.nowlayout);
                                        now.setBackgroundResource(R.drawable.sunday);
                                        nowTextView.setText("");
                                        nextText.setText("");
                                        nextTime.setText("");
                                        nextRoom.setText("");
                                        nextCategory.setText("");
                                        nextClass.setText("");
                                        nextTeacher.setText("NO UPCOMING CLASSES");
                                    }catch (NullPointerException e1){
                                        e1.printStackTrace();
                                    }
                                }
                                else if(generateId.dayID() == 16){
                                    try {
                                        currentSubject.setText("");
                                        ConstraintLayout now = findViewById(R.id.nowlayout);
                                        now.setBackgroundResource(R.drawable.saturday);
                                    }catch (NullPointerException e1){
                                        e1.printStackTrace();
                                    }
                                    try {
                                        nowTextView.setText("");
                                        nextText.setText("");
                                        nextTime.setText("");
                                        nextRoom.setText("");
                                        nextCategory.setText("");
                                        nextClass.setText("");
                                        nextTeacher.setText("NO UPCOMING CLASSES");
                                    }catch (NullPointerException e1){
                                        e1.printStackTrace();
                                    }
                                }
                                else
                                {
                                    if(generateId.time()==21){
                                        try {
                                            nextTeacher.setText("CLASSES NOT STARTED YET");
                                        }catch (NullPointerException nu){
                                            nu.printStackTrace();
                                        }
                                    }
                                    else if(generateId.time()==20){
                                        try {
                                            nextTeacher.setText("NO MORE CLASSES TODAY");
                                        }catch (NullPointerException e1){
                                            e1.printStackTrace();
                                        }
                                    }
                                    else
                                        try {
                                            currentSubject.setText("Not Found :(");
                                        }catch (NullPointerException e1){
                                            e1.printStackTrace();
                                        }
                                }
                            }
                            try {
                                teacher = Objects.requireNonNull(dataSnapshot.child("teacher").getValue()).toString();
                                currentTeacher.setText(teacher);
                            } catch (NullPointerException e) {
                                if(generateId.dayID() == 10){
                                    try{
                                        currentTeacher.setText("");
                                    }catch (NullPointerException e1){
                                        e1.printStackTrace();
                                    }
                                }
                                else if(generateId.dayID() == 16){
                                    try {
                                        currentTeacher.setText("");
                                    }catch (NullPointerException e1){
                                        e1.printStackTrace();
                                    }
                                }
                                else
                                {
                                    if(generateId.time()==21){
                                        try {
                                            currentTeacher.setText("NO UPCOMING CLASSES");
                                        }catch (NullPointerException e1){
                                            e1.printStackTrace();
                                        }
                                    }
                                    else if(generateId.time()==20){
                                        try {
                                            currentTeacher.setText("NO UPCOMING CLASSES");
                                        }catch (NullPointerException e1){
                                            e1.printStackTrace();
                                        }
                                    }
                                    else
                                        try {
                                            currentTeacher.setText("");
                                        }catch (NullPointerException e1){
                                            e1.printStackTrace();
                                        }
                                }
                            }
                            try {
                                room_no = Objects.requireNonNull(dataSnapshot.child("room_no").getValue()).toString();
                                currentRoom.setText(room_no);
                            } catch (NullPointerException e) {
                                try {
                                    currentRoom.setText("");
                                }catch (NullPointerException e1){
                                    e1.printStackTrace();
                                }
                            }
                            try {
                                category = Objects.requireNonNull(dataSnapshot.child("category").getValue()).toString();
                                currentCategory.setText(category);
                            } catch (NullPointerException e) {
                                try {
                                    currentCategory.setText("");
                                }catch (NullPointerException e1){
                                    e1.printStackTrace();
                                }
                            }
                            setNameAndEmail();
                            //setNextView(subject);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            try {
                                textView1.setText("Error");
                            }catch (NullPointerException e1){
                                e1.printStackTrace();
                            }
                        }
                    });
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                try {
                    time.setText(setTime.generateDAndT());
                }catch (NullPointerException e1){
                    e1.printStackTrace();
                }
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        while (!isInterrupted()) {
                            try {
                                Thread.sleep(50);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            timeLeftTextView.setText(timeLeftCalculation.timeLeft());
                                            time.setText(setTime.getWeek() + " " + setTime.generateDAndT());
                                        }catch (NullPointerException e1){
                                            e1.printStackTrace();
                                        }
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                thread.start();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Set up about section and drawerLayout.
     * */
    public void setAbout(MenuItem menuItem){
        setContentView(R.layout.mainforabout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        setNameAndEmail();
        setProfileImageDrawer();
        initialPress=false;
        nextPressed=false;
        prevPressed=false;
    }
    /**
     * WIP
     * */
    public void setNextView(String nowClass){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        final GenerateId generateId = new GenerateId();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mdatabaseReference = databaseReference.child("User").child(firebaseUser.getUid());
        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    dept = dataSnapshot.child("pref").child("dept").getValue().toString();
                }catch (NullPointerException e){
                    Toast.makeText(MainActivity.this,"Dept error",Toast.LENGTH_LONG).show();
                }
                try {
                    section = dataSnapshot.child("pref").child("section").getValue().toString();
                }catch (NullPointerException e){
                    Toast.makeText(MainActivity.this,"Section error",Toast.LENGTH_LONG).show();
                }
                try{
                    yearString = dataSnapshot.child("pref").child("year").getValue().toString();
                }catch (NullPointerException e){
                    Toast.makeText(MainActivity.this,"Year error",Toast.LENGTH_LONG).show();
                }
                try {
                    groupString = dataSnapshot.child("pref").child("group").getValue().toString();
                }catch (NullPointerException e){
                    Toast.makeText(MainActivity.this,"Group error",Toast.LENGTH_LONG).show();
                }
                TextView classID = (TextView) findViewById(R.id.nextclassId);
                GenerateClassId generateClassId = new GenerateClassId(dept, section, yearString, groupString);
                long deptId = generateClassId.dept(dept);
                long sectionId = generateClassId.section(section);
                long yearId = generateClassId.year(yearString);
                long groupId = generateClassId.group(groupString);
                boolean counter = true;
                while (counter) {
                    long timeId = generateId.generate();
                    timeId++;
                    final String id = Long.toString(yearId) + Long.toString(deptId) + Long.toString(sectionId) + Long.toString(groupId) + Long.toString(timeId);
                    try {
                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
                        dbref = dbref.child("classes").child(id);
                        dbref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    subject = Objects.requireNonNull(dataSnapshot.child("subject").getValue()).toString();
                                } catch (NullPointerException e) {
                                   subject="Nan";
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (!subject.equals(nowClass)) {
                            try {
                                TextView nextSubject = findViewById(R.id.nextSubject);
                                nextSubject.setText(subject);
                            } catch (NullPointerException e1) {
                                e1.printStackTrace();
                            }
                            counter = false;
                        }
                    }catch (NullPointerException e1){
                        counter = false;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Set up name and email to Profile
     * */
    public void setNameAndEmailProfile(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mdatabaseReference = databaseReference.child("User").child(firebaseUser.getUid());
        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TextView userEmailTextView = findViewById(R.id.p_emaiId);
                TextView userNameTextView = findViewById(R.id.p_UserName);
                try {
                    userNameTextView.setText(dataSnapshot.child("info").child("name").getValue().toString());
                    userEmailTextView.setText(dataSnapshot.child("info").child("email").getValue().toString());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Set up name and email to drawer.
     * */
    public void setNameAndEmail(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mdatabaseReference = databaseReference.child("User").child(firebaseUser.getUid());
        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TextView userEmailTextView = findViewById(R.id.userEmailTextView);
                TextView userNameTextView = findViewById(R.id.userNameTextView);
                try {
                    userNameTextView.setText(dataSnapshot.child("info").child("name").getValue().toString());
                    userEmailTextView.setText(dataSnapshot.child("info").child("email").getValue().toString());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     *
     * */
    public void viewProfile(MenuItem menuItem){
        setContentView(R.layout.mainforprofile);
        ProgressBar progressBar = findViewById(R.id.uploadProgressBar);
        progressBar.setVisibility(View.GONE);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        setNameAndEmail();
        setUserInfo();
        setNameAndEmailProfile();
        setImage();
        setProfileImageDrawer();
        initialPress=false;
        nextPressed=false;
        prevPressed=false;
    }
    public void setSpinner(){
        final Spinner spinner1 = findViewById(R.id.deptSpinner);
        final Spinner spinner2 = findViewById(R.id.sectionSpinner);
        final Spinner spinner3 = findViewById(R.id.yearSpinner);
        final Spinner spinner4 = findViewById(R.id.groupSpinner);
        final Users users = new Users();
        ArrayAdapter<String> aa1 = new ArrayAdapter<>(MainActivity.this,R.layout.support_simple_spinner_dropdown_item,branch);
        ArrayAdapter<String> aa2 = new ArrayAdapter<>(MainActivity.this,R.layout.support_simple_spinner_dropdown_item,sec);
        ArrayAdapter<String> aa3 = new ArrayAdapter<>(MainActivity.this,R.layout.support_simple_spinner_dropdown_item,year);
        ArrayAdapter<String> aa4 = new ArrayAdapter<>(MainActivity.this,R.layout.support_simple_spinner_dropdown_item,group);
        spinner1.setAdapter(aa1);
        spinner2.setAdapter(aa2);
        spinner3.setAdapter(aa3);
        spinner4.setAdapter(aa4);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                users.dept =  (String)parent.getSelectedItem();
                dept = (String)parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                users.dept =  (String)parent.getSelectedItem();
                dept = (String)parent.getItemAtPosition(0);
            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                section = (String)parent.getSelectedItem();
                users.section = section;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                section = (String)parent.getItemAtPosition(0);
                users.section=section;

            }
        });
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearString = (String)parent.getSelectedItem();
                users.year=yearString;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                yearString = (String)parent.getItemAtPosition(0);
                users.year=yearString;

            }
        });
        spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                groupString = (String)parent.getSelectedItem();
                users.group=groupString;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                groupString = (String)parent.getItemAtPosition(0);
                users.group=groupString;
            }
        });

        currentUser = users;

    }
    class SetTime{
        String currentTime(){
            long time = System.currentTimeMillis();
            DateFormat formatter = new SimpleDateFormat("hh:mm:ss aa");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return (formatter.format(calendar.getTime()));
        }
        String date(){
            long time = System.currentTimeMillis();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return (formatter.format(calendar.getTime()));
        }
        String getWeek(){
            long time = System.currentTimeMillis();
            DateFormat formatter = new SimpleDateFormat("EEE");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return (formatter.format(calendar.getTime()));
        }
        String hour(){
            long time = System.currentTimeMillis();
            DateFormat formatter = new SimpleDateFormat("HH");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return (formatter.format(calendar.getTime()));
        }
        String minute(){
            long time = System.currentTimeMillis();
            DateFormat formatter = new SimpleDateFormat("mm");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return (formatter.format(calendar.getTime()));
        }
        String generateDAndT(){
            return (date()+"\t"+currentTime());
        }
    }
    class GenerateClassId {
        GenerateClassId(String dept,String section, String year, String group){
            dept(dept);
            section(section);
            year(year);
            group(group);
        }
        int dept(String dept){
            if(dept.equals("CSE"))
                return 10;
            if(dept.equals("ECE"))
                return 11;
            if(dept.equals("IT"))
                return 12;
            if(dept.equals("CE"))
                return 13;
            if(dept.equals("ME"))
                return 14;
            if(dept.equals("ChE"))
                return 15;
            if(dept.equals("EE"))
                return 16;
            if(dept.equals("AEIE"))
                return 17;
            if(dept.equals("BT"))
                return 18;
            return 20;
        }
        int section(String sec){
            if(sec.equals("N/A"))
                return 10;
            if(sec.equals("A"))
                return 11;
            if(sec.equals("B"))
                return 12;
            if(sec.equals("C"))
                return 13;

            return 20;
        }
        int year(String yearString){
            if(yearString.equals("1st")){
                return 10;
            }
            if(yearString.equals("2nd")){
                return 11;
            }
            if(yearString.equals("3rd")){
                return 12;
            }
            if(yearString.equals("4th")){
                return 13;
            }
            return 20;
        }
        int group(String groupString){
            if(groupString.equals("A")){
                return 10;
            }
            if(groupString.equals("B"))
                return 11;
            return 20;
        }
    }
    class GenerateId{
        long time = System.currentTimeMillis();
        int dayID(){
            DateFormat formatter = new SimpleDateFormat("EEE");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);

            if(formatter.format(calendar.getTime()).toString().equals("Sun")){
                return 10;
            }if(formatter.format(calendar.getTime()).toString().equals("Mon")){
                return 11;
            }if(formatter.format(calendar.getTime()).toString().equals("Tue")){
                return 12;
            }if(formatter.format(calendar.getTime()).toString().equals("Wed")){
                return 13;
            }if(formatter.format(calendar.getTime()).toString().equals("Thu")){
                return 14;
            }if(formatter.format(calendar.getTime()).toString().equals("Fri")){
                return 15;
            }if(formatter.format(calendar.getTime()).toString().equals("Sat")){
                return 16;
            }
            return 20;
        }
        int time(){
            DateFormat formatterHour = new SimpleDateFormat("HH");
            Calendar calendarHour = Calendar.getInstance();
            calendarHour.setTimeInMillis(time);
            DateFormat formatterMinute = new SimpleDateFormat("mm");
            Calendar calendarMinute = Calendar.getInstance();
            calendarMinute.setTimeInMillis(time);
            int hour = Integer.parseInt(formatterHour.format(calendarHour.getTime()));
            int minute = Integer.parseInt(formatterMinute.format(calendarMinute.getTime()));
            int time = ((100*hour)+minute);
            if(time<899){
                return 21;
            }
            if(time>=900&&time<955){
                return 10;
            }if(time>=955&&time<1050){
                return 11;
            }if(time>=1050&&time<1145){
                return 12;
            }if(time>=1145&&time<1225){
                return 13;
            }if(time>=1225&&time<1320){
                return 14;
            }if(time>=1320&&time<1415){
                return 15;
            }if(time>=1415&&time<1510){
                return 16;
            }if(time>=1510&&time<1605){
                return 17;
            }if(time>=1605&&time<1700){
                return 18;
            }if(time>=1700&&time<1755){
                return 19;
            }
            return 20;
        }
        int generate(){
            return (dayID()*100)+time();
        }
    }

    class TimeLeftCalculation {
        GenerateId generateId = new GenerateId();
        SetTime setTime = new SetTime();

        String timeLeft() {

            if ((true)) {

            }
            return null;
        }
    }
}