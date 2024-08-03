import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class BanksService {

  constructor() { }
  banks() {
    return [
      {
        "code": '01',
        "name": "POSTBANK UGANDA LIMITED",
        "branches": [
            {
                branchCode: 'UGPBUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '02',
        "name": "ABC CAPITAL BANK LIMITED",
        "branches": [
            {
                branchCode: 'ABCFUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '03',
        "name": "ABSA BANK UGANDA LIMITED",
        "branches": [
            {
                branchCode: 'BARCUGKX',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '04',
        "name": "AFRILAND FIRST BANK UGANDA LIMITED",
        "branches": [
            {
                branchCode: 'CCEIUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '05',
        "name": "BANK OF AFRICA-UGANDA LTD.",
        "branches": [
            {
                branchCode: 'AFRIUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '06',
        "name": "BANK OF BARODA (UGANDA) LIMITED",
        "branches": [
            {
                branchCode: 'BARBUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '07',
        "name": "BANK OF INDIA (UGANDA) LTD",
        "branches": [
            {
                branchCode: 'BKIDUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '08',
        "name": "BANK OF UGANDA",
        "branches": [
            {
                branchCode: 'UGBAUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '09',
        "name": "BRAC UGANDA BANK LTD",
        "branches": [
            {
                branchCode: 'BRUGUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '10',
        "name": "CAIRO BANK UGANDA",
        "branches": [
            {
                branchCode: 'CAIEUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '11',
        "name": "CENTENARY RURAL DEVELOPMENT BANK LIMITED",
        "branches": [
            {
                branchCode: 'CERBUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '12',
        "name": "CITIBANK UGANDA LIMITED",
        "branches": [
            {
                branchCode: 'CITIUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '13',
        "name": "CREDIT FONCIER LIMITED",
        "branches": [
            {
                branchCode: 'CDFOUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '14',
        "name": "DFCU BANK LIMITED",
        "branches": [
            {
                branchCode: 'DFCUUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '15',
        "name": "DIAMOND TRUST BANK UGANDA LIMITED",
        "branches": [
            {
                branchCode: 'DTKEUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '16',
        "name": "EAST AFRICAN DEVELOPMENT BANK",
        "branches": [
            {
                branchCode: 'AFDEUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '17',
        "name": "ECOBANK UGANDA",
        "branches": [
            {
                branchCode: 'ECOCUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '18',
        "name": "EQUITY BANK UGANDA LTD",
        "branches": [
            {
                branchCode: 'EQBLUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '19',
        "name": "EXIM BANK (UGANDA) LIMITED",
        "branches": [
            {
                branchCode: 'EXTNUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '20',
        "name": "FINANCE TRUST BANK LTD",
        "branches": [
            {
                branchCode: 'FTBLUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '21',
        "name": "GUARANTY TRUST BANK (UGANDA) LTD",
        "branches": [
            {
                branchCode: 'GTBIUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '22',
        "name": "HOUSING FINANCE BANK LTD.",
        "branches": [
            {
                branchCode: 'HFINUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '23',
        "name": "KCB BANK UGANDA LIMITED",
        "branches": [
            {
                branchCode: 'KCBLUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '24',
        "name": "MERCANTILE CREDIT BANK LTD",
        "branches": [
            {
                branchCode: 'MCBDUGKB',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '25',
        "name": "NCBA BANK UGANDA LIMITED",
        "branches": [
            {
                branchCode: 'CBAFUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '26',
        "name": "OPPORTUNITY BANK",
        "branches": [
            {
                branchCode: 'OPUGUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '27',
        "name": "ORIENT BANK LIMITED",
        "branches": [
            {
                branchCode: 'ORINUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '28',
        "name": "STANBIC BANK UGANDA LIMITED",
        "branches": [
            {
                branchCode: 'SBICUGKX',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '29',
        "name": "STANDARD CHARTERED BANK UGANDA LIMITED",
        "branches": [
            {
                branchCode: 'SCBLUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '30',
        "name": "TOP FINANCE BANK LIMITED",
        "branches": [
            {
                branchCode: 'TOPFUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '31',
        "name": "TROPICAL BANK LTD",
        "branches": [
            {
                branchCode: 'TROAUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '32',
        "name": "UGANDA SECURITIES EXCHANGE LIMITED",
        "branches": [
            {
                branchCode: 'UGSXUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '33',
        "name": "UNITED BANK FOR AFRICA (UGANDA) LTD",
        "branches": [
            {
                branchCode: ' UNAFUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    {
        "code": '34',
        "name": "UNITED BANK FOR AFRICA (UGANDA) LTD",
        "branches": [
            {
                branchCode: 'UNAFUGKA',
                branchName: "Head office branch"
            },          
                         
        ]
    },
    ]
  }
}
