package co.ke.emtechhouse.CustomerProducts.AssetsClassification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetsclassificationRepo extends JpaRepository<Assetsclassification, Long> {
}
