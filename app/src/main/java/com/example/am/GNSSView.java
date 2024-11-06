package com.example.am;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.GnssStatus;
import android.util.AttributeSet;
import android.view.View;

public class GNSSView extends View {
    private GnssStatus newStatus;
    private int r;
    private int height, width;

    public GNSSView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setGnssStatus(GnssStatus status) {
        this.newStatus = status;
        invalidate(); // Redesenha a view com o novo status
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Coletando informações do tamanho da tela de desenho
        width = getMeasuredWidth();
        height = getMeasuredHeight();

        // Definindo o raio da esfera celeste
        r = (width < height) ? (int) (width / 2 * 0.9) : (int) (height / 2 * 0.9);

        // Configurando o pincel para desenhar a projeção da esfera celeste
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLUE);

        // Desenhando círculos concêntricos para a esfera celeste
        int radius = r;
        canvas.drawCircle(computeXc(0), computeYc(0), radius, paint);
        radius = (int) (radius * Math.cos(Math.toRadians(45)));
        canvas.drawCircle(computeXc(0), computeYc(0), radius, paint);
        radius = (int) (radius * Math.cos(Math.toRadians(60)));
        canvas.drawCircle(computeXc(0), computeYc(0), radius, paint);

        // Desenhando os eixos
        canvas.drawLine(computeXc(0), computeYc(-r), computeXc(0), computeYc(r), paint);
        canvas.drawLine(computeXc(-r), computeYc(0), computeXc(r), computeYc(0), paint);

        // Configurando o pincel para desenhar os satélites
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(30);

        // Desenhando os satélites (caso exista um GnssStatus disponível)
        if (newStatus != null) {
            for (int i = 0; i < newStatus.getSatelliteCount(); i++) {
                float az = newStatus.getAzimuthDegrees(i);
                float el = newStatus.getElevationDegrees(i);
                float x = (float) (r * Math.cos(Math.toRadians(el)) * Math.sin(Math.toRadians(az)));
                float y = (float) (r * Math.cos(Math.toRadians(el)) * Math.cos(Math.toRadians(az)));

                canvas.drawCircle(computeXc(x), computeYc(y), 10, paint);
                String satID = newStatus.getSvid(i) + "";
                canvas.drawText(satID, computeXc(x) + 10, computeYc(y) + 10, paint);
            }
        }
    }

    private int computeXc(double x) {
        return (int) (x + width / 2);
    }

    private int computeYc(double y) {
        return (int) (-y + height / 2);
    }
}
