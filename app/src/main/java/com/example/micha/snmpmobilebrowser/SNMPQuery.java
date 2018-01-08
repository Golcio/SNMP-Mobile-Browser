package com.example.micha.snmpmobilebrowser;

import java.io.Serializable;

/**
 * Created by Michał on 08.01.2018.
 */
public class SNMPQuery implements Serializable{
    String message;
    public SNMPQuery(String msg)
    {
        message=msg;
    }
}