import { Component, OnInit } from '@angular/core';
import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import * as Highcharts from 'highcharts';
import { Subscription } from 'rxjs';
import { StatisticsService } from 'src/app/administration/Service/statistics/statistics.service';

@Component({
  selector: 'app-widget-membership',
  templateUrl: './widget-membership.component.html',
  styleUrls: ['./widget-membership.component.scss']
})
export class WidgetMembershipComponent implements OnInit {
  highcharts = Highcharts;
  subscription!: Subscription;
  yearsSubscription!: Subscription;
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
  membershipForm: FormGroup;
  years: any[] = [];
  isLoading: boolean;

  constructor(
    private fb: UntypedFormBuilder,
    private statisticsService: StatisticsService
  ) { }

  ngOnInit(): void {
    this.getYears();
    this.fetchCustomerOnboardingYearwiseStatistics();
    this.membershipForm = this.createMembershipForm();
  }
  createMembershipForm(): FormGroup {
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

      this.fetchCustomerOnboardingYearwiseStatistics();
    }
    if (event.value == 'Month-wise') {
      this.needYear = true;
      this.needMonth = false;
      let year = new Date().getFullYear();
      console.log(year);

      this.fetchCustomerOnboardingMonthwiseStatistics(this.year);
    }
    if (event.value == 'Date-wise') {
      this.needYear = false;
      this.needMonth = true;
      this.year = new Date().getFullYear();

      this.fetchCustomerOnboardingDaywiseStatistics(this.year);
    }
  }

  getYears() {
    let invoicesArray = [];
    this.statisticsService.fetchCustomerOnboardingYearsStatistics().subscribe(
      (res) => {
        invoicesArray = res;
        for (let i = 0; i < invoicesArray.length; i++) {
          console.log("data is", invoicesArray[i]);

          this.years.push(invoicesArray[i].onboardingYear);
        }
      },
      (err) => {
        console.log(err);
      }
    );
  }

  fetchCustomerOnboardingYearwiseStatistics() {
    this.isLoading = true;

    let identities = [];
    let values = [];
    let statisticsArray = [];

    this.statisticsService.fetchCustomerOnboardingYearwiseStatistics().subscribe(
      (res) => {
        statisticsArray = res;
        console.log("ReSponse ", statisticsArray)

        for (let i = 0; i < statisticsArray.length; i++) {
          identities.push(statisticsArray[i].onboardingYear);
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
              text: 'Years',
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

        console.log("Identities ", identities);
        console.log("Values ", values)
      },
      (err) => {
        console.log(err);
      }
    );
  }

  fetchCustomerOnboardingMonthwiseStatistics(event: any) {
    this.isLoading = true;

    let identities = [];
    let values = [];
    let statisticsArray = [];

    this.year = this.membershipForm.controls.year.value;

    this.statisticsService.fetchCustomerOnboardingMonthwiseStatistics(this.year).subscribe(
      (res) => {
        statisticsArray = res;
        console.log("ReSponse ", statisticsArray)

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

        console.log("Identities ", identities);
        console.log("Values ", values)
      },
      (err) => {
        console.log(err);
      }
    );
  }

  fetchCustomerOnboardingDaywiseStatistics(event: any) {
    this.isLoading = true;

    let identities = [];
    let values = [];
    let statisticsArray = [];

    this.year = this.membershipForm.controls.year.value;
    this.month = this.membershipForm.controls.month.value;

    this.statisticsService.fetchCustomerOnboardingDaywiseStatistics(this.year, this.month).subscribe(
      (res) => {
        statisticsArray = res;
        console.log("ReSponse ", statisticsArray)

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

        console.log("Identities ", identities);
        console.log("Values ", values)
      },
      (err) => {
        console.log(err);
      }
    );
  }
}
