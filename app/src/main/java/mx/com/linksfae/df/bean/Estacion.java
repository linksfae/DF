package mx.com.linksfae.df.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

/**
 * Created by FA005216 on 10/02/2015.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Estacion implements Serializable, Comparable<Estacion>{

    private int id;
    private String district;
    private Double lon;
    private Double lat;
    private int bikes;
    private int slots;
    private String zip;
    private String address;
    private String addressNumber;
    private String nearbyStations;
    private String status;
    private String name;

    private Double distancia;
    private Marker marker;



    /*private String stationType;

    public String getStationType() {
        return stationType;
    }

    public void setStationType(String stationType) {
        this.stationType = stationType;
    }*/

    @Override
    public int compareTo(Estacion another) {
        return distancia.compareTo(another.distancia);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public int getBikes() {
        return bikes;
    }

    public void setBikes(int bikes) {
        this.bikes = bikes;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressNumber() {
        return addressNumber;
    }

    public void setAddressNumber(String addressNumber) {
        this.addressNumber = addressNumber;
    }

    public String getNearbyStations() {
        return nearbyStations;
    }

    public void setNearbyStations(String nearbyStations) {
        this.nearbyStations = nearbyStations;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getDistancia() {
        return distancia;
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
