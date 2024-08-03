import { HttpHeaders, HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BatchSalariesService {
  baseURL = `${environment.accountsAPI}/api/v1/batchtransaction`;
  headers = new HttpHeaders().set('Content-Type', 'application/json');

  constructor(private http: HttpClient) { }

  create(data: any): Observable<any> {
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

  post(batchCode: any): Observable<any> {
    let API_URL = `${this.baseURL}/post/batch-upload/${batchCode}`;
    return this.http
      .post(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  findAll(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/all`;
    return this.http
      .get(API_URL, { params: params, headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  getBatchesTransactions(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/batchtransactions`;
    return this.http
      .get(API_URL, {
        params: params,
        headers: this.headers,
        withCredentials: false
      })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  totalBatchTransactions(): Observable<any> {
    let API_URL = `${this.baseURL}/total/batch/transactions/count`;
    return this.http
      .get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  findByCode(batchUploadCode: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/by/code/${batchUploadCode}`;
    return this.http
      .get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  findById(id: any): Observable<any> {
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
  modify(data: any): Observable<any> {
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
  reject(batchUploadCode: any): Observable<any> {
    let API_URL = `${this.baseURL}/reject/batch/upload/${batchUploadCode}`;
    return this.http
      .put(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  verify(batchCode: any): Observable<any> {
    let API_URL = `${this.baseURL}/verify/batch-upload/${batchCode}`;
    return this.http
      .put(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  delete(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/delete`;
    return this.http
      .delete(API_URL, {
       params: params, headers: this.headers, withCredentials: false
      })
      .pipe(
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
