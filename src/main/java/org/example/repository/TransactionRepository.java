package org.example.repository;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class TransactionRepository {
    private final SessionFactory sessionFactory;

    public TransactionRepository(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }
    public  void save(Transaction transaction){
        Transaction transaction1 = null;
        try(Session session = sessionFactory.openSession()){
            transaction1 = session.beginTransaction();
            session.save(transaction1);
            transaction1.commit();
        }catch (Exception e){
            if(transaction1 != null){
                transaction1.rollback();
            }
            e.printStackTrace();
        }
    }
    public Transaction findById(Long transactionId){
        try(Session session = sessionFactory.openSession()){
            return  session.get(Transaction.class, transactionId);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public List<Transaction> findAll(){
        try(Session session = sessionFactory.openSession()){
            return session.createQuery("FROM Transaction", Transaction.class).list();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
