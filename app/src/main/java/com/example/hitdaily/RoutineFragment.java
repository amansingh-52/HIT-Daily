package com.example.hitdaily;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

public class RoutineFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String dept="Hello",section="World",yearString = "year", groupString = "group";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.routine_fragment,container,false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();
            TextView subject1 = getView().findViewById(R.id.sub_view);
            TextView subject2 = getView().findViewById(R.id.sub2_view);
            TextView subject3 = getView().findViewById(R.id.sub3_view);
            TextView subject4 = getView().findViewById(R.id.sub4_view);
            TextView subject5 = getView().findViewById(R.id.sub5_view);
            TextView subject6 = getView().findViewById(R.id.sub6_view);
            TextView subject7 = getView().findViewById(R.id.sub7_view);
            TextView subject8 = getView().findViewById(R.id.sub8_view);
            TextView subject9 = getView().findViewById(R.id.sub9_view);
            TextView subject10 = getView().findViewById(R.id.sub10_view);
            TextView teacher1 = getView().findViewById(R.id.teacher_textView);
            TextView teacher2 = getView().findViewById(R.id.teacher2_textView);
            TextView teacher3 = getView().findViewById(R.id.teacher3_textView);
            TextView teacher4 = getView().findViewById(R.id.teacher4_textView);
            TextView teacher5 = getView().findViewById(R.id.teacher5_textView);
            TextView teacher6 = getView().findViewById(R.id.teacher6_textView);
            TextView teacher7 = getView().findViewById(R.id.teacher7_textView);
            TextView teacher8 = getView().findViewById(R.id.teacher8_textView);
            TextView teacher9 = getView().findViewById(R.id.teacher9_textView);
            TextView teacher10 = getView().findViewById(R.id.teacher10_textView);
            TextView room1 = getView().findViewById(R.id.category_textView);
            TextView room2 = getView().findViewById(R.id.category2_textView);
            TextView room3 = getView().findViewById(R.id.category3_textView);
            TextView room4 = getView().findViewById(R.id.category4_textView);
            TextView room5 = getView().findViewById(R.id.category5_textView);
            TextView room6 = getView().findViewById(R.id.category6_textView);
            TextView room7 = getView().findViewById(R.id.category7_textView);
            TextView room8 = getView().findViewById(R.id.category8_textView);
            TextView room9 = getView().findViewById(R.id.category9_textView);
            TextView room10 = getView().findViewById(R.id.category10_textView);
            TextView center = getView().findViewById(R.id.todayCenter);
            CardView c1 = getView().findViewById(R.id.v1);
            CardView c2 = getView().findViewById(R.id.v2);
            CardView c3 = getView().findViewById(R.id.v3);
            CardView c4 = getView().findViewById(R.id.v4);
            CardView c5 = getView().findViewById(R.id.v5);
            CardView c6 = getView().findViewById(R.id.v6);
            CardView c7 = getView().findViewById(R.id.v7);
            CardView c8 = getView().findViewById(R.id.v8);
            CardView c9 = getView().findViewById(R.id.v9);
            CardView c10 = getView().findViewById(R.id.v10);
            c1.setVisibility(View.VISIBLE);
            c2.setVisibility(View.VISIBLE);
            c3.setVisibility(View.VISIBLE);
            c4.setVisibility(View.VISIBLE);
            c5.setVisibility(View.VISIBLE);
            c6.setVisibility(View.VISIBLE);
            c7.setVisibility(View.VISIBLE);
            c8.setVisibility(View.VISIBLE);
            c9.setVisibility(View.VISIBLE);
            c10.setVisibility(View.VISIBLE);

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
            TextView day = getView().findViewById(R.id.day_TextView);
           GenerateId gid = new GenerateId();
            if(gid.dayID()==10||gid.dayID()==16)
            {
                try {
                    center.setText("NO CLASSES");
                    c1.setVisibility(View.GONE);
                    room1.setText("");
                    c2.setVisibility(View.GONE);
                    room2.setText("");
                    c3.setVisibility(View.GONE);
                    room3.setText("");
                    c4.setVisibility(View.GONE);
                    room4.setText("");
                    c5.setVisibility(View.GONE);
                    room5.setText("");
                    c6.setVisibility(View.GONE);
                    room6.setText("");
                    c7.setVisibility(View.GONE);
                    room7.setText("");
                    c8.setVisibility(View.GONE);
                    room8.setText("");
                    c9.setVisibility(View.GONE);
                    room9.setText("");
                    c10.setVisibility(View.GONE);
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
                                            c1.setVisibility(View.GONE);
                                            room1.setText("");
                                        }
                                        if (clac == 1) {
                                            c2.setVisibility(View.GONE);
                                            room2.setText("");
                                        }
                                        if (clac == 2) {
                                            c3.setVisibility(View.GONE);
                                            room3.setText("");
                                        }
                                        if (clac == 3) {
                                            c4.setVisibility(View.GONE);
                                            room4.setText("");
                                        }
                                        if (clac == 4) {
                                            c5.setVisibility(View.GONE);
                                            room5.setText("");
                                        }
                                        if (clac == 5) {
                                            c6.setVisibility(View.GONE);
                                            room6.setText("");
                                        }
                                        if (clac == 6) {
                                            c7.setVisibility(View.GONE);
                                            room7.setText("");
                                        }
                                        if (clac == 7) {
                                            c8.setVisibility(View.GONE);
                                            room8.setText("");
                                        }
                                        if (clac == 8) {
                                            c9.setVisibility(View.GONE);
                                            room9.setText("");
                                        }
                                        if (clac == 9) {
                                            c10.setVisibility(View.GONE);
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
}
