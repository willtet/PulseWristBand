package com.newhorizon.pulsewristband.helper;

import android.util.Base64;

public class Base64CD {
    public static String codeBase64(String texto){
        return Base64.encodeToString(texto.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)","");

    }

    public static String decodeBase64(String texto){
        return new String(Base64.decode(texto, Base64.DEFAULT));
    }
}
