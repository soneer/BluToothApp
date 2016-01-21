package ss.bleight;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends Activity {


    /*Daniel Edit These 5 Strings Below*/
    private String danielsBlueToothDevice= "SONEER";
    private String lightsOnButtonString= "commandYouWantToSend";
    private String lightsOffButtonString= "commandYouWantToSend";
    private String emergencyLightsButtonString= "commandYouWantToSend";
    private String trailerVoltageButtonString= "commandYouWantToSend";
    /*-------------------------------*/

    private ArrayList <BluetoothDevice> bluetoothList = new ArrayList<BluetoothDevice>();
    private ArrayList <String> bluetoothListString = new ArrayList<String>();

    private Button lightsOnButton, lightsOffButton, emergencyLightsButton, trailerVoltageButton, motionDetectorButton;
    private BluetoothSocket socket = null;
    private static final UUID CONUUID = UUID.fromString("0000110E-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    private OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVariables();
    }

    public void initializeVariables() {

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4db7df")));
       bar.setTitle(Html.fromHtml("<font color='#ffffff'> BLEIGHT </font>"));





        lightsOnButton = (Button) findViewById(R.id.lightsOn);
        lightsOffButton = (Button) findViewById(R.id.lightsOff);
        emergencyLightsButton = (Button) findViewById(R.id.emergencyFlashers);
        trailerVoltageButton = (Button) findViewById(R.id.trailerVoltage);
        motionDetectorButton = (Button) findViewById(R.id.motionDevicesButton);

        motionDetectorButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MotionDetectorTab.class);
                startActivity(intent);

            }
        });
      //  blueTooth = (Button) findViewById(R.id.blueTooth);
        buttonController(lightsOnButtonString, lightsOnButton);
        buttonController(lightsOffButtonString, lightsOffButton);
        buttonController(emergencyLightsButtonString, emergencyLightsButton);
        buttonController(trailerVoltageButtonString, trailerVoltageButton);


        BA = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = BA.getBondedDevices();
        for (BluetoothDevice bt : pairedDevices) {
            bluetoothList.add(bt);
            bluetoothListString.add(bt.getName());
           /* if (bt.getName().equals(danielsBlueToothDevice)) {
                BluetoothDevice actual = BA.getRemoteDevice(bt.getAddress());
                try {
                    socket = createSocket(actual);
                    socket.connect();
                    outputStream = socket.getOutputStream();
                } catch (IOException e) {

                    e.printStackTrace();
                }
                if (socket.isConnected()) {
                    Toast.makeText(getApplicationContext(), "Connected to " + bt.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_SHORT).show();
                }
            }*/
        }
        final ListView lv = (ListView) findViewById(R.id.bluetoothList);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, bluetoothListString);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                for (BluetoothDevice bt : bluetoothList) {
            if (bt.getName().equals(((TextView) view).getText())) {
                BluetoothDevice actual = BA.getRemoteDevice(bt.getAddress());
                try {
                    socket = createSocket(actual);
                    socket.connect();
                    outputStream = socket.getOutputStream();
                } catch (IOException e) {

                    e.printStackTrace();
                }
                if (socket.isConnected()) {
                    Toast.makeText(getApplicationContext(), "Connected to " + bt.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Not Connected to ", Toast.LENGTH_SHORT).show();
                }
            }
                }
                Toast.makeText(getApplicationContext(),
                        "You selected : " + ((TextView) view).getText(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void buttonController(final String cmd, Button button){
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Executing Command:" + cmd,
                        Toast.LENGTH_SHORT).show();
                byte[] bytes = new byte[0];
                try {
                    bytes = cmd.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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



    public static BluetoothSocket createSocket(final BluetoothDevice device) throws IOException {
        BluetoothSocket socket = null;
        try {
            Method m = device.getClass().getMethod("createRfcommSocket", int.class);
            socket = (BluetoothSocket) m.invoke(device, 1);
        } catch (NoSuchMethodException e) {
            socket = device.createInsecureRfcommSocketToServiceRecord(CONUUID);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return socket;
    }


}
