import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';
@Injectable({
  providedIn: 'root'
})
export class LoanAccountService {
  headers = new HttpHeaders().set('Content-Type', 'application/json');
  baseURL = `${environment.productAPI}/api/v1/product/interest/value`;

  constructor(private http: HttpClient) { }

  getInterest(params: any): Observable<any> {
    let API_URL = `${this.baseURL}`;
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
