import { HttpHeaders, HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map } from 'highcharts';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class KraexcisedutyService {
  headers = new HttpHeaders().set('Content-Type', 'application/json');
  baseURL = `${environment.accountsAPI}/api/v1/exciseduty`;

  constructor(private http: HttpClient) { }

  find(): Observable<any> {
    let API_URL = `${this.baseURL}/all`;
    return this.http.get(API_URL,{headers: this.headers, withCredentials: false })
      .pipe(
        catchError(this.errorMgmt)
      )
  }

  verify(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/verify/${id}`;
    return this.http.put(API_URL,{headers: this.headers, withCredentials: false })
      .pipe(
        catchError(this.errorMgmt)
      )
  }

  modify(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/modify`;
    return this.http.put(API_URL, data, { headers: this.headers, withCredentials: false })
      .pipe(
        catchError(this.errorMgmt)
      )
  }
  delete(id: any): Observable<any> {
    var API_URL = `${this.baseURL}/delete/${id}`;
    return this.http.delete(API_URL, { withCredentials: false })
      .pipe(
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
