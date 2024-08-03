import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { ExcisedutyService } from '../exciseduty.service';

@Component({
  selector: 'app-kratariff',
  templateUrl: './kratariff.component.html',
  styleUrls: ['./kratariff.component.scss']
})
export class KratariffComponent implements OnInit, OnDestroy {
  kraTariffColumns: string[] = [
    'index',
    'tarrifCd',
    'tarrifNm',
    'exDtCharge',
    'exdutyRt'
  ];
  kraTariffDataSource: MatTableDataSource<any>;
  @ViewChild("kraTariffPaginator") kraTariffPaginator!: MatPaginator;
  @ViewChild(MatSort) kraTariffSort!: MatSort;
  tariffs: any;
  loading: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>()
  constructor(
    private exciseDutyAPI: ExcisedutyService,
    private notificationAPI: NotificationService
  ) { }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.getKraTariffs();
  }
  getKraTariffs() {
    this.loading = true;
    this.exciseDutyAPI.findTariffs().pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode == 200) {
              this.tariffs = res.entity;
              this.loading = false;
              this.kraTariffDataSource = new MatTableDataSource(this.tariffs);
              this.kraTariffDataSource.paginator = this.kraTariffPaginator;
              this.kraTariffDataSource.sort = this.kraTariffSort;
            } else {
              this.loading = false;
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
    ), Subscription;
  }

  kratafiriffFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.kraTariffDataSource.filter = filterValue.trim().toLowerCase();
    if (this.kraTariffDataSource.paginator) {
      this.kraTariffDataSource.paginator.firstPage();
    }
  }

}
