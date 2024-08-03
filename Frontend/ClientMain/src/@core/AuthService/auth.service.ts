import { environment } from '../../environments/environment';

import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpHeaders,
  HttpErrorResponse,
} from '@angular/common/http';
import { catchError, map } from 'rxjs/operators';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { User } from 'src/@core/Models/user/user.model';
import { NotificationService } from '../helpers/NotificationService/notification.service';
import { Router } from '@angular/router';

const AUTH_API = `${environment.userAPI}/auth/`;


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
};

@Injectable({ providedIn: 'root' })
export class AuthService {
  static getToken() {
    throw new Error('Method not implemented.');
  }
  private currentUserSubject: BehaviorSubject<User>;
  public currentUser: Observable<User>;

  headers = new HttpHeaders().set('Content-Type', 'application/json');

  constructor(private http: HttpClient,
    private notificationApi: NotificationService,
    private router: Router
  ) {
    this.currentUserSubject = new BehaviorSubject<User>(
      JSON.parse(localStorage.getItem('currentUser') || '{}')
    );
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): User {
    return this.currentUserSubject.value;
  }
  validateOTP(params: any): Observable<any> {
    let API_URL = `${AUTH_API}otp/verify`;
    return this.http.get(API_URL, { headers: this.headers, params: params, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }

  resetPasswordRequest(data: any): Observable<any> {
    return this.http.post(AUTH_API + 'reset', data, httpOptions);
  }

  resetPassword(data: any): Observable<any> {
    return this.http.post(AUTH_API + 'reset-password', data, httpOptions);
  }

  forgotPassword(data: any): Observable<any> {
    return this.http.post(AUTH_API + 'forgot-password', data, httpOptions);
  }

  getRoles(): Observable<any> {
    return this.http.get(AUTH_API + 'roles', httpOptions);
  }

  authenticateUser(data: any): Observable<any> {
    return this.http.post(AUTH_API + 'signin', data, httpOptions);
  }
  singin(data: any): Observable<any> {
    return this.http.post( AUTH_API + 'signin/new', data, httpOptions);
  }
  singinOtp(otpCode: any): Observable<any> {
    return this.http.get(`${AUTH_API}otp/verify?otpCode=` + otpCode);
  }
  registerUser(data: any): Observable<any> {
    return this.http.post(AUTH_API + 'signup', data, httpOptions);
  }

  allUsers(): Observable<any> {
    return this.http.get(AUTH_API + 'users', httpOptions);
  }
  allTellers(): Observable<any> {
    return this.http.get(AUTH_API + 'tellers', httpOptions);
  }

  getUserByUsername(username: any): Observable<any> {
    return this.http.get(AUTH_API + `account/${username}`, httpOptions);
  }
  isLoggedIn() {
    let currentUser = JSON.parse(sessionStorage.getItem('auth-user'));
    if (currentUser !== null) {
      this.notificationApi.alertWarning("You must sign in")
      this.router.navigateByUrl('sso');
      return false;
    } else {
      return true;
    }
  }
  updateUser(data: any): Observable<any> {
    return this.http.put(AUTH_API + 'users/update', data, httpOptions);
  }
  delete(id: any): Observable<any> {
    return this.http.delete(AUTH_API + `delete/${id}`, httpOptions);
  }
  lock(id: any): Observable<any> {
    return this.http.put(AUTH_API + `lock/${id}`, httpOptions);
  }
  unlock(id: any): Observable<any> {
    return this.http.put(AUTH_API + `unlock/${id}`, httpOptions);
  }
  getToken() {
    return localStorage.getItem('jwtToken');
  }
  logout() {
    localStorage.removeItem('currentUser');
  }
  signout(id: any): Observable<any> {
    sessionStorage.clear();
    return this.http.put(AUTH_API + `logout/${id}`, httpOptions);
  }
  // Message Medium
  private messageSource = new BehaviorSubject('default message');
  currentMessage = this.messageSource.asObservable();
  changeMessage(message: string) {
    this.messageSource.next(message);
  }
  // Error handling
  errorMgmt(error: HttpErrorResponse) {
    return throwError(error);
  }
}
