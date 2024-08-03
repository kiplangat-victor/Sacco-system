import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ShareCapitalParamsService {
  headers =  new HttpHeaders().set('Content-Type', 'application/json')
  constructor(private http:HttpClient) { }
  baseURL = `${environment.systemConfigAPI}/api/v1/sharecapital/params`;

  // Add
  create(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/add`;
    return this.http.post(API_URL, data, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  // Get all
  findAll() {
    let API_URL = `${this.baseURL}/all`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  verify(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/verify/${id}`;
    return this.http.put(API_URL,{headers: this.headers, withCredentials: false })
      .pipe(
        catchError(this.errorMgmt)
      )
  }

  modify(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/modify/`;
    return this.http.put(API_URL, data, { headers: this.headers, withCredentials: false })
      .pipe(
        catchError(this.errorMgmt)
      )
  }
  getLastEntry(){
  let API_URL = `${this.baseURL}/find/last/entry`
  return this.http.get(API_URL, {headers:this.headers,
  withCredentials:false}).pipe(map(res =>{
    return res || {}
  }),
  catchError(this.errorMgmt)
  )
}
  delete(id: any): Observable<any> {
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

//   //message medium
//   private messageSource = new BehaviorSubject('default message');
//   currentMessage = this.messageSource.asObservable();
//   changeMessage(message: string) {
//     this.messageSource.next(message)
//   }
// //error Handling
// errorMgmt(error:HttpErrorResponse){
//   let errorMessage = '';
//   if(error.error instanceof ErrorEvent){
//     errorMessage = error.error.message;
//   }else{
//     errorMessage = `${error.error.message}`;
//   }
//   return throwError(errorMessage)
// }

// //add

// createShareCapitalParams(data:any):Observable<any>{
//   let API_URL = `${this.baseURL}/add`
//   return this.http.post(API_URL, data, {headers: this.headers, withCredentials:false}).pipe(map(
//     res =>{
//       return res || {}
//     }
//   ),
//   catchError(this.errorMgmt)
//   )
// }

// //get all

// getAllShareCapitalParams(){
//   let API_URL = `${this.baseURL}/all`
//   return this.http.get(API_URL, {headers:this.headers,
//   withCredentials:false}).pipe(map(res =>{
//     return res || {}
//   }),
//   catchError(this.errorMgmt)
//   )
// }
// getLastEntry(){
//   let API_URL = `${this.baseURL}/find/last/entry`
//   return this.http.get(API_URL, {headers:this.headers,
//   withCredentials:false}).pipe(map(res =>{
//     return res || {}
//   }),
//   catchError(this.errorMgmt)
//   )
// }






// }
