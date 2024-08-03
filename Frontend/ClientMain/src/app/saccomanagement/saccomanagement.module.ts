import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { RouterModule } from "@angular/router";
import { DataTablesModule } from "angular-datatables";
import { HighchartsChartModule } from "highcharts-angular";
import { MatTableExporterModule } from "mat-table-exporter";
import { MaterialModule } from "../material.module";
import { FooterComponent } from "./layouts/footer/footer.component";
import { HeaderComponent } from "./layouts/header/header.component";
import { SideBarComponent } from "./layouts/side-bar/side-bar.component";
import { DashboardComponent } from "./pages/dashboard/dashboard.component";
import { SpinnerComponent } from "./pages/dashboard/spinner/spinner.component";
import { WidgetChargesComponent } from "./pages/dashboard/widget-charges/widget-charges.component";
import { WidgetLendingComponent } from "./pages/dashboard/widget-lending/widget-lending.component";
import { WidgetMembershipComponent } from "./pages/dashboard/widget-membership/widget-membership.component";
import { WidgetShareCapitalComponent } from "./pages/dashboard/widget-share-capital/widget-share-capital.component";
import { EntitybasicactionsComponent } from "./pages/entitybasicactions/entitybasicactions.component";
import { EntitybasicactionsmaintenanceComponent } from "./pages/entitybasicactions/entitybasicactionsmaintenance/entitybasicactionsmaintenance.component";
import { EntitybranchesComponent } from "./pages/entitybranches/entitybranches.component";
import { EntitybrancheslookupComponent } from "./pages/entitybranches/entitybrancheslookup/entitybrancheslookup.component";
import { EntitybranchmaintenanceComponent } from "./pages/entitybranches/entitybranchmaintenance/entitybranchmaintenance.component";
import { EntityrolelookupComponent } from "./pages/entityroles/entityrolelookup/entityrolelookup.component";
import { EntityrolesComponent } from "./pages/entityroles/entityroles.component";
import { EntityrolesmaintenanceComponent } from "./pages/entityroles/entityrolesmaintenance/entityrolesmaintenance.component";
import { EntitytellersComponent } from "./pages/entitytellers/entitytellers.component";
import { EntitytellerslookupComponent } from "./pages/entitytellers/entitytellerslookup/entitytellerslookup.component";
import { EntitytellersmaintenanceComponent } from "./pages/entitytellers/entitytellersmaintenance/entitytellersmaintenance.component";
import { EntityuserComponent } from "./pages/entityuser/entityuser.component";
import { EntityuserlookupComponent } from "./pages/entityuser/entityuserlookup/entityuserlookup.component";
import { EntityusermaintenanceComponent } from "./pages/entityuser/entityusermaintenance/entityusermaintenance.component";
import { EntityworkclassComponent } from "./pages/entityworkclass/entityworkclass.component";
import { EntityworkclasslookupComponent } from "./pages/entityworkclass/entityworkclasslookup/entityworkclasslookup.component";
import { EntityworkclassmaintenanceComponent } from "./pages/entityworkclass/entityworkclassmaintenance/entityworkclassmaintenance.component";
import { SaccoEntityLookupComponent } from "./pages/sacco-entity/sacco-entity-lookup/sacco-entity-lookup.component";
import { SaccoEntityMaintenanceComponent } from "./pages/sacco-entity/sacco-entity-maintenance/sacco-entity-maintenance.component";
import { SaccoEntityComponent } from "./pages/sacco-entity/sacco-entity.component";
import { SaccomanagementRoutingModule } from "./saccomanagement-routing.module";
import { SaccomanagementComponent } from "./saccomanagement.component";


@NgModule({
  declarations: [
    SaccomanagementComponent,
    DashboardComponent,
    SideBarComponent,
    HeaderComponent,
    FooterComponent,
    SaccoEntityComponent,
    SaccoEntityMaintenanceComponent,
    SaccoEntityLookupComponent,
    SpinnerComponent,
    WidgetChargesComponent,
    WidgetLendingComponent,
    WidgetMembershipComponent,
    WidgetShareCapitalComponent,
    EntitybranchesComponent,
    EntitybrancheslookupComponent,
    EntitybranchmaintenanceComponent,
    EntityuserComponent,
    EntityusermaintenanceComponent,
    EntityuserlookupComponent,
    EntityrolesComponent,
    EntityrolesmaintenanceComponent,
    EntityrolelookupComponent,
    EntitytellersComponent,
    EntitytellersmaintenanceComponent,
    EntitytellerslookupComponent,
    EntityworkclassComponent,
    EntitybasicactionsComponent,
    EntitybasicactionsmaintenanceComponent,
    EntityworkclassmaintenanceComponent,
    EntityworkclasslookupComponent
  ],
  imports: [
    CommonModule,
    CommonModule,
    DataTablesModule,
    RouterModule,
    HighchartsChartModule,
    MatTableExporterModule,
    MaterialModule,
    SaccomanagementRoutingModule,
    
    
  ],
  exports: [
    SideBarComponent,
    HeaderComponent,
    FooterComponent
  ]
})
export class SaccomanagementModule { }
