package cl.rescuecar.www.rescuecarclient;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class detalleInfo extends ConexionMysqlHelper{

    ImageView perfil;
    ImageView internet;
    int servicioInt, cantidadServ;
    String grut, gdiv, gnombre, gapellido, gtelefono, gtipo;
    String rut, time, dist, tip;
    EditText nombre, solicitud, tipo;
    JSONObject jsonObject;
    JSONArray jsonArray;
    TextView rut_user, dig_user, nom_user, ape_user, ema_user, tel_user, patente_serv;
    String[] services;
    EditText nombre_serv, telefono_serv;
    TextView serv1,serv2, serv3, serv4, serv5,serv6, serv7, serv8, serv9, serv10, tiempo_serv, distancia_serv;
    ImageView star1, star2, star3, star4, star5;
    String detalle, calif, vehiculo, servicios_serv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_info);

        rut_user = (TextView) findViewById(R.id.tvRut);
        nom_user =(TextView) findViewById(R.id.tvNombre);
        ema_user = (TextView) findViewById(R.id.tvEmail);
        tel_user = (TextView) findViewById(R.id.tvTelefono);
        patente_serv= (TextView) findViewById(R.id.tvPat);


        perfil = (ImageView) findViewById(R.id.improfile);
        internet = (ImageView) findViewById(R.id.imInt);
        star1 = (ImageView) findViewById(R.id.star1);
        star2 = (ImageView) findViewById(R.id.star2);
        star3 = (ImageView) findViewById(R.id.star3);
        star4 = (ImageView) findViewById(R.id.star4);
        star5 = (ImageView) findViewById(R.id.star5);

        escuchaServicios();
        obtenerDatos();
    }

    public void escuchaServicios() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null) {
            internet.setImageResource(R.drawable.int_si);
            servicioInt = 1;
        } else {
            internet.setImageResource(R.drawable.int_no);
            Toast.makeText(getApplicationContext(), "¡¡ Tu teléfono no esta conectado a internet!!", Toast.LENGTH_SHORT).show();
            servicioInt = 0;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        escuchaServicios();
                    }
                });
            }
        }, 50000);

    }

    private void obtenerDatos() {

        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            rut = (String) b.get("rut");
            time = (String) b.get("time");
            dist = (String) b.get("dist");
        }

        varGlob varglob = (varGlob) getApplicationContext();

        grut = varglob.getRut();
        gdiv = varglob.getDiv();
        rut_user.setText(grut+"-"+gdiv) ;
        gnombre = varglob.getNombre();
        gapellido = varglob.getApellido();
        gtelefono = varglob.getTelefono();
        gtipo = varglob.getServicios();
        BuscarAlerta();
    }

    public void BuscarAlerta() {

        new BackgroundTask().execute();

    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String json_url;

        @Override
        protected void onPreExecute() {

            json_url = "http://www.webinfo.cl/soshelp/cons_chofer_client.php?rut="+grut;

        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_STRING + "\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            JSON_STRING = null;
            JSON_STRING = result;
            presentarDatos();

        }
    }

    public void presentarDatos() {
        if (JSON_STRING != null) {

            try {
                jsonObject = new JSONObject(JSON_STRING);
                jsonArray = jsonObject.getJSONArray("server_response");

                JSONObject JO = jsonArray.getJSONObject(0);
                detalle = JO.getString("detalle");
                calif = JO.getString("calificaciones");
                vehiculo = JO.getString("vehiculo");

                if (detalle.length() > 2 && calif.length() > 2 && vehiculo.length() > 2) {

                    String[]  infoP = detalle.split(",");
                    nom_user.setText(infoP[1]+" "+infoP[2]);
                    tel_user.setText("+569"+infoP[3]);
                    ema_user.setText(infoP[4]);

                    String[] infoV = vehiculo.split(",");
                    patente_serv.setText(infoV[0]);

                    Toast.makeText(this, "Detalle"+detalle, Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "calif"+calif, Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "vehiculo"+vehiculo, Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(this, "No se ha encontrado al conductor de servicio", Toast.LENGTH_SHORT).show();

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
