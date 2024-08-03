import { Component, OnInit } from '@angular/core';
import * as Highcharts from 'highcharts';

@Component({
  selector: 'app-widget-charges',
  templateUrl: './widget-charges.component.html',
  styleUrls: ['./widget-charges.component.scss']
})
export class WidgetChargesComponent implements OnInit {
  chartoptions: any;
   ngOnInit(): void {
     
  }
  getYearWiseData(){
    this.chartoptions = {
      chart : {
         plotBorderWidth: null,
         plotShadow: false
      },
      title : {
         text: 'Charges Collected per product'
      },
      tooltip : {
         pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
       },
       credits: {
          enabled: false
      },
      plotOptions : {
         pie: {
            allowPointSelect: true,
            cursor: 'pointer',
            dataLabels: {
               enabled: true,
               format: '<b>{point.name}%</b>: {point.percentage:.1f} %',
               style: {
                  color: (Highcharts.theme)||
                  'black'
               }
            }
         }
      },
      series : [{
         type: 'pie',
         name: 'Browser share',
         data: [
            ['Shortterm Loans',   45.0],
            ['Longterm Loans',    8.5],
            ['Deposit',     6.2],
            ['Savings',      0.7]
         ]
      }]
   };
    Highcharts.chart('chartOptions', this.chartoptions);

  }
}
