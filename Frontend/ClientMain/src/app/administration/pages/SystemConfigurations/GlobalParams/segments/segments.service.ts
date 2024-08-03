import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SegmentsService {

  headers = new HttpHeaders().set('Content-Type', 'application/json')
  baseURL = `${environment.systemConfigAPI}/api/v1/segment`;
  constructor(private http: HttpClient) { }

  createSegment(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/add`
    return this.http.post(API_URL, data, { headers: this.headers, withCredentials: false }).pipe(map(
      res => {
        return res || {}
      }
    ),
      catchError(this.errorMgmt)
    )
  }
  getAllSegments(): Observable<any> {
    let API_URL = `${this.baseURL}/all`;
    return this.http.get(API_URL, {
      headers: this.headers, withCredentials: false
    }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  getSegmentByCode(code: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/segment/code/${code}`;
    return this.http.get(API_URL, {
      headers: this.headers, withCredentials: false
    }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  findBySegmentId(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/${id}`;
    return this.http.get(API_URL, {
      headers: this.headers, withCredentials: false
    }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  verify(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/verify/${id}`
    return this.http.put(API_URL, { headers: this.headers, withCredentials: false }).pipe(map(
      res => {
        return res || {}
      }
    ),
      catchError(this.errorMgmt)
    )
  }

  delete(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/delete/${id}`;
    return this.http.delete(API_URL, {
      headers: this.headers, withCredentials: false
    }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  updateSegment(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/modify`;
    return this.http.put(API_URL, data, {
      headers: this.headers,
      withCredentials: false
    }).pipe(map(
      res => {
        return res || {}
      }
    ),
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
    return throwError(errorMessage)
  }


}
