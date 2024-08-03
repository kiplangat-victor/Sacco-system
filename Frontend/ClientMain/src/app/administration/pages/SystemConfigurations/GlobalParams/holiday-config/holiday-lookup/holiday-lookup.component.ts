import { Component, OnInit, OnDestroy, ViewChild } from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { Subject } from "rxjs";
import { takeUntil } from "rxjs/operators";
import { NotificationService } from "src/@core/helpers/NotificationService/notification.service";
import { HolidayService } from "../holiday.service";

@Component({
  selector: 'app-holiday-lookup',
  templateUrl: './holiday-lookup.component.html',
  styleUrls: ['./holiday-lookup.component.scss']
})
export class HolidayLookupComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['index', 'holidayCode','holidayName', 'holidayDate', 'isActive', 'postedBy', 'verifiedFlag'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  data: any;
  respData: any;
  loading: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private holidayAPI: HolidayService,
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<HolidayLookupComponent>
  ) { }
  ngOnInit() {
    this.getData();
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  getData() {
    this.loading = true;
    this.holidayAPI.find().pipe(takeUntil(this.destroy$)).subscribe(
      (data) => {
        if (data.statusCode === 302) {
          this.respData = data.entity;
          this.loading = false;
          this.dataSource = new MatTableDataSource(this.respData);
          this.dataSource.paginator = this.paginator;
        } else {
          this.loading = false;
        }
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("SERVER ERROR: TRY AGAIN LATER");
      }
    );
  }
  onSelect(data: any) {
    this.dialogRef.close({ event: 'close', data: data });
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

}

