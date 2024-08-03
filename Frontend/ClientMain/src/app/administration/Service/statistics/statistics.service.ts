import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {

  constructor(private http: HttpClient) { }

  fetchCustomerOnboardingDaywiseStatistics(year, monthname): Observable<any>{
    const fetchCustomerOnboardingDaywiseStatisticsUrl = `${environment.crmAPI}/api/v1/customer/retail/statistics/customer-onboarding-statictics-day-wise`;

    return this.http.get<any>(fetchCustomerOnboardingDaywiseStatisticsUrl, { params: {year: year, monthname:monthname }});
  }

  fetchCustomerOnboardingMonthwiseStatistics(year): Observable<any>{
    const fetchCustomerOnboardingMonthwiseStatisticsUrl = `${environment.crmAPI}/api/v1/customer/retail/statistics/customer-onboarding-statictics-month-wise`;

    return this.http.get<any>(fetchCustomerOnboardingMonthwiseStatisticsUrl, { params: { year: year} });
  }

  fetchCustomerOnboardingYearwiseStatistics(): Observable<any>{
    const fetchCustomerOnboardingYearwiseStatisticsUrl = `${environment.crmAPI}/api/v1/customer/retail/statistics/customer-onboarding-statictics-year-wise`;

    return this.http.get<any>(fetchCustomerOnboardingYearwiseStatisticsUrl);
  }

  fetchCustomerOnboardingYearsStatistics(): Observable<any>{
    const fetchCustomerOnboardingYearsStatisticsUrl = `${environment.crmAPI}/api/v1/customer/retail/statistics/customer-onboarding-statictics-years`;

    return this.http.get<any>(fetchCustomerOnboardingYearsStatisticsUrl);
  }

  fetchLoanRepaymentsDaywiseStatistics(year, monthname): Observable<any>{
    const fetchLoanRepaymentsDaywiseStatisticsUrl = `${environment.accountsAPI}/api/repayments/statistics/loan-repayments-statictics-day-wise`;

    return this.http.get<any>(fetchLoanRepaymentsDaywiseStatisticsUrl, { params: {year: year, monthname:monthname }});
  }

  fetchLoanRepaymentsMonthwiseStatistics(year): Observable<any>{
    const fetchLoanRepaymentsMonthwiseStatisticsUrl = `${environment.accountsAPI}/api/repayments/statistics/loan-repayments-statictics-month-wise`;

    return this.http.get<any>(fetchLoanRepaymentsMonthwiseStatisticsUrl, { params: { year: year} });
  }

  fetchLoanRepaymentsYearwiseStatistics(): Observable<any>{
    const fetchLoanRepaymentsYearwiseStatisticsUrl = `${environment.accountsAPI}/api/repayments/statistics/loan-repayments-statictics-year-wise`;

    return this.http.get<any>(fetchLoanRepaymentsYearwiseStatisticsUrl);
  }

  fetchLoanRepaymentsYearsStatistics(): Observable<any>{
    const fetchCustomerOnboardingYearsStatisticsUrl = `${environment.accountsAPI}/api/repayments/statistics/loan-repayments-statictics-years`;

    return this.http.get<any>(fetchCustomerOnboardingYearsStatisticsUrl);
  }

  fetchShareCapitalDaywiseStatistics(year, monthname): Observable<any>{
    const fetchShareCapitalDaywiseStatisticsUrl = `${environment.accountsAPI}/sharecapital/amount/share-capital-statistics-day-wise`;

    return this.http.get<any>(fetchShareCapitalDaywiseStatisticsUrl, { params: {year: year, monthname:monthname }});
  }

  fetchShareCapitalMonthwiseStatistics(year): Observable<any>{
    const fetchShareCapitalMonthwiseStatisticsUrl = `${environment.accountsAPI}/sharecapital/amount/share-capital-statistics-month-wise`;

    return this.http.get<any>(fetchShareCapitalMonthwiseStatisticsUrl, { params: { year: year} });
  }


  fetchShareCapitalYearwiseStatistics(): Observable<any>{
    const fetchShareCapitalYearwiseStatisticsUrl = `${environment.accountsAPI}/sharecapital/amount/share-capital-statistics-year-wise`;

    return this.http.get<any>(fetchShareCapitalYearwiseStatisticsUrl);
  }

  fetchShareCapitalYearsStatistics(): Observable<any>{
    const fetchShareCapitalYearsStatisticsUrl = `${environment.accountsAPI}/sharecapital/amount/share-capital-statistics-years`;

    return this.http.get<any>(fetchShareCapitalYearsStatisticsUrl);
  }

}
