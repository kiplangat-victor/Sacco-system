import { Component, OnInit } from '@angular/core';
import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import * as Highcharts from 'highcharts';
import { Subscription } from 'rxjs';
import { StatisticsService } from 'src/app/administration/Service/statistics/statistics.service';

@Component({
  selector: 'app-widget-share-capital',
  templateUrl: './widget-share-capital.component.html',
  styleUrls: ['./widget-share-capital.component.scss']
})
export class WidgetShareCapitalComponent implements OnInit {
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
  sharecapitalForm: FormGroup;
  years: any[] = [];
  isLoading: boolean;

  constructor(
    private fb: UntypedFormBuilder,
    private statisticsService: StatisticsService
  ) {}

  ngOnInit(): void {
    this.sharecapitalForm = this.createsharecapitalForm();

    this.getYears();

    this.fetchShareCapitalYearwiseStatistics();

  }
  createsharecapitalForm(): FormGroup {
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
      this.fetchShareCapitalYearwiseStatistics();
    }
    if (event.value == 'Month-wise') {
      this.needYear = true;
      this.needMonth = false;
      let year = new Date().getFullYear();

      this.fetchShareCapitalMonthwiseStatistics(this.year);
    }
    if (event.value == 'Date-wise') {
      this.needYear = false;
      this.needMonth = true;
      this.year = new Date().getFullYear();

       this.fetchShareCapitalDaywiseStatistics(this.year);
    }
  }

  getYears() {
    let years = [];
    this.statisticsService.fetchShareCapitalYearsStatistics().subscribe(
      (res) => {
        years = res;


        for (let i = 0; i < years.length; i++) {
          this.years.push(parseInt(years[i].contrYear));
        }

      },
      (err) => {
      }
    );
  }

  fetchShareCapitalYearwiseStatistics() {
    this.isLoading = true;

    let identities = [];
    let values = [];
    let statisticsArray = [];

    this.statisticsService.fetchShareCapitalYearwiseStatistics().subscribe(
      (res) => {
        statisticsArray = res;

        for (let i = 0; i < statisticsArray.length; i++) {
          identities.push(statisticsArray[i].onboardingYear);
          values.push(parseInt(statisticsArray[i].totalCustomers))
        }

        this.barChartoptions = {
          chart: {
            type: 'column',
          },
          title: {
            text: 'Share Capital Annualy',
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
              name: 'Total Paid',
              data: values,
            },
          ],
        };
        Highcharts.chart('Account-chart', this.barChartoptions);

        this.isLoading = false;
      },
      (err) => {
      }
    );
  }

  fetchShareCapitalMonthwiseStatistics(event: any) {
    this.isLoading = true;

    let identities = [];
    let values = [];
    let statisticsArray = [];

    this.year = this.sharecapitalForm.controls.year.value;

    this.statisticsService.fetchShareCapitalMonthwiseStatistics(this.year).subscribe(
      (res) => {
        statisticsArray = res;
        for (let i = 0; i < statisticsArray.length; i++) {
          identities.push(statisticsArray[i].monthName);
          values.push(parseInt(statisticsArray[i].totalCustomers))
        }
        this.options = {
          accessibility: {
            description: '',
          },
          title: {
            text: 'Average No of Members Annually',
          },
          subtitle: {
            text: 'Sources: API',
          },
          xAxis: {
            categories: identities,
            tickmarkPlacement: 'on',
            title: {
              text: 'Month name',
            },
          },

          yAxis: {
            min: 0,
            title: {
              text: 'No. of Members',
              align: 'high',
            },
            labels: {
              overflow: 'justify',
            },
          },
          credits: {
            enabled: false,
          },
          tooltip: {
            valueSuffix: ' Members',
          },
          plotOptions: {
            bar: {
              dataLabels: {
                enabled: true,
              },
            },
          },
          series: [
            {
              name: 'Active ',
              data: values,
              type: 'spline',
            },
          ],
        };
        Highcharts.chart('Overal-Perfomance', this.options);
        this.isLoading = false;
      },
      (err) => {
      }
    );
  }
  fetchShareCapitalDaywiseStatistics(event: any){
    this.isLoading = true;
    let identities = [];
    let values = [];
    let statisticsArray = [];
    this.year = this.sharecapitalForm.controls.year.value;
    this.month = this.sharecapitalForm.controls.month.value;
    this.statisticsService.fetchShareCapitalDaywiseStatistics(this.year, this.month).subscribe(
      (res) => {
        statisticsArray = res;
        for (let i = 0; i < statisticsArray.length; i++) {
          identities.push(statisticsArray[i].onboardingDay);
          values.push(parseInt(statisticsArray[i].totalCustomers))
        }
        this.options = {
          accessibility: {
            description: '',
          },
          title: {
            text: 'Average No of Members Annually',
          },
          subtitle: {
            text: 'Sources: API',
          },
          xAxis: {
            categories: identities,
            tickmarkPlacement: 'on',
            title: {
              text: 'Day of month',
            },
          },
          yAxis: {
            min: 0,
            title: {
              text: 'No. of Members',
              align: 'high',
            },
            labels: {
              overflow: 'justify',
            },
          },
          credits: {
            enabled: false,
          },
          tooltip: {
            valueSuffix: ' Members',
          },
          plotOptions: {
            bar: {
              dataLabels: {
                enabled: true,
              },
            },
          },
          series: [
            {
              name: 'Active ',
              data: values,
              type: 'spline',
            },
          ],
        };
        Highcharts.chart('Overal-Perfomance', this.options);
        this.isLoading = false;
      },
      (err) => {
      }
    );
  }

}
