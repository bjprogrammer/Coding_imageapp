package com.bobby.coding.model;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;


@IgnoreExtraProperties
public class Upload implements Serializable {

    public String name;
    public String url;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Upload() { }

    public Upload(String name, String url) {
        this.name = name;
        this.url= url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
