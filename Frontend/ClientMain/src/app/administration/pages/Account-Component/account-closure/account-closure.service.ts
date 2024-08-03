import { HttpHeaders, HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AccountClosureService {
  headers = new HttpHeaders().set('Content-Type', 'application/json');
  baseURL = `${environment.accountsAPI}/accounts/closure`;

  constructor(private http: HttpClient) { }
  closeAccount(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/close/account`;
    return this.http.post(API_URL, {}, { params: params, headers: this.headers, withCredentials: false}).pipe(map((res) => { return res || {}; }),
      catchError(this.errorMgmt)
    );
  }
  verifyClosure(acid: any): Observable<any> {
    let API_URL = `${this.baseURL}/verify/account/closure/${acid}`;
    return this.http.put(API_URL, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  activateAccount(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/activate/account`;
    return this.http.post(API_URL, {}, { params: params, headers: this.headers, withCredentials: false}).pipe(map((res) => { return res || {}; }),
      catchError(this.errorMgmt)
    );
  }
  verifyActivation(acid: any): Observable<any> {
    let API_URL = `${this.baseURL}/verify/account/activation/${acid}`;
    return this.http.put(API_URL, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  
  getClosure(acid: any): Observable<any> {
    let API_URL = `${this.baseURL}/get/account/closure/details/${acid}`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  getActivation(acid: any): Observable<any> {
    let API_URL = `${this.baseURL}/get/account/activation/details/${acid}`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false }).pipe(map(res => {
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
