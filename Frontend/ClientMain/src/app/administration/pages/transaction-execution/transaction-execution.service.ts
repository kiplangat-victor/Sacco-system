import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
  HttpParams,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class TransactionExecutionService {

  headers = new HttpHeaders().set('Content-Type', 'application/json');
  private baseURL: string = `${environment.accountsAPI}/transactions`;
  private baseURL2: string = `${environment.accountsAPI}/api/v1/transaction`;
  private baseImgsURL: string = `${environment.accountsAPI}`;
  private baseRecieptURL: string = `${environment.reportsAPI}`;
  private receiptURL: any =`${environment.transactionReceiptAPI}`

  constructor(private http: HttpClient) { }
  enter(data: any): Observable<any> {
    console.log("Enter");
    console.log(data);
    let API_URL = `${this.baseURL2}/enter`;
    return this.http.post(API_URL, data, { headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  verify(params: any): Observable<any> {
    let API_URL = `${this.baseURL2}/verify`;
    return this.http.put(API_URL, {}, { params: params, headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  post(params: any): Observable<any> {
    let API_URL = `${this.baseURL2}/post`;
    return this.http.put(API_URL, {}, { params: params, headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  reverse(params: any): Observable<any> {
    let API_URL = `${this.baseURL2}/reverse`;
    return this.http.put(API_URL, {}, { params: params, headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  systemReversal(params: any): Observable<any> {
    let API_URL = `${this.baseURL2}/system/reverse`;
    return this.http.put(API_URL, {}, { params: params, headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  } 
  rejectTransaction(params: any): Observable<any> {
    let API_URL = `${this.baseURL2}/rejectTransaction`;
    return this.http.put(API_URL, {}, { params: params, headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  approval(params: any): Observable<any> {
    let API_URL = `${this.baseURL2}/request-approval`;
    return this.http.put(API_URL, {}, { params: params, headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  getFilteredTransactions(params: any): Observable<any> {
    let API_URL = `${this.baseURL2}/all/filter`;
    return this.http.get(API_URL, { params: params, headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  getApprovalList(): Observable<any> {
    let API_URL = `${this.baseURL2}/all/approvelist`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  getTotalTransactions(): Observable<any> {
    let API_URL = `${this.baseURL2}/total/transactions/count`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  getRoughApprovalList(): Observable<any> {
    let API_URL = `${environment.accountsAPI}/accounts/batchsalarycheque`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }

  getUnclosedCheques(): Observable<any> {
    let API_URL = `${environment.accountsAPI}/api/v1/chequeprocessing/unposted`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  // Add
  createTransaction(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/enter`;
    return this.http.post(API_URL, data, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }

  verifyTransaction(transactionCode: any): Observable<any> {
    let API_URL = `${this.baseURL}/verify/${transactionCode}`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  // /transactions/post/{transactionCode}
  postTransaction(transactionCode: any): Observable<any> {
    let API_URL = `${this.baseURL}/post/${transactionCode}`;
    return this.http.post(API_URL, { headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }

  updateTransaction(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/update`;
    console.log('server data', data);
    return this.http
      .put(API_URL, data, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }

  getTransactions(params: any) {
    let API_URL = `${this.baseURL}/transactions`;
    return this.http
      .get(API_URL, { headers: this.headers, withCredentials: false, params: params })
      .pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }


  getAccountDetails(accountNumber: any): Observable<any> {
    let API_URL = `${this.baseURL}/acount_balance/${accountNumber}`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  getTransactionByCode(transactionCode: any): Observable<any> {
    let API_URL = `${this.baseURL}/${transactionCode}`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }

  getAcknowledgement(params: any): Observable<any> {
    let API_URL = `${this.baseURL2}/acknowledge`;
    return this.http.put(API_URL, {}, {params: params, headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  getAccountImagesByCode(accountNumber: any): Observable<any> {
    let API_URL = `${this.baseImgsURL}/accounts/accounts/documents/by/acid/${accountNumber}`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }


  getTransactionId(id: any): Observable<any> {
    let API_URL = `${this.baseURL}/by/${id}`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  cancelTransactionById(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/cancel`;
    return this.http.delete(API_URL, { headers: this.headers, withCredentials: false, params: params }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }

  deleteTransactionById(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/delete`;
    return this.http.delete(API_URL, { headers: this.headers, withCredentials: false, params: params }).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }

  generateRecieptReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append("Accept", "application/pdf");
    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: "blob",
      withCredentials: false,
    };
    let API_URL = `${this.baseRecieptURL}/api/v1/reports/cash-receipt`;
    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: "Report",
          data: new Blob([response], { type: "application/pdf" }),
        };
      })
    );
  }
  generateDepositReciept(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append("Accept", "application/pdf");
    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: "blob",
      withCredentials: false,
    };

    let API_URL = `${this.receiptURL}/cashdeposit`
    // let API_URL=  'http://52.15.152.26:9006/api/v1/transaction/receipts/cashdeposit'
    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: "cash-deposit-Receipt",
          data: new Blob([response], { type: "application/pdf" }),
        };
      })
    );
  }
  generateWithdrawalReciept(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append("Accept", "application/pdf");
    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: "blob",
      withCredentials: false,
    };
    // let API_URL = 'http://52.15.152.26:9006/api/v1/transaction/receipts/withdrawal'

    let API_URL = `${this.receiptURL}/withdrawal`
    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: "cash-Withdwal-Receipt",
          data: new Blob([response], { type: "application/pdf" }),
        };
      })
    );
  }
  generateTranRecieptReport(params: HttpParams) {
    let headers = new HttpHeaders();
    headers.append("Accept", "application/pdf");
    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: "blob",
      withCredentials: false,
    };
    let API_URL = `${this.baseRecieptURL}/api/v1/reports/tran-receipt`;
    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        console.log("Hey this is status file", response);
        return {
          filename: "Report",
          data: new Blob([response], { type: "application/pdf" }),
        };
      })
    );
  }

  fetchtransaction(params) {
    const fetchtransactionUrl = `${environment.accountsAPI}/transactions/transactions/by/account/and/year`;
    return this.http.get<any[]>(fetchtransactionUrl, { params })
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
