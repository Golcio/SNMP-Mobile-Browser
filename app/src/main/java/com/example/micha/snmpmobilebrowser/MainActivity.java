package com.example.micha.snmpmobilebrowser;

import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.format.Formatter;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView textview;
    TextView r1c2;
    TextView r2c2;
    TextView r3c2;
    TextView r4c2;
    EditText ip;
    EditText port;
    Button save;
    String ipaddress;
    int sendport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textview = findViewById(R.id.textView);
        r1c2 = findViewById(R.id.r1c2);
        r2c2 = findViewById(R.id.r2c2);
        r3c2 = findViewById(R.id.r3c2);
        r4c2 = findViewById(R.id.r4c2);
        ip = findViewById(R.id.ipaddress);
        port = findViewById(R.id.port);
        save = findViewById(R.id.savebutton);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ipaddress = ip.getText().toString();
                sendport = Integer.parseInt(port.getText().toString());

            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.ipindelivers) {
            sendQuery(".1.3.6.1.2.1.4.9.0");
            textview.setText("ipInDelivers");
        } else if (id == R.id.sysobjectid) {
            sendQuery(".1.3.6.1.2.1.1.2.0");
            textview.setText("sysObjectID");
        } else if (id == R.id.sysuptime) {
            sendQuery(".1.3.6.1.2.1.1.3.0");
            textview.setText("sysUpTime");
        } else if (id == R.id.ifnumber) {
            sendQuery(".1.3.6.1.2.1.2.1.0");
            textview.setText("ifNumber");
        } else if (id == R.id.ipinreceives) {
            sendQuery(".1.3.6.1.2.1.4.3.0");
            textview.setText("ipInReceives");
        } else if (id == R.id.ipinhdrerrors) {
            sendQuery(".1.3.6.1.2.1.4.4.0");
            textview.setText("ipInHdrErrors");
        } else if (id == R.id.ipinaddrerrors) {
            sendQuery(".1.3.6.1.2.1.4.5.0");
            textview.setText("ipInAddrErrors");
        } else if (id == R.id.ipforwdatagrams) {
            sendQuery(".1.3.6.1.2.1.4.6.0");
            textview.setText("ipForwDatagrams");
        } else if (id == R.id.ipinunknownprotos) {
            sendQuery(".1.3.6.1.2.1.4.7.0");
            textview.setText("ipInUnknownProtos");
        } else if (id == R.id.ipindiscards) {
            sendQuery(".1.3.6.1.2.1.4.8.0");
            textview.setText("ipInDiscards");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void sendQuery(String name) {
        new Thread(() -> socketSend(name)).start();

    }

    public void socketSend(String name) {
        try {
            Socket socket = new Socket(ipaddress, sendport);
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(os, true);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String message = "get|" + name;
            SNMPQuery newquery = new SNMPQuery(message);
            Gson gson = new Gson();
            String serializedquery = gson.toJson(newquery);
            byte[] b = serializedquery.getBytes(StandardCharsets.UTF_8);
            os.write(b);
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                total.append(line).append('\n');
            }
            String totalstring1 = total.toString();
            newquery = new Gson().fromJson(totalstring1, SNMPQuery.class);
            String totalstring = newquery.message;
            runOnUiThread(() -> {
                if (!totalstring.contains("No results")){
                    String[] results = totalstring.split("_");
                    r1c2.setText(results[0]);
                    r2c2.setText(results[1]);
                    r3c2.setText(results[2]);
                    r4c2.setText(results[3]);
                }
                else {
                    r1c2.setText("-");
                    r2c2.setText("-");
                    r3c2.setText("-");
                    r4c2.setText("-");
                }

            });
            printWriter.close();
            os.close();
            is.close();
            socket.close();
        } catch (Exception ioex) {
            textview.setText("Can't connect with agent");
        }
    }
}
