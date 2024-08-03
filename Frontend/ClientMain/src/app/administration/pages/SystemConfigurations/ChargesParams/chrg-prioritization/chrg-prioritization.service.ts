import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ChrgPrioritizationService {

  headers = new HttpHeaders().set('Content-Type', 'application/json');
    // API endpoint
  baseURL = `${environment.productAPI}/api/v1/system/configurations/charge/params/Prioritizations`;
    constructor(private http: HttpClient) { }
      // Message Medium
  private messageSource = new BehaviorSubject('default message');
  currentMessage = this.messageSource.asObservable();
  changeMessage(message: string) {
    this.messageSource.next(message)
    console.log("service message", message)
  }
  // check if data exists
  checkIfChrgPrioritizationExists(params: any): Observable<any> {
    console.log("service console params", params)
    let API_URL = `${this.baseURL}/check/charge/Prioritization/`;
    return this.http.get(API_URL, { params:params,  withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  // Add
  createChrgPrioritization(data: any): Observable<any> {
    console.log("Review data", data)
    let API_URL = `${this.baseURL}/add/`;
    return this.http.post(API_URL, data, { headers: this.headers, withCredentials: false }).pipe(map(res => {
        return res || {}
      }),
      catchError(this.errorMgmt)
    )
  }
  // Get all
  getChrgPrioritizations() {
    let API_URL = `${this.baseURL}/all`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
    .pipe(
      map((res) => {
        return res || {}
      }),
      catchError(this.errorMgmt)
    )
  }
  // getActualChrgPrioritizationByEventId(params: any): Observable<any> {
  //   let API_URL = `${this.baseURL}/find/actual/charge/Prioritization/`;
  //   return this.http.get(API_URL, { params:params,  withCredentials: false })
  //     .pipe(
  //       map((res) => {
  //         return res || {}
  //       }),
  //       catchError(this.errorMgmt)
  //     )
  // }
  getActualChrgPrioritization(params: any): Observable<any> {
    console.log("service console params", params)
  let API_URL = `${this.baseURL}/check/charge/Prioritization/`;
  return this.http.get(API_URL, { params:params,  withCredentials: false })
    .pipe(
      map((res) => {
    console.log("Data Respond", res)
        return res || {}
      }),
      catchError(this.errorMgmt)
    )
  }
  getActualChrgPrioritizationByEventId(params: any): Observable<any> {
    console.log("service console params", params)
  let API_URL = `${this.baseURL}/check/charge/Prioritization/`;
  return this.http.get(API_URL, { params:params,  withCredentials: false })
    .pipe(
      map((res) => {
    console.log("Data Respond", res)
        return res || {}
      }),
      catchError(this.errorMgmt)
    )
  }
  // /find/actual/charge/Prioritization/
  // Get by id /find/by/event_id/{event_id}

  getChrgPrioritizationByEventIds(event_id: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/by/event_id/${event_id}`;
    return this.http.get(API_URL, { withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }

  getChrgPrioritizationId(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/${id}`;
    return this.http.get(API_URL, { withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  checkEntryIfExist(params:any): Observable<any> {
    let API_URL = `${this.baseURL}/check/entry/if/exist`;
    return this.http.get(API_URL, { params:params, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  getChrgPrioritizationByChrgPrioritization(params:any): Observable<any> {
    let API_URL = `${this.baseURL}/find/by/event_id`;
    return this.http.get(API_URL, { params:params, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  
  // updateChrgPrioritization(ChrgPrioritization: string | null, data: any): Observable<any> {
  //   let API_URL = `${this.baseURL}/update/${ChrgPrioritization}`;
  //   return this.http.put(API_URL, data, {headers: this.headers, withCredentials: false})
  //     .pipe(
  //       catchError(this.errorMgmt)
  //     )
  // }
  updateChrgPrioritization(id: any, data: any): Observable<any> {
  
    let API_URL = `${this.baseURL}/update/${id}`;
    return this.http.put(API_URL, data, {headers: this.headers, withCredentials: false})
      .pipe(
        catchError(this.errorMgmt)
      )
  }
  deleteChrgPrioritization(id: any): Observable<any> {
    var API_URL = `${this.baseURL}/delete/${id}`;
    return this.http.delete(API_URL, { withCredentials: false })
      .pipe(
        catchError(this.errorMgmt)
      )
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
