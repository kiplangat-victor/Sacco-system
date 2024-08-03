import { HttpParams } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-classification-lookup',
  templateUrl: './classification-lookup.component.html',
  styleUrls: ['./classification-lookup.component.scss']
})
export class ClassificationLookupComponent implements OnInit {
  displayedColumns: string[] = ['index', 'classification'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  loading = false;
  params: HttpParams;
  results: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  productType: any;
  productCode: any;
  data = [
    "PERFORMING", "WATCH", "SUB_STANDARD", "DOUBTFUL", "LOSS"
  ]
  constructor(
    public dialogRef: MatDialogRef<ClassificationLookupComponent>
  ) { }

  ngOnInit() {
    this.dataSource = new MatTableDataSource(this.data);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  onSelect(data: any) {
    this.dialogRef.close({ event: 'close', classification: data });
  }
}
