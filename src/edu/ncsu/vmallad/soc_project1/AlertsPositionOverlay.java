package edu.ncsu.vmallad.soc_project1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.location.Location;
import android.net.Uri;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.provider.*;

public class AlertsPositionOverlay extends Overlay {
  Context context;

  public AlertsPositionOverlay(Context _context) {
    this.context = _context;
  }

  /** Get the position location */
  public Location getLocation() {
    return location;
  }

  /** Set the position location */
  public void setLocation(Location location) {
    this.location = location;
  }

  Location location;
  
  private final int mRadius = 5;

  @Override
  public void draw(Canvas canvas, MapView mapView, boolean shadow) {
    Projection projection = mapView.getProjection();

    if (location == null)
      return;

    if (shadow == false) {
      // Get the current location
      Double latitude = location.getLatitude() * 1E6;
      Double longitude = location.getLongitude() * 1E6;
      GeoPoint geoPoint = new GeoPoint(latitude.intValue(), longitude.intValue());

      // Convert the location to screen pixels
      Point point = new Point();
      projection.toPixels(geoPoint, point);

      RectF oval = new RectF(point.x - mRadius, point.y - mRadius, point.x + mRadius, point.y
          + mRadius);

      // Setup the paint
      Paint paint = new Paint();
      paint.setARGB(255, 255, 255, 255);
      paint.setAntiAlias(true);
      paint.setFakeBoldText(true);

      Paint backPaint = new Paint();
      backPaint.setARGB(180, 50, 50, 50);
      backPaint.setAntiAlias(true);

      RectF backRect = new RectF(point.x + 2 + mRadius, point.y - 3 * mRadius, point.x + 65,
          point.y + mRadius);

      // Draw the marker
      canvas.drawOval(oval, paint);
      canvas.drawRoundRect(backRect, 5, 5, backPaint);
      canvas.drawText("Here I Am", point.x + 2 * mRadius, point.y, paint);
    }
    super.draw(canvas, mapView, shadow);
  }

  @Override
  public boolean onTap(GeoPoint point, MapView mapView) {
    final int latitude = point.getLatitudeE6();
    final int longitude = point.getLongitudeE6();

    final FrameLayout fl = new FrameLayout(context);
    final EditText input = new EditText(context);
    input.setGravity(Gravity.CENTER);

    fl.addView(input, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
        FrameLayout.LayoutParams.WRAP_CONTENT));

    input.setText("Alert text");
    new AlertDialog.Builder(context)
        .setView(fl)
        .setTitle("Please enter the alert...")
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface d, int which) {
            d.dismiss();

            Toast.makeText(context,
                latitude + ", " + longitude + ": " + input.getText().toString(), Toast.LENGTH_LONG)
                .show();
            
            //ContentResolver cr = getContext().getContentResolver();
            ContentValues values = new ContentValues();
            values.put(AlertProvider.KEY_ALERT, input.getText().toString());
            values.put(AlertProvider.KEY_PLACE_LAT, latitude);
            values.put(AlertProvider.KEY_PLACE_LNG, longitude);
            
              Uri myuri = context.getContentResolver().insert(AlertProvider.CONTENT_URI, values);
              System.out.println("Data  inserted");
 //           Uri uri = getContentResolver().insert(AlertProvider.CONTENT_URI, values);
            
            
          }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface d, int which) {
            d.dismiss();
          }
        }).create().show();

    return true;
  }
}
