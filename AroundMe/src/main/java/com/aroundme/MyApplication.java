package com.aroundme;

import android.app.Application;

/**
 * Created by nezer14 on 6/10/15.
 */
public class MyApplication extends Application {

    private String someVariable;

    public String getSomeVariable() {
        return someVariable;
    }

    public void setSomeVariable(String someVariable) {
        this.someVariable = someVariable;
    }
}//