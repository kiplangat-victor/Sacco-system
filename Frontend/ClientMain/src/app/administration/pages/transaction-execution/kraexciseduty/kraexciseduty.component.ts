import { Component, OnInit, OnDestroy, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { Subject } from "rxjs";
import { takeUntil } from "rxjs/operators";
import { NotificationService } from "src/@core/helpers/NotificationService/notification.service";
import { KraexcisedutyService } from "./kraexciseduty.service";



@Component({
  selector: 'app-kraexciseduty',
  templateUrl: './kraexciseduty.component.html',
  styleUrls: ['./kraexciseduty.component.scss']
})
export class KraexcisedutyComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['index', 'transactionID', 'transactionDate', 'chargeAmount', 'status', 'isCleared','postedBy','postedTime','action'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  data: any;
  respData: any;
  loading: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private notificationAPI: NotificationService,
    private kraexcisedutyService: KraexcisedutyService,
    private fb: FormBuilder
  ) { }

  ngOnInit() {
    this.getData();
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  formData = this.fb.group({
    from_date:[''],
    to_date:['']
  })
  getData() {
    this.loading = true;
    this.kraexcisedutyService.find().pipe(takeUntil(this.destroy$)).subscribe(
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
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

}
