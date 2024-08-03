import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { EventIdLookupComponent } from '../../SystemConfigurations/ChargesParams/event-id/event-id-lookup/event-id-lookup.component';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { CollateralService } from 'src/app/administration/Service/Collaterals/collateral.service';
import { DocumentsComponent } from './documents/documents.component';
import { UniversalMembershipLookUpComponent } from '../../MembershipComponent/universal-membership-look-up/universal-membership-look-up.component';

@Component({
  selector: 'app-collateral',
  templateUrl: './collateral.component.html',
  styleUrls: ['./collateral.component.scss']
})
export class CollateralComponent implements OnInit {
  collateralTypeArray: any = [
    {
      value: 'SHARES',
      name: 'SHARES'
    },
    {
      value: 'STOCKS',
      name: 'STOCKS'
    },

    {
      value: 'IMMOVABLE',
      name: 'IMMOVABLE'
    },
    {
      value: 'TERM-DEPOSITS',
      name: 'TERM DEPOSITS'
    },
    {
      value: 'VEHICLE-MACHINERIES',
      name: 'VEHICLE & MACHINERIES'
    },
  ];
  submissionFreqArray: any = ['NONE', 'DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY'];
  subscription !: Subscription
  deleting = false;
  isEnabled = true;
  isDisabled = false;
  submitted = false;
  event_id: any;
  params: any;
  eventId: any;
  loading = false;
  LodgingDesc: any;
  withdrawalDesc: any;
  function_type: any;
  description: any;
  collateralType: any;
  collateralCode: any;
  results: any
  error: any;
  lookupdata: any;
  showContractInput = false;
  flagArray: any = [
    'Y', 'N'
  ];
  id: any;
  onShowParticulars = false;
  date: any = new Date();
  with_description: any;
  lodge_collateralCode: any;
  lodge_description: any;
  customerName: any;
  customerCode: any;
  vehicle_and_machineries = false;
  onShowImmovable = false;
  shares = false;
  stocks = false;
  term_deposits = false;
  fmData: any;
  fetchData: any;
  showCollateralCode = true;
  onShowResults = false;
  onShowShares = false;
  onShowStocks = false;
  onShowTermDeposits = false;
  showWarning = true;
  onShowSearchIcon = true;
  customer_lookup: any;
  onShowUpdateButton = false;
  imageSrc: string;
  signatureImage: any;
  onShowAddButton = true;
  imageFile: any;
  documentElement: any;
  arrayIndex: any;
  showTableAction = true;
  showTableOperation = true;
  onShowDocumentsForm = true;
  showButtons = true;
  onShowImageDivider = false;
  onShowUploadedDocuments = false;
  onShowSubmitButton = true;
  onShowModifyButton = false;
  onShowVerifyButton = false;
  onShowDeleteButton = false;
  uploadedDocuments: any;
  documents: any;
  docoumentId: any;
  onViewDocument = false;
  dialogConfig: MatDialogConfig<any>;
  showVehicleAndMachinaries = false;

