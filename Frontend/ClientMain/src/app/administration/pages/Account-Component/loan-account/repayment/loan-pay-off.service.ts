import { HttpHeaders, HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LoanPayOffService {
  headers = new HttpHeaders().set('Content-Type', 'application/json');
  baseURL = `${environment.accountsAPI}/loan/payoff`;

  constructor(private http: HttpClient) { }

  addPayOff(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/initiate/loan/payoff`;
    return this.http.put(API_URL, {}, { params: params, headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  verify(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/verify/loan/payoff`
    return this.http.put(API_URL, {}, { params: params, headers: this.headers, withCredentials: false }).pipe(
      catchError(this.errorMgmt)
    )
  }
  getLoanPayOffs(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/get/payoff/amount`;
    return this.http.get(API_URL, {params: params, headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
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
