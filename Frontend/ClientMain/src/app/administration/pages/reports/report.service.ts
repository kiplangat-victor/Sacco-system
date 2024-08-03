import { Injectable } from '@angular/core';
import { ReportDefination } from './interfaces/report-defination'
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import { Observable, Subject, throwError} from 'rxjs';
import { DownloadRequest } from './interfaces/downloadRequest'
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  // private baseUrl = 'http://localhost:9094/reports/';
  headers = new HttpHeaders().set('Content-Type', 'application/json');
  private baseUrl = `${environment.reportsAPI}/reports/`;


  constructor(private http: HttpClient) { }

  sacconame(): Observable<any> {
    let API_URL = `${environment.reportsAPI}/api/v1/dynamic/sacconame`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  saccoLogo(): Observable<any> {
    let API_URL = `${environment.reportsAPI}/api/v1/dynamic/saccologo`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }

  addReport(reportdef: ReportDefination,jrxmlImage: File){
    const formData: FormData = new FormData();
       formData.append('jrxml', jrxmlImage);
       formData.append('reportDefinition',  JSON.stringify(reportdef));
       return this.http.post<any>(this.baseUrl+'add', formData);
  }
  // downloadReport(request: DownloadRequest){
  //   return this.http.get(this.baseUrl+'download');
  // }
  allReports(){
    return this.http.get(this.baseUrl+'all');
  }

  downloadReport(request: DownloadRequest): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');
    let requestOptions: any = { headers: headers, responseType: 'blob',  withCredentials: false };
    return this.http.post(this.baseUrl+'download',request,requestOptions)
      .pipe(map((response)=>{
        return {
          filename: 'account_statement.pdf',
          data: new Blob([response], {type: 'application/pdf'})
        };
      }));
  }

  generateConsDailyLiquidityReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append("Accept", "application/pdf");

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: "blob",
      withCredentials: false,
    };
    let API_URL = `${this.baseUrl}/api/v1/reports/acrualpayments/all`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        console.log("Hey this is status file", response);
        return {
          filename: "Report",
          data: new Blob([response], { type: "application/pdf" }),
        };
      })
    );
  }
  errorMgmt(error: HttpErrorResponse) {
    let errorMessage = '';
    if (error.error instanceof ErrorEvent) {
      errorMessage = error.error.message;
    } else {
      errorMessage = `${error.error.message}`;
    }
    return throwError(errorMessage);
  }
}
