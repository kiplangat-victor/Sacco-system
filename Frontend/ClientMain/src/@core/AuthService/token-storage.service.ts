import { Injectable } from '@angular/core';
import * as store from 'store';

const TOKEN_KEY = 'auth-token';
const USER_KEY = 'auth-user';
const THEME_KEY = 'theme';
const SACCO_NAME = 'sacco_name';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {
  constructor() { }
  signOut(): void {
    // store.clear();
    window.localStorage.removeItem(TOKEN_KEY);
    window.localStorage.removeItem(USER_KEY);
    // window.sessionStorage.clear();
  }

  public saveToken(token: string): void {
    window.localStorage.removeItem(TOKEN_KEY);
    window.localStorage.setItem(TOKEN_KEY, token);
  }

  public getToken(): string | null {
    return window.localStorage.getItem(TOKEN_KEY);
  }

  public saveSaccoName(token: string): void {
    window.localStorage.removeItem(SACCO_NAME);
    window.localStorage.setItem(SACCO_NAME, token);
  }

  public getSaccoName(): string | null {
    return window.localStorage.getItem(SACCO_NAME);
  }

  public saveUser(user: any): void {
    window.localStorage.removeItem(USER_KEY);
    window.localStorage.setItem(USER_KEY, JSON.stringify(user));
  }
  public getUser(): any {
    const user = window.localStorage.getItem(USER_KEY);
    if (user) {
      return JSON.parse(user);
    }
    return {};
  }

  public saveTheme(theme: any): void {
    console.log("Saving theme");
    console.log(theme);
    window.localStorage.removeItem(THEME_KEY);
    window.localStorage.setItem(THEME_KEY, JSON.stringify(theme));
  }
  public getTheme(): any {
    const theme = window.localStorage.getItem(THEME_KEY);
    console.log("Getting theme");
    console.log(theme);
    if (theme) {
      return JSON.parse(theme);
    }
    return null;
  }
}
