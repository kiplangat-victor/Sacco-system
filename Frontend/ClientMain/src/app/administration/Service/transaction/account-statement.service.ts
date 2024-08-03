import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';
@Injectable({
  providedIn: 'root'
})
export class AccountStatementService {

  headers = new HttpHeaders().set('Content-Type', 'application/json');
  baseURL = `${environment.accountsAPI}/transactions/statement`;
  baseURL2 = `${environment.accountsAPI}/transactions/statement?acid=G01-020001&fromdate=2022-10-05&todate=2022-12-05`;

  constructor(private http: HttpClient) { }

  //Retrieving an Account
  statements(acid: any): Observable<any> {
    let API_URL = `${this.baseURL}/ministatement/${acid}`;
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => {
        return res || {}
      }
    ), catchError(this.errorMgmt))
  }
  statement(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/ministatement`;
    return this.http.get(API_URL, {params: params, withCredentials: false }).pipe(map(
      res => {
        return res || {}
      }
    ), catchError(this.errorMgmt))
  }
  getStatement(params: any): Observable<any> {
    let API_URL = `${this.baseURL}`;
    return this.http
      .get(API_URL, {params,}
      ) .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
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
