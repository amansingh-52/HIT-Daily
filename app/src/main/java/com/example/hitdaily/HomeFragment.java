package com.example.hitdaily;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HomeFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    SetTime setTime = new SetTime();
    String dept = "Hello", section = "World", yearString = "year", groupString = "group";
    String subject;
    String teacher;
    String room_no;
    String category;
    boolean nextClassCounter = true;
    LocationManager locationManager;
    LocationListener locationListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.now_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null)
                    updateLocation(location);
                else {
                    TextView city = getView().findViewById(R.id.city);
                    try {
                        city.setText("Can't find location");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Toast.makeText(getContext(), provider, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(getContext(), provider, Toast.LENGTH_LONG).show();

            }
        };
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500000, 1000, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastKnownLocation != null) {
                updateLocation(lastKnownLocation);
            } else {
                TextView city = getView().findViewById(R.id.city);
                try {
                    city.setText("Can't find location");
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final TextView timeLeftTextView = (TextView) getView().findViewById(R.id.nowClassStartTime);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        final TextView time = getView().findViewById(R.id.timeTextView);
        final GenerateId generateId = new GenerateId();
        final TextView classTextView = (TextView) getView().findViewById(R.id.classNameText);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mdbr = databaseReference.child("notice");
        mdbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    TextView notice = getView().findViewById(R.id.notice);
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

                TextView classID = (TextView) getView().findViewById(R.id.nowclassId);
                GenerateClassId generateClassId = new GenerateClassId(dept, section, yearString, groupString);
                try {
                    classTextView.setText("D: " + dept + "\nS: " + section + "\nG: " + groupString + "\nY:" + yearString + "");
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
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
                final TextView currentSubject = (TextView) getView().findViewById(R.id.nowSubject);
                final TextView currentTeacher = (TextView) getView().findViewById(R.id.nowTeacher);
                final TextView currentRoom = (TextView) getView().findViewById(R.id.nowRoom_no);
                final TextView currentCategory = (TextView) getView().findViewById(R.id.nowCategory);
                final TextView nextText = getView().findViewById(R.id.nextText);
                final TextView nextTime = getView().findViewById(R.id.nextClassStartTime);
                final TextView nextRoom = getView().findViewById(R.id.nextRoom_no);
                final TextView nextCategory = getView().findViewById(R.id.nextCategory);
                final TextView nextClass = getView().findViewById(R.id.nextSubject);
                final TextView nextTeacher = getView().findViewById(R.id.nextTeacher);
                try {
                    timeLeftTextView.setText(timeLeftCalculation.timeLeft());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
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
                                if (generateId.dayID() == 10) {
                                    try {
                                        currentSubject.setText("");
                                        ConstraintLayout now = getView().findViewById(R.id.nowConstraint);
                                        now.setBackgroundResource(R.drawable.sunday);
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
                                        ConstraintLayout now = getView().findViewById(R.id.nowlayout);
                                        now.setBackgroundResource(R.drawable.saturday);
                                    } catch (NullPointerException e1) {
                                        e1.printStackTrace();
                                    }
                                    try {
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
                                    TextView nextClassId = getView().findViewById(R.id.nextclassId);
                                    try {
                                        nextClassId.setText("@classID\n" + nextId);
                                    } catch (NullPointerException e1) {
                                        e1.printStackTrace();
                                    }
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
                CountDownTimer countDownTimer = new CountDownTimer(500, 50) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        try {
                            timeLeftTextView.setText(timeLeftCalculation.timeLeft());
                            time.setText(setTime.getWeek_Full() + "\n" + setTime.generateDAndT());
                        } catch (NullPointerException e1) {
                            e1.printStackTrace();
                        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        }
    }

    void startListening() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500000, 1000, locationListener);
        }
    }

    void updateLocation(Location location) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addressList = null;

        try {
            addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);


            if (addressList != null && addressList.size() > 0) {
                String url;
                double random = Math.random();
                if (random >= 0 && random < 0.2) {
                    url = "https://api.openweathermap.org/data/2.5/weather?q=" + addressList.get(0).getLocality() + "&appid=b124a801dd5ed56f829228de8658723e&units=imperial";
                } else if (random >= 0.2 && random < 0.4) {
                    url = "https://api.openweathermap.org/data/2.5/weather?q=" + addressList.get(0).getLocality() + "&appid=fe98b60411a7f017746d6b63c213dc79&units=imperial";
                } else if (random >= 0.4 && random < 0.6) {
                    url = "https://api.openweathermap.org/data/2.5/weather?q=" + addressList.get(0).getLocality() + "&appid=e113d73038717b73d9283c8ce04702eb&units=imperial";
                } else if (random >= 0.6 && random < 0.8) {
                    url = "https://api.openweathermap.org/data/2.5/weather?q=" + addressList.get(0).getLocality() + "&appid=df5e38bd3e4d2196175487be356fa75e&units=imperial";
                } else {
                    url = "https://api.openweathermap.org/data/2.5/weather?q=" + addressList.get(0).getLocality() + "&appid=0d945bcf7cf94527e68cc4d81671796a&units=imperial";
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("main");
                            JSONArray jsonArray = response.getJSONArray("weather");
                            JSONObject object = jsonArray.getJSONObject(0);
                            String temperature = String.valueOf(jsonObject.getDouble("temp"));
                            JSONObject jo = response.getJSONObject("wind");
                            String wind = String.valueOf(jo.getDouble("speed"));
                            double tempInC = Double.parseDouble(temperature);
                            double centi = (tempInC - 32) / 1.80000;
                            centi = Math.floor(centi);
                            String description = object.getString("description");
                            String city = response.getString("name");
                            try {
                                TextView cityString = getView().findViewById(R.id.city);
                                cityString.setText(city);
                                TextView temp = getView().findViewById(R.id.temp);
                                temp.setText(Double.toString(centi) + " \u2103");
                                TextView humidity = getView().findViewById(R.id.humidity);
                                humidity.setText(description);
                                TextView speed = getView().findViewById(R.id.wind);
                                speed.setText(wind + " km/h");
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class SetTime {
        String currentTime() {
            long time = System.currentTimeMillis();
            DateFormat formatter = new SimpleDateFormat("hh:mm:ss aa");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return (formatter.format(calendar.getTime()));
        }

        String date() {
            long time = System.currentTimeMillis();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return (formatter.format(calendar.getTime()));
        }

        String getWeek() {
            long time = System.currentTimeMillis();
            DateFormat formatter = new SimpleDateFormat("EEE");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return (formatter.format(calendar.getTime()));
        }

        String getWeek_Full() {
            long time = System.currentTimeMillis();
            DateFormat formatter = new SimpleDateFormat("EEEE");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return (formatter.format(calendar.getTime()));
        }

        String hour() {
            long time = System.currentTimeMillis();
            DateFormat formatter = new SimpleDateFormat("HH");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return (formatter.format(calendar.getTime()));
        }

        String minute() {
            long time = System.currentTimeMillis();
            DateFormat formatter = new SimpleDateFormat("mm");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return (formatter.format(calendar.getTime()));
        }

        String generateDAndT() {
            return (date() + "\n" + currentTime());
        }
    }

    class GenerateClassId {
        GenerateClassId(String dept, String section, String year, String group) {
            dept(dept);
            section(section);
            year(year);
            group(group);
        }

        int dept(String dept) {
            if (dept.equals("CSE"))
                return 10;
            if (dept.equals("ECE"))
                return 11;
            if (dept.equals("IT"))
                return 12;
            if (dept.equals("CE"))
                return 13;
            if (dept.equals("ME"))
                return 14;
            if (dept.equals("ChE"))
                return 15;
            if (dept.equals("EE"))
                return 16;
            if (dept.equals("AEIE"))
                return 17;
            if (dept.equals("BT"))
                return 18;
            return 20;
        }

        int section(String sec) {
            if (sec.equals("N/A"))
                return 10;
            if (sec.equals("A"))
                return 11;
            if (sec.equals("B"))
                return 12;
            if (sec.equals("C"))
                return 13;

            return 20;
        }

        int year(String yearString) {
            if (yearString.equals("1st")) {
                return 10;
            }
            if (yearString.equals("2nd")) {
                return 11;
            }
            if (yearString.equals("3rd")) {
                return 12;
            }
            if (yearString.equals("4th")) {
                return 13;
            }
            return 20;
        }

        int group(String groupString) {
            if (groupString.equals("A")) {
                return 10;
            }
            if (groupString.equals("B"))
                return 11;
            return 20;
        }
    }

    class GenerateId {
        long time = System.currentTimeMillis();

        int dayID() {
            DateFormat formatter = new SimpleDateFormat("EEE");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);

            if (formatter.format(calendar.getTime()).toString().equals("Sun")) {
                return 10;
            }
            if (formatter.format(calendar.getTime()).toString().equals("Mon")) {
                return 11;
            }
            if (formatter.format(calendar.getTime()).toString().equals("Tue")) {
                return 12;
            }
            if (formatter.format(calendar.getTime()).toString().equals("Wed")) {
                return 13;
            }
            if (formatter.format(calendar.getTime()).toString().equals("Thu")) {
                return 14;
            }
            if (formatter.format(calendar.getTime()).toString().equals("Fri")) {
                return 15;
            }
            if (formatter.format(calendar.getTime()).toString().equals("Sat")) {
                return 16;
            }
            return 20;
        }

        int time() {
            DateFormat formatterHour = new SimpleDateFormat("HH");
            Calendar calendarHour = Calendar.getInstance();
            calendarHour.setTimeInMillis(time);
            DateFormat formatterMinute = new SimpleDateFormat("mm");
            Calendar calendarMinute = Calendar.getInstance();
            calendarMinute.setTimeInMillis(time);
            int hour = Integer.parseInt(formatterHour.format(calendarHour.getTime()));
            int minute = Integer.parseInt(formatterMinute.format(calendarMinute.getTime()));
            int time = ((100 * hour) + minute);
            if (time < 899) {
                return 21;
            }
            if (time >= 900 && time < 955) {
                return 10;
            }
            if (time >= 955 && time < 1050) {
                return 11;
            }
            if (time >= 1050 && time < 1145) {
                return 12;
            }
            if (time >= 1145 && time < 1225) {
                return 13;
            }
            if (time >= 1225 && time < 1320) {
                return 14;
            }
            if (time >= 1320 && time < 1415) {
                return 15;
            }
            if (time >= 1415 && time < 1510) {
                return 16;
            }
            if (time >= 1510 && time < 1605) {
                return 17;
            }
            if (time >= 1605 && time < 1700) {
                return 18;
            }
            if (time >= 1700 && time < 1755) {
                return 19;
            }
            return 20;
        }

        int generate() {
            return (dayID() * 100) + time();
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

