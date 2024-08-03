import { HttpHeaders, HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LiensServiceService {
  headers = new HttpHeaders().set('Content-Type', 'application/json');
  baseURL = `${environment.accountsAPI}/lien`;

  constructor(private http: HttpClient) { }

  create(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/add`;
    console.log(API_URL);
    return this.http.post(API_URL, data, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    );
  }
  getAll(): Observable<any> {
    let API_URL = `${this.baseURL}/active/liens`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    );
  }
  findByCode(code: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/by/lien-code/${code}`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    );
  }
  closeLien(code: any): Observable<any> {
    let API_URL = `${this.baseURL}/close/lien/by/lien-code/${code}`;
    return this.http.put(API_URL, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    );
  }
  verifyLienClose(code: any): Observable<any> {
    let API_URL = `${this.baseURL}/verify/lien/close/by/lien-code/${code}`;
    return this.http.put(API_URL, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    );
  }
  getBySourceAcid(acid: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/by/source-account/${acid}`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    );
  }
  satisfy(acid: any): Observable<any> {
    let API_URL = `${this.baseURL}/satisfy/lien/${acid}`;
    return this.http.put(API_URL, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    );
  }
  verify(code: any): Observable<any> {
    let API_URL = `${this.baseURL}/verify/lien/by/lien-code/${code}`;
    return this.http.put(API_URL, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    );
  }
  modify(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/modify/lien`;
    return this.http.put(API_URL, data, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    );
  }
  satisfyLienByLienCode(acid: any): Observable<any> {
    let API_URL = `${this.baseURL}/satisfy/lien/by/lien-code`;
    return this.http.put(API_URL, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    );
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
