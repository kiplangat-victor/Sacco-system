
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ReportsService {
  headers = new HttpHeaders().set('Content-Type', 'application/json');
  baseURL = `${environment.accountsAPI}/accounts`;
  baseURL2 = `${environment.accountsAPI}/loans`;
  getMemberAccounts = "/api/v1/reports/accounts/all/by/member";
  accountsByGroupMembershipReport = "/api/v1/reports/accounts/by/product-and-group";
  accountsByProductReport = "/api/v1/reports/accounts/by/product";
  memberLoanDemandsReport = "/api/v1/reports/loan-demands/member";
  customerTransactionsReport = "/api/v1/reports/transactions/per-customer";
  productLoanDemandsReport = "/api/v1/reports/loan-demands/product";
  analysisproducttransactions = "/api/v1/reports/transactions/per-product";
  memberAccountTransactionsreport = "/api/v1/reports/transactions/customer-account-type/";
  usertransactions = "/api/v1/reports/transactions/user";
  generateSchemeTransactionsReport = "/api/v1/reports/transactions/analyze-accounts-by-scheme";
  generateAllTransactionsAnalysis = "/api/v1/reports/transactions/analyze-allcustomer-accounts";
  accountsBySchemeReport = "/api/v1/reports/accounts/all";
  guarantorshipByLoanReport = "/api/v1/reports/guarantorship/for-one-loan";
  allGuarantorsReport = "/api/v1/reports/guarantorship/all";
  accountsStatementReport = "/api/v1/reports/account-statement";
  getAllMembersReport = "/api/v1/reports/members/all";
  getListOfHousesReport = "/api/v1/reports/members/all/groups";
  balanceSheetReport = "/api/v1/reports/balance-sheet/all";
  getCorporateCustomersReport = "/api/v1/reports/members/all/corporate";
  getMemberOfHouseReport = "/api/v1/reports/members/all/in/group";
  profitAndLossUrl = "/api/v1/reports/profit_loss/all";
  allUsersReport = "/api/v1/reports/users";
  allRolesReport = "/api/v1/reports/roles";
  allExpensesReport = "/api/v1/reports/expenses/all";
  constructor(private http: HttpClient) {}

  // findAccountsLookups(params): Observable<any> {
  //   // const accountsUrl = `http://52.15.152.26:9006/accounts/accounts/by/accounttype/solcode`;

  //   const accountsUrl = `http://52.15.152.26:9006/accounts/accounts/by/accounttype/solcode`;

  //   return this.http.get<any>(accountsUrl, { params });
  // }

  findAccountsLookups(params: any) {
    let API_URL = `${this.baseURL}/accounts/by/accounttype/solcode`;
    return this.http
      .get(API_URL, {
        params: params,
        headers: this.headers,
        withCredentials: false,
      })
      .pipe(
        map((res) => {
          return res || {};
        })
        // catchError(this.errorMgmt)
      );
  }

  saccotheme(): Observable<any> {
    let API_URL =  `${environment.reportsAPI}/api/v1/dynamic/customcss.json`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          console.log(res);
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }
  sacconame(): Observable<any> {
    let API_URL =  `${environment.reportsAPI}/api/v1/dynamic/sacconame`;
    return this.http.get(API_URL, { headers: this.headers, withCredentials: false })
      .pipe(
        map((res) => {
          return res || {}
        }),
        catchError(this.errorMgmt)
      )
  }

  generateAccountStatementReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/account-statement`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Account Statement',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateAccountsStatementTypeReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/account-statement/type`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Account By Scheme Type Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateAllDisbursementsReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/disbursements/all`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'All Disbursements Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateBranchDisbursementsReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/disbursements/branch`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Branch Disbursements Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateUserDisbursementsReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');
    let requestOptions: any = {
      headers: headers,
      params: params,
      responseType: 'blob',
      withCredentials: false,
    };
    let API_URL = `${environment.reportsAPI}/api/v1/reports/disbursements/user`;
    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Manager Disbursements Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateLoanPortfoliosReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/loan-portfolio/all`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Loan Portfolio Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateBranchLoanPortfoliosReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/loan-portfolio/branch`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Branch Loan Portfolio Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateUserLoanPortfoliosReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/loan-portfolio/user`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Manager Loan Portfolio Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateRetailCustomersReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/retail-customers/all`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Retail Customers Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateBranchRetailCustomersReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/retail-customers/branch`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Branch Retail Customers Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateUserRetailCustomersReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/retail-customers/user`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateUserBranchRetailCustomersReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/retail-customers/user/branch`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Retail Customer By Branch manager',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateAllCorporateCustomersReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/group_customers/all`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Cooperate Customers',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateBranchCorporateCustomersReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/group_customers/branch`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Branch Cooperate Customers',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateUserCorporateCustomersReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/corporate-customers/user`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Cooperate Customers By User',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateAllArrearsReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/arrears/all`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Arrears Reports',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateBranchArrearsReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/arrears/branch`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Branch Arrears Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateManagerArrearsReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/arrears/user`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Manager Arrears Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateManagerRepaymentsReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/repayments/user`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Manager Repayments Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateAllRepaymentsReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/repayments/all`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'All Repayments Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateBranchRepaymentsReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/repayments/branch`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Branch Repayments Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateAllTransactions(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/transactions/all`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'All Transactions Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateBranchTransactions(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/transactions/branch`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Branch Trancasctions Reports',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateAllCooperateAccountsByTypeReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/accounts/corporate/all`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Cooperate Accounts By Scheme Type Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateBranchCorporateAccountsByAccountType(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/accounts/corporate/branch`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Branch Cooperate Accounts By Type',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateAllRetailAccountsByAccountType(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/accounts/retail/all`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Retail Accounts Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateAllBranchRetailAccountsByAccountType(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/accounts/retail/branch`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Branch Retail Accounts Type',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateAllLoanDemandsReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/loan-demands/all`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'All Loan Demands Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateBranchLoanDemandsReports(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/loan-demands/branch`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Branch Loan Demands Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateUserLoanDemandsReports(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/loan-demands/user`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'User Loan Demands Reports',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateCapitalAdequacyReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/capital-adequacy`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Capital Adequacy Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateStatementComprehensiveReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/comprehensive-income`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Statement Comphrehensive Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateStatementofFinancialPositionReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/financial-position`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Statement of Financial Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateInvestmentReturnReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/investment-return`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Investment Return Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateConsolidatedDailyLiquidityReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/consolidated-daily-liquidity`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Consolidated Daily Liquidity Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateLiquidityStatementReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/liquidity-statement`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Liquidity Statement Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateBalanceSheet(): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/balance-sheet/all`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Balance Sheet',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  getBranchBalanceSheet(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/balance-sheet/branch`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Branch Balance Sheet',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateDepositReturnReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/deposit-return`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Deposit Return Statement',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateRiskClassificationReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/risk-classification`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Risk Classification Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateAllExpensesReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/expenses/all`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Expenses Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateBranchExpensesReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/expenses/branch`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Branch Expenses Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateFeesReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/fees/all`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Fees Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateBranchFeesReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/fees/branch`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Branch Fees Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateTrialBalanceReport(): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/trial_balance/all`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Trial Balance Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateBranchTrialBalanceReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/trial_balance/branch`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Branch Fees Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateAllActiveDormantAccountsReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/active-dormant-accounts/all`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Accounts By Status Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateBranchActiveDormantAccountsReport(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/active-dormant-accounts/branch`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Accounts By Status Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  loadDynamic(params: any, filename: string): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/dynamic/load`;

    return this.http.get(API_URL, requestOptions).pipe (
      map((response) => {
        return {
          filename: filename,
          data: new Blob([response], { type: 'application/pdf'}),
        };
      })
    );
  }

  loadDynamicXlsx(params: any, filename: string): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/octet-stream');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/dynamic/load/xlsx`;

    return this.http.get(API_URL, requestOptions).pipe (
      map((response) => {
        return {
          filename: filename,
          data: new Blob([response], { type: 'application/octet-stream'}),
        };
      })
    );
  }

  loadDynamicXlsx2(params: any, filename: string): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/octet-stream');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/dynamic/load/xlsx2`;

    return this.http.get(API_URL, requestOptions).pipe (
      map((response) => {
        return {
          filename: filename,
          data: new Blob([response], { type: 'application/octet-stream'}),
        };
      })
    );
  }

  loadDynamicCSV(params: any, filename: string): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'text/csv');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/dynamic/load/csv`;
    console.log(API_URL);
    return this.http.get(API_URL, requestOptions).pipe (
      map((response) => {
        return {
          filename: filename,
          data: new Blob([response], { type: 'text/csv'}),
        };
      })
    );
  }

  errorMgmt(error: HttpErrorResponse) {
    let errorMessage = '';
    if (error.error instanceof ErrorEvent) {
      errorMessage = error.error.message;
    } else if(error.error != null) {
      errorMessage = `${error.error.message}`;
    } else{
      errorMessage = "Unknonw error";
    }
    return throwError(errorMessage);
  }

  dynamicReportGenerator(params: any, url: string, filename: string): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/${url}`;
    if(url.startsWith("/")){
      API_URL = `${environment.reportsAPI}${url}`;
    }
    console.log(API_URL);

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: filename,
          data: new Blob([response], { type: 'application/pdf'}),
        };
      })
    );
  }

  generateAllAccountTypesTransactions(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/transactions/account-type/all`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Accounts By Status Report',
          data: new Blob([response], { type: 'application/pdf'}),
        };
      })
    );
  }

  generateBranchAccountTypesTransactions(params: any): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/account-type-transactions/branch`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Accounts By Status Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateAllCustomerTypesAccountTypesTransactions(
    params: any
  ): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/customer-account-type-transactions/all`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Customer Account Type Transactions Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateProfitAndLossReport(): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/profit_loss/all`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Profit And Loss Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }

  generateMyGuaranteedLoansReport(params): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');

    let requestOptions: any = {
      params: params,
      headers: headers,
      responseType: 'blob',
      withCredentials: false,
    };

    let API_URL = `${environment.reportsAPI}/api/v1/reports/guarantorship/forperson`;

    return this.http.get(API_URL, requestOptions).pipe(
      map((response) => {
        return {
          filename: 'Guarantor Loans Report',
          data: new Blob([response], { type: 'application/pdf' }),
        };
      })
    );
  }
}
