import { Subscription } from 'rxjs';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatRadioChange } from '@angular/material/radio';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsNotificationService } from 'src/@core/helpers/accounts/accounts-notification.service';
import { CountiesService } from 'src/@core/helpers/counties/counties.service';
import { occupations } from 'src/@core/helpers/occupation';
import { MembershipService } from 'src/app/administration/Service/Membership/membership.service';
import { relationType } from '../../../Account-Component/savings-account/relation-types';
import { relationships } from '../../../Account-Component/savings-account/relationship';
import { BranchesLookupComponent } from '../../../SystemConfigurations/GlobalParams/branches/branches-lookup/branches-lookup.component';
import { EmployeeConfigLookupComponent } from '../../../SystemConfigurations/GlobalParams/employee-config/employee-config-lookup/employee-config-lookup.component';
import { MembershipConfigService } from '../../../SystemConfigurations/GlobalParams/membership-config/membership-config.service';
import { SegmentsService } from '../../../SystemConfigurations/GlobalParams/segments/segments.service';
import { MembershipDocumentsComponent } from '../membership-documents/membership-documents.component';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';

@Component({
  selector: 'app-new-membership',
  templateUrl: './new-membership.component.html',
  styleUrls: ['./new-membership.component.scss']
})
export class NewMembershipComponent implements OnInit, OnDestroy {
  accountDocumentsColumns: string[] = [
    "index",
    "documentTitle",
    'view',
    "actions",
  ];
  accountDocumentsDataSource: MatTableDataSource<any>;
  @ViewChild("accountDocumentsPaginator") accountDocumentsPaginator!: MatPaginator;
  @ViewChild(MatSort) accountDocumentsSort!: MatSort;
  accountDocumentsForm!: FormGroup;
  accountDocumentsArray: any[] = [];
  editButton: boolean = false;
  addButton: boolean = true;
  disableAddButton: boolean = false;
  disableActions: boolean = false;
  index: number;
  membershipTypeArray: any;
  employementTypeArray: any = ['EMPLOYED', 'SELF-EMPLOYED'
  ];
  workTypeArray: any = [
    {
      value: 'salaried',
      name: "SALARIED"
    },
    {
      value: 'retired',
      name: "RETIRED"
    },
    {
      value: 'not_working',
      name: "NOT WORKING"
    },
    {
      value: 'salaried',
      name: "SELF EMPLOYED"
    }
  ];
  imageTypeArray: any = [

    'ID', 'SIGNATURE IMAGE', 'PASSPORT-SIZED PHOTO', 'TAX DOCUMENT'
  ]
  maritalStatusArray: any = ['SINGLE', 'MARRIED', 'WIDOWED', 'DIVORCED'];
  residentTypeArray: any = [
    {
      value: 'rented',
      name: 'RENTED'
    },
    {
      value: 'live_with_parents',
      name: 'LIVE WITH PARENTS'
    },
    {
      value: 'own_premises',
      name: 'LIVE IN OWN PREMISES'
    }
  ];
  onShowMaritalStatus = false;
  submittedNk = false;
  submittedDocs = false;
  onShowSelect = true;
  onHideInput = false
  onShowKRAPIN = false;
  fmData: any;
  onShowSubSeegemnt = true;
  displayBackButton = true;
  displayApproavalBtn = false;
  function_type: any;
  membershipCode: any;
  beneficiaryElement: any;
  arrayIndex: any;
  loading = false;
  isSubmitting = false;
  showUniqueId = false;
  onShowResults = false;
  onShowMembeship = true;
  onShowMembeshipCodeType = false;
  onShowWarning = true;
  onShowSearchIcon = true;
  lookupData: any;
  branchCode: any;
  onShowDate = true;
  segmentData: any;
  subSegmentData: any;
  showButtons = true;
  submitted = false;
  onShowBeneficiariesForm = true;
  showTableOperation = true;
  showTableAction = true;
  onShowAddButton = true;
  onShowUpdateButton = false;
  onShowNextOfKinForm = true;
  nextKinElement: any;
  county: any = [];
  subCounty: any = [];
  relationship: any;
  relationtypes: any;
  occupations: any;
  signatureImage: any;
  signatureImageSrc: string;
  currImageSrc: any;
  documentElement: any;
  results: any;
  error: any;
  imageSrc: any;
  imageFile: any;
  imageResults: any;
  onShowMembershipPhotos = true;
  onViewDocument = false;
  onShowUploadedImages = false;
  onShowImagesForm = true;
  onShowNationalID = false;
  onShowBirthCerificate = false;
  onShowBeneficiaryID = false;
  onShowImageDivider = false;
  onShowEmployementDetailsTabs = false;
  onShowNextOfKinsTable = true;
  onShowBeneficiaryBirthCertificate = false;
  data: any;
  onShowIsMinorradiobtn = false;
  dialogConfig: MatDialogConfig<any>;
  uploadedDocuments: any;
  kinsResults: any;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  dbTable = "retailcustomer";
  nextOfKinArray = new Array();
  memberPhotosArray = new Array();
  destroy$: Subject<boolean> = new Subject<boolean>();
  uniqueId: any;
  membershipId: number;
  showMembershipCode: boolean = false;
  submitImages: boolean = false;
  disableViewAction: boolean = true;
  hideRejectBtn: boolean = false;
  btnRejectColor: any;
  rejectBtnText: any;
  rejectionData: any;
  constructor(
    private router: Router,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private segmentService: SegmentsService,
    private countiesService: CountiesService,
    private membershipAPI: MembershipService,
    private notificationAPI: NotificationService,
    private customerConfigAPI: MembershipConfigService,
    private accountsNotification: AccountsNotificationService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.uniqueId = this.fmData.uniqueId;
    this.membershipId = this.fmData.id;
    this.membershipCode = this.fmData.customerCode;
    if (this.fmData.backBtn == 'APPROVAL') {
      this.displayBackButton = false;
      this.displayApproavalBtn = true;
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.getPage();
    this.initialiseAccountDocumentsForm();
    this.getMemberType();
    this.fetchCounties();
    this.getRelationships();
    this.getRelationTypes();
    this.getOccupations();
    this.getSegements();
  }

  formData: FormGroup = this.fb.group({
    id: [''],
    entityId: [''],
    memberType: ['', Validators.required],
    customerCode: [''],
    employerCode: [''],
    employerName: [''],
    branchCode: ['', Validators.required],
    firstName: ['', Validators.required],
    middleName: [''],
    lastName: ['', Validators.required],
    gender: ['', Validators.required],
    maritalStatus: [''],
    citizen: ['', Validators.required],
    isMinor: [],
    birthCertificateNo: [],
    nationalId: [],
    dob: [new Date()],
    kraPin: [],
    uniqueId: [],
    uniqueType: [''],
    segment: [''],
    subSegment: [''],
    monthlyContribution: ['', Validators.required],
    phoneNumber: [''],
    alternatePhone: [''],
    emailAddress: [''],
    postalAddress: [''],
    town: [''],
    residenceType: [''],
    nearestSchool: [''],
    nearestChurch: [''],
    county: [''],
    subCounty: [''],
    location: [''],
    employmentType: [''],
    profession: [''],
    currentOccupation: [''],
    organization: [''],
    workType: [''],
    employeeNo: [''],
    companyAddress: [''],
    workLocation: [''],
    salary: [''],
    nextofkins: new FormArray([]),
    images: [[]]
  });
  nextOfKinForm = this.fb.group({
    id: [''],
    name: [''],
    is_beneficiary: [''],
    is_next_kin: [''],
    relationship: [''],
    phone: [''],
    allocation: ['1'],
    dob: [new Date()],
    occupation: ['']
  });
  onInitNextofKinForm() {
    this.nextOfKinForm = this.fb.group({
      id: [''],
      name: [''],
      is_beneficiary: [''],
      is_next_kin: [''],
      relationship: [''],
      phone: [''],
      allocation: ['1'],
      dob: [new Date()],
      occupation: ['']
    });
  }

  getMemberType() {
    this.loading = true;
    this.customerConfigAPI.findByType(this.dbTable).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        if (res.statusCode === 302) {
          this.loading = false;
          this.membershipTypeArray = res.entity;
          console.log("this.membershipTypeArray:: ", this.membershipTypeArray)
        } else {
          this.loading = false;
        }
      },
      (err) => {
        this.error = err;
        this.loading = false;
        // this.notificationAPI.alertWarning("Server Error: !!");
      }
    );
  }
  getRelationships() {
    this.relationship = relationships;
  }
  getRelationTypes() {
    this.relationtypes = relationType;
  }
  getOccupations() {
    this.occupations = occupations;
  }
  fetchCounties() {
    this.county = this.countiesService.counties();
  }
  onSelectCounty(event: any) {
    this.subCounty = this.countiesService.counties().filter(ev => ev.name == event.target.value)[0].sub_counties;
  }
  getSegements() {
    this.loading = true;
    this.segmentService.getAllSegments().subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 302) {
              this.loading = false;
              this.segmentData = res.entity;
            } else {
              this.loading = false;
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
          }
        ),
        complete: (
          () => {

          }
        )
      }
    )
  }
  onSegmentChange(event: any) {
    this.loading = true;
    this.segmentService.findBySegmentId(event.target.value).subscribe(
      (res) => {
        this.loading = false;
        this.data = res.entity;
        this.subSegmentData = this.segmentData.filter((eValue: { id: any; }) => eValue.id == event.target.value)[0].subSegments;
      }
    )
  }

  get f() {
    return this.formData.controls;
  }
  get n() {
    return this.f.nextofkins as FormArray;
  }
  onAddNextOfKin() {
    this.loading = true;
    this.submittedNk = true;
    if (this.nextOfKinForm.valid) {
      this.loading = false;
      this.n.push(this.fb.group(this.nextOfKinForm.value));
      this.nextOfKinArray.push(this.nextOfKinForm.value);
      this.onInitNextofKinForm();
    } else if (this.nextOfKinForm.invalid) {
      this.loading = false;
      this.notificationAPI.alertWarning("NEXT OF KIN DETAILS CAN NOT BE EMPTY");
    }
  }
  onUpdateNextOfKin() {
    let i = this.nextKinElement;
    this.nextOfKinArray[i] = this.nextOfKinForm.value;
    this.onShowUpdateButton = false;
    this.onShowAddButton = true;
    this.onInitNextofKinForm();
  }
  onEditNextOfKin(i: any) {
    this.nextKinElement = i;
    this.arrayIndex = this.nextOfKinArray[i];
    this.nextOfKinForm = this.fb.group({
      id: [this.nextOfKinArray[i].id],
      name: [this.nextOfKinArray[i].name],
      phone: [this.nextOfKinArray[i].phone],
      is_beneficiary: [this.nextOfKinArray[i].is_beneficiary],
      allocation: [this.nextOfKinArray[i].allocation],
      is_next_kin: [this.nextOfKinArray[i].is_next_kin],
      dob: [this.nextOfKinArray[i].dob],
      occupation: [this.nextOfKinArray[i].occupation],
      relationship: [this.nextOfKinArray[i].relationship],
    });
    this.onShowUpdateButton = true;
    this.onShowAddButton = false;
  }
  onDeleteNextOfKin(i: any) {
    const index: number = this.nextOfKinArray.indexOf(this.nextOfKinArray.values);
    this.nextOfKinArray.splice(i, 1);
    this.n.removeAt(i);
    this.nextOfKinArray = this.nextOfKinArray
  }
  onNextOfKinClear() {
    this.onInitNextofKinForm();
    this.nextOfKinArray = new Array();
  }
  initialiseAccountDocumentsForm() {
    this.initAccountDocumentsForm();
    this.accountDocumentsForm.controls.id.setValidators([]);
    this.accountDocumentsForm.controls.image.setValidators([Validators.required]);
    this.accountDocumentsForm.controls.image_name.setValidators([Validators.required]);
  }
  initAccountDocumentsForm() {
    this.accountDocumentsForm = this.fb.group({
      id: [''],
      image: [''],
      image_name: ['']
    })
  }
  addAccountDocuments() {
    if (this.accountDocumentsForm.valid) {
      this.accountDocumentsArray.push(this.accountDocumentsForm.value);
      this.resetAccountDocumentsForm();
    }
  }
  getAccountDocuments() {
    this.accountDocumentsDataSource = new MatTableDataSource(this.accountDocumentsArray);
    this.accountDocumentsDataSource.paginator = this.accountDocumentsPaginator;
    this.accountDocumentsDataSource.sort = this.accountDocumentsSort;
  }
  resetAccountDocumentsForm() {
    this.formData.patchValue({
      images: this.accountDocumentsArray,
    });
    this.getAccountDocuments();
    this.initAccountDocumentsForm();
    this.accountDocumentsForm.controls.image.setValue("");
    this.imageSrc = "";
  }
  editAccountDocuments(data: any) {
    this.index = this.accountDocumentsArray.indexOf(data);
    this.editButton = true;
    this.addButton = false;
    this.accountDocumentsForm.patchValue({
      id: data.id,
      image: data.image,
      image_name: data.image_name
    });
  }
  updateAccountDocuments() {
    this.editButton = false;
    this.addButton = true;
    this.accountDocumentsArray[this.index] = this.accountDocumentsForm.value;
    this.formData.patchValue({
      images: this.accountDocumentsArray
    });
    this.resetAccountDocumentsForm();
  }
  deleteAccountDocuments(docdata: any) {
    let deleteIndex = this.accountDocumentsArray.indexOf(docdata);
    this.accountDocumentsArray.splice(deleteIndex, 1);
    this.resetAccountDocumentsForm();
  }
  applyAccountDocumentsFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.accountDocumentsDataSource.filter = filterValue.trim().toLowerCase();
    if (this.accountDocumentsDataSource.paginator) {
      this.accountDocumentsDataSource.paginator.firstPage();
    }
  }

  onViewDoument(id: number) {
    this.dialogConfig = new MatDialogConfig();
    this.dialogConfig.disableClose = true;
    this.dialogConfig.autoFocus = true;
    this.dialogConfig.width = "30%";
    this.dialogConfig.data = id;
    const dialogRef = this.dialog.open(MembershipDocumentsComponent, this.dialogConfig);
    dialogRef.afterClosed().subscribe(
      (_res: any) => {
        this.loading = false;
      })
  }

  onImageFileChange(event: any) {
    this.imageFile = event.target.files[0];
    if (event.target.files && event.target.files[0]) {
      var reader = new FileReader();
      reader.readAsDataURL(event.target.files[0]);
      reader.onload = () => {
        this.signatureImage = reader.result;
        this.accountDocumentsForm.controls.image.setValue(this.signatureImage);
        this.imageSrc = reader.result as string;
      }
      reader.onerror = function (error) {
      };
    }
  }
  onSelectAccount(event: any) {
    this.loading = true;
    console.log("event.target.value:: ",  event.target.value)
    this.customerConfigAPI.findByTypeCode(event.target.value).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (res) => {
          console.log(" this.results:: ",  res)
          if (res.statusCode === 302) {
            this.results = res.entity;

            this.loading = false;
            if (this.results.isJunior == 'N') {
              this.onShowNationalID = true;
              this.onShowKRAPIN = true;
              this.onShowMaritalStatus = true;
              this.onShowBirthCerificate = false;
              this.onShowEmployementDetailsTabs = true;
              this.formData.controls.isMinor.setValue("N");
            }
            else if (this.results.isJunior == 'Y') {
              this.onShowNationalID = false;
              this.onShowBirthCerificate = true;
              this.onShowKRAPIN = false;
              this.onShowMaritalStatus = false;
              this.onShowEmployementDetailsTabs = false;
              this.formData.controls.isMinor.setValue("Y");
            }
            else if (this.results.isJunior == null) {
              this.onShowNationalID = false;
              this.onShowBirthCerificate = false;
              this.onShowKRAPIN = false;
              this.onShowMaritalStatus = false;
              this.onShowEmployementDetailsTabs = false;
              this.formData.controls.isMinor.setValue("N");
            }

          } else {
            this.loading = false;
            this.notificationAPI.alertWarning("ERROR IN LOADING REQUEST");
          }
        },
        error: (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("SERVER ERROR: TRY AGAIN LATER");
        },
        complete: () => {

        }
      }
    )
  }
  radioChange(event: MatRadioChange) {
    if (event.value == 'Y') {
      this.onShowNationalID = false;
      this.onShowBirthCerificate = true;
      this.onShowKRAPIN = false;
      this.onShowEmployementDetailsTabs = false;
    } else if (event.value == 'N') {
      this.onShowNationalID = true;
      this.onShowKRAPIN = true;
      this.onShowBirthCerificate = false;
      this.onShowEmployementDetailsTabs = true;
    }
  }
  employerCodeLookup(): void {
    const dialogRef = this.dialog.open(EmployeeConfigLookupComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.formData.controls.employerCode.setValue(this.lookupData.employerCode);
      this.formData.controls.employerName.setValue(this.lookupData.name);
    });
  }

  branchesCodeLookup(): void {
    const dialogRef = this.dialog.open(BranchesLookupComponent, {
      width: '35%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.branchCode = this.lookupData.branchCode;
      this.formData.controls.branchCode.setValue(this.branchCode);
    });
  }
  getMembershipFuncions() {
    this.loading = false;
    this.showUniqueId = true;
    this.disableActions = true;
    this.disableViewAction = false;
    this.showMembershipCode = true;
    this.onShowResults = true;
    this.showButtons = false;
    this.onShowSelect = false;
    this.onShowUploadedImages = true;
    this.onShowImagesForm = false;
    this.onShowBeneficiariesForm = false;
    this.onShowNextOfKinForm = false;
    this.onShowSearchIcon = false;
    this.onShowDate = false;
    this.onViewDocument = true;
    this.showTableOperation = false;;
    this.onShowNextOfKinsTable = true;
    this.showTableAction = true;
    this.onShowEmployementDetailsTabs = true;
    this.onHideInput = true;
    this.onShowIsMinorradiobtn = true;
    this.formData.disable();
  }
  getMembershipRecords() {
    this.loading = true;
    this.membershipAPI.findBYId(this.fmData.id).subscribe(
      (res) => {
        if (res.statusCode === 200) {
          this.loading = false;
          this.results = res.entity;
          this.nextOfKinArray = this.results.nextofkins;
          if (this.results.verifiedFlag == 'Y') {
            this.showMembershipCode = true;
          } else if (this.results.verifiedFlag == 'N') {
            this.showMembershipCode = false;
          }
          if (this.results.verifiedFlag == 'Y') {
            this.hideRejectBtn = false;
          } else if (this.results.verifiedFlag == 'N') {
            this.btnRejectColor = 'accent';
            this.rejectBtnText = 'REJECT';
            this.hideRejectBtn = true;
          }
          this.formData = this.fb.group({
            id: [this.results.id],
            entityId: [this.results.entity],
            memberType: [this.results.memberType],
            customerCode: [this.results.customerCode],
            employerCode: [this.results.employerCode],
            employerName: [this.results.employerCode],
            branchCode: [this.results.branchCode],
            firstName: [this.results.firstName],
            middleName: [this.results.middleName],
            lastName: [this.results.lastName],
            gender: [this.results.gender],
            maritalStatus: [this.results.maritalStatus],
            citizen: [this.results.citizen],
            isMinor: [this.results.isMinor],
            birthCertificateNo: [this.results.birthCertificateNo],
            nationalId: [this.results.nationalId],
            dob: [this.results.dob],
            kraPin: [this.results.kraPin],
            uniqueId: [this.results.uniqueId],
            uniqueType: [this.results.uniqueType],
            segment: [this.results.segment],
            subSegment: [this.results.subSegment],
            monthlyContribution: [this.results.monthlyContribution],
            phoneNumber: [this.results.phoneNumber],
            alternatePhone: [this.results.alternatePhone],
            emailAddress: [this.results.emailAddress],
            postalAddress: [this.results.postalAddress],
            town: [this.results.town],
            residenceType: [this.results.residenceType],
            nearestSchool: [this.results.nearestSchool],
            nearestChurch: [this.results.nearestChurch],
            county: [this.results.county],
            subCounty: [this.results.subCounty],
            location: [this.results.location],
            employmentType: [this.results.employmentType],
            profession: [this.results.profession],
            currentOccupation: [this.results.currentOccupation],
            organization: [this.results.organization],
            workType: [this.results.workType],
            employeeNo: [this.results.employeeNo],
            companyAddress: [this.results.companyAddress],
            workLocation: [this.results.workLocation],
            salary: [this.results.salary],
            postedBy: [this.results.postedBy],
            postedFlag: [this.results.postedFlag],
            postedTime: [this.results.postedTime],
            modifiedBy: [this.results.modifiedBy],
            modifiedFlag: [this.results.modifiedFlag],
            modifiedTime: [this.results.modifiedTime],
            verifiedBy: [this.results.verifiedBy],
            verifiedFlag: [this.results.verifiedFlag],
            verifiedTime: [this.results.verifiedTime],
            deletedBy: [this.results.deletedBy],
            deletedFlag: [this.results.deletedFlag],
            deletedTime: [this.results.deletedTime],
            nextofkins: new FormArray([]),
            images: [[]]
          });
          this.nextOfKinArray.forEach((kins) => {
            this.nextOfKinForm.patchValue({
              id: kins.id,
              name: kins.name,
              phone: kins.phone,
              dob: kins.dob,
              allocation: kins.allocation,
              occupation: kins.occupation,
              relationship: kins.relationship,
              is_beneficiary: kins.is_beneficiary,
              is_next_kin: kins.is_next_kin,
            });
            this.n.push(this.fb.group(this.nextOfKinForm.value));
            this.nextOfKinArray.push(this.nextOfKinForm.value);
          });

          this.results.images.forEach((element: any) => {
            this.accountDocumentsArray.push(element);
          });
          this.formData.patchValue({
            images: this.accountDocumentsArray
          });
          this.getAccountDocuments();

          if (this.results.isJunior == 'N') {
            this.onShowNationalID = true;
            this.onShowKRAPIN = true;
            this.onShowMaritalStatus = true;
            this.onShowBirthCerificate = false;
            this.onShowEmployementDetailsTabs = true;
          }
          else if (this.results.isJunior == 'Y') {
            this.onShowNationalID = false;
            this.onShowBirthCerificate = true;
            this.onShowKRAPIN = false;
            this.onShowMaritalStatus = false;
            this.onShowEmployementDetailsTabs = false;
          }
          else if (this.results.isJunior == null) {
            this.onShowNationalID = false;
            this.onShowBirthCerificate = false;
            this.onShowKRAPIN = false;
            this.onShowMaritalStatus = false;
            this.onShowEmployementDetailsTabs = false;;
          }

        } else {
          this.loading = false;
          this.results = res;
          this.accountsNotification.alertWarning(this.results.message);
          this.router.navigate([`/system/membership/maintenance`], { skipLocationChange: true });
        }
      }, err => {
        this.loading = false;
        this.error = err;
        this.notificationAPI.alertWarning(this.error);
      }
    );
  }
  getPage() {
    if (this.function_type == 'ADD') {
      this.loading = false;
      this.showUniqueId = false;
      this.onShowResults = false;
      this.formData = this.fb.group({
        id: [''],
        entityId: [''],
        memberType: ['', Validators.required],
        customerCode: [''],
        employerCode: [''],
        employerName: [''],
        branchCode: ['', Validators.required],
        firstName: ['', Validators.required],
        middleName: [''],
        lastName: ['', Validators.required],
        gender: ['', Validators.required],
        maritalStatus: [''],
        citizen: ['', Validators.required],
        isMinor: [],
        birthCertificateNo: [],
        nationalId: [],
        dob: [new Date()],
        kraPin: [],
        uniqueId: [],
        uniqueType: [''],
        segment: [''],
        subSegment: [''],
        monthlyContribution: ['', Validators.required],
        phoneNumber: [''],
        alternatePhone: [''],
        emailAddress: [''],
        postalAddress: [''],
        town: [''],
        residenceType: [''],
        nearestSchool: [''],
        nearestChurch: [''],
        county: [''],
        subCounty: [''],
        location: [''],
        employmentType: [''],
        profession: [''],
        currentOccupation: [''],
        organization: [''],
        workType: [''],
        employeeNo: [''],
        companyAddress: [''],
        workLocation: [''],
        salary: [''],
        nextofkins: new FormArray([]),
        images: [[]]
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';

    } else if (this.function_type == 'INQUIRE') {
      this.getMembershipRecords();
      this.getMembershipFuncions();
      this.loading = false;
    }
    else if (this.function_type == 'MODIFY') {
      this.getMembershipRecords();
      this.loading = false;
      this.disableViewAction = false;
      this.onHideInput = true;
      this.onShowSelect = false;
      this.onViewDocument = true;
      this.showUniqueId = true;
      this.showMembershipCode = true;
      this.onShowResults = true;
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
    }
    else if (this.function_type == 'VERIFY') {
      this.getMembershipRecords();
      this.getMembershipFuncions();
      this.loading = false;
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
    }
    else if (this.function_type == 'REJECT') {
      this.getMembershipRecords();
      this.getMembershipFuncions();
      this.loading = false;
      this.btnColor = 'accent';
      this.btnText = 'REJECT';
    }
    else if (this.function_type == 'DELETE') {
      this.getMembershipRecords();
      this.getMembershipFuncions();
      this.loading = false;
      this.btnColor = 'accent';
      this.btnText = 'DELETE';
    }
  }

  onSubmit() {

    console.log("this.formData.value ",this.formData.value)

    this.submitted = true;
    if (this.function_type == "ADD") 
    {
      this.isSubmitting = true;
      if (this.accountDocumentsForm.valid) {
        if (this.formData.valid) {
          this.membershipAPI.create(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
            res => {

              console.log("resresresres:: ", res)
              
              this.isSubmitting = false;
              this.results = res;
              if (res.statusCode === 201) {
                this.accountsNotification.alertSuccess(this.results.message);
                this.router.navigate([`/system/membership/maintenance`], { skipLocationChange: true });
              } else {
                this.loading = false;
                this.isSubmitting = false;
                this.notificationAPI.alertWarning(this.results.message);
              }
            },
            err => {
              this.loading = false;
              this.isSubmitting = false;
              this.notificationAPI.alertWarning("Server Error: !!");
            });
        } else if (this.formData.invalid) {
          this.loading = false;
          this.isSubmitting = false;
          this.notificationAPI.alertWarning("Membership Form data invalid");
        }
      } else {
        this.loading = false;
        this.isSubmitting = false;
        this.notificationAPI.alertWarning("Membership Photos Must Be Added: !!!");
      }
    }
    else if (this.function_type == "VERIFY") {
      this.loading = true;
      console.log(this.results);
      console.log(this.results.id);
      this.membershipAPI.verify(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(
        res => {
          this.loading = false;
          this.results = res;
          console.log(res);
          if (res.statusCode === 200) {
            this.accountsNotification.alertSuccess(this.results.message);
            this.router.navigate([`/system/membership/maintenance`], { skipLocationChange: true });
          } else {
            this.notificationAPI.alertWarning(this.results.message);
          }
        }, err => {
          this.loading = false;
          this.error = err;
          this.notificationAPI.alertWarning(this.error);
        }
      );
    }
    else if (this.function_type == "MODIFY") {
      this.loading = true;
      this.membershipAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
        res => {
          this.loading = false;
          this.results = res;
          if (res.statusCode === 200) {
            this.accountsNotification.alertSuccess(this.results.message);
            this.router.navigate([`/system/membership/maintenance`], { skipLocationChange: true });
          } else {
            this.notificationAPI.alertWarning(this.results.message);
          }
        },
        err => {
          this.loading = false;
          this.error = err
          this.notificationAPI.alertWarning(this.error);
        }
      );
    }
    else if (this.function_type == "REJECT") {
      this.loading = true;
      this.membershipAPI.reject(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(
        res => {
          this.loading = false;
          this.results = res;
          if (res.statusCode === 200) {
            this.accountsNotification.alertSuccess(this.results.message);
            this.router.navigate([`/system/membership/maintenance`], { skipLocationChange: true });
          } else {
            this.notificationAPI.alertWarning(this.results.message);
          }
        },
        err => {
          this.loading = false;
          this.error = err
          this.notificationAPI.alertWarning(this.error);
        }
      )
    }
    else if (this.function_type == "DELETE") {
      this.loading = true;
      this.membershipAPI.delete(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(
        res => {
          this.loading = false;
          this.results = res;
          if (res.statusCode === 200) {
            this.accountsNotification.alertSuccess(this.results.message);
            this.router.navigate([`/system/membership/maintenance`], { skipLocationChange: true });
          } else {
            this.notificationAPI.alertWarning(this.results.message);
          }
        },
        err => {
          this.loading = false;
          this.error = err
          this.notificationAPI.alertWarning(this.error);
        }
      )
    }
  }
  onRejectMember() {
    if (window.confirm("ARE YOU SURE YOU WANT TO REJECT MEMBERSHIP FOR " + this.results.firstName + " " + this.results.middleName + " " + this.results.lastName + "WITH IDENTITY " + this.results.uniqueId)) {
      this.hideRejectBtn = true;
      this.membershipAPI.reject(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200) {
                this.hideRejectBtn = false;
                this.accountsNotification.alertSuccess(res.message);
                if (this.fmData.backBtn == 'APPROVAL') {
                  this.router.navigate([`/system/new/notification/allerts`], { skipLocationChange: true });
                } else {
                  this.router.navigate([`/system/membership/maintenance`], { skipLocationChange: true });
                }
              } else {
                this.hideRejectBtn = false;
                this.notificationAPI.alertWarning(res.message);
              }
            }
          ),
          error: (
            (err) => {
              this.loading = false;
              this.notificationAPI.alertWarning("Service Error: !!!");
            }
          ),
          complete: (
            () => {

            }
          )
        }
      ), Subscription;
    }
  }
}
