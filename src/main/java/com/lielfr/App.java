package com.lielfr;

import com.lielfr.entities.Customer;
import com.lielfr.entities.CustomerGame;
import com.lielfr.entities.Game;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Hello world!
 */
public class App {
    private static Session session;

    private static SessionFactory getSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Game.class);
        configuration.addAnnotatedClass(Customer.class);
        configuration.addAnnotatedClass(CustomerGame.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        return configuration.buildSessionFactory(serviceRegistry);
    }

    private static void generateData() throws Exception {
        session.beginTransaction();
        Game massEffect1 = new Game("Mass Effect", 50.0);
        Game massEffect2 = new Game("Mass Effect 2", 55.0);
        Game massEffect3 = new Game("Mass Effect 3", 60.0);
        Game starWarsTheOldRepublic = new Game("Star Wars: The Old Republic", 10.0);
        session.save(massEffect1);
        session.save(massEffect2);
        session.save(massEffect3);
        session.save(starWarsTheOldRepublic);

        session.flush();

        Customer yossi = new Customer("Yossi", "Cohen", "yossi@lielfr.com");
        Customer benny = new Customer("Benny", "Levi", "b.levi@lielfr.com");
        Customer liel = new Customer("Liel", "Fridman", "liel@lielfr.com");
        Customer amit = new Customer("Amit", "Fridman", "amit@lielfr.com");
        session.save(yossi);
        session.save(benny);
        session.save(liel);
        session.save(amit);

        session.flush();

        CustomerGame lielME1 = new CustomerGame(liel, massEffect1, Instant.now(), 45.0);
        CustomerGame lielME2 = new CustomerGame(liel, massEffect2, Instant.now(), 50.0);
        CustomerGame lielME3 = new CustomerGame(liel, massEffect3, Instant.now(), 55.0);
        lielME1.setRating(5); // Where it all began
        lielME2.setRating(3); // Kinda boring..
        lielME3.setRating(4); // THAT ENDING :(
        CustomerGame lielSW = new CustomerGame(liel, starWarsTheOldRepublic, Instant.now(), 0.0);

        CustomerGame amitME1 = new CustomerGame(amit, massEffect1, Instant.now(), 50.0);
        CustomerGame amitME2 = new CustomerGame(amit, massEffect2, Instant.now(), 55.0);

        CustomerGame yossiME1 = new CustomerGame(yossi, massEffect1, Instant.now(), 50.0);
        yossiME1.setRating(1);
        CustomerGame yossiSW = new CustomerGame(yossi, starWarsTheOldRepublic, Instant.now(), 20.0);
        yossiSW.setRating(4);

        CustomerGame bennyME1 = new CustomerGame(benny, massEffect1, Instant.now(), 51.0);
        CustomerGame bennyME2 = new CustomerGame(benny, massEffect2, Instant.now(), 56.0);
        bennyME1.setRating(2);
        bennyME2.setRating(3);

        session.save(lielME1);
        session.save(lielME2);
        session.save(lielME3);
        session.save(lielSW);
        session.save(amitME1);
        session.save(amitME2);
        session.save(yossiME1);
        session.save(yossiSW);
        session.save(bennyME1);
        session.save(bennyME2);

        session.getTransaction().commit();

    }

    private static List<Customer> getAllCustomers() throws Exception {
        session.beginTransaction();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Customer> query = builder.createQuery(Customer.class);
        Root<Customer> root = query.from(Customer.class);
        query.select(root);

        List<Order> orderList = new ArrayList<>();
        orderList.add(builder.asc(root.get("firstName")));
        orderList.add(builder.asc(root.get("lastName")));
        query.orderBy(orderList);

        session.getTransaction().commit();
        return session.createQuery(query).getResultList();
    }

    private static List<Game> getAllGames() throws Exception {
        session.beginTransaction();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Game> query = builder.createQuery(Game.class);
        query.from(Game.class);
        List<Game> games = session.createQuery(query).getResultList();
        session.getTransaction().commit();
        return games;
    }

    public static void main(String[] args) {
        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();

            generateData();

            List<Customer> allClients = getAllCustomers();

            AtomicReference<Double> maxTotalSpend = new AtomicReference<>((double) 0);
            AtomicReference<Customer> maxSpender = new AtomicReference<>(null);

            allClients.forEach((customer) -> {
                double totalSpend = customer.getGames().stream().mapToDouble(CustomerGame::getPaidPrice).sum();
                if (maxSpender.get() == null || totalSpend > maxTotalSpend.get()) {
                    maxTotalSpend.set(totalSpend);
                    maxSpender.set(customer);
                }
                System.out.printf("ID: %d, First name: %s, Last name: %s, Email: %s, Total spent: %f\n", customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getEmail(), totalSpend);
            });

            List<Game> allGames = getAllGames();

            allGames.forEach((game) -> {
                double averageRating;
                // Just a convenient way to get the average rating of a game
                averageRating = game
                        .getCustomers()
                        .stream()
                        .filter((item) -> item.getRating() != null)
                        .mapToInt(CustomerGame::getRating)
                        .average().orElse(0);
                System.out.printf("ID: %d, Name: %s, Price: %f, Average rating: %f\n", game.getId(), game.getName(), game.getPrice(), averageRating);
            });

            if (maxSpender.get() != null)
                System.out.printf("Max spender: %s %s\n", maxSpender.get().getFirstName(), maxSpender.get().getLastName());


        } catch (Exception exception) {
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            System.err.println("An error occurred, changes have been rolled back.");
            exception.printStackTrace();
        } finally {
            if (session != null) {
                session.clear();
                session.close();
            }

        }
    }
}
