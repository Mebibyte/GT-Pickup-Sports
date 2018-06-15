package com.team45.gtpickupsports.networking.security.token;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * JWT encoder.
 *
 * @author vikrum
 */
public class JWTEncoder {

    private static final String TOKEN_SEP = ".";
    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private static final String HMAC_256 = "HmacSHA256";

    /**
     * Encode and sign a set of claims.
     *
     * @param claims JSON Claims
     * @param secret Secret string
     * @return Encoded JWT
     */
    public static String encode(JSONObject claims, String secret) {
        String secureBits = getCommonHeader() + TOKEN_SEP + encodeJson(claims);
        return secureBits + TOKEN_SEP + sign(secret, secureBits);
    }

    private static String sign(String secret, String secureBits) {
        try {
            Mac sha256_HMAC = Mac.getInstance(HMAC_256);
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(UTF8_CHARSET), HMAC_256);
            sha256_HMAC.init(secret_key);
            byte sig[] = sha256_HMAC.doFinal(secureBits.getBytes(UTF8_CHARSET));
            return new String(Base64.encodeBase64(sig)).replace('+','-').replace('/','_');
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getCommonHeader() {
        try {
            JSONObject headerJson = new JSONObject();
            headerJson.put("typ", "JWT");
            headerJson.put("alg", "HS256");
            return encodeJson(headerJson);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static String encodeJson(JSONObject jsonData) {
        return new String(Base64.encodeBase64(jsonData.toString().getBytes(UTF8_CHARSET))).replace('+','-').replace('/','_');
    }
}
