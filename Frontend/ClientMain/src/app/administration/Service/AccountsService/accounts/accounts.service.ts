import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AccountsService {

  

  retrieveCardById(id: any) :Observable<any>{
    let API_URL = `${this.baseURL}/by/id/${id}`;
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
 
  rejectCardApplication(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/reject/account`;
    return this.http
      .put(API_URL, {}, { params: params, withCredentials: false }
      ).pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
 
  activateCard(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/activate`;
    return this.http
      .put(API_URL, {}, { params: params, withCredentials: false }
      ).pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  updateCards(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/update`;
    return this.http.put(API_URL, data, { headers: this.headers, withCredentials: false }).pipe(
      map(res => {
        return res || {};
      })
    );
  }
  temporaryDeleteCard(params: any) {
    let API_URL = `${this.baseURL}/temporary/delete`;
    return this.http
      .put(API_URL, {}, { params: params, withCredentials: false }
      ).pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }

   private headers = new HttpHeaders().set('Content-Type', 'application/json');
  baseURL = `${environment.accountsAPI}/accounts`;
   private apiService =`${environment.productAPI}/api/v1/atmCard`;
  

  constructor(private http: HttpClient) { }

  
  addCard(data: any): Observable<any> {
    const url = `${this.apiService}/add`;
    return this.http.post(url, data, { headers: this.headers, withCredentials: false }).pipe(
       map(res => res || {}),
       catchError(this.errorMgmt)
    );
   }
   

   modifyCard( data: any): Observable<any> {
    const url = `${this.apiService}/modify`;
    return this.http.put(url, data, { headers: this.headers, withCredentials: false }).pipe(
       map(res => res || {}),
       catchError(this.errorMgmt)
    );
   }

   verifyCard(data: any): Observable<any> {
    const url = `${this.apiService}/verify`;
    return this.http.post(url, data, { headers: this.headers, withCredentials: false }).pipe(
       map(res => res || {}),
       catchError(this.errorMgmt)
    );
   }

   deleteCard(id: number): Observable<any> {
    const url = `${this.apiService}/delete/${id}`;
    return this.http.delete(url, { headers: this.headers, withCredentials: false }).pipe(
       map(res => res || {}),
       catchError(this.errorMgmt)
    );
   }

   getCards(data: any): Observable<any> {
    const url = `${this.apiService}/getAtms`;
    return this.http.get(url, { headers: this.headers, withCredentials: false }).pipe(
       map(res => res || {}),
       catchError(this.errorMgmt)
    );
   }

   getCardTypes():Observable<any>{
    const url =  `${this.apiService}/getAtmCardType/cardType`;
    return this.http.get(url,{headers:this.headers, withCredentials:false}).pipe(
      map(res => res || {}),
      catchError(this.errorMgmt)
    )
   }


  createAccount(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/open`;
    return this.http.post(API_URL, data, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }


  repayLoan(data: any): Observable<any> {
    let API_URL = `${environment.accountsAPI}/loans/prepay`;
    return this.http.put(API_URL, data, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  read(): Observable<any> {
    let API_URL = `${this.baseURL}/all`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  getUserAccountsApproval(): Observable<any> {
    let API_URL = `${this.baseURL}/user/accounts/approval/list`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      );
  }

  
  getUnVerifiedAccounts(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/accounts/approvallist`;
    return this.http.get(API_URL, { params: params, headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      );
  }



  getUnVerifiedLoans(): Observable<any> {
    let API_URL = `${this.baseURL}/accounts/approvallist/loans`;
    return this.http.get(API_URL, {headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      );
  }


  getUnVerifiedNonLoanAccounts(): Observable<any> {
    let API_URL = `${this.baseURL}/accounts/approvallist/nonloan`;
    return this.http.get(API_URL, {headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      );
  }
  approval(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/request/acid/approval`;
    return this.http.put(API_URL, {}, { params: params, headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  getTotalAccounts(): Observable<any> {
    let API_URL = `${this.baseURL}/total/accounts/count`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  getRepaymentAccounts(): Observable<any> {
    let API_URL = `${this.baseURL}/get/all/repayment/accounts/look/up`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(map((res) => { return res || {} }),
        catchError(this.errorMgmt)
      )
  }
  updateAccounts(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/update`
    return this.http.put(API_URL, data, { headers: this.headers, withCredentials: false }).pipe(
      map(res => {
        return res || {}
      })
    )
  }
  getAccountsPerType(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/by/account/type`;
    return this.http
      .get(API_URL, { params: params, withCredentials: false }
      ) .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  getOfficeAccountsPerGl(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/by/account/type`;
    return this.http
      .get(API_URL, { params: params, withCredentials: false }
      ).pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  getTellerAccounts(): Observable<any> {
    let API_URL = `${this.baseURL}/find/tellers/account/`;
    return this.http
      .get(API_URL,
      ).pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }

  getAccounts(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/get/accounts/details/for/lookup/by/account/type`
    return this.http.get(API_URL, {params: params, headers: this.headers, withCredentials: false }).pipe(
      map(res => {
        return res || {}
      })
    )
  }
  rejectAccount(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/reject/account`;
    return this.http
      .put(API_URL, {}, { params: params, withCredentials: false }
      ).pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  verifyAccount(params: any): Observable<any>{
    let API_URL = `${this.baseURL}/verify`;
    return this.http
      .put(API_URL, {}, { params: params, withCredentials: false }
      ) .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  activateAccount(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/activate`;
    return this.http
      .put(API_URL, {}, { params: params, withCredentials: false }
      ).pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  temporaryDeleteAccount(params: any) {
    let API_URL = `${this.baseURL}/temporary/delete`;
    return this.http
      .put(API_URL, {}, { params: params, withCredentials: false }
      ) .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  payTermDeposit(params: any): Observable<any>{
    let API_URL = `${this.baseURL}/term-deposit/pay/term-deposit`;
    return this.http
      .put(API_URL, {}, { params: params, withCredentials: false }
      ) .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  retrieveAccount(acid: any): Observable<any> {
    let API_URL = `${this.baseURL}/${acid}`
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  retrieveAccountById(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/by/id/${id}`
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  retrieveAccountPerCustomerCode(customerCode: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/account/by/customer/code/${customerCode}`
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  retrieveOperativeAccountPerCustomerCode(customerCode: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/operative/accounts/by/customer/code/${customerCode}`
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  retrieveAccountPerCustomerId(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/find/account/by/customer/code/${id}`
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  getGeneralAccounts(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/accounts/look-up/one`;
    return this.http
      .get(API_URL, { params: params, withCredentials: false }).pipe(map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  findAccountDetails(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/accounts/general/accounts/look/up`;
    console.log("URL", API_URL);

    return this.http
      .get(API_URL, { params: params, withCredentials: false }).pipe(map((res) => {
        return res || {};
      }),
        catchError(this.errorMgmt)
      );
  }
  findSavingsContributionDetails(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/accounts/general/accounts/look/up`;
    console.log("URL", API_URL);

    return this.http
      .get(API_URL, { params: params, withCredentials: false }).pipe(map((res) => {
        return res || {};
      }),
        catchError(this.errorMgmt)
      );
  }
  sendOtp(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/send/collateral/verification/otp`;
    return this.http
      .put(API_URL, {}, { params: params, withCredentials: false }
      ).pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  verifyOtp(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/verify/collateral/otp`;
    return this.http
      .put(API_URL, {}, { params: params, withCredentials: false }
      ).pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  documents(sn: any): Observable<any> {
    let API_URL = `${this.baseURL}/get/account/images/account_id_fk/${sn}`
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => {
        return res || {}
      }
    ))
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
