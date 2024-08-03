import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsNotificationService } from 'src/@core/helpers/accounts/accounts-notification.service';
import { BanksService } from 'src/@core/helpers/banks/banks.service';
import { CountriesService } from 'src/@core/helpers/countries/countries.service';
import { occupations } from 'src/@core/helpers/occupation';
import { GroupMembershipService } from 'src/app/administration/Service/GroupMembership/group-membership.service';
import { BranchesLookupComponent } from '../../../SystemConfigurations/GlobalParams/branches/branches-lookup/branches-lookup.component';
import { EmployeeConfigLookupComponent } from '../../../SystemConfigurations/GlobalParams/employee-config/employee-config-lookup/employee-config-lookup.component';
import { MembershipConfigService } from '../../../SystemConfigurations/GlobalParams/membership-config/membership-config.service';
import { MembershipLookUpComponent } from '../../Membership/membership-look-up/membership-look-up.component';
import { GroupMembershipDocumentsComponent } from '../group-membership-documents/group-membership-documents.component';

@Component({
  selector: 'app-new-group-membership',
  templateUrl: './new-group-membership.component.html',
  styleUrls: ['./new-group-membership.component.scss']
})
export class NewGroupMembershipComponent implements OnInit, OnDestroy {
  accountDocumentsColumns: string[] = [
    "index",
    "documentTitle",
    'view',
    "actions",
  ];
  signatoriesColumns: string[] = [
    "index",
    "memberCode",
    'memberName',
    'signOperation',
    'nationality',
    'verificationDocument',
    'verificationDocumentNo',
    'expriyDate',
    'designation',
    'phoneNumber',
    'mustSign',
    "actions",
  ];
  groupmemberColumns: string[] = [
    "index",
    "customerCode",
    'customerName',
    'serialNo',
    'contact',
    'loanBalance',
    'totalSaving',
    "actions",
  ];
  signatoriesDataSource: MatTableDataSource<any>;
  @ViewChild("signatoriesPaginator") signatoriesPaginator!: MatPaginator;
  @ViewChild(MatSort) signatoriesSort!: MatSort;
  groupmemberDataSource: MatTableDataSource<any>;

  @ViewChild("groupmemberPaginator") groupmemberPaginator!: MatPaginator;
  @ViewChild(MatSort) groupmemberSort!: MatSort;

  accountDocumentsDataSource: MatTableDataSource<any>;
  @ViewChild("accountDocumentsPaginator") accountDocumentsPaginator!: MatPaginator;
  @ViewChild(MatSort) accountDocumentsSort!: MatSort;

  accountDocumentsForm!: FormGroup;
  accountDocumentsArray: any[] = [];

  signatoriesForm!: FormGroup;
  signatoriesArray: any[] = [];

  groupmemberForm!: FormGroup;
  groupmemberArray: any[] = [];

  editButton: boolean = false;
  addButton: boolean = true;
  disableAddButton: boolean = false;
  disableActions: boolean = false;
  index: number;
  membershipTypeArray: any;
  imageTypeArray: any = [
    'PASSPORT', 'SIGNATURE'
  ];
  showgroupmemberForm: boolean = true;
  operationsArray: any = ['ANY TO SIGN', 'ALL TO SIGN', 'EITHER TO SIGN', 'INDIVIDUAL TO SIGN'];
  documentTypeArray: any = ['NATIONAL ID', 'PASSPORT', 'MILITARY ID', 'DIPLOMATIC ID', 'ALIEN ID', 'DRIVERS LICENCE', 'BIRTH CERTICATE', 'BUSINESS NUMBER'];
  groupStatusArray: any = ['DRAFT', 'APPROVED', 'PENDING', 'DEACTIVATED'];
  businessTypeArray: any = ['PARTNERSHIP', 'INFORMAL BODY', 'REGISTERED GROUP', 'SOLE PROPRIETORSHIP', 'LIMITED LIABILITY COMPANY'];
  fmData: any;
  function_type: any;
  showTableAction = true;
  membershipDocumnets: any;
  groupMemberList: any;
  onShowSelect = true;
  loading = false;
  onShowWarning = true;
  submitted = false;
  onShowSearchIcon = true;
  documentElement: any;
  arrayIndex: any;
  signatoriesList: any;
  imageFile: any;
  signatureImage: any;
  imageSrc: string;
  onViewDocument = false;
  onShowImageDivider = false;
  onShowUploadedImages = false;
  showButtons = true;
  onShowImagesForm = true;
  onShowMembershipPhotos = true;
  onShowSignatoriesForm = true;
  country: any;
  onShowDate = true;
  occupations: any;
  customer_lookup: any;
  lookupdata: any;
  branchCode: any;
  onShowGroupStatus = false;
  onShowStatusSelectValue = false;
  onShowStatusValue = true;
  results: any;
  error: any;
  banks: any;
  branch: any;
  isHidden = false;
  onShowCode = false;
  onShowResults = false;
  imageResults: any;
  submittedSing = false;
  onShowAllgroupmembersForm = true;
  submitting = false;
  dbTable = "group_member";
  destroy$: Subject<boolean> = new Subject<boolean>()
  dialogConfig: MatDialogConfig<any>;
  customerId: any;
  customerName: any;
  displayBackButton = true;
  displayApproavalBtn = false;

