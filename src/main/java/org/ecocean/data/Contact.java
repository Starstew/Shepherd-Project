package org.ecocean.data;

/**
 * @author mmcbride
 */
public class Contact {

  public Contact() {

  }

  public Contact(String name, String email, String phone, String organization, String project, String address, String id) {
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.organization = organization;
    this.project = project;
    this.address = address;
    this.id = id;
  }
  public String name;
  public String email;
  public String phone;
  public String organization;
  public String project;
  public String address;
  public String id;
}
