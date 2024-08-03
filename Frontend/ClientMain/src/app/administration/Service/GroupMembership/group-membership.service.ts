import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class GroupMembershipService {
  headers = new HttpHeaders().set('Content-Type', 'application/json');
  baseURL = `${environment.crmAPI}/api/v1/groups`;

  constructor(private http: HttpClient) { }

  add(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/add`;
    return this.http.post(API_URL, data, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }

  getAll(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/all`;
    return this.http.get(API_URL, { params: params, headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  read(): Observable<any> {
    let API_URL = `${this.baseURL}/get/all/group/members`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  readAllUnverified(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/all/unVerified/group/members/list`;
    return this.http.get(API_URL, { params: params, headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  getTotalGroups(): Observable<any> {
    let API_URL = `${this.baseURL}/get/total/group/members`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  retrieveAccount(code: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/by/code/${code}`;
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => {
        return res || {}
      }
    ), catchError(this.errorMgmt))
  }
  findById(id: number): Observable<any> {
    let API_URL = `${this.baseURL}/find/by/id/${id}`;
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
  reject(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/reject/${id}`
    return this.http.put(API_URL, { headers: this.headers, withCredentials: false }).pipe(
      catchError(this.errorMgmt)
    )
  }
  verify(code: any): Observable<any> {
    let API_URL = `${this.baseURL}/verify/${code}`
    return this.http.put(API_URL, {}, { headers: this.headers, withCredentials: false }).pipe(
      catchError(this.errorMgmt)
    )
  }
  delete(code: any): Observable<any> {
    let API_URL = `${this.baseURL}/verify/${code}`
    return this.http.delete(API_URL, { headers: this.headers, withCredentials: false }).pipe(
      catchError(this.errorMgmt)
    )
  }
  documents(id: any): Observable<any> {
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