  hideBtn = false;
  btnColor: any;
  btnText: any;
  disableViewAction: boolean = true;
  hideRejectBtn: boolean = false;
  btnRejectColor: any;
  rejectBtnText: any;
  rejectionData: any;
  currentUser: any;
  constructor(
    private router: Router,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private banksAPI: BanksService,
    private countriesAPI: CountriesService,
    private notificationAPI: NotificationService,
    private memberConfigAPI: MembershipConfigService,
    private groupMembershipAPI: GroupMembershipService,
    private accountsNotification: AccountsNotificationService
  ) {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.customerId = this.fmData.id;
    this.customerName = this.fmData.customerName;
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
    this.initialisesignatoriesForm();
    this.initialisegoupmemberForm();
    this.initialiseAccountDocumentsForm();
    this.getCountries();
    this.getOccupations();
    this.getBanks();
    this.getPage();
    this.getMemberType();
  }

  formData: FormGroup = this.fb.group({
    id: [''],
    entityId: [''],
    no: ['AUTO'],
    code: [''],
    branchCode: ['', Validators.required],
    groupType: ['', Validators.required],
    groupName: ['', Validators.required],
    membershipDate: [new Date()],
    country: [''],
    verificationDocument: ['', Validators.required],
    verificationDocNumber: ['', Validators.required],
    status: ['DRAFT'],
    jointAccount: [''],
    hasApproval: [''],
    groupManagerId: ['', Validators.required],
    groupManagerName: [''],
    businessType: ['', Validators.required],
    otherBusinessType: [''],
    registrationDate: [new Date()],
    businessRegNo: ['', Validators.required],
    businessNature: ['', Validators.required],
    postCode: [''],
    town: [''],
    countryCode: [''],
    county: [''],
    subCounty: [''],
    ward: [''],
    postalAddress: [''],
    homeAddress: [''],
    currentAddress: [''],
    primaryPhone: [''],
    otherPhone: [''],
    groupMail: [''],
    representative: ['', Validators.required],
    bankName: [''],
    bankBranch: [''],
    bankAccountNo: [''],
    uniqueId: [],
    uniqueType: [''],
    images: [[]],
    groupMemberSignatories: [[]],
    allGroupMembers: [[]]
  });

