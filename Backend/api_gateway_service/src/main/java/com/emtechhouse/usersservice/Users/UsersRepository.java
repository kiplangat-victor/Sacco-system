package com.emtechhouse.usersservice.Users;

import com.emtechhouse.usersservice.utils.AccountStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@RequestMapping
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmailAndDeletedFlag(String email, Character deletedFlag);

    Boolean existsByEntityIdAndPhoneNoAndDeletedFlag(String entityId,String phone, Character deletedFlag);

    Optional<Users> findByEntityIdAndMemberCodeAndDeletedFlag(String entityId,String memberCode,Character deletedFlag);

    List<Users> findAllByDeletedFlagAndEntityId(Character deletedFlag, String entityId);

    @Query(value = "SELECT * FROM users WHERE is_entity_user=:isEntityUser and deleted_flag=:deletedFlag ", nativeQuery = true)
    List<Users> findAllEntityUsersByDeletedFlag(Character deletedFlag, String isEntityUser);

    List<Users> findAllByDeletedFlagAndOnBoardingMethod(Character deletedFlag, String onBoardingMethod);

//    Optional<Users> findByEntityIdAndEmailAndDeletedFlag(String entityId,String email,Character deletedFlag);
    Optional<Users> findByEmailAndDeletedFlag(String email,Character deletedFlag);

    List<Users> findByEntityIdAndIsTellerAndDeletedFlag(String entityId,String isTeller, Character flag);

    @Query(value = "SELECT * FROM users WHERE entity_id=:entityId AND workclass_fk IS NULL", nativeQuery = true)
    List<Users> allWithoutWorkclass(String entityId);

    @Query(value = "SELECT * FROM users WHERE entity_id=:entityId AND sn NOT IN (SELECT user_id FROM user_roles)", nativeQuery = true)
    List<Users> allWithoutRoles(String entityId);
    @Query(value = "SELECT rc.first_name as firstName, rc.entity_id as entityId, rc.last_name as lastName, rc.phone_number as phoneNumber, rc.email_address as emailAddress FROM retailcustomer rc join accounts ac on ac.customer_code=rc.customer_code where ac.acid=:acid and rc.entity_id=:entityId", nativeQuery = true)
    getMemberDetails getMemberDetails(String entityId, String acid);

    public interface getMemberDetails {
        String getPhoneNumber();
        String getFirstName();
        String getEmailAddress();
        String getLastName();
        String getAcid();
        String getEntityId();

    }

    @Query(value = "\n" +
            "\n" +
            "SELECT k.*, rel_amount AS relative_amount FROM(\n" +
            "SELECT gl.classification AS cls, prd.product_code, prd.product_code_desc, ac.account_name, ac.acid, ptdtd.dtd_sn, ptdtd.sn, ptdtd.transaction_code, ptdtd.tran_date, ptdtd.tran_particulars, ptdtd.c_amount, ptdtd.d_amount, ptdtd.rel_amount, ptdtd.tran_amount FROM (\n" +
            "\n" +
            "select distinct pt.acid, pt.sn, dtd.sn as dtd_sn, dtd.transaction_code, \n" +
            "pt.tran_date, tran_particulars,\n" +
            "CAST((case when pt.part_tran_type='Credit' then tran_amount else 0.00 end) AS DECIMAL(65, 2)) as c_amount,\n" +
            "CAST((case when pt.part_tran_type='Debit' then tran_amount else 0.00 end) AS DECIMAL(65, 2)) as d_amount,\n" +
            "CAST((case when pt.part_tran_type='Debit' then tran_amount*-1 else tran_amount end) AS DECIMAL(65, 2)) as rel_amount,\n" +
            "pt.tran_amount  \n" +
            "from part_tran pt join dtd on pt.transaction_header_id = dtd.sn and dtd.posted_flag = 'Y'  and acid =  :acid   \n" +
            "order by pt.tran_date desc limit :maxCount\n" +
            "\n" +
            ") AS ptdtd\n" +
            "\n" +
            "JOIN accounts AS ac ON ac.acid = ptdtd.acid and ac.acid = :acid    \n" +
            "left join (SELECT product_code, product_code_desc FROM product) AS prd On ac.product_code = prd.product_code\n" +
            "join glsubhead ON glsubhead.gl_subhead_code = ac.gl_subhead\n" +
            "join gl ON gl.gl_code = glsubhead.gl_code\n" +
            ") AS k\n" +
            "order by  tran_date asc, dtd_sn ASC, tran_amount DESC \n" +
            "\n", nativeQuery = true)
    List<AccountStatement> getAccountStatement(@Param("acid") String acid, @Param("maxCount") Integer maxCount);
}
