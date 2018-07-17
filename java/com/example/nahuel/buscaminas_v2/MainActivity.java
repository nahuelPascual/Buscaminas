package com.example.nahuel.buscaminas_v2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{

    private Tablero fondo;
    int x, y, dimTablero=8;
    private Casilla[][] casillas;
    private boolean activo = true;
    ToggleButton toggleBandera;
    Button restart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toggleBandera = (ToggleButton) findViewById(R.id.toggleBanderita);
        restart = (Button) findViewById(R.id.restart);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reiniciarJuego();
            }
        });

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);

        fondo = new Tablero(this);
        fondo.setOnTouchListener(this);
        layout.addView(fondo);

        reiniciarJuego();

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

//        if (id== R.id.settings_Easy) {
//            dimTablero = 8;
//            return true;
//        }
//        if (id==R.id.settings_Normal) {
//            dimTablero = 12;
//            return true;
//        }
//        if (id== R.id.settings_Hard) {
//            dimTablero = 16;
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int posX = (int) event.getX();
        int posY = (int) event.getY();

        if (activo){
            for (int i = 0; i < dimTablero; i++){
                for (int j = 0; j<dimTablero; j++){

                    if (casillas[i][j].dentroDeLaCasilla(posX,posY)){
                        if(toggleBandera.isChecked()) {
                            if(!casillas[i][j].destapada) {
                                casillas[i][j].banderita = !casillas[i][j].banderita;
                            }
                        }else{

                           if(!casillas[i][j].banderita){
                               casillas[i][j].destapada = true;

                               if (casillas[i][j].contenido == 100) {
                                   Toast.makeText(this, "Boooooom! Perdiste",
                                           Toast.LENGTH_LONG).show();
                                   destaparBombas();
                                   activo = false;
                               } else if (casillas[i][j].contenido == 0) {
                                   recorrer(i, j);
                               }
                           }
                        }
                        fondo.invalidate();
                    }
                }
            }

            if (activo && heGanado()){
                Toast.makeText(this, "Felicitacinoes, ganaste!",
                        Toast.LENGTH_LONG).show();
                destaparBombas();
                activo = false;
            }

        }

        return false;
    }

    public void destaparBombas(){
        for (int i = 0; i<dimTablero; i++){
            for (int j = 0; j < dimTablero; j++){
                casillas[i][j].banderita = false;
                if (casillas[i][j].contenido==100){
                    casillas[i][j].destapada = true;
                }
            }
        }
        fondo.invalidate();
    }

    public void reiniciarJuego(){
        casillas = new Casilla[dimTablero][dimTablero];
        for (int i = 0; i<dimTablero; i++){
            for (int j = 0; j<dimTablero; j++){
                casillas[i][j] = new Casilla();
            }
        }
        colocarMinas();
        contarBombasDelPerimetro();
        activo = true;

        fondo.invalidate();
    }

    private void colocarMinas(){
        int cantidadDeMinasPorColocar = 8;
        if(dimTablero==12) cantidadDeMinasPorColocar = 20;
        if(dimTablero==16) cantidadDeMinasPorColocar = 40;
        while (cantidadDeMinasPorColocar>0){
            int fila = (int) (Math.random()*dimTablero);
            int columna = (int) (Math.random()*dimTablero);
            if (casillas[fila][columna].contenido == 0){
                casillas[fila][columna].contenido = 100;
                cantidadDeMinasPorColocar --;
            }
        }
    }

    private boolean heGanado(){
        int cantidad = 0;
        for (int i = 0; i< dimTablero; i++){
            for (int j = 0; j<dimTablero; j++){
                if (casillas[i][j].destapada){
                    cantidad++;
                }
            }
        }

        if (cantidad == 56){
            return true;
        } else {
            return false;
        }
    }

    private void contarBombasDelPerimetro() {
        for (int i = 0; i < dimTablero; i++) {
            for (int j = 0; j < dimTablero; j++) {
                if (casillas[i][j].contenido == 0) {
                    casillas[i][j].contenido = contarCoordenada(i, j);
                }
            }
        }
    }

    private int contarCoordenada(int fila, int columna){
        int cantidadDeBombas = 0;

        if (fila - 1 >= 0 && columna - 1 >=0){
            if (casillas[fila-1][columna-1].contenido==100){
                cantidadDeBombas++;
            }
        }

        if (fila - 1 >= 0){
            if (casillas[fila-1][columna].contenido==100){
                cantidadDeBombas++;
            }
        }

        if (fila - 1 >= 0 && columna + 1 <dimTablero){
            if (casillas[fila-1][columna+1].contenido==100){
                cantidadDeBombas++;
            }
        }

        if ( columna + 1 <dimTablero){
            if (casillas[fila][columna+1].contenido==100){
                cantidadDeBombas++;
            }
        }

        if (fila + 1 <dimTablero && columna + 1 <dimTablero){
            if (casillas[fila+1][columna+1].contenido==100){
                cantidadDeBombas++;
            }
        }

        if (fila + 1 <dimTablero ){
            if (casillas[fila+1][columna].contenido==100){
                cantidadDeBombas++;
            }
        }

        if (fila + 1 <dimTablero && columna - 1 >=0){
            if (casillas[fila+1][columna-1].contenido==100){
                cantidadDeBombas++;
            }
        }

        if ( columna - 1 >=0){
            if (casillas[fila][columna-1].contenido==100){
                cantidadDeBombas++;
            }
        }

        return cantidadDeBombas;
    }

    private void recorrer(int fila, int columna){

        if (fila>=0 && fila <dimTablero && columna>=0 && columna<dimTablero){
            if (casillas[fila][columna].contenido == 0 &&
                    !casillas[fila][columna].banderita){

                casillas[fila][columna].destapada = true;
                casillas[fila][columna].contenido = 50;

                recorrer(fila-1, columna-1);
                recorrer(fila-1,columna);
                recorrer(fila-1,columna+1);
                recorrer(fila,columna+1);
                recorrer(fila+1, columna+1);
                recorrer(fila+1, columna);
                recorrer(fila+1, columna-1);
                recorrer(fila, columna-1);

            } else if (casillas[fila][columna].contenido<=8 &&
                    !casillas[fila][columna].banderita){
                casillas[fila][columna].destapada = true;
            }
        }


    }






    public class Tablero extends View {


        public Tablero(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas){

            canvas.drawRGB(0,0,0);

            int ancho = 0;
            int alto = 0;
            if (canvas.getWidth()<canvas.getHeight()){
                ancho = fondo.getWidth();
                alto = fondo.getHeight();
            } else {
                ancho = fondo.getHeight();
                alto = fondo.getWidth();
            }

            int anchoCasilla = ancho/8;


            Paint paint = new Paint();
            paint.setTextSize(40);

            Paint paintLinea1 = new Paint();

            paintLinea1.setARGB(255,255,255,255);

            int filaActual = (alto-ancho)/4 ;

            for (int i = 0; i < dimTablero; i++){
                for (int j = 0; j < dimTablero ; j++){
                    casillas[i][j].fijarCoordenadasXY(j * anchoCasilla, filaActual, anchoCasilla);

                    if (casillas[i][j].destapada){
                        paint.setARGB(255,153,153,153);
                    } else {
                        paint.setARGB(153, 204,204,204);
                    }

                    canvas.drawRect(j*anchoCasilla, filaActual, j*anchoCasilla+anchoCasilla-1,
                            filaActual+anchoCasilla-2, paint);
                    canvas.drawLine(j*anchoCasilla, filaActual, j*anchoCasilla+anchoCasilla,
                            filaActual, paintLinea1);
                    canvas.drawLine(j*anchoCasilla + anchoCasilla-1, filaActual,
                            j*anchoCasilla+anchoCasilla-1, filaActual+anchoCasilla, paintLinea1);

                    // Defino pinceles para escribir de distintos colores segun la cantidad de minas vecinas
                    Paint blue = new Paint();
                    blue.setTextSize((anchoCasilla/8)*5);
                    blue.setTypeface(Typeface.DEFAULT_BOLD);
                    blue.setARGB(255,0,0,255);

                    Paint red = new Paint();
                    red.setTextSize((anchoCasilla/8)*5);
                    red.setTypeface(Typeface.DEFAULT_BOLD);
                    red.setARGB(255,255,0,0);

                    Paint green = new Paint();
                    green.setTextSize((anchoCasilla/8)*5);
                    green.setTypeface(Typeface.DEFAULT_BOLD);
                    green.setARGB(255,60, 118, 54);

                    Paint orange = new Paint();
                    orange.setTextSize((anchoCasilla/8)*5);
                    orange.setTypeface(Typeface.DEFAULT_BOLD);
                    orange.setARGB(255,239, 129, 37);

                    Paint violet = new Paint();
                    violet.setTextSize((anchoCasilla/8)*5);
                    violet.setTypeface(Typeface.DEFAULT_BOLD);
                    violet.setARGB(255,152, 47, 229);

                    Paint ligthGreen = new Paint();
                    ligthGreen.setTextSize((anchoCasilla/8)*5);
                    ligthGreen.setTypeface(Typeface.DEFAULT_BOLD);
                    ligthGreen.setARGB(255,152, 47, 229);

                    // En este caso, la casilla no tiene mina pero alguno de sus vecinos sí.
                    if (casillas[i][j].contenido>=1 &&
                            casillas[i][j].contenido<8 &&
                            casillas[i][j].destapada) {
                        switch (casillas[i][j].contenido) {
                            case 1:
                                canvas.drawText(
                                        String.valueOf(casillas[i][j].contenido),
                                        j * anchoCasilla + (anchoCasilla / 2) - 20,
                                        filaActual + anchoCasilla / 2 + 30,
                                        blue);
                                break;
                            case 2:
                                canvas.drawText(
                                        String.valueOf(casillas[i][j].contenido),
                                        j * anchoCasilla + (anchoCasilla / 2) - 20,
                                        filaActual + anchoCasilla / 2 + 30,
                                        orange);
                                break;
                            case 3:
                                canvas.drawText(
                                        String.valueOf(casillas[i][j].contenido),
                                        j * anchoCasilla + (anchoCasilla / 2) - 20,
                                        filaActual + anchoCasilla / 2 + 30,
                                        green);
                                break;
                            case 4:
                                canvas.drawText(
                                        String.valueOf(casillas[i][j].contenido),
                                        j * anchoCasilla + (anchoCasilla / 2) - 20,
                                        filaActual + anchoCasilla / 2 + 30,
                                        violet);
                                break;
                            case 5:
                                canvas.drawText(
                                        String.valueOf(casillas[i][j].contenido),
                                        j * anchoCasilla + (anchoCasilla / 2) - 20,
                                        filaActual + anchoCasilla / 2 + 30,
                                        ligthGreen);
                                break;
                            default:
                                canvas.drawText(
                                        String.valueOf(casillas[i][j].contenido),
                                        j * anchoCasilla + (anchoCasilla / 2) - 20,
                                        filaActual + anchoCasilla / 2 + 30,
                                        red);
                        }
                    }

                    // En este caso, la casilla tiene una mina
                    if (casillas[i][j].contenido == 100 &&
                            casillas[i][j].destapada){
                        canvas.drawCircle(
                                j*anchoCasilla+anchoCasilla/2,
                                filaActual+anchoCasilla/2,
                                20,
                                red);
                    }

                    Paint flag = new Paint();
                    flag.setARGB(255, 0, 0, 0);
                    flag.setTextSize((anchoCasilla/4)*3);
                    flag.setTypeface(Typeface.DEFAULT_BOLD);

                    if(casillas[i][j].banderita == true /*&&
                            casillas[i][j].destapada == false*/){
                        canvas.drawText(
                                "B",
                                j*anchoCasilla + (anchoCasilla/2) - 25,
                                filaActual + anchoCasilla/2 + 30,
                                flag);
                    }
                }
                filaActual += anchoCasilla;
            }
        }
    }
}