  getMemberType() {
    this.loading = true;
    this.memberConfigAPI.findByType(this.dbTable).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        if (res.statusCode === 302) {
          this.loading = false;
          this.membershipTypeArray = res.entity;
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
  getCountries() {
    this.country = this.countriesAPI.countries();
  }
  getBanks() {
    this.banks = this.banksAPI.banks();
  }
  onSelectedBank(event: any) {
    this.branch = this.banksAPI.banks().filter((e: { name: any; }) => e.name == event.target.value)[0].branches;
  }

  getOccupations() {
    this.occupations = occupations;
  }
  get f() {
    return this.formData.controls;
  }

  initialisesignatoriesForm() {
    this.initsignatoriesForm();
    this.signatoriesForm.controls.id.setValidators([]);
    this.signatoriesForm.controls.memberCode.setValidators([Validators.required]);
    this.signatoriesForm.controls.memberName.setValidators([Validators.required]);
    this.signatoriesForm.controls.signOperation.setValidators([]);
    this.signatoriesForm.controls.nationality.setValidators([Validators.required]);
    this.signatoriesForm.controls.verificationDocument.setValidators([Validators.required]);
    this.signatoriesForm.controls.verificationDocumentNo.setValidators([]);
    this.signatoriesForm.controls.expriyDate.setValidators([Validators.required]);
    this.signatoriesForm.controls.designation.setValidators([Validators.required]);
    this.signatoriesForm.controls.phoneNumber.setValidators([Validators.required]);
    this.signatoriesForm.controls.mustSign.setValidators([Validators.required]);
  }
  initsignatoriesForm() {
    this.signatoriesForm = this.fb.group({
      id: [''],
      memberCode: [''],
      memberName: [''],
      signOperation: [''],
      nationality: [''],
      verificationDocument: [''],
      verificationDocumentNo: [''],
      expriyDate: [new Date()],
      designation: [''],
      phoneNumber: [''],
      mustSign: [''],
    })
  }
  addSignatory() {
    if (this.signatoriesForm.valid) {
      this.signatoriesArray.push(this.signatoriesForm.value);
      this.resetSignatoriesForm();
    }
  }
  getSignatories() {
    this.signatoriesDataSource = new MatTableDataSource(this.signatoriesArray);
    this.signatoriesDataSource.paginator = this.signatoriesPaginator;
    this.signatoriesDataSource.sort = this.signatoriesSort;
  }
  resetSignatoriesForm() {
    this.formData.patchValue({
      groupMemberSignatories: this.signatoriesArray
    });

    this.getSignatories();
    this.initsignatoriesForm();
  }
  editSignatories(data: any) {
    this.index = this.signatoriesArray.indexOf(data);
    this.editButton = true;
    this.addButton = false;
    this.signatoriesForm.patchValue({
      id: data.id,
      memberCode: data.memberCode,
      memberName: data.memberName,
      signOperation: data.signOperation,
      nationality: data.nationality,
      verificationDocument: data.verificationDocument,
      verificationDocumentNo: data.verificationDocumentNo,
      expriyDate: data.expriyDate,
      designation: data.designation,
      phoneNumber: data.phoneNumber,
      mustSign: data.mustSign,
    });
  }
  updateSignatory() {
    this.editButton = false;
    this.addButton = true;
    this.signatoriesArray[this.index] = this.signatoriesForm.value;
    this.formData.patchValue({
      groupMemberSignatories: this.signatoriesArray
    });
    this.resetSignatoriesForm();
  }
  deleteSignatory(del: any) {
    let deleteIndex = this.signatoriesArray.indexOf(del);
    this.signatoriesArray.splice(deleteIndex, 1);
    this.resetSignatoriesForm();
  }
  applySignatoryFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.signatoriesDataSource.filter = filterValue.trim().toLowerCase();
    if (this.signatoriesDataSource.paginator) {
      this.signatoriesDataSource.paginator.firstPage();
    }
  }
  initialisegoupmemberForm() {
    this.initsigroupmemberForm();
    this.groupmemberForm.controls.customerCode.setValidators([Validators.required]);
    this.groupmemberForm.controls.customerName.setValidators([Validators.required]);
    this.groupmemberForm.controls.contact.setValidators([Validators.required]);
    this.groupmemberForm.controls.id.setValidators([]);
    this.groupmemberForm.controls.loanBalance.setValidators([Validators.required]);
    this.groupmemberForm.controls.serialNo.setValidators([Validators.required]);
    this.groupmemberForm.controls.totalSaving.setValidators([Validators.required]);
  }
  initsigroupmemberForm() {
    this.groupmemberForm = this.fb.group({
      customerCode: [''],
      customerName: [''],
      contact: [''],
      id: [''],
      loanBalance: [''],
      serialNo: [''],
      totalSaving: ['']
    })
  }
  addgroupmember() {
    if (this.groupmemberForm.valid) {
      this.groupmemberArray.push(this.groupmemberForm.value);
      this.resetgroupmemberForm();
    }
  }
  getgroupmembers() {
    this.groupmemberDataSource = new MatTableDataSource(this.groupmemberArray);
    this.groupmemberDataSource.paginator = this.groupmemberPaginator;
    this.groupmemberDataSource.sort = this.groupmemberSort;
  }
  resetgroupmemberForm() {
    this.formData.patchValue({
      allGroupMembers: this.groupmemberArray
    });
    this.getgroupmembers();
    this.initAccountDocumentsForm();
  }
  editgroupmember(data: any) {
    this.index = this.groupmemberArray.indexOf(data);
    this.editButton = true;
    this.addButton = false;
    this.groupmemberForm.patchValue({
      customerCode: data.customerCode,
      customerName: data.customerName,
      contact: data.contact,
      id: data.id,
      loanBalance: data.loanBalance,
      serialNo: data.serialNo,
      totalSaving: data.totalSaving
    });
  }
  updategroupmember() {
    this.editButton = false;
    this.addButton = true;
    this.groupmemberArray[this.index] = this.groupmemberForm.value;
    this.formData.patchValue({
      allGroupMembers: this.groupmemberArray
    });
    this.resetgroupmemberForm();
  }
  deletegroupmember(deldata: any) {
    let deleteIndex = this.groupmemberArray.indexOf(deldata);
    this.groupmemberArray.splice(deleteIndex, 1);
    this.resetgroupmemberForm();
  }
  applygroupmemberFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.groupmemberDataSource.filter = filterValue.trim().toLowerCase();
    if (this.groupmemberDataSource.paginator) {
      this.groupmemberDataSource.paginator.firstPage();
    }
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
  onViewDoument(id: number) {
    this.dialogConfig = new MatDialogConfig();
    this.dialogConfig.disableClose = true;
    this.dialogConfig.autoFocus = true;
    this.dialogConfig.width = "30%";
    this.dialogConfig.data = id;
    const dialogRef = this.dialog.open(GroupMembershipDocumentsComponent, this.dialogConfig);
    dialogRef.afterClosed().subscribe(
      (_res: any) => {
        this.loading = false;
      })
  }
  employerCodeLookup(): void {
    const dialogRef = this.dialog.open(EmployeeConfigLookupComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.lookupdata = result.data;
      this.formData.controls.employerCode.setValue(this.lookupdata.employerCode);
      this.formData.controls.employerName.setValue(this.lookupdata.name);
    });
  }
  branchesCodeLookup(): void {
    const dialogRef = this.dialog.open(BranchesLookupComponent, {
      width: '35%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.lookupdata = result.data;
      this.branchCode = this.lookupdata.branchCode;
      this.formData.controls.branchCode.setValue(this.branchCode);
    });
  }

