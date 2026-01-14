package kwee.osmmapper.lib;

import java.awt.Color;

public class MemoContent {
  private String postcode = "";
  private String housenumber = "";
  private String street = "";
  private String city = "";
  private String surname = "";
  private String familyname = "";
  private String phonenumber = "";
  private String mailaddress = "";
  private String projects = "";
  private String country = "";
  private String pictureIdx = "";

  private Color color = null;
  private double longitude = Const.c_LongLatUndefined;
  private double latitude = Const.c_LongLatUndefined;

  public MemoContent() {
  }

  public String getPostcode() {
    return postcode;
  }

  public String getHousenumber() {
    return housenumber;
  }

  public String getStreet() {
    return street;
  }

  public String getCity() {
    return city;
  }

  public String getCountry() {
    return country;
  }

  public String getSurname() {
    return surname;
  }

  public String getFamilyname() {
    return familyname;
  }

  public String getPhonenumber() {
    return phonenumber;
  }

  public String getMailaddress() {
    return mailaddress;
  }

  public String getProjects() {
    return projects;
  }

  public double getLongitude() {
    return longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public Color getColor() {
    return color;
  }

  public String getPicturIdx() {
    if (pictureIdx.isBlank()) {

    }
    return pictureIdx;
  }

  // ======= Setters ===========
  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }

  public void setHousenumber(String housenumber) {
    this.housenumber = housenumber;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public void setFamilyname(String familyname) {
    this.familyname = familyname;
  }

  public void setPhonenumber(String phonenumber) {
    this.phonenumber = phonenumber;
  }

  public void setMailaddress(String mailaddress) {
    this.mailaddress = mailaddress;
  }

  public void setProjects(String projects) {
    this.projects = projects;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public void setClor(Color color) {
    this.color = color;
  }

  public void setColor(String scolor) {
    this.color = ColorConverter.getColor(scolor);
  }

  public void setPictureIdx(String pictureIdx) {
    this.pictureIdx = pictureIdx;
  }

  // =============================

  public Address getAddress() {
    Address laddress = new Address();
    laddress.setStreet(street);
    laddress.setHousenumber(housenumber);
    laddress.setPostalcode(postcode);
    laddress.setCity(city);
    laddress.setCountry(country);
    return laddress;
  }

  public boolean isEmpty() {
    boolean bstat = true;
    bstat = postcode.isBlank();
    bstat = bstat && housenumber.isBlank();
    bstat = bstat && street.isBlank();
    bstat = bstat && city.isBlank();
    bstat = bstat && surname.isBlank();
    bstat = bstat && familyname.isBlank();
    bstat = bstat && phonenumber.isBlank();
    bstat = bstat && mailaddress.isBlank();
    bstat = bstat && projects.isBlank();
    return bstat;
  }
}
