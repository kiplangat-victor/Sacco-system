import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { GroupMembershipService } from 'src/app/administration/Service/GroupMembership/group-membership.service';
import { MembershipConfigService } from '../../../SystemConfigurations/GlobalParams/membership-config/membership-config.service';

@Component({
  selector: 'app-group-membership-details',
  templateUrl: './group-membership-details.component.html',
  styleUrls: ['./group-membership-details.component.scss']
})
export class GroupMembershipDetailsComponent implements OnInit, OnDestroy {
  results: any;
  loading: boolean = false;membershipTypeArray: any;
;
  destroy$: Subject<boolean> = new Subject<boolean>();
  memberPhotosArray = new Array();
  groupSignatoryArray = new Array();
  allgroupmembersArray = new Array();
  onShowWarning: boolean = false;
  submitted: boolean = false;
  dbTable = "group_member";
  constructor(
    private fb: FormBuilder,
    private notificationAPI: NotificationService,
    private groupMembershipAPI: GroupMembershipService,
    private memberConfigAPI: MembershipConfigService,
    @Inject(MAT_DIALOG_DATA) public customer_account_id: number,
    public dialogRef: MatDialogRef<GroupMembershipDetailsComponent>
  ) {
    console.log("Customer ID", customer_account_id);
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.getMembershipRecords();
    this.getMemberType();
  }
  formData: FormGroup = this.fb.group({
    id: [''],
    entityId: [''],
    no: ['AUTO'],
    code: [''],
    employerCode: [''],
    employerName: [''],
    branchCode: [''],
    groupType: [''],
    groupName: [''],
    membershipDate: [new Date()],
    country: [''],
    verificationDocument: [''],
    verificationDocNumber: [''],
    status: [''],
    jointAccount: [''],
    hasApproval: [''],
    groupManagerId: [''],
    groupManagerName: [''],
    businessType: [''],
    otherBusinessType: [''],
    registrationDate: [new Date()],
    businessRegNo: [''],
    businessNature: [''],
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
    representative: [''],
    bankName: [''],
    bankBranch: [''],
    bankAccountNo: [''],
    signatories: new FormArray([]),
    images: new FormArray([]),
    allgroupmembers: new FormArray([])
  });
  get f() {
    return this.formData.controls;
  }
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
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      }
    );
  }
  getMembershipRecords() {
    this.loading = true;
    this.groupMembershipAPI.findById(this.customer_account_id).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.results = res.entity;
              console.log("RES", this.results);
              this.memberPhotosArray = this.results.images;
              this.groupSignatoryArray = this.results.signatories;
              this.allgroupmembersArray = this.results.allgroupmembers;
              this.formData = this.fb.group({
                id: [this.results.id],
                entityId: [this.results.entity],
                no: [this.results.no],
                code: [this.results.code],
                employerCode: [this.results.employerCode],
                employerName: [this.results.employerName],
                branchCode: [this.results.branchCode],
                groupType: [this.results.groupType],
                groupName: [this.results.groupName],
                membershipDate: [this.results.membershipDate],
                country: [this.results.country],
                verificationDocument: [this.results.verificationDocument],
                verificationDocNumber: [this.results.verificationDocument],
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
                signatories: new FormArray([]),
                images: new FormArray([]),
                allgroupmembers: new FormArray([])
              });
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning("Group Membership Data Not Available !!");
              this.dialogRef.close();
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
            this.dialogRef.close();
          }
        ),
        complete: (
          () => {

          }
        )
      }
    )
  }
}
