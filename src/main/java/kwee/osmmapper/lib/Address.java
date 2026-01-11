package kwee.osmmapper.lib;

public class Address {
  private String street = "";
  private String housenumber = "";
  private String postalcode = "";
  private String city = "";
  private String country = "";

  public String getPostalcode() {
    return postalcode;
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

  // === Setters =======
  public void setPostalcode(String postalcode) {
    this.postalcode = postalcode;
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

  public void setCountry(String Country) {
    this.country = Country;
  }

  /*
   * toString
   * 
   * return String ; delimited street; number; postalcode; city; country
   */
  public String toString() {
    String str = this.street;
    str = str + "; " + this.housenumber;
    str = str + "; " + this.postalcode;
    str = str + "; " + this.city;
    str = str + "; " + this.country;
    return str;
  }
}
