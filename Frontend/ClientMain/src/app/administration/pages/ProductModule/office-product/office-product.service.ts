import { HttpHeaders, HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OfficeProductService {
  headers = new HttpHeaders().set('Content-Type', 'application/json');
  baseURL = `${environment.productAPI}/api/v1/product/office`;

  constructor(private http: HttpClient) { }

  addOab(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/oab/add`;
    return this.http.post(API_URL, data, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  findOab(): Observable<any> {
    let API_URL = `${this.baseURL}/oab/all`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  oabCode(productCode: any): Observable<any> {
    let API_URL = `${this.baseURL}/oab/${productCode}`;
    return this.http.get(API_URL, {  headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  modifyOab(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/oab/modify`;
    return this.http.put(API_URL, data, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  verify(productCode: any): Observable<any> {
    let API_URL = `${this.baseURL}/verify/${productCode}`;
    return this.http.put(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  delete(productCode: any): Observable<any> {
    let API_URL = `${this.baseURL}/delete/${productCode}`;
    return this.http.delete(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
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
