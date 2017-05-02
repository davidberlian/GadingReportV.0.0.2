package com.david.gadingreport2017_fragment_test;

/**
 * Created by davidberlian on 4/19/17.
 */

public class EnvironmentVariable {
    private static EnvironmentVariable mInstance = null;
    private static String location = "login";
    private static String username = "";

    public static String getNickname() {
        return nickname;
    }

    public static void setNickname(String nickname) {
        EnvironmentVariable.nickname = nickname;
    }

    private static String nickname = "";

    public String url = "https://www.davidberlian.com/gadingreport/API/";

    protected EnvironmentVariable(){}

    public static synchronized EnvironmentVariable getInstance(){
        if (null == mInstance){
            mInstance = new EnvironmentVariable();
        }
        return mInstance;
    }
    public static void setIntanceLoc(String var){
        if(var != null){
            location = var;
        }
    }
    public static String getInstanceLoc(){
        return location;
    }

    public static void setUsername(String u){
        if(u != null){
            username = u;
        }
    }
    public static String getUsername(){
        return username;
    }




}
