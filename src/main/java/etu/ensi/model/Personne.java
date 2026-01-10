package etu.ensi.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "personnes")
@Inheritance(strategy = InheritanceType.JOINED)
public class Personne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_personne;

    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private int phone_number;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creation_date;

    @Enumerated(EnumType.STRING)
    private Role role;

    //Constructor
    public Personne() {
        this.creation_date = new Date();
    }

    public int getId_personne() { return id_personne; }
    public void setId_personne(int id_personne) { this.id_personne = id_personne; }
    public String getFirst_name() { return first_name; }
    public void setFirst_name(String first_name) { this.first_name = first_name; }
    public String getLast_name() { return last_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getPhone_number() { return phone_number; }
    public void setPhone_number(int phone_number) { this.phone_number = phone_number; }
    public Date getCreation_date() { return creation_date; }
    public void setCreation_date(Date creation_date) { this.creation_date = creation_date; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}