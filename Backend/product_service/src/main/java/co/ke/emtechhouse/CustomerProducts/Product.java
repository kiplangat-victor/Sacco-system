package co.ke.emtechhouse.CustomerProducts;

import co.ke.emtechhouse.CustomerProducts.AssetsClassification.Assetsclassification;
import co.ke.emtechhouse.CustomerProducts.AutoAddedAccounts.AutoAddedAccount;
import co.ke.emtechhouse.CustomerProducts.Product_exception.Product_exception;
import co.ke.emtechhouse.CustomerProducts.Product_fee.Product_fee;
import co.ke.emtechhouse.CustomerProducts.Product_specific_details.CAA_Details.CAA_Details;
import co.ke.emtechhouse.CustomerProducts.Product_specific_details.LAA_Details.LAA_Details;
import co.ke.emtechhouse.CustomerProducts.Product_specific_details.ODA_Details.ODA_Details;
import co.ke.emtechhouse.CustomerProducts.Product_specific_details.SBA_Details.SBA_Details;
import co.ke.emtechhouse.CustomerProducts.Product_specific_details.TDA_Details.TDA_Details;
import co.ke.emtechhouse.CustomerProducts.RecurringFee.ProductRecurringFee;
import co.ke.emtechhouse.CustomerProducts.product_gls.Productgls;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@EqualsAndHashCode(exclude = {"tdaDetails", "autoAddedAccounts"})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 30, nullable = false)
    private String entityId;
    @Column(length = 30, nullable = false, updatable = false, unique = true)
    private String productCode;
    @Column(length = 30, nullable = false, updatable = false)
    private String productType; //LAA,CAA,SBA,ODA
    //    private String scheme_type;
    @Column(length = 100, nullable = false)
    private String productCodeDesc;
    //  General Details
    private LocalDate effective_from_date;
    private LocalDate effective_to_date;
    @Column(length = 150)
    private String principal_lossline_ac;
    @Column(length = 150)
    private String recovery_lossline_ac;
    @Column(length = 150)
    private String charge_off_ac;
    @Column(length = 150)
    private String acid_generation;
    @Column(length = 150)
    private String increment;
    private String acid_structure = "PRODUCT-RUNNO";
    private Integer running_no_size = 6;
    private String taxCollectionAccount;

    // Interest Details
    @Column(nullable = false)
    private String interest_table_code;
    @Column(length = 150)
    private String pl_ac_ccy;
    @Column(nullable = false, length = 150)
    private String pl_ac;
    @Column(nullable = false, length = 150)
    private String int_receivable_ac;
    @Column(nullable = false, length = 150)
    private String princ_receivable_ac;
    @Column(nullable = false, length = 150)
    private String penal_int_receivable_ac;
    @Column(nullable = false, length = 150)
    private String penal_receivable_ac;
    @Column(nullable = false, length = 150)
    private String advance_int_ac;
    @Column(length = 150)
    private String dr_int_compounding_freq;
    @Column(length = 150)
    private String int_cal_freq_dr_week;
    @Column(length = 150)
    private String app_discounted_int_rate;
    @Column(length = 150)
    private String int_cal_freq_dr_day;
    @Column(length = 150)
    private String int_cal_freq_dr_date;
    @Column(length = 150)
    private String int_cal_freq_dr_holiday;

    @OneToMany(targetEntity = ProductRecurringFee.class, cascade = CascadeType.ALL, mappedBy = "product")
    private List<ProductRecurringFee> productRecurringFees;

    @OneToOne(targetEntity = LAA_Details.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_fk1", referencedColumnName = "id")
    private LAA_Details laaDetails;
    @OneToOne(targetEntity = CAA_Details.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_fk2", referencedColumnName = "id")
    private CAA_Details caaDetails;
    @OneToOne(targetEntity = SBA_Details.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_fk3", referencedColumnName = "id")
    private SBA_Details sbaDetails;
    @OneToOne(targetEntity = ODA_Details.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_fk4", referencedColumnName = "id")
    private ODA_Details odaDetails;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private TDA_Details  tdaDetails;

    public void  setTdaDetails(TDA_Details tdaDetails) {
        tdaDetails.setProduct(this);
        this.tdaDetails = tdaDetails;
    }

    //   Assets Classicification
    @OneToMany(targetEntity = Assetsclassification.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "product", referencedColumnName = "id")
    private List<Assetsclassification> assetsClassification;

    //   Loan Product Fee
    @OneToMany(targetEntity = Product_fee.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "product", referencedColumnName = "id")
    private List<Product_fee> fees;

    //   GL Subheads
    @OneToMany(targetEntity = Productgls.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "product", referencedColumnName = "id")
    private List<Productgls> glsubheads;
    // Exceptions
    @OneToMany(targetEntity = Product_exception.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "product", referencedColumnName = "id")
    private List<Product_exception> exceptions;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<AutoAddedAccount> autoAddedAccounts;

    public void setAutoAddedAccounts(List<AutoAddedAccount> autoAddedAccounts) {
        autoAddedAccounts.forEach(autoAddedAccount -> {
            autoAddedAccount.setProduct(this);
        });
        this.autoAddedAccounts=autoAddedAccounts;
    }

    //*****************Operational Audit *********************
    @Column(length = 150, nullable = false)
    private String postedBy;
    @Column(nullable = false)
    private Character postedFlag = 'Y';
    @Column(nullable = false)
    private Date postedTime;
    private String modifiedBy;
    private Character modifiedFlag = 'N';
    private Date modifiedTime;
    private String verifiedBy;
    private Character verifiedFlag = 'N';
    private Date verifiedTime;
    private String deletedBy;
    private Character deletedFlag = 'N';
    private Date deletedTime;
}