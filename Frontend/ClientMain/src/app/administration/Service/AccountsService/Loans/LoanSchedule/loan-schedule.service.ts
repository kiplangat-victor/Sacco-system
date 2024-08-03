import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class LoanScheduleService {
  headers = new HttpHeaders().set('Content-Type', 'application/json');
  baseURL = `${environment.accountsAPI}/loans`;
  constructor(private http: HttpClient) { }

  open(data: any): Observable<any> {
    let API_URL = `${this.baseURL}/open`;
    return this.http.post(API_URL, data, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  retrieveAccrual(acid: any): Observable<any> {
    let API_URL = `${this.baseURL}/accrual/info/per/acid/${acid}`
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => {
        return res || {}
      }
    ))
  }
  retrieveDemands(acid: any): Observable<any> {
    let API_URL = `${this.baseURL}/demands/info/per/acid/${acid}`
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => {
        return res || {}
      }
    ))
  }
  retrieveAllDemands(acid: any): Observable<any> {
    let API_URL = `${this.baseURL}/demands/info/per/acid/${acid}`
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => {
        return res || {}
      }
    ))
  }
  retrieveBokings(acid: any): Observable<any> {
    let API_URL = `${this.baseURL}/booking/info/per/acid/${acid}`
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => {
        return res || {}
      }
    ))
  }
  loanInterest(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/loan/schedules`;
    return this.http.post(API_URL, {}, { params: params, }).pipe(map((res) => { return res || {}; }),
      catchError(this.errorMgmt)
    );
  }
  forceAccrual(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/accrual/force`;
    return this.http.put(API_URL, {}, { params, }).pipe(map((res) => { return res || {}; }),
      catchError(this.errorMgmt)
    );
  }
  accrueAll(): Observable<any> {
    let API_URL = `${this.baseURL}/accrual/all`;
    return this.http.put(API_URL, {}, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  notAccruedEvents(date: any): Observable<any> {
    let API_URL = `${this.baseURL}/accrual/accounts/not/accrued/loans/on/date/${date}`
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => { return res || {} }
    ), catchError(this.errorMgmt))
  }
  totalAccrual(date: any): Observable<any> {
    let API_URL = `${this.baseURL}/accrual/count/accrued/loans/on/date/${date}`
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => { return res || {} }
    ), catchError(this.errorMgmt))
  }
  forceLoanBooking(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/book/force`;
    return this.http.put(API_URL, {}, { params, }).pipe(map((res) => { return res || {}; }),
      catchError(this.errorMgmt));
  }
  bookAll(): Observable<any> {
    let API_URL = `${this.baseURL}/book/all`;
    return this.http.put(API_URL, {}, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  forceBookings(acid: any): Observable<any> {
    let API_URL = `${this.baseURL}/book/force/${acid}`;
    return this.http.post(API_URL, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  notBookedEvents(date: any): Observable<any> {
    let API_URL = `${this.baseURL}/booking/accounts/not/booked/loans/on/date/${date}`
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => { return res || {} }
    ), catchError(this.errorMgmt))
  }
  totalBookings(date: any): Observable<any> {
    let API_URL = `${this.baseURL}/booking/count/booked/loans/on/date/${date}`
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => { return res || {} }
    ), catchError(this.errorMgmt))
  }
  fixInterestBalance(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/fixed/rate/loan/schedules`;
    return this.http.post(API_URL, {}, { params: params, headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  MassiveIntrest(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/massive/demandreversals`;
    return this.http.put(API_URL, {}, { params: params, }).pipe(map((res) => { 
      console.log(res);
      return res || {}; }),
      catchError(this.errorMgmt)
    );
  }
  reducingInterestBalance(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/reducing/balance/loan/schedules`;
    return this.http.post(API_URL, {}, { params: params, headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  loanDemands(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/demand/force`;
    return this.http.put(API_URL, {}, { params, }).pipe(map((res) => { return res || {}; }),
      catchError(this.errorMgmt));
  }

  demandAllLoans(): Observable<any> {
    let API_URL = `${this.baseURL}/demand/all`;
    return this.http.put(API_URL, {}, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  satisfyDemands(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/satisfy/demand/force`;
    return this.http.put(API_URL, {}, { params, }
    ).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  pauseDemandGeneration(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/pause/demands`;
    return this.http.put(API_URL, {}, { params, }
    ).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  unpauseDemandGeneration(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/unpause/demands`;
    return this.http.put(API_URL, {}, { params, }
    ).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  unpauseDemandSatisfaction(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/unpause/demands`;
    return this.http.put(API_URL, {}, { params, }
    ).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  pauseDemandSatisfaction(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/pause/demandsatisfaction`;
    return this.http.put(API_URL, {}, { params, }
    ).pipe(
      map((res) => {
        return res || {};
      }),
      catchError(this.errorMgmt)
    );
  }
  satisfyAllDemands(): Observable<any> {
    let API_URL = `${this.baseURL}/satisfy/demand/all`;
    return this.http.put(API_URL, {}, { headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  notDemandedEvents(date: any): Observable<any> {
    let API_URL = `${this.baseURL}/demands/accounts/not/demanded/on/${date}`
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => { return res || {} }
    ), catchError(this.errorMgmt))
  }
  totalDemanded(date: any): Observable<any> {
    let API_URL = `${this.baseURL}/demands/count/demanded/loans/on/date/${date}`
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => { return res || {} }
    ), catchError(this.errorMgmt))
  }
  unsatisfiedDemands(): Observable<any> {
    let API_URL = `${this.baseURL}/demands/get/accounts/of/demands/generated/but/not/satisfied/on`
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => { return res || {} }
    ), catchError(this.errorMgmt))
  }
  disburseLoans(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/disburse`;
    return this.http
      .put(API_URL, {}, { params, }
      ).pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }

  verifyLoanDisbursement(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/verify/loan/disbursment`;
    return this.http
      .put(API_URL, {}, { params, }
      ).pipe(
        map((res) => {
          return res || {};
        }),
        catchError(this.errorMgmt)
      );
  }
  getLoanFees(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/prodItem/fees`;
    return this.http.get(API_URL, { params: params, headers: this.headers, withCredentials: false }).pipe(map(res => {
      return res || {}
    }),
      catchError(this.errorMgmt)
    )
  }
  demandEvent(date: any): Observable<any> {
    let API_URL = `${this.baseURL}/demands/satisfied/on/date/${date}`
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => {
        return res || {}
      }
    ))
  }
  generatedEvent(date: any): Observable<any> {
    let API_URL = `${this.baseURL}/demands/generated/on/date/${date}`
    return this.http.get(API_URL, { withCredentials: false }).pipe(map(
      res => {
        return res || {}
      }
    ))
  }
  loanRestructure(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/restructure/loan`;
    return this.http.put(API_URL, {}, { params, }).pipe(map((res) => { return res || {}; }),
      catchError(this.errorMgmt));
  }
  retrieveDisursment(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/get/loan/disbursment/info/per/acid`;
    return this.http.get(API_URL, { params: params }).pipe(map((res) => { return res || {}; }),
      catchError(this.errorMgmt));
  }
  validateGuarantor(params: any): Observable<any> {
    let API_URL = `${this.baseURL}/guarantor/available/amt`;
    return this.http.get(API_URL, { params: params, headers: this.headers, withCredentials: false }).pipe(map(res => {
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
