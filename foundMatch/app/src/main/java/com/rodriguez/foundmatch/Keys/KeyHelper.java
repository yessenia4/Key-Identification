package com.rodriguez.foundmatch.Keys;

import java.util.ArrayList;

public class KeyHelper {
    private KeyInformation key = null;
    private static final KeyHelper instanceS = new KeyHelper();

    public KeyHelper() { }

    public static KeyHelper getInstanceS() { return instanceS; }

    public KeyInformation getKey() { return key; }

    public void setKey(KeyInformation key) { this.key = key; }
}