  constructor(private fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private collateralAPI: CollateralService,
    private notificationAPI: NotificationService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.fetchData = this.router.getCurrentNavigation().extras.queryParams.fetchedData;
    this.function_type = this.fmData.function_type;
    this.collateralCode = this.fmData.collateral_code;
    this.formData.patchValue({
      customerCode: this.customerCode
    })
  }
  collateralDocumentsArray = new Array();
  formData: FormGroup = this.fb.group({
    collateralCode: [''],
    collateralType: ['', Validators.required],
    customerCode: ['', Validators.required],
    customerName: ['', Validators.required],
    description: ['', Validators.required],
    marginPercent: ['', Validators.required],
    marketValue: ['', Validators.required],
    loanValue: ['', Validators.required],
    chargeEventForWithdrawal: [''],
    percentageDrawingPower: ['', Validators.required],
    percentageLoanToTake: [''],
    lastEvaluationDate: [new Date()],
    collateralValue: ['', Validators.required],

    yearofManufacture: [''],
    dateofPurchase: [new Date()],
    registrationNumber: [''],
    chasisNumber: [''],
    engineNo: [''],
    registeredownerName: [''],
    model: [''],
    manufacture: [''],
    machineNo: [''],
    propertyDocumentNo: [''],
    purchaseDate: [new Date()],
    builtArea: [''],
    landArea: [''],
    unitMeasurement: [''],
    propertyAddress: [''],
    leased: [''],
    leasedExpiryDate: [new Date()],
    ageBuilding: [''],
    lodgedDate: [''],
    frequencyforSubmission: [''],
    applypenalInterest: [''],
    reviewDate: [new Date()],
    dueDate: [new Date()],
    withdrawnDate: [new Date()],
    depositAccountNo: [''],
    denominationsNo: [''],
    fullBenefit: [''],
    apportionedValue: [''],
    lienAmount: [''],
    companyDetails: [''],
    sharesCapital: [''],
    nosharesIsssued: [''],
    contactDetails: [''],

    insuranceType: [''],
    policyNo: [''],
    policyAmount: [''],
    insurerDetails: [''],
    risk_cover_start_date: [new Date()],
    risk_cover_end_date: [new Date()],
    last_premium_paid_date: [new Date()],
    premiumAmount: [''],
    frequency: [''],
    itemsInsured: [''],

    name: [''],
    city: [''],
    address: [''],
    state: [''],
    postal_code: [''],
    receipt_type: [''],
    receipt_amount: [''],
    payment_type: [''],
    payment_amount: [''],
    due_date_for_rec: [new Date()],
    paid_received_date: [new Date()],
    date_from: [new Date()],
    to_date: [new Date()],

    proof_verified_date: [new Date()],
    mode_of_pay: [''],
    remarks: [''],
    inspection_type: [''],
    insp_address: [''],
    insp_city: [''],
    insp_state: [''],
    insp_postal_code: [0.00],
    insp_telephone_no: [''],
    due_date_for_visit: [new Date()],
    date_of_visit: [new Date()],
    inspected_value: [''],
    inspection_emp_id: [''],
    insp_remarks: [''],

    brokerName: [''],
    sent_for_sale_on: [new Date()],
    sales_due_date: [new Date()],
    sales_review_date: [new Date()],
    proceeds_received_on: [new Date()],
    expected_min_price: [''],
    sales_proceed_received: [''],
    sales_notes: [''],

    percentage_amount_collected: [''],
    collected_amount: [''],
    documents: new FormArray([])
  });
  documentsForm: FormGroup = this.fb.group({
    id: [''],
    documentTitle: [''],
    document: ['']
  });
  onInitdocumentsForm() {
    this.documentsForm = this.fb.group({
      id: [''],
      documentTitle: [''],
      document: ['']
    });
  }
  get f() {
    return this.formData.controls;
  }

  get d() {
    return this.f.documents as FormArray;
  }
  get dc() {
    return this.documentsForm.controls;
  }
 
  onAddDocuments() {
    if (this.documentsForm.valid) {
      this.d.push(this.fb.group(this.documentsForm.value));
      this.collateralDocumentsArray.push(this.documentsForm.value);
      this.onInitdocumentsForm();
    } else if (!this.documentsForm.valid) {
      if (this.documentsForm.controls.documentTitle.value == "") {
        this.notificationAPI.alertWarning("DOCUMENT TITLE IS EMPTY");
      } else if (this.documentsForm.controls.document.value == "" || this.documentsForm.controls.documentSource.value == "") {
        this.notificationAPI.alertWarning("CHOOSE DOCUMENT");
      } else {
        this.notificationAPI.alertWarning("DOCUMENTS FORM DATA NOT ALLOWED");
      }
    }
  }
  onUpdateDocument() {
    let i = this.documentElement;
    this.collateralDocumentsArray[i] = this.documentsForm.value;
    this.onShowUpdateButton = false;
    this.onShowAddButton = true;
    this.onInitdocumentsForm();
  }
  onEditDocument(i: any) {
    this.documentElement = i;
    this.arrayIndex = this.collateralDocumentsArray[i];
    this.documentsForm = this.fb.group({
      id: [this.collateralDocumentsArray[i].id],
      documentTitle: [this.collateralDocumentsArray[i].documentTitle],
      document: [this.collateralDocumentsArray[i].document]
    });
    this.onShowUpdateButton = true;
    this.onShowAddButton = false;
  }
  onDeleteDoument(i: any) {
    const index: number = this.collateralDocumentsArray.indexOf(this.collateralDocumentsArray.values);
    this.collateralDocumentsArray.splice(i, 1);
    this.d.removeAt(i);
    this.collateralDocumentsArray = this.collateralDocumentsArray;
  }
  documnetsClear() {
    this.collateralDocumentsArray = new Array();
    this.onInitdocumentsForm();
  }

