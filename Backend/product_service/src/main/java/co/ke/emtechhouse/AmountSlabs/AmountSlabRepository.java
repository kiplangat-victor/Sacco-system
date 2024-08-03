package co.ke.emtechhouse.AmountSlabs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmountSlabRepository extends JpaRepository<AmountSlab, Long> {
    List<AmountSlab> findByDeletedFlagAndInterestCode(Character deletedFlag, String interestCode);

    @Query(nativeQuery = true, value = "select * from amount_slab where deleted_flag = 'Y'")
    List<AmountSlab> findalldeletedAmountSlabs();

    @Query(nativeQuery = true, value = "select * from amount_slab where deleted_flag = 'N'")
    List<AmountSlab> findallundeletedAmountSlabs();
}
