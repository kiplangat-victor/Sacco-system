import { HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import * as Highcharts from 'highcharts';
import { Subscription } from 'rxjs';
import { StatisticsService } from 'src/app/administration/Service/statistics/statistics.service';

@Component({
  selector: 'app-widget-lending',
  templateUrl: './widget-lending.component.html',
  styleUrls: ['./widget-lending.component.scss']
})
export class WidgetLendingComponent implements OnInit {
  highcharts = Highcharts;
  subscription!: Subscription;
  yearsSubscription!: Subscription;
  barChartoptions: any;
  year: any;
  params: any;
  resData: any;
  options: any;
  needYear = false;
  needMonth = false;
  chartDispType: any = ['Year-wise', 'Month-wise', 'Date-wise'];
  monthsArray: any = [
    'JANUARY',
    'FEBRUARY',
    'MARCH',
    'APRIL',
    'MAY',
    'JUNE',
    'JULY',
    'AUGUST',
    'SEPTEMBER',
    'OCTOBER',
    'NOVEMBER',
    'DECEMBER',
  ];
  currentYear = new Date().getFullYear();
  currentMonth = this.monthsArray[new Date().getMonth()];
  month: any;
  yearArray: any;
  selectedYear: any;
  loanRepaymentForm: FormGroup;
  years: any[] = [];
  isLoading: boolean;

  constructor(
    private fb: UntypedFormBuilder,
    private statisticsService: StatisticsService
  ) {}

  ngOnInit(): void {
    this.getYears();

    this.fetchLoanRepaymentsYearwiseStatistics();

    this.loanRepaymentForm = this.createloanRepaymentForm();
  }
  createloanRepaymentForm(): FormGroup {
    return this.fb.group({
      period: [''],
      year: [this.currentYear],
      month: [this.currentMonth],
    });
  }

  onSelectPeriod(event: any) {
    if (event.value == 'Year-wise') {
      this.needYear = false;
      this.needMonth = false;
      this.fetchLoanRepaymentsYearwiseStatistics();
    }
    if (event.value == 'Month-wise') {
      this.needYear = true;
      this.needMonth = false;
      let year = new Date().getFullYear();
      this.fetchLoanRepaymentsMonthwiseStatistics(this.year);
    }
    if (event.value == 'Date-wise') {
      this.needYear = false;
      this.needMonth = true;
      this.year = new Date().getFullYear();

       this.fetchLoanRepaymentsDaywiseStatistics(this.year);
    }
  }

  getYears() {
    let statisticsArray = [];
    this.statisticsService.fetchLoanRepaymentsYearsStatistics().subscribe(
      (res) => {
        statisticsArray = res;
        if(statisticsArray == null){
        }else{
          for (let i = 0; i < statisticsArray.length; i++) {
            if(statisticsArray[i] != null){
              this.years.push(statisticsArray[i].repaymentYears);
            }
          }
        }
      },
      (err) => {
      }
    );
  }

  fetchLoanRepaymentsYearwiseStatistics() {
    this.isLoading = true;

    let identities = [];
    let values = [];
    let statisticsArray = [];

    this.statisticsService.fetchLoanRepaymentsYearwiseStatistics().subscribe(
      (res) => {
        statisticsArray = res;
        for (let i = 0; i < statisticsArray.length; i++) {
          if(statisticsArray[i].paidDate  != null){
            identities.push(statisticsArray[i].paidDate);
            values.push(parseInt(statisticsArray[i].paidAmount))
          }
        }
        this.barChartoptions = {
          chart: {
            type: 'column',
          },
          title: {
            text: 'Average Lending Paid per Month',
          },
          subtitle: {
            text: 'API Server',
          },
          xAxis: {
            categories: identities,
            crosshair: true,
          },
          yAxis: {
            min: 0,
            title: {
              text: 'Total Paid',
            },
          },
          credits: {
            enabled: false
        },
          tooltip: {
            headerFormat:
              '<span style = "font-size:10px">{point.key}</span><table>',
            pointFormat:
              '<tr><td style = "color:{series.color};padding:0">{series.name}: </td>' +
              '<td style = "padding:0"><b>{point.y:.1f}</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true,
          },
          plotOptions: {
            column: {
              pointPadding: 0.2,
              borderWidth: 0,
            },
          },
          series: [
            {
              name: 'Total Amount Paid',
              data: values,
            },
          ],
        };
        Highcharts.chart('barChartOptions', this.barChartoptions);
        this.isLoading = false
      },
      (err) => {
      }
    );
  }

  fetchLoanRepaymentsMonthwiseStatistics(event: any) {
    this.isLoading = true;

    let identities = [];
    let values = [];
    let statisticsArray = [];

    this.year = this.loanRepaymentForm.controls.year.value;

    this.statisticsService.fetchLoanRepaymentsMonthwiseStatistics(this.year).subscribe(
      (res) => {
        statisticsArray = res;
        for (let i = 0; i < statisticsArray.length; i++) {
          if(statisticsArray[i].paidDate  != null){
            identities.push(statisticsArray[i].paidDate);
            values.push(parseInt(statisticsArray[i].paidAmount))
          }
        }

        this.barChartoptions = {
          chart: {
            type: 'column',
          },
          title: {
            text: 'Average Lending Paid per Month',
          },
          subtitle: {
            text: 'API Server',
          },
          xAxis: {
            categories: identities,
            crosshair: true,
          },
          yAxis: {
            min: 0,
            title: {
              text: 'Total Paid',
            },
          },
          credits: {
            enabled: false
        },
          tooltip: {
            headerFormat:
              '<span style = "font-size:10px">{point.key}</span><table>',
            pointFormat:
              '<tr><td style = "color:{series.color};padding:0">{series.name}: </td>' +
              '<td style = "padding:0"><b>{point.y:.1f}</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true,
          },
          plotOptions: {
            column: {
              pointPadding: 0.2,
              borderWidth: 0,
            },
          },
          series: [
            {
              name: 'Total Amount Paid',
              data: values,
            },
          ],
        };
        Highcharts.chart('barChartOptions', this.barChartoptions);
        this.isLoading = false;
      },
      (err) => {
      }
    );
  }

  fetchLoanRepaymentsDaywiseStatistics(event: any){
    this.isLoading = true;

    let identities = [];
    let values = [];
    let statisticsArray = [];

    this.year = this.loanRepaymentForm.controls.year.value;
    this.month = this.loanRepaymentForm.controls.month.value;

    this.statisticsService.fetchLoanRepaymentsDaywiseStatistics(this.year, this.month).subscribe(
      (res) => {
        statisticsArray = res;
        for (let i = 0; i < statisticsArray.length; i++) {
          if(statisticsArray[i].paidDate  != null){

            identities.push(statisticsArray[i].paidDate);

            values.push(parseInt(statisticsArray[i].paidAmount))
          }
        }

        this.barChartoptions = {
          chart: {
            type: 'column',
          },
          title: {
            text: 'Average Lending Paid per Month',
          },
          subtitle: {
            text: 'API Server',
          },
          xAxis: {
            categories: identities,
            crosshair: true,
          },
          yAxis: {
            min: 0,
            title: {
              text: 'Total Paid',
            },
          },
          credits: {
            enabled: false
        },
          tooltip: {
            headerFormat:
              '<span style = "font-size:10px">{point.key}</span><table>',
            pointFormat:
              '<tr><td style = "color:{series.color};padding:0">{series.name}: </td>' +
              '<td style = "padding:0"><b>{point.y:.1f}</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true,
          },
          plotOptions: {
            column: {
              pointPadding: 0.2,
              borderWidth: 0,
            },
          },
          series: [
            {
              name: 'Total Amount Paid',
              data: values,
            },
          ],
        };
        Highcharts.chart('barChartOptions', this.barChartoptions);
        this.isLoading = true;
      },
      (err) => {
      }
    );
  }
}
