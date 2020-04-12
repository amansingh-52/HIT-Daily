package com.example.hitdaily;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    SetTime setTime = new SetTime();
    String[] year ={"1st"/*,"2nd","3rd","4th"*/};
    String[] branch = {/*"CSE",*/ "ECE",/*"IT","CE","ME",*/"ChE"/*,"EE","AEIE","BT"*/};
    String[] sec = {"N/A","A","B","C"};
    String[] group = {"A","B"};
    String dept="Hello",section="World",yearString = "year", groupString = "group";
    String subject;
    String teacher;
    String room_no;
    String category;
    Users currentUser;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser == null) {
            launchLogIN();
        }
        else {
           launch();
        }
    }

    public void launchLogIN(){
        setContentView(R.layout.signin);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

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
            information.password=password1.getText().toString();

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
            password1.setText("");
            password2.setText("");
        }
    }

    public void logout(MenuItem menuItem){
        firebaseAuth.signOut();
        launchLogIN();
    }

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

    public void display(View view){
        Toast.makeText(this,firebaseAuth.getUid(),Toast.LENGTH_LONG).show();
    }
    //Has to be implemented for navigation view
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            return true;
        }

        public void collegeSite(MenuItem menuItem){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.heritageit.edu/"));
            startActivity(intent);
        }

    public void noticeSite(MenuItem menuItem){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.heritageit.edu/Notice.aspx"));
        startActivity(intent);
    }

    public void examinationSite(MenuItem menuItem){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.heritageit.edu/ExamCell.aspx"));
        startActivity(intent);
    }

    public void toEmail(View view){
        Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto","amansingh8066@gmail.com",null));
        intent.putExtra(Intent.EXTRA_SUBJECT,"Report HIT Daily");
        startActivity(Intent.createChooser(intent,"Choose an email client :"));
    }

    public void toGitHub(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/amansingh-52/HIT-Daily"));
        startActivity(intent);
    }
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
    }

    public void uploadData(Users users){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("User").child(firebaseUser.getUid()).child("pref").setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this,"All set !",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void mainScreen (MenuItem menuItem){
        launch();
        setUpHomeScreen();
    }

    public void signUp(View view){
        setContentView(R.layout.signup);
    }

    public void signIn(View view){
        setContentView(R.layout.signin);
    }
    //launches main screen
    void launch() {
        setContentView(R.layout.activity_main);
        int saturday = R.drawable.saturday;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        setUpHomeScreen();

    }
    //Function for on click Log In from the menu drawer.
    public void toLogIN(MenuItem item){

        setContentView(R.layout.pref);
        setSpinner();

    }
    public void toEmailLogIn(MenuItem menuItem){
        setContentView(R.layout.signup);
    }
    //Function for on click save button
    public void toHome(View view){
       launch();
       setUpHomeScreen();
       uploadData(currentUser);
    }
    public void setUpHomeScreen() {
        final ProgressBar progressBar1 = findViewById(R.id.progressBar2);
        progressBar1.setVisibility(View.VISIBLE);
        final TextView textView1 = findViewById(R.id.classNameText);
        final TextView timeLeftTextView = (TextView) findViewById(R.id.timeLeftTextView);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        final TextView time = findViewById(R.id.timeTextView);
        final GenerateId generateId = new GenerateId();
        final TextView classTextView = (TextView) findViewById(R.id.classNameText);
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
                progressBar1.setVisibility(View.GONE);
                TextView classID = (TextView) findViewById(R.id.classId);
                GenerateClassId generateClassId = new GenerateClassId(dept, section, yearString, groupString);
                classTextView.setText(dept + " -" + section + " \"" + groupString + "\" (" + yearString + ")");
                long deptId = generateClassId.dept(dept);
                long sectionId = generateClassId.section(section);
                long yearId = generateClassId.year(yearString);
                long groupId = generateClassId.group(groupString);
                final long timeId = generateId.generate();
                final String id = Long.toString(yearId) + Long.toString(deptId) + Long.toString(sectionId) + Long.toString(groupId) + Long.toString(timeId);
                classID.setText("@classID\n" + id);
                final TimeLeftCalculation timeLeftCalculation = new TimeLeftCalculation();
                final TextView currentSubject = (TextView) findViewById(R.id.currentSubject);
                final TextView currentTeacher = (TextView) findViewById(R.id.currentTeacher);
                final TextView currentRoom = (TextView) findViewById(R.id.currentRoom_no);
                final TextView currentCategory = (TextView) findViewById(R.id.currentCategory);
                final TextView nowTextView = (TextView) findViewById(R.id.nowText);
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
                                    currentSubject.setText("");
                                    ConstraintLayout now = findViewById(R.id.nowlayout);
                                    ConstraintLayout next = findViewById(R.id.nextLayout);
                                    next.setBackgroundResource(R.drawable.sundaysupport);
                                    now.setBackgroundResource(R.drawable.sunday);
                                    nowTextView.setText("");
                                }
                                else if(generateId.dayID() == 16){
                                    currentSubject.setText("");
                                    ConstraintLayout now = findViewById(R.id.nowlayout);
                                    ConstraintLayout next = findViewById(R.id.nextLayout);
                                    next.setBackgroundResource(R.drawable.sundaysupport);
                                    now.setBackgroundResource(R.drawable.saturday);
                                    nowTextView.setText("");
                                }
                                else
                                {
                                    if(generateId.time()==21){
                                        currentSubject.setText("CLASSES NOT STARTED YET");
                                    }
                                    else if(generateId.time()==20){
                                        currentSubject.setText("NO MORE CLASSES TODAY");
                                    }
                                    else
                                    currentSubject.setText("Not Found :(");
                                }
                            }
                            try {
                                teacher = Objects.requireNonNull(dataSnapshot.child("teacher").getValue()).toString();
                                currentTeacher.setText(teacher);
                            } catch (NullPointerException e) {
                                if(generateId.dayID() == 10){
                                    currentTeacher.setText("");
                                }
                                else if(generateId.dayID() == 16){
                                    currentTeacher.setText("");
                                }
                                else
                                {
                                    if(generateId.time()==21){
                                        currentTeacher.setText("NO UPCOMING CLASSES");
                                    }
                                    else if(generateId.time()==20){
                                        currentTeacher.setText("NO UPCOMING CLASSES");
                                    }
                                    else
                                        currentTeacher.setText("Please contactmeemail at amansingh8066@gmail.com\nKindly send your class routine");
                                }

                            }
                            try {
                                room_no = Objects.requireNonNull(dataSnapshot.child("room_no").getValue()).toString();
                                currentRoom.setText(room_no);
                            } catch (NullPointerException e) {
                                currentRoom.setText("");
                            }
                            try {
                                category = Objects.requireNonNull(dataSnapshot.child("category").getValue()).toString();
                                currentCategory.setText(category);
                            } catch (NullPointerException e) {
                                currentCategory.setText("");
                            }
                            setNameAndEmail();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            textView1.setText("Error");
                        }
                    });
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                time.setText(setTime.generateDAndT());
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        while (!isInterrupted()) {
                            try {
                                Thread.sleep(50);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeLeftTextView.setText(timeLeftCalculation.timeLeft());
                                        time.setText(setTime.getWeek() + " " + setTime.generateDAndT());

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