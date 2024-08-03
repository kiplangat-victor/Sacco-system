import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subject } from 'rxjs';
import { MatDialogRef } from '@angular/material/dialog';
import { MisSectorService } from '../mis-sector.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';

@Component({
  selector: 'app-mis-sector-lookup',
  templateUrl: './mis-sector-lookup.component.html',
  styleUrls: ['./mis-sector-lookup.component.scss']
})
export class MisSectorLookupComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = [
    'index',
    'misCode',
    'misSector',
    'postedBy',
    'verifiedFlag'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  loading: boolean = false;
  error: any;
  fromDialog: any;
  misData: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private sectorAPI: MisSectorService,
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<MisSectorLookupComponent>
  ) { }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.getData();
  }
  getData() {
    this.loading = true;
    this.sectorAPI.find().pipe(takeUntil(this.destroy$)).subscribe(
      res => {
        if (res.statusCode === 302) {
          this.misData = res;
          this.loading = false;
          this.dataSource = new MatTableDataSource(this.misData.entity);
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
        } else {
          this.loading = false;
        }
    },
      err => {
        this.loading = false;
        this.notificationAPI.alertWarning("SERVER ERROR!!");
    });
  }
  onSelect(data:any){
    this.dialogRef.close({ event: 'close', data:data });
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}
