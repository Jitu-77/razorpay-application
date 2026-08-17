package com.jitu.razorpay_application.common_lib.uti;

import java.security.SecureRandom;
import java.util.Base64;

public class RandomizerUtil {
    public static final SecureRandom SECURE_RANDOM = new SecureRandom();
  public static String  randomBase64(int length) {
//      return UUID.randomUUID().toString().replace("_", ""); // any random number generation
      byte[] buf = new byte[length];
      SECURE_RANDOM.nextBytes(buf);
      return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
      //getUrlEncoder ----> removes any %20 ,%d from the url
      //withoutPadding ---> removes ==
      //Base64 ----> <a-zA-Z0-9-_>
    }
}
