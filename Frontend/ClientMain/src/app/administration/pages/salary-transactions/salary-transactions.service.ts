import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class SalaryTransactionsService {
  headers = new HttpHeaders().set('Content-Type', 'application/json');
  private baseURL: string = `${environment.accountsAPI}/api/v1/salaryuploads`;
  constructor(private http: HttpClient) {}

  createSalaryTransaction(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/add`;
    return this.http
      .post(API_URL, data, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  updateSalaryTransaction(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/modify`;
    return this.http
      .put(API_URL, data, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  rejectSalaryTransaction(salaryTransactionCode: any): Observable<any> {
    let API_URL = `${this.baseURL}/reject/salary/upload/${salaryTransactionCode}`;
    return this.http
      .put(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }

  verifySalaryTransaction(salaryTransactionCode: any): Observable<any> {
    let API_URL = `${this.baseURL}/verify/salary-upload/${salaryTransactionCode}`;
    return this.http
      .put(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }

  postSalaryTransaction(salaryTransactionCode: any): Observable<any> {
    let API_URL = `${this.baseURL}/post/salary-upload/${salaryTransactionCode}`;
    return this.http
      .post(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  getSalaryTransactions(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/salaryuploads`;
    return this.http
      .get(API_URL, {
        headers: this.headers,
        withCredentials: false,
        params: params,
      })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }

  // Get all
  find(): Observable<any> {
    let API_URL = `${this.baseURL}/all`;
    return this.http
      .get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }

  getAccountDetails(accountNumber: any): Observable<any> {
    let API_URL = `${this.baseURL}/acount_balance/${accountNumber}`;
    return this.http
      .get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }

  getSalaryTransactionByCode(salaryTransactionCode: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/by/code/${salaryTransactionCode}`;
    return this.http
      .get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }

  cancelSalaryTransactionById(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/cancel`;
    return this.http
      .delete(API_URL, {
        headers: this.headers,
        withCredentials: false,
        params: params,
      })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }

  deleteSalaryTransactionById(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/delete`;
    return this.http
      .delete(API_URL, {
        headers: this.headers,
        withCredentials: false,
        params: params,
      })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }

  getSalaryTransactionId(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/by/${id}`;
    return this.http
      .get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  getTotalTransactions(): Observable<any> {
    let API_URL = `${this.baseURL}/total/salary/uploads/count`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }

  //error handling
  errorMgmt(error: HttpErrorResponse) {
    let errorMessage = '';
    if (error.error instanceof ErrorEvent) {
      //get Client side error
      errorMessage = error.error.message;
    } else {
      // get-server side error
      errorMessage = `${error.error.message}`;
    }
    return throwError(errorMessage);
  }
}
