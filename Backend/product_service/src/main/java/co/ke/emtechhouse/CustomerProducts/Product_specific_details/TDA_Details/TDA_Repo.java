package co.ke.emtechhouse.CustomerProducts.Product_specific_details.TDA_Details;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TDA_Repo extends JpaRepository<TDA_Details, Long> {
}
