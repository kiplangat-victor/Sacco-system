package co.ke.emtechhouse.InterestMaintenance;

import co.ke.emtechhouse.AmountSlabs.AmountSlab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Long> {
    Optional<Interest> findByDeletedFlagAndInterestCode(Character deletedFlag, String interestCode);
    List<Interest> findByDeletedFlag(Character deletedFlag);
    @Query(nativeQuery = true, value = "SELECT * from amount_slab where interest_code=:interestCode and version=" +
            "(select max(version) from amount_slab where interest_code=:interestCode) limit 1")
    AmountSlab mostRecentVersion(@Param("interestCode") String interestCode);

    @Query(nativeQuery = true, value = "select * from interest where deleted_flag = 'Y'")
    List<Interest> findalldeletedInterests();

    @Query(nativeQuery = true, value = "select * from interest where deleted_flag = 'N'")
    List<Interest> findallundeletedInterests();
}
