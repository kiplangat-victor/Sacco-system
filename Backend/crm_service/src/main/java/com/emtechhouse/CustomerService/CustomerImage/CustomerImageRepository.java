package com.emtechhouse.CustomerService.CustomerImage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerImageRepository extends JpaRepository<CustomerImage, Long> {


    @Query(nativeQuery = true, value = "SELECT customer_image.* from customer_image join retailcustomer on customer_image.retailcustomer_fk = retailcustomer.id where retailcustomer.id =:id  and retailcustomer.deleted_flag='N';")
    List<CustomerImage> getImages(Long id);

    @Query(nativeQuery = true, value = "SELECT customer_image.* from customer_image join retailcustomer on customer_image.retailcustomer_fk = retailcustomer.id where retailcustomer.customer_code =:customer_code  and retailcustomer.deleted_flag='N';")
    List<CustomerImage> findImages(String customer_code);


}
