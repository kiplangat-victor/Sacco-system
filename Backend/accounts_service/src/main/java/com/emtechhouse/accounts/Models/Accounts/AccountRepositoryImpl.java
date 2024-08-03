package com.emtechhouse.accounts.Models.Accounts;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class AccountRepositoryImpl implements AccountCustomRepo{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Account customFindMethod(Long id) {
        return (Account) entityManager.createQuery("FROM Account a WHERE a.id = :id")
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public List<Account> findUsingNativeQuery(String query) {
        return (List<Account>) entityManager.createQuery(query)
                .getResultList();
    }

//    public List<Account> lookup() {
//        return (List<Account>) entityManager.createQuery("FROM Account a WHERE acid='400008' JOIN retailcustomer r ON a.customer_code=r.customer_code")
//                .getSingleResult();
//    }
}
