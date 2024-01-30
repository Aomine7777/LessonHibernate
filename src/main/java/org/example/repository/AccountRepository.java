package org.example.repository;

import jakarta.persistence.Query;
import org.example.currency.CurrencyType;
import org.example.data.entity.Account;
import org.example.data.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class AccountRepository {
    private final SessionFactory sessionFactory;
    public AccountRepository(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }
    public void save(Account account){
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()){
            transaction = session.beginTransaction();
            session.save(account);
            transaction.commit();
        }catch (Exception e){
            if (transaction !=null){
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void update(Account account) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(account);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    public List<Account> findByUser(User user){
        try( Session session = sessionFactory.openSession()){
            return session.createQuery("FROM Account WHERE user = :user", Account.class)
                    .setParameter("user",user)
                    .list();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public Optional<Account> findByUserAndCurrency(User user, CurrencyType currency) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM Account WHERE user = :user AND currency = :currency";
            Query query = session.createQuery(hql, Account.class);
            query.setParameter("user", user);
            query.setParameter("currency", currency);
            List<Account> result = query.getResultList();
            if (!result.isEmpty()) {
                return Optional.of(result.get(0));
            } else {
                return Optional.empty();
            }
        }
    }
}
