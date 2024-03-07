package org.example.repository;

import jakarta.persistence.Query;
import org.example.currency.CurrencyType;
import org.example.data.entity.ExchangeRate;
import org.example.data.entity.User;
import org.example.exeptions.CustomException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.example.AppLauncher.exchangeRateRepository;

public class ExchangeRateRepository extends BaseRepository {
    private final SessionFactory sessionFactory;

    public ExchangeRateRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void saveAll(List<ExchangeRate> exchangeRates) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            for (ExchangeRate exchangeRate : exchangeRates) {
                session.save(exchangeRate);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.error("Error occurred while save all account", e);
        }
    }

    public List<ExchangeRate> findByDate(LocalDate date) {
        try (Session session = sessionFactory.openSession()) {
            String qeryFindByDate = "FROM ExchangeRate WHERE date = :date";
            return session.createQuery(qeryFindByDate, ExchangeRate.class)
                    .setParameter("date", date)
                    .list();
        } catch (Exception e) {
            LOGGER.error("Error occurred while finding by date account", e);
            return Collections.emptyList();
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
    public double getExchangeRate(CurrencyType sourceCurrency, CurrencyType targetCurrency) {
        ExchangeRate exchangeRate = exchangeRateRepository.findByBaseCurrencyAndTargetCurrency(sourceCurrency, targetCurrency)
                .orElseThrow(() -> new CustomException("Курс обміну не знайдено для вказаних валют"));
        return exchangeRate.getRate();
    }

    @Override
    public Optional<User> getById(long id) {
        return null;
    }

    @Override
    public List getAll() {
        return null;
    }

    @Override
    public Optional<User> save(User user) {
        return null;
    }

    @Override
    public Object update(Object entity) {
        return null;
    }
}
