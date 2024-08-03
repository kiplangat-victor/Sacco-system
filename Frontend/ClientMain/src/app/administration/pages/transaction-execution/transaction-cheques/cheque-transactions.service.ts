import { HttpHeaders, HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ChequeTransactionsService {
  headers = new HttpHeaders().set('Content-Type', 'application/json')
  private baseURL: string = `${environment.accountsAPI}/api/v1/chequeprocessing`;
  private receiptURL: any = `${environment.transactionReceiptAPI}`

  constructor(private http: HttpClient) { }
  enter(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/enter`;
    return this.http.post(API_URL, data, { headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  read(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/all`;
    return this.http.get(API_URL, {params: params, headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  findAll(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/all/filter`;
    return this.http.get(API_URL, { params: params, headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  find(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/by/ChequeRandCode`;
    return this.http.get(API_URL, { params: params, headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  modify(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/modify`;
    return this.http.put(API_URL, data, { headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }

  clear(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/clear`;
    return this.http.put(API_URL, {}, { params: params, headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  post(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/post`;
    return this.http.put(API_URL, {}, { params: params, headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  bounce(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/bounce`;
    return this.http.put(API_URL, {}, { params: params, headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  verify(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/verify`;
    return this.http.put(API_URL, {}, { params: params, headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  reject(chequeRandCode: any): Observable<any> {
    let API_URL = `${this.baseURL}/reject/${chequeRandCode}`;
    return this.http.put(API_URL,  {  headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  generateReciept(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append("Accept", "application/pdf");
    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: "blob",
      withCredentials: false,
    };
    let API_URL = `${this.receiptURL}/chequedeposit`
    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: "cheque-deposit-Receipt",
          data: new Blob([response], { type: "application/pdf" }),
        };
      })
    );
  }
  delete(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/delete`;
    return this.http.put(API_URL, {}, { params: params, headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  // Error handling
  errorMgmt(error: HttpErrorResponse) {
    let errorMessage = '';
    if (error.error instanceof ErrorEvent) {
      // Get client-side error
      errorMessage = error.error.message;
    } else {
      // Get server-side error
      errorMessage = `${error.error.message}`;
    }
    return throwError(errorMessage);
  }
}
