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

public class AccountRepository extends BaseRepository {


    private final SessionFactory sessionFactory;

    public AccountRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<User> save(User account) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(account);
            transaction.commit();
            return Optional.ofNullable(account);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.error("Error occurred while saving account", e);
            return Optional.empty();
        }
    }

    @Override
    public Object update(Object account) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(account);
            transaction.commit();
            return account;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.error("Error occurred while updating account", e);
            return Optional.empty();
        }
    }

    public List<Account> findByUser(Optional <User> user) {
        try (Session session = sessionFactory.openSession()) {
            String queryFindUserAcc = "FROM Account WHERE user = :user";
            return session.createQuery(queryFindUserAcc, Account.class)
                    .setParameter("user", user.get())
                    .list();
        } catch (Exception e) {
            LOGGER.error("Error occurred while find by user account", e);
            return null;
        }
    }

    public Optional<Account> findByUserAndCurrency(Optional<User> user, CurrencyType currency) {
        try (Session session = sessionFactory.openSession()) {
            String queryfindByUserAndCurrency = "FROM Account WHERE user = :user AND currency = :currency";
            Query query = session.createQuery(queryfindByUserAndCurrency, Account.class);
            user.ifPresent(u -> query.setParameter("user", u)); // Устанавливаем параметр user, если он присутствует
            query.setParameter("currency", currency);
            List<Account> result = query.getResultList();
            if (!result.isEmpty()) {
                return Optional.of(result.get(0));
            } else {
                return Optional.empty();
            }
        }
    }
    public Optional<Account> createAccountForUser(User user, CurrencyType currency, double initialBalance) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Account newAccount = new Account();
            newAccount.setUser(user);
            newAccount.setCurrency(currency);
            newAccount.setBalance(initialBalance);

            session.save(newAccount);

            transaction.commit();
            return Optional.of(newAccount);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.error("Error occurred while creating account for user", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> getById(long id) {
        return null;
    }

    @Override
    public List getAll() {
        return null;
    }
}
