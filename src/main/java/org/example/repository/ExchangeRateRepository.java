package org.example.repository;

import jakarta.persistence.Query;
import org.example.currency.CurrencyType;
import org.example.data.entity.ExchangeRate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ExchangeRateRepository {
    private final SessionFactory sessionFactory;

    public ExchangeRateRepository(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }
    public void saveAll(List<ExchangeRate> exchangeRates){
        exchangeRates.forEach(exchangeRate -> {
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
        });

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
    public Optional<ExchangeRate> findByBaseCurrencyAndTargetCurrency(CurrencyType baseCurrency, CurrencyType targetCurrency) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM ExchangeRate WHERE baseCurrency= :baseCurrency AND targetCurrency= :targetCurrency";
            Query query = session.createQuery(hql, ExchangeRate.class);
            query.setParameter("baseCurrency", baseCurrency);
            query.setParameter("targetCurrency", targetCurrency);
            List<ExchangeRate> result = query.getResultList();
            if (!result.isEmpty()) {
                return Optional.of(result.get(0));
            } else {
                return Optional.empty();
            }
        }
    }
}