  onViewDoument(i: any) {
    this.documentElement = i;
    this.arrayIndex = this.collateralDocumentsArray[i].id;
    this.dialogConfig = new MatDialogConfig();
    this.dialogConfig.disableClose = true;
    this.dialogConfig.autoFocus = true;
    this.dialogConfig.width = "30%";
    this.dialogConfig.data = this.arrayIndex;
    const dialogRef = this.dialog.open(DocumentsComponent, this.dialogConfig);
    dialogRef.afterClosed().subscribe(
      (_res: any) => {
        this.loading = false;
        // this.getCollateralData(this.fetchData.sn);
      })
  }

  onImageFileChange(event: any) {
    this.loading = true;
    this.imageFile = event.target.files[0];
    if (event.target.files && event.target.files[0]) {
      var reader = new FileReader();
      this.loading = true;
      reader.readAsDataURL(event.target.files[0]);
      reader.onload = () => {
        this.loading = false;
        this.signatureImage = reader.result;
        this.documentsForm.controls.document.setValue(this.signatureImage);
        this.imageSrc = reader.result as string;
      }
      reader.onerror = function (error) {

      };
    }
  }

  onSelectionType(event: any) {
    if (event.target.value == "VEHICLE-MACHINERIES") {
      this.onShowParticulars = true;
      this.onShowShares = false;
      this.onShowStocks = false;
      this.onShowImmovable = false;
      this.onShowTermDeposits = false;
      this.showVehicleAndMachinaries = true;
    } if (event.target.value == "IMMOVABLE") {
      this.onShowParticulars = true;
      this.onShowShares = false;
      this.onShowStocks = false;
      this.onShowImmovable = true;
      this.onShowTermDeposits = false;
      this.showVehicleAndMachinaries = false;
    } if (event.target.value == "SHARES") {
      this.onShowParticulars = true;
      this.onShowShares = true;
      this.onShowStocks = false;
      this.onShowImmovable = false;
      this.onShowTermDeposits = false;
      this.showVehicleAndMachinaries = false;
    } else if (event.target.value == "STOCKS") {
      this.onShowParticulars = true;
      this.onShowStocks = true;
      this.onShowShares = false;
      this.onShowImmovable = false;
      this.onShowTermDeposits = false;
      this.showVehicleAndMachinaries = false;
    } else if (event.target.value == "TERM-DEPOSITS") {
      this.onShowParticulars = true;
      this.onShowStocks = false;
      this.onShowShares = false;
      this.onShowImmovable = false;
      this.onShowTermDeposits = true;
      this.showVehicleAndMachinaries = false;
    }
  }
  ngOnInit(): void {
    this.getPage();
  }
  disableForm() {
    this.formData.disable();
    this.formData.get('companyDetails');
  }
  customerLookup(): void {
    const dialogRef = this.dialog.open(UniversalMembershipLookUpComponent, {
      width: '50%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe(result => {
      this.customer_lookup = result.data;
      this.formData.controls.customerCode.setValue(this.customer_lookup.customerCode);
      this.formData.controls.customerName.setValue(this.customer_lookup.customerName);
    });
  }
  // charge event for withdrawal
  withdrawalLookup() {
    const dialogRef = this.dialog.open(EventIdLookupComponent, {
      width: '50%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe(
      result => {
        this.lookupdata = result.data;
        this.formData.controls.chargeEventForWithdrawal.setValue(this.lookupdata.amt);
      }
    )
  }
  getFunctions() {
    this.loading = false;
    this.isEnabled = false;
    this.onShowAddButton = false;
    this.showButtons = false;
    this.onViewDocument = true;
    this.showWarning = true;
    this.onShowSubmitButton = false;
    this.onShowResults = true;
    this.formData.disable();
    this.documentsForm.disable();
    this.onShowSearchIcon = false;
    this.onShowDocumentsForm = false;
    this.showTableOperation = false;
    this.showTableAction = true;
    this.onShowParticulars = true;
  }
  getCollateralData(sn: any) {
    this.loading = true;
    this.collateralAPI.retrieveCollateral(this.fetchData.sn).subscribe(
      res => {
        this.loading = false
        this.results = res;

        this.collateralDocumentsArray = this.results.entity.documents;

        this.collateralType = this.results.entity.collateralType;

        if (this.collateralType == "VEHICLE-MACHINERIES") {
          this.showVehicleAndMachinaries = true;
        } else if (this.collateralType == "IMMOVABLE") {
          this.onShowImmovable = true;
        } else if (this.collateralType == "SHARES") {
          this.onShowShares = true;
        } else if (this.collateralType == "STOCKS") {
          this.onShowStocks = true;
        } else if (this.collateralType == "TERM-DEPOSITS") {
          this.onShowTermDeposits = true;
        }

        this.formData = this.fb.group({
          sn: [this.results.entity.sn],
          collateralCode: [this.results.entity.collateralCode],
          collateralType: [this.collateralType],
          customerCode: [this.results.entity.customerCode],
          customerName: [this.results.entity.customerName],
          description: [this.results.entity.description],
          loanValue: [this.results.entity.loanValue],
          marginPercent: [this.results.entity.marginPercent],
          marketValue: [this.results.entity.marketValue],
          otherDetails: [this.results.entity.otherDetails],
          chargeEventForLodging: [this.results.entity.chargeEventForLodging],
          chargeEventForWithdrawal: [this.results.entity.chargeEventForWithdrawal],
          percentageDrawingPower: [this.results.entity.percentageDrawingPower],
          percentageLoanToTake: [this.results.entity.percentageLoanToTake],
          lastEvaluationDate: [this.results.entity.lastEvaluationDate],
          //1. Vehicle and Machineries
          yearofManufacture: [this.results.entity.yearofManufacture],
          dateofPurchase: [this.results.entity.dateofPurchase],
          registrationNumber: [this.results.entity.registrationNumber],
          chasisNumber: [this.results.entity.chasisNumber],
          engineNo: [this.results.entity.engineNo],
          registeredownerName: [this.results.entity.registeredownerName],
          model: [this.results.entity.model],
          manufacture: [this.results.entity.manufacture],
          machineNo: [this.results.entity.machineNo],
          //2.Immovable Properties
          propertyDocumentNo: [this.results.entity.propertyDocumentNo],
          purchaseDate: [this.results.entity.purchaseDate],
          builtArea: [this.results.entity.builtArea],
          landArea: [this.results.entity.landArea],
          unitMeasurement: [this.results.entity.unitMeasurement],
          propertyAddress: [this.results.entity.propertyAddress],
          leased: [this.results.entity.leased],
          leasedExpiryDate: [this.results.entity.leasedExpiryDate],
          ageBuilding: [this.results.entity.ageBuilding],
          //3.Stock
          lodgedDate: [this.results.entity.lodgedDate],
          collateralValue: [this.results.entity.collateralValue],
          frequencyforSubmission: [this.results.entity.frequencyforSubmission],
          applypenalInterest: [this.results.entity.applypenalInterest],
          reviewDate: [this.results.entity.reviewDate],
          dueDate: [this.results.entity.duedate],
          withdrawnDate: [this.results.entity.withdrawnDate],
          //4.Term Deposits
          depositAccountNo: [this.results.entity.depositAccountNo],
          denominationsNo: [this.results.entity.denominationsNo],
          fullBenefit: [this.results.entity.fullBenefit],
          apportionedValue: [this.results.entity.apportionedValue],
          lienAmount: [this.results.entity.lienAmount],
          //5.Shares
          companyDetails: [this.results.entity.companyDetails],
          contactDetails: [this.results.entity.contactDetails],
          sharesCapital: [this.results.entity.sharesCapital],
          nosharesIsssued: [this.results.entity.nosharesIsssued],
          //Insurance Details
          insuranceType: [this.results.entity.insuranceType],
          policyNo: [this.results.entity.policyNo],
          policyAmount: [this.results.entity.policyAmount],
          insurerDetails: [this.results.entity.insurerDetails],
          risk_cover_start_date: [this.results.entity.risk_cover_start_date],
          risk_cover_end_date: [this.results.entity.risk_cover_end_date],
          last_premium_paid_date: [this.results.entity.last_premium_paid_date],
          premiumAmount: [this.results.entity.premiumAmount],
          frequency: [this.results.entity.frequency],
          itemsInsured: [this.results.entity.itemsInsured],
          notes: [this.results.entity.notes],
          //receipt and payment
          name: [this.results.entity.name],
          city: [this.results.entity.city],
          address: [this.results.entity.address],
          state: [this.results.entity.state],
          postal_code: [this.results.entity.postal_code],
          receipt_type: [this.results.entity.receipt_type],
          receipt_amount: [this.results.entity.receipt_amount],
          payment_type: [this.results.entity.payment_type],
          payment_amount: [this.results.entity.payment_amount],
          due_date: [this.results.entity.due_date],
          due_date_for_rec: [this.results.entity.due_date_for_rec],
          paid_received_date: [this.results.entity.paid_received_date],
          date_from: [this.results.entity.date_from],
          to_date: [this.results.entity.to_date],
          proof_verified_date: [this.results.entity.proof_verified_date],
          mode_of_pay: [this.results.entity.mode_of_pay],
          remarks: [this.results.entity.remarks],
          //inspection details
          inspection_type: [this.results.entity.inspection_type],
          insp_address: [this.results.entity.insp_address],
          insp_city: [this.results.entity.insp_city],
          insp_state: [this.results.entity.insp_state],
          insp_postal_code: [this.results.entity.insp_postal_code],
          insp_telephone_no: [this.results.entity.insp_telephone_no],
          due_date_for_visit: [this.results.entity.due_date_for_visit],
          date_of_visit: [this.results.entity.date_of_visit],
          inspected_value: [this.results.entity.inspected_value],
          inspection_emp_id: [this.results.entity.inspection_emp_id],
          insp_remarks: [this.results.entity.insp_remarks],
          //sales notes for Tradable Securities
          brokerName: [this.results.entity.brokerName],
          sent_for_sale_on: [this.results.entity.sent_for_sale_on],
          sales_due_date: [this.results.entity.sales_due_date],
          sales_review_date: [this.results.entity.sales_review_date],
          proceeds_received_on: [this.results.entity.proceeds_received_on],
          expected_min_price: [this.results.entity.expected_min_price],
          sales_proceed_received: [this.results.entity.sales_proceed_received],
          sales_notes: [''],
          //fees
          percentage_amount_collected: [this.results.entity.percentage_amount_collected],
          collected_amount: [this.results.entity.collected_amount],
        });

        this.collateralDocumentsArray.forEach((docs => {
          this.documentsForm.patchValue({
            id: docs.id,
            documentTitle: docs.documentTitle,
            document: docs.document,
            documentSource: docs.documentSource,
            uploadTime: docs.uploadTime
          });
          this.d.push(this.fb.group(this.documentsForm.value));
          this.collateralDocumentsArray.push(this.documentsForm.value);
        }));
      }
    );
  }
  getPage() {
    if (this.function_type == "ADD") {
      this.formData = this.fb.group({
        collateralCode: [this.fmData.collateral_code],
        collateralType: ['', Validators.required],
        customerCode: ['', Validators.required],
        customerName: ['', Validators.required],
        description: ['', Validators.required],
        marginPercent: ['', Validators.required],
        marketValue: ['', Validators.required],
        loanValue: ['', Validators.required],
        chargeEventForWithdrawal: [''],
        percentageDrawingPower: ['', Validators.required],
        percentageLoanToTake: [''],
        lastEvaluationDate: [new Date()],
        collateralValue: ['', Validators.required],

        yearofManufacture: [''],
        dateofPurchase: [new Date()],
        registrationNumber: [''],
        chasisNumber: [''],
        engineNo: [''],
        registeredownerName: [''],
        model: [''],
        manufacture: [''],
        machineNo: [''],
        propertyDocumentNo: [''],
        purchaseDate: [new Date()],
        builtArea: [''],
        landArea: [''],
        unitMeasurement: [''],
        propertyAddress: [''],
        leased: [''],
        leasedExpiryDate: [new Date()],
        ageBuilding: [''],
        lodgedDate: [''],
        frequencyforSubmission: [''],
        applypenalInterest: [''],
        reviewDate: [new Date()],
        dueDate: [new Date()],
        withdrawnDate: [new Date()],
        depositAccountNo: [''],
        denominationsNo: [''],
        fullBenefit: [''],
        apportionedValue: [''],
        lienAmount: [''],
        companyDetails: [''],
        sharesCapital: [''],
        nosharesIsssued: [''],
        contactDetails: [''],

        insuranceType: [''],
        policyNo: [''],
        policyAmount: [''],
        insurerDetails: [''],
        risk_cover_start_date: [new Date()],
        risk_cover_end_date: [new Date()],
        last_premium_paid_date: [new Date()],
        premiumAmount: [''],
        frequency: [''],
        itemsInsured: [''],

        name: [''],
        city: [''],
        address: [''],
        state: [''],
        postal_code: [''],
        receipt_type: [''],
        receipt_amount: [''],
        payment_type: [''],
        payment_amount: [''],
        due_date_for_rec: [new Date()],
        paid_received_date: [new Date()],
        date_from: [new Date()],
        to_date: [new Date()],

        proof_verified_date: [new Date()],
        mode_of_pay: [''],
        remarks: [''],
        inspection_type: [''],
        insp_address: [''],
        insp_city: [''],
        insp_state: [''],
        insp_postal_code: [0.00],
        insp_telephone_no: [''],
        due_date_for_visit: [new Date()],
        date_of_visit: [new Date()],
        inspected_value: [''],
        inspection_emp_id: [''],
        insp_remarks: [''],

        brokerName: [''],
        sent_for_sale_on: [new Date()],
        sales_due_date: [new Date()],
        sales_review_date: [new Date()],
        proceeds_received_on: [new Date()],
        expected_min_price: [''],
        sales_proceed_received: [''],
        sales_notes: [''],

        percentage_amount_collected: [''],
        collected_amount: [''],
        documents: new FormArray([])
      });
      this.documentsForm = this.fb.group({
        id: [''],
        documentTitle: [''],
        document: ['']
      });
    }
    else if (this.function_type == "INQUIRE") {
      this.getFunctions();
      this.getCollateralData(this.fetchData.sn);
    }
    else if (this.function_type == "MODIFY") {
      this.loading = false;
      this.onShowResults = true;
      this.onShowAddButton = true;
      this.onShowModifyButton = true;
      this.onShowSubmitButton = false;
      this.getCollateralData(this.fetchData.sn);
    }
    else if (this.function_type == "VERIFY") {
      this.getFunctions();
      this.onShowVerifyButton = true;
      this.getCollateralData(this.fetchData.sn);
    } else if (this.function_type == "DELETE") {
      this.getFunctions();
      this.onShowDeleteButton = true;
      this.getCollateralData(this.fetchData.sn);
    }
  }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    if (this.function_type == "ADD") {
      if (this.formData.valid) {
        this.collateralAPI.create(this.formData.value).subscribe(
          res => {
            if (res.statusCode === 201) {
              this.results = res;
              this.loading = false;
              this.notificationAPI.alertSuccess(this.results.message);
              this.router.navigate([`system/configurations-collateral-maintenance`], { skipLocationChange: true });
            } else {
              this.results = res;
              this.loading = false;
              this.notificationAPI.alertWarning(this.results.message);
              this.router.navigate([`system/configurations-collateral-maintenance`], { skipLocationChange: true });
            }
          }, err => {
            this.loading = false;
            this.error = err;
            this.notificationAPI.alertWarning(this.error);
          }
        )
      } else if (this.formData.invalid) {
        this.loading = false;
        this.notificationAPI.alertWarning("COLLATERAL FORM DATA INVALID");
      }

    } else if (this.function_type == "MODIFY") {
      this.collateralAPI.modify(this.formData.value).subscribe(
        (res) => {
          if (res.statusCode === 200 || res.statusCode === 201) {
            this.results = res;
            this.loading = false;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`system/configurations-collateral-maintenance`], { skipLocationChange: true });
          } else {
            this.results = res;
            this.loading = false;
            this.notificationAPI.alertWarning(this.results.message);
            this.router.navigate([`system/configurations-collateral-maintenance`], { skipLocationChange: true });
          }

        }, (err) => {
          this.loading = false;
          this.error = err;
          this.notificationAPI.alertWarning(this.error);
        }
      )
    }
    else if (this.function_type == "VERIFY") {
      this.collateralAPI.verify(this.fetchData.sn).subscribe(
        res => {
          if (res.statusCode === 200) {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`system/configurations-collateral-maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertWarning(this.results.message);
            this.router.navigate([`system/configurations-collateral-maintenance`], { skipLocationChange: true });
          }
        }, err => {
          this.loading = false;
          this.error = err;
          this.notificationAPI.alertWarning(this.error);
        }
      )

    } else if (this.function_type == "DELETE") {
      this.collateralAPI.delete(this.fetchData.sn).subscribe(
        res => {
          if (res.statusCode === 200) {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`system/configurations-collateral-maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertWarning(this.results.message);
            this.router.navigate([`system/configurations-collateral-maintenance`], { skipLocationChange: true });
          }
        }, err => {
          this.loading = false;
          this.error = err;
          this.notificationAPI.alertWarning(this.error);
        }
      )
    }
  }
}
