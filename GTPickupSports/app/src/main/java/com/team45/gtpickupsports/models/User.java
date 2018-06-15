package com.team45.gtpickupsports.models;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.team45.gtpickupsports.networking.FirebaseWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Glenn
 */
public class User{
    public static String username;
    public static List<String> eventList;

    public static void createUser(String uname){
        if (username != null){
            return;
        }
        eventList = new ArrayList<String>();

        username = uname;
        FirebaseWrapper.myFirebase.child("Users").child(username).child("EventList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear();
                @SuppressWarnings("unchecked")
                Map<String, HashMap<String, Object>> newData =  (Map<String, HashMap<String, Object>>) dataSnapshot.getValue();
                if (newData != null) {
                    for (Map.Entry<String, HashMap<String, Object>> entry : newData.entrySet()) {
                        eventList.add(entry.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}