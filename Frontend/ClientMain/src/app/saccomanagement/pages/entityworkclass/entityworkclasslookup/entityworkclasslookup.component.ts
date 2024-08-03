import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { WorkClassComponent } from 'src/app/administration/pages/AccessManagement/work-class/work-class.component';
import { WorkClassService } from 'src/app/administration/pages/AccessManagement/work-class/work-class.service';

@Component({
  selector: 'app-entityworkclasslookup',
  templateUrl: './entityworkclasslookup.component.html',
  styleUrls: ['./entityworkclasslookup.component.scss']
})
export class EntityworkclasslookupComponent implements OnInit {
  displayedColumns: string[] = [
    'index',
    'workClass',
    'postedTime',
    'verifiedFlag',
  ];

  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  data: any;
  error: any;
  employeeEmail: any;
  employee_id: any;
  creatingAccount = false;
  respData: any;
  loading = false;

  constructor(
    private workClassService: WorkClassService,
    public dialogRef: MatDialogRef<WorkClassComponent>
  ) { }
  ngOnInit() {
    this.getWorkClasses();
  }
  ngOnDestroy(): void {
  }

  getWorkClasses() {
    this.loading = true;
    this.workClassService.find().subscribe((res) => {
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
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}
