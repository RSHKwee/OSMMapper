package kwee.osmmapper.lib;

public class MemoContent {
  String postcode = "";
  String housenumber = "";
  String street = "";
  String city = "";
  String surname = "";
  String familyname = "";
  String phonenumber = "";
  String mailaddress = "";
  String projects = "";
  String country = "";

  double longitude = Const.c_LongLatUndefined;
  double latitude = Const.c_LongLatUndefined;

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
