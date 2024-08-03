import { HttpHeaders, HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SaccoEntityService {

  headers = new HttpHeaders().set('Content-Type', 'application/json');
  baseURL = `${environment.userAPI}/sacco/entity/configurations`;


  constructor(private http: HttpClient) { }

  create(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/add`;
    console.log("URL", API_URL);

    return this.http.post(API_URL, data, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  findAll(): Observable<any> {
    let API_URL = `${this.baseURL}/find/all`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  findActive(): Observable<any> {
    let API_URL = `${this.baseURL}/find/all/active`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }

  saccoentity(saccoentity: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/by/entityId/${saccoentity}`;

    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  find(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/by/id/${id}`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  modify(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/modify`;
    return this.http.put(API_URL, data, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  verify(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/verify/${id}`;
    return this.http.put(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }

  temporarydelete(id: any): Observable<any> {
    var API_URL = `${this.baseURL}/temporary/delete/${id}`;
    return this.http.delete(API_URL, { withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  permanentdelete(id: any): Observable<any> {
    var API_URL = `${this.baseURL}/permanent/delete/${id}`;
    return this.http.delete(API_URL, { withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }


  getUserSaccoDetails(entityId: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/by/entityId/${entityId}`;

    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
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
