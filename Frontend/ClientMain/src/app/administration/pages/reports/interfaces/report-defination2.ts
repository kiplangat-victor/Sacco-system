import {Parameter} from '../interfaces/parameter'
export interface ReportDefination2 {
  sn?: number
  reportName?: string
  rcre?: Date
  query?: string
  postedBy?:string
  postedTime?: Date
  modifiedBy?: string
  modifiedTime?: Date
  jrxmlName?: string
  parameterList?: Parameter[]
}
