package com.emtechhouse.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configurations {
    Properties properties;

    public Configurations(){
    }

    public Properties getProperties(){
        properties = new Properties();
        try {
            InputStream url = getClass().getClassLoader().getResourceAsStream("application.properties");
            properties.load(url);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return properties;
    }
}
