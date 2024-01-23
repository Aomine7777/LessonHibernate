package org.example.repository;

import org.example.entity.ExchangeRate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;

public class ExchangeRateRepository {
    private final SessionFactory sessionFactory;

    public ExchangeRateRepository(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }
    public void save(ExchangeRate exchangeRate){
        Transaction transaction2 = null;
        try(Session session = sessionFactory.openSession()){
            transaction2 = session.beginTransaction();
            session.save(exchangeRate);
            transaction2.commit();
        }catch (Exception e){
            if (transaction2 != null){
                transaction2.rollback();
            }
            e.printStackTrace();
        }
    }
    public ExchangeRate findById(Long exchangedRateId){
        try(Session session = sessionFactory.openSession()){
            return session.get(ExchangeRate.class, exchangedRateId);
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public List<ExchangeRate> findAll(){
        try(Session session = sessionFactory.openSession()){
            return session.createQuery("FROM ExchangeRate", ExchangeRate.class).list();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public List<ExchangeRate> findByDate(LocalDate date){
        try(Session session = sessionFactory.openSession()){
            return  session.createQuery("FROM ExchangeRate WHERE date = :date", ExchangeRate.class)
                    .setParameter("date", date)
                    .list();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
