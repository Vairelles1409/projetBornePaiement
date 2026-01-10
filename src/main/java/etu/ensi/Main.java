package etu.ensi;

import etu.ensi.dao.PlatDAO;
import etu.ensi.dao.PersonneDAO;

import etu.ensi.model.Administrateur;
import etu.ensi.model.Client;
import etu.ensi.model.Cuisinier;
import etu.ensi.model.Plat;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        PersonneDAO personDao = new PersonneDAO();
        PlatDAO platDao = new PlatDAO();


        Administrateur admin = new Administrateur();
        admin.setFirst_name("Admin");
        admin.setLast_name("Frank");
        admin.setEmail("admin@tchop.com");
        admin.setPassword("admin123");

        Cuisinier chef = new Cuisinier();
        chef.setFirst_name("Chef");
        chef.setLast_name("Pium");
        chef.setEmail("chef@tchop.com");
        chef.setPassword("chef123");

        Client client = new Client();
        client.setFirst_name("Kelly");
        client.setLast_name("Alex");
        client.setEmail("Akelly@tchop.com");
        client.setPassword("akelly123");


        personDao.save(admin);
        personDao.save(chef);
        personDao.save(client);

        // 3. Add a few starting Plats (optional but helpful)
        platDao.savePlat(new Plat("Pomme Pilé", 10, 5.0));
        platDao.savePlat(new Plat("Eru Garri blanc", 15, 25.0));

        System.out.println("DATABASE RESET & SEEDED SUCCESSFULLY!");
    }
}