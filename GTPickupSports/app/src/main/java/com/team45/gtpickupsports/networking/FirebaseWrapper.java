package com.team45.gtpickupsports.networking;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.team45.gtpickupsports.models.SportEvent;
import com.team45.gtpickupsports.models.User;
import com.team45.gtpickupsports.networking.security.token.TokenGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper class for Firebase integration
 *
 * Created by kushagramansingh on 9/16/14.
 */
public class FirebaseWrapper {
    public static Firebase myFirebase = new Firebase("https://gt-pickup-tracker.firebaseio.com");
    private static final String FIREBASE_SECRET = "6jcZCnk74nCcmiL4t54etBgLlX6MwZ5mqcbNoFxs";

    public static void addAttendee(String username, SportEvent event) {
        event.addAttendee();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attendees", event.getAttendees());
        myFirebase.child("Events").child(event.getId()).updateChildren(map);
        myFirebase.child("Users").child(username).child("EventList").child(event.getId()).setValue(event);
    }

    public static void removeAttendee(String username, SportEvent event) {
        event.removeAttendee();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attendees", event.getAttendees());
        myFirebase.child("Events").child(event.getId()).updateChildren(map);
        myFirebase.child("Users").child(username).child("EventList").child(event.getId()).removeValue();
    }

    public static void addEvent(SportEvent event) {
        Firebase eventsRef = myFirebase.child("Events");
        Firebase eventRef = eventsRef.push();
        eventRef.setValue(event);
        myFirebase.child("Users").child(User.username).child("EventList")
                .child(eventRef.getName()).setValue(event);
    }

    public static void unauth(){
        myFirebase.unauth();
    }

    public static void authWithUsername(String username){
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("uid", username);
        TokenGenerator tokenGenerator = new TokenGenerator(FIREBASE_SECRET);
        String token = tokenGenerator.createToken(payload);
        myFirebase.authWithCustomToken(token, new FirebaseAuthHandler());
    }

    public static void addAuthStateListener(Firebase.AuthStateListener authListener){
        myFirebase.addAuthStateListener(authListener);
    }

    private static class FirebaseAuthHandler implements Firebase.AuthResultHandler {

        @Override
        public void onAuthenticated(AuthData authData) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("provider", authData.getProvider());
            if(authData.getProviderData().containsKey("id")) {
                map.put("provider_id", authData.getProviderData().get("id").toString());
            }
            FirebaseWrapper.myFirebase.child("Users").child(authData.getUid()).updateChildren(map);
        }
        @Override
        public void onAuthenticationError(FirebaseError error) {
        }
    }
}