  representativeLookup(): void {
    const dialogRef = this.dialog.open(MembershipLookUpComponent, {
      width: '50%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(results => {
      this.customer_lookup = results.data;
      this.formData.controls.representative.setValue(`${this.customer_lookup.customerCode}- ${this.customer_lookup.customerName}`);
    })
  }
  signatoryCodeLookup(): void {
    const dialogRef = this.dialog.open(MembershipLookUpComponent, {
      width: '50%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(results => {
      this.customer_lookup = results.data;
      this.signatoriesForm.controls.memberCode.setValue(this.customer_lookup.customerCode);
      this.signatoriesForm.controls.memberName.setValue(this.customer_lookup.customerName);
    })
  }

  groupMemberCodeLookup(): void {
    const dialogRef = this.dialog.open(MembershipLookUpComponent, {
      width: '60%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.customer_lookup = result.data;
      this.groupmemberForm.controls.customerName.setValue(this.customer_lookup.customerName);
      this.groupmemberForm.controls.customerCode.setValue(this.customer_lookup.customerCode)
    });
  }
  getGroupFunctions() {
    this.loading = false;
    this.onShowCode = true;
    this.onShowResults = true;
    this.disableActions = true;
    this.disableViewAction = false;
    this.showgroupmemberForm = false;
    this.onShowImagesForm = false;
    this.onShowSearchIcon = false;
    this.onShowImageDivider = true;
    this.onShowGroupStatus = false;
    this.onShowDate = false;
    this.showButtons = false;
    this.isHidden = true;
    this.onShowAllgroupmembersForm = false;
    this.onShowSelect = false;
    this.onViewDocument = true;
    this.onShowSignatoriesForm = false;
    this.onShowImageDivider = true;
    this.onShowUploadedImages = true;
    this.onShowMembershipPhotos = true;
    this.formData.disable();
    this.accountDocumentsForm.disable();
    this.signatoriesForm.disable();
  }
  getGroupMembershipResults() {
    this.loading = true;
    this.groupMembershipAPI.findById(this.fmData.id).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        if (res.statusCode === 200) {
          this.loading = false;
          this.results = res.entity;
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
            no: [this.results.no],
            code: [this.results.code],
            branchCode: [this.results.branchCode],
            customerCode: [this.results.customerCode],
            groupType: [this.results.groupType],
            groupName: [this.results.groupName],
            membershipDate: [this.results.membershipDate],
            country: [this.results.country],
            verificationDocument:  [this.results.verificationDocument? this.results.verificationDocument: this.results.uniqueType],
            verificationDocNumber: [this.results.verificationDocNumber? this.results.verificationDocNumber: this.results.uniqueId],
            status: [this.results.status],
            jointAccount: [this.results.jointAccount],
            hasApproval: [this.results.hasApproval],
            groupManagerId: [this.results.groupManagerId],
            groupManagerName: [this.results.groupManagerName],
            businessType: [this.results.businessType],
            otherBusinessType: [this.results.otherBusinessType],
            registrationDate: [this.results.registrationDate],
            businessRegNo: [this.results.businessRegNo],
            businessNature: [this.results.businessNature],
            postCode: [this.results.postCode],
            town: [this.results.town],
            countryCode: [this.results.countryCode],
            county: [this.results.country],
            subCounty: [this.results.subCounty],
            ward: [this.results.ward],
            postalAddress: [this.results.postalAddress],
            homeAddress: [this.results.homeAddress],
            currentAddress: [this.results.currentAddress],
            primaryPhone: [this.results.primaryPhone],
            otherPhone: [this.results.otherPhone],
            groupMail: [this.results.groupMail],
            representative: [this.results.representative],
            bankName: [this.results.bankName],
            bankBranch: [this.results.bankBranch],
            bankAccountNo: [this.results.bankAccountNo],
            uniqueId: [this.results.uniqueId],
            uniqueType: [this.results.uniqueType],
            images: [[]],
            groupMemberSignatories: [[]],
            allGroupMembers: [[]]
          });
          this.results.groupMemberSignatories.forEach((element: any) => {
            this.signatoriesArray.push(element);
          });
          this.formData.patchValue({
            groupMemberSignatories: this.signatoriesArray
          });
          this.getSignatories();
          this.results.allGroupMembers.forEach((element: any) => {
            this.groupmemberArray.push(element);
          });
          this.formData.patchValue({
            allGroupMembers: this.groupmemberArray
          });
          this.getgroupmembers();
          this.results.images.forEach((element: any) => {
            this.accountDocumentsArray.push(element);
          });
          this.formData.patchValue({
            images: this.accountDocumentsArray
          });
          this.getAccountDocuments();

        } else {
          this.results = res;
          this.accountsNotification.alertWarning(this.results.message);
          this.router.navigate([`/system/group-membership/maintenance`], { skipLocationChange: true });
        }
      }, (err) => {
        this.loading = false;
        this.error = err;
        this.notificationAPI.alertWarning(this.error);
      }
    )
  }
  getPage() {
    this.loading = true;
    if (this.fmData.function_type == "ADD") {
      this.loading = false;
      this.formData = this.fb.group({
        id: [''],
        entityId: [''],
        no: ['AUTO'],
        code: [''],
        branchCode: ['', Validators.required],
        groupType: ['', Validators.required],
        groupName: ['', Validators.required],
        membershipDate: [new Date()],
        country: [''],
        verificationDocument: ['', Validators.required],
        verificationDocNumber: ['', Validators.required],
        status: ['DRAFT'],
        jointAccount: [''],
        hasApproval: [''],
        groupManagerId: [this.currentUser.username, Validators.required],
        groupManagerName: [this.currentUser.firstName + " " + this.currentUser.lastName],
        businessType: ['', Validators.required],
        otherBusinessType: [''],
        registrationDate: [new Date()],
        businessRegNo: ['', Validators.required],
        businessNature: ['', Validators.required],
        postCode: [''],
        town: [''],
        countryCode: [''],
        county: [''],
        subCounty: [''],
        ward: [''],
        postalAddress: [''],
        homeAddress: [''],
        currentAddress: [''],
        primaryPhone: [''],
        otherPhone: [''],
        groupMail: [''],
        representative: ['', Validators.required],
        bankName: [''],
        bankBranch: [''],
        bankAccountNo: [''],
        uniqueId: [],
        uniqueType: [''],
        images: [[]],
        groupMemberSignatories: [[]],
        allGroupMembers: [[]]
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    } else if (this.fmData.function_type == "INQUIRE") {
      this.getGroupFunctions();
      this.getGroupMembershipResults();
    }
    else if (this.fmData.function_type == "VERIFY") {
      this.getGroupFunctions();
      this.getGroupMembershipResults();
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
    }
    else if (this.function_type == 'REJECT') {
      this.getGroupFunctions();
      this.getGroupMembershipResults();
      this.loading = false;
      this.btnColor = 'accent';
      this.btnText = 'REJECT';
    }
    else if (this.fmData.function_type == "DELETE") {
      this.getGroupFunctions();
      this.getGroupMembershipResults();
      this.btnColor = 'accent';
      this.btnText = 'DELETE';
    }
    else if (this.fmData.function_type == "MODIFY") {
      this.onShowCode = true;
      this.onShowResults = true;
      this.onShowSelect = false;
      this.disableViewAction = false;
      this.getGroupMembershipResults();
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
    }
  }

  onSubmit() {
    this.submitted = true;
    if (this.function_type == "ADD") {
      this.submitting = true;
      if (this.formData.valid) {
        console.log("form data", this.formData.value);
        this.groupMembershipAPI.add(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 201) {
                  this.accountsNotification.alertSuccess(res.message);
                  this.router.navigate([`/system/group-membership/maintenance`], { skipLocationChange: true });
                } else {
                  this.loading = false;
                  this.notificationAPI.alertWarning(res.message);
                }
              }
            ),
            error: (
              (err) => {
                this.loading = false;
                this.notificationAPI.alertWarning("Server Error: !!");
              }
            ),
            complete: (
              () => {

              }
            )
          }
        );
      } else if (this.formData.invalid) {
        this.submitting = false;
        this.notificationAPI.alertWarning("Group membership form data invalid");
      }
    }
    else if (this.function_type == "VERIFY") {
      this.loading = true;
      this.groupMembershipAPI.verify(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(
        res => {
          if (res.statusCode === 200) {
            this.loading = false;
            this.notificationAPI.alertSuccess(res.message);
            this.router.navigate([`/system/group-membership/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning(res.message);
          }
        },
        err => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
          this.router.navigate([`/system/group-membership/maintenance`], { skipLocationChange: true });
        });
    }
    else if (this.function_type == "REJECT") {
      this.loading = true;
      this.groupMembershipAPI.reject(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(
        res => {
          if (res.statusCode === 200) {
            this.loading = false;
            this.notificationAPI.alertSuccess(res.message);
            this.router.navigate([`/system/group-membership/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning(res.message);
          }
        },
        err => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
          this.router.navigate([`/system/group-membership/maintenance`], { skipLocationChange: true });
        });
    }
    else if (this.function_type == "DELETE") {
      this.loading = true;
      this.groupMembershipAPI.delete(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(
        res => {
          if (res)
            if (res.statusCode === 200) {
              this.loading = false;
              this.results = res;
              this.notificationAPI.alertSuccess(this.results.message);
              this.router.navigate([`/system/group-membership/maintenance`], { skipLocationChange: true });
            } else {
              this.loading = false;
              this.results = res;
              this.notificationAPI.alertWarning(this.results.message);
            }
        },
        err => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
          this.router.navigate([`/system/group-membership/maintenance`], { skipLocationChange: true });

        });
    }
    else if (this.function_type == "MODIFY") {
      this.loading = true;
      this.groupMembershipAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200) {
                this.loading = false;
                this.notificationAPI.alertSuccess(res.message);
                this.router.navigate([`/system/group-membership/maintenance`], { skipLocationChange: true });
              } else {
                this.loading = false;
                this.notificationAPI.alertWarning(res.message);
              }
            }
          ),
          error: (
            (err) => {
              this.loading = false;
              this.notificationAPI.alertWarning("Server Error: !!");
            }
          ),
          complete: (
            () => {

            }
          )
        }
      );

    }
  }
  onRejectMember() {
    if (window.confirm("ARE YOU SURE YOU WANT TO REJECT MEMBERSHIP FOR " + this.results.groupName  + " WITH IDENTITY " + this.results.uniqueId)) {
      this.hideRejectBtn = true;
      this.groupMembershipAPI.reject(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(
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
