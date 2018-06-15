package com.team45.gtpickupsports.networking.security.token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

/**
 * Firebase JWT token generator.
 *
 * @author vikrum
 */
public class TokenGenerator {

    private static final int TOKEN_VERSION = 0;

    private final String firebaseSecret;

    /**
     * Default constructor given a Firebase secret.
     *
     * @param firebaseSecret Secret from Firebase
     */
    public TokenGenerator(String firebaseSecret) {
        super();
        this.firebaseSecret = firebaseSecret;
    }

    /**
     * Create a token for the given object.
     *
     * @param data Data to generate token for
     * @return Token string
     */
    public String createToken(Map<String, Object> data) {
        if (data == null || data.size() == 0) {
            throw new IllegalArgumentException("TokenGenerator.createToken: data is empty.  This token will have no effect on Firebase.");
        }

        JSONObject claims = new JSONObject();
        try {
            claims.put("v", TOKEN_VERSION);
            claims.put("iat", new Date().getTime() / 1000);

            validateToken("TokenGenerator.createToken", data, false);

            if (data.size() > 0) {
                claims.put("d", new JSONObject(data));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        String token = computeToken(claims);
        if (token.length() > 1024) {
            throw new IllegalArgumentException("TokenGenerator.createToken: Generated token is too long. The token cannot be longer than 1024 bytes.");
        }
        return token;
    }

    private String computeToken(JSONObject claims) {
        return JWTEncoder.encode(claims, firebaseSecret);
    }

    private void validateToken(String functionName, Map<String, Object> data, boolean isAdminToken) {
        boolean containsUid = (data != null && data.containsKey("uid"));
        if ((!containsUid && !isAdminToken) || (containsUid && !(data.get("uid") instanceof String))) {
            throw new IllegalArgumentException(functionName + ": Data payload must contain a \"uid\" key that must be a string.");
        } else if (containsUid && data.get("uid").toString().length() > 256) {
            throw new IllegalArgumentException(functionName + ": Data payload must contain a \"uid\" key that must not be longer than 256 characters.");
        }
    }
}
