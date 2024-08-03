import { HttpParams } from "@angular/common/http";
import { Component, OnInit, ViewChild } from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { Subscription } from "rxjs";
import { NotificationService } from "src/@core/helpers/NotificationService/notification.service";
import { CollateralService } from "src/app/administration/Service/Collaterals/collateral.service";

@Component({
  selector: 'app-collateral-lookup',
  templateUrl: './collateral-lookup.component.html',
  styleUrls: ['./collateral-lookup.component.scss']
})
export class CollateralLookupComponent implements OnInit {
  displayedColumns: string[] = ['index', 'collateralSerial', 'collateralName', 'collateralType', 'verifiedFlag'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  subscription!: Subscription;
  data: any;
  error: any;
  results: any;
  loading = false;
  params: HttpParams;
  constructor(
    private collateralAPI: CollateralService,
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<CollateralLookupComponent>
  ) { }
  ngOnInit() {
    this.getData();
  }
  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  getData() {
    this.loading = true;
    this.subscription = this.collateralAPI.getCollaterals().subscribe(
      res => {
        this.loading = false;
        this.data = res;
        this.results = this.data.entity;
        this.dataSource = new MatTableDataSource(this.results);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      }, err => {
        this.loading = false;
        this.error = err;
        this.notificationAPI.alertWarning(this.error);
      })
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

