package com.team45.gtpickupsports.networking;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

/**
 * A class to login and obtain an authentication cookie from
 * GaTech CAS login system.
 *
 * Created by Glenn on 9/15/2014.
 */
public class CASAuthenticator {

    private static final String casURI = "https://login.gatech.edu/cas/login";
    private String user, pass;

    public CASAuthenticator(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    /**
     * Connects to CAS authentication given a user name and password.
     *
     * @return A map of cookie name/value pairs needed to access CAS protected pages.
     * 		   Returns null if authentication fails.
     */
    public Map<String, String> connect() {
        Map<String, String> cookies = null;

        try {
            Response loginPage = Jsoup.connect(casURI).method(Method.GET).execute();
            String randomKey = getRandomKey(loginPage.parse());
            Response submitPage = Jsoup
                    .connect(casURI)
                    .data("username", user)
                    .data("password", pass)
                    .data("lt", randomKey)
                    .data("execution", "e1s1")
                    .data("_eventId", "submit")
                    .cookies(loginPage.cookies())
                    .method(Method.POST)
                    .execute();
            cookies = submitPage.cookies();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        //Verify if a valid CAS cookie was received
        if (cookies != null && !cookies.containsKey("CASTGT") ) {
            cookies = null;
        }

        return cookies;
    }


    /**
     * Find the value of the hidden input "lt".
     * This value is a random key and must be forwarded to POST
     * when entering a user name and password.
     *
     * @param doc The login page document
     * @return The randomized key
     */
    private String getRandomKey(Document doc) {
        return doc.select("input[name=lt]").first().attr("value");
    }
}
