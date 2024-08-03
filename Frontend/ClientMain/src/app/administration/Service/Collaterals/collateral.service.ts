import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';
@Injectable({
  providedIn: 'root'
})
export class CollateralService {
  headers = new HttpHeaders().set('Content-Type', 'application/json');
  baseURL = `${environment.crmAPI}/api/v1/collateral`;

  constructor(private http: HttpClient) { }

  create(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/add`;
    return this.http.post(API_URL, data, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  getCollaterals(): Observable<any> {
    let API_URL = `${this.baseURL}/all`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  retrieveCollateral(sn: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/${sn}`;
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => {
        return res || {}
      }
    ), catchError(this.errorMgmt))
  }
  modify(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/modify`
    return this.http.put(API_URL, data, { headers: this.headers, withCredentials: false }).pipe(
      map(res => {
        return res || {}
      }), catchError(this.errorMgmt)
    )
  }
  verify(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/verify/${id}`
    return this.http.put(API_URL, {}, { headers: this.headers, withCredentials: false }).pipe(
      catchError(this.errorMgmt)
    )
  }
  delete(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/delete/${id}`
    return this.http.delete(API_URL, { headers: this.headers, withCredentials: false }).pipe(
      catchError(this.errorMgmt)
    )
  }
  retrieveDocument(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/image/by/${id}`;
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => {
        return res || {}
      }
    ), catchError(this.errorMgmt))
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
