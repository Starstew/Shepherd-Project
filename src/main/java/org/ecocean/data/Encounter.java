package org.ecocean.data;

import org.ecocean.DataCollectionEvent;

import java.util.*;

/**
 * @author mmcbride
 */
public class Encounter {
  //define the variables for encounter submission
  private Calendar release_date;
  private Double size;
  private Double depth;
  private Double elevation;
  private String measure_units;
  private String location;
  private String sex;
  private String comments;
  private Contact submitter;
  private Contact photographer;
  private String living_status;
  private Calendar date;
  private Double lat;
  private Double longitude;
  private String lat_direction;
  private String long_direction;
  private String scars;
  private Double gps_longitude_minutes;
  private Double gps_longitude_seconds;
  private Double gps_latitude_minutes;
  private Double gps_latitude_seconds;
  private boolean inform_others;
  private String genus;
  private String species;
  private String behavior;
  private String life_stage;
  //TODO: add method to post measurements as well
  //TODO: add method to add tags
  //TODO: add method to attach images

  public static Encounter fromEncounter(org.ecocean.Encounter encIn) {
    Encounter ret = new Encounter();
    if (encIn.getReleaseDate() != null) {
      Calendar c = Calendar.getInstance();
      c.setTime(encIn.getReleaseDate());
      ret.setRelease_date(c);
    }
    ret.setSize(encIn.getSize());
    ret.setDepth(encIn.getDepth());
    // elevation?
    ret.setMeasure_units(encIn.getMeasureUnits());
    ret.setLocation(encIn.getLocation());
    ret.setSex(encIn.getSex());
    ret.setComments(encIn.getComments());
    ret.setSubmitter(new Contact(encIn.getSubmitterName(), encIn.getSubmitterEmail(), encIn.getSubmitterPhone(), encIn.getSubmitterOrganization(), encIn.getSubmitterProject(), encIn.getSubmitterAddress(), encIn.getSubmitterID()));
    ret.setPhotographer(new Contact(encIn.getPhotographerName(), encIn.getPhotographerEmail(), encIn.getPhotographerPhone(), null, null, encIn.getPhotographerAddress(), null));
    ret.setLiving_status(encIn.getLivingStatus());
    // date?
    // gps stuff
    ret.setScars(encIn.getDistinguishingScar());
    // inform others
    ret.setGenus(encIn.getGenus());
    ret.setSpecies(encIn.getHaplotype());
    ret.setBehavior(encIn.getBehavior());
    ret.setLife_stage(encIn.getLifeStage());
    return ret;
  }

  public Calendar getRelease_date() {
    return release_date;
  }

  public void setRelease_date(Calendar release_date) {
    this.release_date = release_date;
  }

  public Double getSize() {
    return size;
  }

  public void setSize(Double size) {
    this.size = size;
  }

  public Double getDepth() {
    return depth;
  }

  public void setDepth(Double depth) {
    this.depth = depth;
  }

  public Double getElevation() {
    return elevation;
  }

  public void setElevation(Double elevation) {
    this.elevation = elevation;
  }

  public String getMeasure_units() {
    return measure_units;
  }

  public void setMeasure_units(String measure_units) {
    this.measure_units = measure_units;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public Contact getSubmitter() {
    return submitter;
  }

  public void setSubmitter(Contact submitter) {
    this.submitter = submitter;
  }

  public Contact getPhotographer() {
    return photographer;
  }

  public void setPhotographer(Contact photographer) {
    this.photographer = photographer;
  }

  public String getLiving_status() {
    return living_status;
  }

  public void setLiving_status(String living_status) {
    this.living_status = living_status;
  }

  public Calendar getDate() {
    return date;
  }

  public void setDate(Calendar date) {
    this.date = date;
  }

  public Double getLat() {
    return lat;
  }

  public void setLat(Double lat) {
    this.lat = lat;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public String getLat_direction() {
    return lat_direction;
  }

  public void setLat_direction(String lat_direction) {
    this.lat_direction = lat_direction;
  }

  public String getLong_direction() {
    return long_direction;
  }

  public void setLong_direction(String long_direction) {
    this.long_direction = long_direction;
  }

  public String getScars() {
    return scars;
  }

  public void setScars(String scars) {
    this.scars = scars;
  }

  public Double getGps_longitude_minutes() {
    return gps_longitude_minutes;
  }

  public void setGps_longitude_minutes(Double gps_longitude_minutes) {
    this.gps_longitude_minutes = gps_longitude_minutes;
  }

  public Double getGps_longitude_seconds() {
    return gps_longitude_seconds;
  }

  public void setGps_longitude_seconds(Double gps_longitude_seconds) {
    this.gps_longitude_seconds = gps_longitude_seconds;
  }

  public Double getGps_latitude_minutes() {
    return gps_latitude_minutes;
  }

  public void setGps_latitude_minutes(Double gps_latitude_minutes) {
    this.gps_latitude_minutes = gps_latitude_minutes;
  }

  public Double getGps_latitude_seconds() {
    return gps_latitude_seconds;
  }

  public void setGps_latitude_seconds(Double gps_latitude_seconds) {
    this.gps_latitude_seconds = gps_latitude_seconds;
  }

  public boolean isInform_others() {
    return inform_others;
  }

  public void setInform_others(boolean inform_others) {
    this.inform_others = inform_others;
  }

  public String getGenus() {
    return genus;
  }

  public void setGenus(String genus) {
    this.genus = genus;
  }

  public String getSpecies() {
    return species;
  }

  public void setSpecies(String species) {
    this.species = species;
  }

  public String getBehavior() {
    return behavior;
  }

  public void setBehavior(String behavior) {
    this.behavior = behavior;
  }

  public String getLife_stage() {
    return life_stage;
  }

  public void setLife_stage(String life_stage) {
    this.life_stage = life_stage;
  }
}
