package co.ke.emtechhouse.CustomerProducts.AutoAddedAccounts;

import co.ke.emtechhouse.CustomerProducts.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoAddedAccountRepo extends JpaRepository<AutoAddedAccount, Long> {
    List<AutoAddedAccount> findByProduct(Product product);
}
