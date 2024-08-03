import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EndOfDayService {

  baseURL = `${environment.accountsAPI}`
  headers = new HttpHeaders().set('Content-Type', 'application/json');

  constructor(private http: HttpClient) { }
  //error handling
  errorMgmt(error: HttpErrorResponse) {
    let errorMessage = '';

    if (error.error instanceof ErrorEvent) {
      //get Client side error
      errorMessage = error.error.message
    } else {
      // get-server side error
      errorMessage = `${error.error.message}`;
    }
    return throwError(errorMessage)
  }

  //message medium
  private messageSource = new BehaviorSubject('default message');
  currentMessage = this.messageSource.asObservable();
  changeMessage(message: string) {
    this.messageSource.next(message)

  }


  initiateEndOfDay(): Observable<any> {
    let API_URL = `${this.baseURL}/api/v1/eod/initiateEOD`;
    return this.http.post(API_URL, { headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }

  trackEndOfDay(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/api/v1/eodStatus/currentEodData`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false, params:params }).pipe(
      map((res) => {

        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }

  enableUserAccounts(): Observable<any> {
    let API_URL = `${this.baseURL}/api/v1/eodManageUsers/enableAllAccounts`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false }).pipe(
      map((res) => {

        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }


}
