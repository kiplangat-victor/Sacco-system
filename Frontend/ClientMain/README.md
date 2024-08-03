# Clientside

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 12.0.0.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via a platform of your choice.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI Overview and Command Reference](https://angular.io/cli) page.

<!-- dependencies utilised -->
`ng add @angular/material`

`npm install jquery --save`
`npm install datatables.net --save`
`npm install datatables.net-dt --save`
`npm install angular-datatables --save`
`npm install @types/jquery --save-dev`
`npm install @types/datatables.net --save-dev`

`npm i @angular/flex-layout`

subcribe( data =>{
  data.token
})

`mock server`
generate mock data `npm run generate`

run server `npm run server`










**************************Core Functionalities * ******************************************************************


Perfomance Management Modules

Steps
1. Task Assignment
   1.2 Perfomance Paramenters
	in this phase an employer will assign an employee a task. This will provide a detailed captured description and documentation of the work to be done by the employee.
	Things to capture.
	1. Project Name, Project component, start date, end date.
	Assign the checkins that will be used to review the perfomance.
	The system should sent an email to employee informing them that a job has been assigned to them.
	
	Team work?
	The system should facilitate the parameters that defines wheather the project is individual based or a team work project. 
	How will the evaluation be done on a team work based project?
	The perfomance will be done on > team leader which laterone the team participants will received their perfomance in their personal score card .
	The system ofcouse will always have the totals of each task.
2. Goal Setting
	This is where employee acknowledge that they have received the task.
	They acknoledgement will act as an approval that they have accepted to work with the timline.
	
	
	The employee is expected to create their deliverables time schedule on what they will deliver with dates.
3. Project Commitment
	This is where an employer will comit that the project has been assigned to a particular employee and update the task status as progressing.
	Basing on the deliverables, the system will auto generate phases which correspond to delivery. This phases will be used as a tracking for the perfomance	
3. Review and Continiouse Evaluation
	THis is where the employeer, particularly supervisor will review the perfomance of the employee with regards to task assigned.
	The review will use the predefined perfomance parameters.
	
	The employee should regularly update the project perfomance basing on the individula time based.
	
	For the case of overdues, the system should alert the employee with warnings that their project are about to overdue,
4. Recognition
	This is where an employee will raised the regognition of the employee and team who are working best.
5. Reports
6. Annual Review and Reports.
# Modular-Sacco-Front-End
# EMT_002_Sacco_Clientside
