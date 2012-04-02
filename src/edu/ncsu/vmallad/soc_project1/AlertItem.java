package edu.ncsu.vmallad.soc_project1;

public class AlertItem {

  String alert;
  int lat; // micro-degrees
  int lng; // micro-degrees

  public AlertItem(String _alert, int _lat, int _lng) {
    alert = _alert;
    lat = _lat;
    lng = _lng;
  }

  public String getAlert() {
    return alert;
  }

  public void setAlert(String alert) {
    this.alert = alert;
  }

  public int getLat() {
    return lat;
  }

  public void setLat(int lat) {
    this.lat = lat;
  }

  public int getLng() {
    return lng;
  }

  public void setLng(int lng) {
    this.lng = lng;
  }

  @Override
  public String toString() {
    return "(" + lat + ", " + lng + "): " + alert;
  }
}
