package com.rodriguez.foundmatch.Keys;

import java.util.ArrayList;

public class MatchesHelper {
    private ArrayList<KeyInformation> closestKeys = null;
    private static MatchesHelper matchInstance;

    public static MatchesHelper getMatchInstance(){
        if(matchInstance == null)
            matchInstance = new MatchesHelper();

        return matchInstance;
    }

    private MatchesHelper(){
        closestKeys = new ArrayList<KeyInformation>();
    }

    public ArrayList<KeyInformation> getArray(){
        return this.closestKeys;
    }

    public void addToArray(KeyInformation key){
        closestKeys.add(key);
    }
}
