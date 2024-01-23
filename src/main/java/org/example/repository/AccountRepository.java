package org.example.repository;

import org.example.currency.CurrencyType;
import org.example.entity.Account;
import org.example.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

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
    public Account findById(Long accountId){
        try(Session session = sessionFactory.openSession()){
            return session.get(Account.class, accountId);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public List<Account> findByAll(){
        try(Session session = sessionFactory.openSession()){
            return session.createQuery("FROM Account", Account.class).list();
        }catch (Exception e){
            e.printStackTrace();
            return null;
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
    public List<Account> findByCurrency(CurrencyType currency){
        try(Session session = sessionFactory.openSession()){
            return session.createQuery("FROM Account WHERE currency = :currency", Account.class)
                    .setParameter("currency", currency)
                    .list();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
