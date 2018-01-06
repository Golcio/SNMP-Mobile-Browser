package com.example.micha.snmpmobilebrowser;

import android.net.wifi.WifiManager;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView textview;
    Socket socket;
    Socket socket2;
    String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textview = findViewById(R.id.textView);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
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

        if (id == R.id.sysdescr) {
            sendQuery(".1.3.6.1.2.1.1.1.0");
        } else if (id == R.id.sysobjectid) {
            sendQuery("sysObjectID");
        } else if (id == R.id.sysuptime) {
            sendQuery("sysUpTime");
        } else if (id == R.id.ifnumber) {
            sendQuery("ifNumber");
        } else if (id == R.id.ipinreceives) {
            sendQuery("ipInReceives");
        } else if (id == R.id.ipinhdrerrors) {
            sendQuery("ipInHdrErrors");
        } else if (id == R.id.ipinaddrerrors) {
            sendQuery("ipInAddrErrors");
        } else if (id == R.id.ipforwdatagrams) {
            sendQuery("ipForwDatagrams");
        } else if (id == R.id.ipinunknownprotos) {
            sendQuery("ipInUnknownProtos");
        } else if (id == R.id.ipindiscards) {
            sendQuery("ipInDiscards");
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void sendQuery(String name) {
        textview.setText(ipAddress);
        new Thread(() -> socketSend(name)).start();

    }

    public void socketSend(String name)
    {
        try {
            socket = new Socket("192.168.43.230", 14000);
            OutputStream out = socket.getOutputStream();
            //PrintWriter output = new PrintWriter(out);
            String message = "get|" + name + "|" + ipAddress;
            byte[] b = message.getBytes(StandardCharsets.UTF_8);
            //output.println(b);
            out.write(b);
            //out.flush();
            out.close();
            //socket.close();
            InputStream stream = socket.getInputStream();
            byte[] data = new byte[100];
            String messagereceived = new String(data, StandardCharsets.UTF_8);
            textview.setText(messagereceived);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
