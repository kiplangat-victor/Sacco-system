import { Component, OnInit } from '@angular/core';
import { Parameter } from './interfaces/parameter';
import { ReportDefination } from './interfaces/report-defination';
import { ReportService } from './report.service';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.scss']
})
export class ReportsComponent implements OnInit {

  reportdef!: ReportDefination
  query!: string;
  rcre!: Date
  postedBy!: string
  postedTime!: Date
  modifiedBy!: string
  modifiedtime!: Date
  jrxmlName!: string
  reportName!: string;
  jrxmlfile!: File  // Variable to store file
  size!: number

  parameters!: Parameter[]
  params: Parameter[] = []

  constructor(private reportService: ReportService) {
    this.reportdef = {},
      this.parameters = [
        {
          parameterName: 'sol_code',
          rcre: new Date(),
          postedBy: 'kamau',
          postedTime: new Date(),
          modifiedBy: 'kamau',
          modifiedTime: new Date()
        },
        {
          parameterName: 'manager',
          rcre: new Date(),
          postedBy: 'kamau',
          postedTime: new Date(),
          modifiedBy: 'kamau',
          modifiedTime: new Date()
        },
        {
          parameterName: 'start_date',
          rcre: new Date(),
          postedBy: 'kamau',
          postedTime: new Date(),
          modifiedBy: 'kamau',
          modifiedTime: new Date()
        },
        {
          parameterName: 'end_date',
          rcre: new Date(),
          postedBy: 'kamau',
          postedTime: new Date(),
          modifiedBy: 'kamau',
          modifiedTime: new Date()
        },
        {
          parameterName: 'customer_code',
          rcre: new Date(),
          postedBy: 'kamau',
          postedTime: new Date(),
          modifiedBy: 'kamau',
          modifiedTime: new Date()
        },
        {
          parameterName: 'account_no',
          rcre: new Date(),
          postedBy: 'kamau',
          postedTime: new Date(),
          modifiedBy: 'kamau',
          modifiedTime: new Date()
        }
      ]
  }
  ngOnInit(): void {
  }


  submitReport() {
    this.reportdef.query = "test"
    this.reportdef.postedTime = new Date()
    this.reportdef.reportName = this.reportName
    this.reportdef.jrxmlName = this.jrxmlfile.name
    this.reportdef.postedBy = "kamau"
    this.reportdef.modifiedTime = new Date()
    this.reportdef.modifiedBy = "kamau"
    this.reportdef.rcre = new Date()
    this.reportdef.parameters = this.params

    console.log("query", this.query)
    console.log("name", this.reportName)
    console.log("para", this.params)
    console.log(this.jrxmlfile.name);

    this.reportService.addReport(this.reportdef, this.jrxmlfile).subscribe(
      data => {
        console.log(data)
        window.alert("UPLOAD SUCCESSFUL");
      }
    )


  }

  jrxmlChange(event: any) {
    this.jrxmlfile = event.target.files[0];
    console.log(this.jrxmlfile.name);
    this.size = this.jrxmlfile.size / 1024 / 1024
    console.log(this.size);
  }
  add(param: Parameter) {
    this.params.push(param)
  }
  remove(param: Parameter) {
    //this.params.splice(index,1);
    this.params = this.params.filter(obj => obj.parameterName !== param.parameterName);

  }

  containsObject(obj: Parameter, list: Parameter[]) {
    var i;
    for (i = 0; i < list.length; i++) {
      if (list[i] === obj) {
        return true;
      }
    }

    return false;
  }

}
