import { HttpHeaders, HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { User } from 'src/@core/Models/user/user.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private currentUserSubject: BehaviorSubject<any>;
  public currentUser: Observable<any>;

  headers = new HttpHeaders().set('Content-Type', 'application/json');
  baseURL = `${environment.userAPI}/auth`;

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<any>(
      JSON.parse(localStorage.getItem('currentUser') || '{}')
    );
    this.currentUser = this.currentUserSubject.asObservable();
  }
  signup(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/signup`;
    return this.http.post(API_URL, data, { headers: this.headers, withCredentials: false })
      .pipe(map(res => {
        return res || {}
      }),
        catchError(this.errorMgmt)
      )
  }
  verifyOTP(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/verifyOTP`;
    return this.http.post(API_URL, data, { headers: this.headers, withCredentials: false })
      .pipe(map(res => {
        return res || {}
      }),
        catchError(this.errorMgmt)
      )
  }
  signin(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/signin`;
    return this.http.post(API_URL, data, { headers: this.headers, withCredentials: false })
      .pipe(map(res => {
        return res || {}
      }),
        catchError(this.errorMgmt)
      )
  }
  forgotpassword(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/reset-password`;
    console.log("URL", API_URL);

    return this.http.post(API_URL, data, { headers: this.headers, withCredentials: false })
      .pipe(map(res => {
        console.log("RESULTS FROM RES", res);

        return res || {}
      }),
        catchError(this.errorMgmt)
      )
  }
  users(): Observable<any> {
    let API_URL = `${this.baseURL}/users`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  username(user: any): Observable<any> {
    let API_URL = `${this.baseURL}/username/${user}`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  tellers(): Observable<any> {
    let API_URL = `${this.baseURL}/tellers`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  lock(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/lock/${id}`;
    return this.http.put(API_URL, {}, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  unlock(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/unlock/${id}`;
    return this.http.put(API_URL, {}, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  restore(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/restore/${id}`;
    return this.http.put(API_URL, {}, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }

  update(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/users/update`;
    return this.http.put(API_URL, data, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  delete(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/delete/${id}`;
    return this.http.delete(API_URL, { withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }

  getToken() {
    return localStorage.getItem('jwtToken');
  }
  logout() {
    localStorage.removeItem('currentUser');
  }
  signout(id: any) {
    sessionStorage.clear();
    let API_URL = `${this.baseURL}/logout/${id}`;
    return this.http.put(API_URL, { withCredentials: false })
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
