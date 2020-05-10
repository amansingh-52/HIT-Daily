package com.example.hitdaily;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    SetTime setTime = new SetTime();
    String dept="Hello",section="World",yearString = "year", groupString = "group";
    String subject;
    String teacher;
    String room_no;
    String category;
    boolean nextClassCounter = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.now_fragment,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading");
            progressDialog.setCancelable(false);
            progressDialog.show();
                final ProgressBar progressBar1 = getView() .  findViewById(R.id.nowClassProgressBar);
                final ProgressBar nextProgressBar = getView() .  findViewById(R.id.nextClassProgressBar);
                final TextView timeLeftTextView = (TextView) getView().findViewById(R.id.nowClassStartTime);
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseUser = firebaseAuth.getCurrentUser();
                final TextView time = getView() .findViewById(R.id.timeTextView);
                final GenerateId generateId = new GenerateId();
                final TextView classTextView = (TextView) getView() .  findViewById(R.id.classNameText);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference mdbr = databaseReference.child("notice");
                mdbr.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        progressDialog.cancel();
                        TextView notice = getView().  findViewById(R.id.notice);
                        try {
                            notice.setText(dataSnapshot.getValue().toString());
                        } catch (NullPointerException e1) {
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
                        progressDialog.cancel();
                        try {
                            dept = dataSnapshot.child("pref").child("dept").getValue().toString();
                        } catch (NullPointerException e) {
                          e.printStackTrace();
                        }
                        try {
                            section = dataSnapshot.child("pref").child("section").getValue().toString();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        try {
                            yearString = dataSnapshot.child("pref").child("year").getValue().toString();
                        } catch (NullPointerException e) {
                           e.printStackTrace();
                        }
                        try {
                            groupString = dataSnapshot.child("pref").child("group").getValue().toString();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        progressBar1.setVisibility(View.GONE);
                        nextProgressBar.setVisibility(View.GONE);

                        TextView classID = (TextView) getView() . findViewById(R.id.nowclassId);
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
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        final TimeLeftCalculation timeLeftCalculation = new TimeLeftCalculation();
                        final TextView currentSubject = (TextView) getView() . findViewById(R.id.nowSubject);
                        final TextView currentTeacher = (TextView) getView() . findViewById(R.id.nowTeacher);
                        final TextView currentRoom = (TextView) getView(). findViewById(R.id.nowRoom_no);
                        final TextView currentCategory = (TextView) getView() . findViewById(R.id.nowCategory);
                        final TextView nowTextView = (TextView) getView() . findViewById(R.id.nowText);
                        final TextView nextText = getView() . findViewById(R.id.nextText);
                        final TextView nextTime = getView() . findViewById(R.id.nextClassStartTime);
                        final TextView nextRoom = getView() . findViewById(R.id.nextRoom_no);
                        final TextView nextCategory = getView() . findViewById(R.id.nextCategory);
                        final TextView nextClass = getView() . findViewById(R.id.nextSubject);
                        final TextView nextTeacher = getView() . findViewById(R.id.nextTeacher);
                        timeLeftTextView.setText(timeLeftCalculation.timeLeft());
                        try {
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
                            dbref = dbref.child("classes").child(id);
                            dbref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    progressDialog.cancel();
                                    try {
                                        subject = Objects.requireNonNull(dataSnapshot.child("subject").getValue()).toString();
                                        currentSubject.setText(subject);
                                    } catch (NullPointerException e) {
                                        if (generateId.dayID() == 10) {
                                            try {
                                                currentSubject.setText("");
                                                ConstraintLayout now = getView() .  findViewById(R.id.nowlayout);
                                                now.setBackgroundResource(R.drawable.sunday);
                                                nowTextView.setText("");
                                                nextText.setText("");
                                                nextTime.setText("");
                                                nextRoom.setText("");
                                                nextCategory.setText("");
                                                nextClass.setText("");
                                                nextTeacher.setText("NO UPCOMING CLASSES");
                                            } catch (NullPointerException e1) {
                                                e1.printStackTrace();
                                            }
                                        } else if (generateId.dayID() == 16) {
                                            try {
                                                currentSubject.setText("");
                                                ConstraintLayout now = getView() .  findViewById(R.id.nowlayout);
                                                now.setBackgroundResource(R.drawable.saturday);
                                            } catch (NullPointerException e1) {
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
                                            } catch (NullPointerException e1) {
                                                e1.printStackTrace();
                                            }
                                        } else {
                                            if (generateId.time() == 21) {
                                                try {
                                                    nextTeacher.setText("CLASSES NOT STARTED YET");
                                                } catch (NullPointerException nu) {
                                                    nu.printStackTrace();
                                                }
                                            } else if (generateId.time() == 20) {
                                                try {
                                                    nextTeacher.setText("NO MORE CLASSES TODAY");
                                                } catch (NullPointerException e1) {
                                                    e1.printStackTrace();
                                                }
                                            } else
                                                try {
                                                    currentSubject.setText("Not Found :(");
                                                } catch (NullPointerException e1) {
                                                    e1.printStackTrace();
                                                }
                                        }
                                    }
                                    try {
                                        teacher = Objects.requireNonNull(dataSnapshot.child("teacher").getValue()).toString();
                                        currentTeacher.setText(teacher);
                                    } catch (NullPointerException e) {
                                        if (generateId.dayID() == 10) {
                                            try {
                                                currentTeacher.setText("");
                                            } catch (NullPointerException e1) {
                                                e1.printStackTrace();
                                            }
                                        } else if (generateId.dayID() == 16) {
                                            try {
                                                currentTeacher.setText("");
                                            } catch (NullPointerException e1) {
                                                e1.printStackTrace();
                                            }
                                        } else {
                                            if (generateId.time() == 21) {
                                                try {
                                                    currentTeacher.setText("NO UPCOMING CLASSES");
                                                } catch (NullPointerException e1) {
                                                    e1.printStackTrace();
                                                }
                                            } else if (generateId.time() == 20) {
                                                try {
                                                    currentTeacher.setText("NO UPCOMING CLASSES");
                                                } catch (NullPointerException e1) {
                                                    e1.printStackTrace();
                                                }
                                            } else
                                                try {
                                                    currentTeacher.setText("");
                                                } catch (NullPointerException e1) {
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
                                        } catch (NullPointerException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                    try {
                                        category = Objects.requireNonNull(dataSnapshot.child("category").getValue()).toString();
                                        currentCategory.setText(category);
                                    } catch (NullPointerException e) {
                                        try {
                                            currentCategory.setText("");
                                        } catch (NullPointerException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                    List<Classes> list = new ArrayList<>();
                                    Classes classes = new Classes();
                                    int i = 1;
                                   GenerateId generateId1 = new GenerateId();
                                    while (nextClassCounter && (generateId1.time() + i < 20)) {
                                        try {
                                            final int nextTimeId = (int) timeId + i;
                                            final String nextId = Long.toString(yearId) + Long.toString(deptId) + Long.toString(sectionId) + Long.toString(groupId) + Integer.toString(nextTimeId);
                                            TextView nextClassId = getView() . findViewById(R.id.nextclassId);
                                            nextClassId.setText("@classID\n" + nextId);
                                            i++;
                                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
                                            dbref = dbref.child("classes").child(nextId);
                                            dbref.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    try {
                                                        classes.subject = dataSnapshot.child("subject").getValue().toString();
                                                    } catch (NullPointerException e) {
                                                        e.printStackTrace();
                                                    }
                                                    try {

                                                        classes.teacher = dataSnapshot.child("teacher").getValue().toString();
                                                    } catch (NullPointerException e1) {
                                                        e1.printStackTrace();
                                                    }
                                                    try {
                                                        classes.category = Objects.requireNonNull(dataSnapshot.child("category").getValue()).toString();
                                                        classes.room_no = dataSnapshot.child("room_no").getValue().toString();
                                                    } catch (NullPointerException e1) {
                                                        e1.printStackTrace();
                                                    }
                                                    list.add(classes);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }
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
                            time.setText(setTime.generateDAndT());
                        } catch (NullPointerException e1) {
                            e1.printStackTrace();
                        }
                        CountDownTimer countDownTimer = new CountDownTimer(500,50) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                timeLeftTextView.setText(timeLeftCalculation.timeLeft());
                                time.setText(setTime.getWeek() + " " + setTime.generateDAndT());
                            }

                            @Override
                            public void onFinish() {
                                    start();
                            }
                        };
                        countDownTimer.start();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        }
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

