import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ChequeBooksComponent } from '../cheque-books.component';
import { ChequeBookService } from '../cheque-books.service';

@Component({
  selector: 'app-cheque-book-lookup',
  templateUrl: './cheque-book-lookup.component.html',
  styleUrls: ['./cheque-book-lookup.component.scss']
})
export class ChequeBookLookupComponent implements OnInit, OnDestroy {

  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  title = 'export-table-data-to-any-format';
  displayedColumns: string[] = [
    'index',
    'accountName',
    'postedTime',
    'verifiedFlag',
  ];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  subscription!: Subscription;
  data: any;
  error: any;
  employeeEmail: any;
  employee_id: any;
  creatingAccount = false;
  respData: any;
  today = new Date();
  tommorrowDate = new Date(new Date().setDate(this.today.getDate() + 1));
  priorDate = new Date(new Date().setDate(this.today.getDate() - 30));
  loading= false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public fb: UntypedFormBuilder,
    private chequebookAPI: ChequeBookService,
    public dialogRef: MatDialogRef<ChequeBooksComponent>
  ) {}
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.getCheckBooks();
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  getCheckBooks() {
    this.loading = true;
    this.subscription = this.chequebookAPI.find().pipe(takeUntil(this.destroy$)).subscribe((res) => {
        this.respData = res;
        this.loading = false;
        this.dataSource = new MatTableDataSource(this.respData.entity);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      });
  }
  onSelect(data: any) {
    this.dialogRef.close({ event: 'close', data: data });
  }
}
