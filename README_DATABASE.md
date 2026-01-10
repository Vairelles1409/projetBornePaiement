//Base de Données (Hibernate)

Ce projet utilise Hibernate (ORM) pour gérer la persistance des données. Ce choix nous permet de manipuler des objets Java plutôt que d'écrire du SQL manuellement, garantissant une meilleure cohérence avec notre diagramme de classes UML.
Installation et Configuration 

//Pour faire fonctionner la base de données sur votre machine, suivez ces étapes :
1.	Lancer le serveur MySQL : Ouvrez XAMPP et lancez le service MySQL.
2.	Créer la base de données : Allez sur phpMyAdmin (ou DBeaver) et créez une base de données vide nommée afoum_tchop .
3.	Vérifier la configuration : Ouvrez le fichier src/main/resources/hibernate.cfg.xml.
//Si vous avez un mot de passe MySQL, modifiez la ligne :
<property name="connection.password">VOTRE_MDP</property>
//Sinon, laissez-le vide.
4.	Synchroniser Maven : Cliquez sur l'icône Reload dans l'onglet Maven d'IntelliJ pour télécharger les dépendances (Hibernate & MySQL Connector).
//Architecture du Projet
etu.ensi.model : Contient toutes les entités (classes Java) mappées avec la base de données.
//Note sur l'héritage : Nous utilisons la stratégie JOINED. Les données communes sont dans personnes, et les données spécifiques dans administrateurs, cuisiniers ou clients.
etu.ensi.dao : Contient les Data Access Objects. C'est ici que se trouve toute la logique de sauvegarde et de récupération des données.(pour javaFX Frank)
etu.ensi.util : Contient HibernateUtil.java qui gère la connexion.

//Comment utiliser la base de données dans votre code ?
Vous ne devez pas manipuler les sessions Hibernate directement. Utilisez les classes du package dao.

//Exemple :
//Récupérer le menu (Plats)
PlatDAO platDao = new PlatDAO();
List<Plat> menu = platDao.getAllPlats();

//Gérer une connexion (Login)
PersonneDAO personDao = new PersonneDAO();
Personne user = personDao.login("email@test.com", "password123");
if (user != null) {
    System.out.println("Bienvenue " + user.getFirst_name());}
//Enregistrer une commande
CommandeDAO commandeDao = new CommandeDAO();
Commande nouvelleCommande = new Commande();
// ... ajouter les plats ...
commandeDao.saveCommande(nouvelleCommande); // Sauvegarde auto de la commande ET des lignes grâce au Cascade.

//NB 
•	Modification des classes : Si vous modifiez un attribut dans une classe du package model, Hibernate mettra à jour la table automatiquement au prochain lancement (grâce à hbm2ddl.auto = update).

•	Reset de la base : Pour vider la base et repartir de zéro (pour une démo par exemple), passez temporairement la propriété hbm2ddl.auto à create dans le hibernate.cfg.xml, lancez le Main, puis remettez-la sur update.


