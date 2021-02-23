<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li>
      <a href="#build-and-run">Build And Run</a>
      <ul>
        <li><a href="#build-with-maven">Build With Maven</a></li>
        <li><a href="#run-with-maven">Run With Maven</a></li>
        <li><a href="#run-using-jar">Run Using Jar</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <ul>
      <li><a href="#embeded-database">Embeded Database</a></li>
      <li><a href="#ddl">DDL</a></li>
      <li><a href="#local-machine-hosting">Local Machine Hosting</a></li>
      <li><a href="#software-for-testing">Software for testing</a></li>
      <li><a href="#webservice">Webservice</a></li>
      <ul>
        <li><a href="#upload-api">Upload Api</a></li>
        <li><a href="#fetch-api">Fetch API</a></li>
        <li><a href="#get-api">Get API</a></li>
        <li><a href="#create-api">Create API</a></li>
        <li><a href="#update-api">Update API</a></li>
        <li><a href="#delete-api">Delete API</a></li>
      </ul>
    </ul>
    <li><a href="#assumption">Assumption</a></li>

  </ol>
</details>

## Getting Started

### Prerequisites

Required JDK version

* jdk 1.8
```
  jdk1.8
```

* java home point to jdk
```sh
SET JAVA_HOME
```

Required maven version,

* maven 3
```
  maven 3
```

or use maven wrapper included

{projectDir} refers to source code root path
* maven wrapper windows
```sh
{projectDir}\mvnw.cmd
```

* maven wrapper linux
```sh
{projectDir}\mvnw
```


### Installation

1. Clone the repo

  ```sh
   git clone https://github.com/pureiboi/nphcswe.git
  ```
2. Download dependency with maven under <a href="#build-and-run">Build and Run</a>

## Build And Run

### Build With Maven

Build using maven wrapper, under working directory
* mvn wrapper (Windows) - command to build
```sh
  mvnw.cmd clean package
```

* mvn wrapper (linux) - command to build
```sh
  mvnw clean package
```

Maven build tool installed on local machine
* maven - command to build
  ```sh
  mvn clean package
  ```

### Run With Maven

* command to start application
```sh
mvn spring-boot:run
```

### Run using Jar

Project is required to be built to generate jar file @ <a href="#build-with-maven">Build With Maven</a>

{projectDir} refers to source code root path

* command to start application
```sh
java -jar {projectDir}\nphcswe-0.0.1-SNAPSHOT.jar
```


## Usage

### Embeded Database

http://localhost:8080/h2-console

user | password
------------ | -------------
sa |  

### DDL

DDL is stored and auto executed uppon application stats up
```
{projectDir}\src\main\resources\schema.sql
```

#### TB_USER

Information stored for user, with audit information

Column | Data Type | Length | Remark
-|-|-| -
ID | string | 100 | - Primary Key <br/> - Unique
LOGIN | string | 100 | - Unique <br/> - Not Null
NAME | string  | 255 | - Not Null
SALARY | number | 2 decimal place | - Not Null
START_DATE | date | | - Not Null <br/> - supported format: yyyy-MM-dd, dd-MMM-yy
VERSION | number | |
CREATED_BY | string | 255 |
CREATED_DATE | timestamp | |
UPDATED_BY | string | 255 | |
UPDATED_DATE | timestamp | |

#### TB_USER_REV

Record versioning for User

Column | Data Type | Length | Remark
-|-|-| -
REVISION_ID | integer  | | - Primary Key <br/>  - Not Null
REVISION_TYPE | integer  |  | - 0 = insert  <br/>  - 1 = update  <br/>  - 2 = delete <br/>  - Not Null
ID | string  |  100 | - Primary Key <br/>  - Not Null
LOGIN | string |  100  |  Not Null
NAME  | string |  255  | Not Null
SALARY  | number | 2 decimal |  Not Null
START_DATE  | date  | Not Null
CREATED_BY  | string | 255 |
CREATED_DATE  | timestamp |
UPDATED_BY  | string | 255|
UPDATED_DATE  | timestamp |


#### REV_INFO

Revision changes with time stamp info

Column | Data Type | Length | Remark
-|-|-| -
REVISION_ID | integer | | - Primary Key
REV_TIMESTAMP | integer | | - time stamp in number for revision info updated

### Local Machine Hosting

http://localhost:8080


### Software for testing

[Postman https://www.postman.com/](https://www.postman.com/)


### Webservice

Available end points

Name | URL | Request method | Response Type
------------ | ------------ | ------------- | -------------
 Upload API | /users/upload | POST | application/json
 Fetch API | /users | GET | application/json
 Get API | /users/{$id} | GET | application/json
 Create API | /users/ | POST | application/json
 Update API | /users/ | PUT/PATCH | application/json
 Delete API | /users/ | DELEE | application/json

### Upload API

```
POST http://localhost:8080/users/upload
```

Parameter Name| Data Type | Type | Remark
------------ | ------------- | - | -------------
file | multipart/form-data | Request Parameter | <ul><li>required</li><li>max 10mb</li><li>comma delimited</li><li>first row is header</li><li>supported column: id,login,name,salary,startDate</li><li>all columns are mandatory</li><li>id and login to be unique</li><li>startDate - supported format: yyyy-MM-dd, dd-MMM-yy</li></ul>

### Fetch API

```
GET http://localhost:8080/users
```

Parameter Name| Data Type | Type| Remark
------------ | ------------- | - |-------------
minSalary | decimal | request parameter | <ul><li>optional</li><li>default value when not provided: 0</li><li>value inclusive</li><li>parameter is case sensitive, not applied when wrong case</li></ul>
maxSalary | decimal | request parameter | <ul><li>optional</li><li>default value when not provided: 4000</li><li>value not inclusive</li><li>parameter is case sensitive, not applied when wrong case</li></ul>
offset | integer | request parameter | <ul><li>optional</li><li>default value when not provided: 0</li><li>parameter is case sensitive, not applied when wrong case</li></ul>
limit | integer | request parameter | <ul><li>optional</li><li>default value when not provided: no limit</li><li>parameter is case sensitive, not applied when wrong case</li><li>max records 2147483647, integer max value</li></ul>
startDateFrom | string | request parameter | <ul><li>optional</li><li>value inclusive</li><li>accepted format: yyyy-mm-dd, dd-MMM-yy</li><li>parameter is case sensitive, not applied when wrong case</li></ul>
startDateTo | string | request parameter | <ul><li>optional</li><li>value inclusive</li><li>accepted format: yyyy-mm-dd, dd-MMM-yy</li><li>parameter is case sensitive, not applied when wrong case</li></ul>
id | string | request parameter | <ul><li>optional</li><li>filter value is case insensitive</li><li>wild card match by default</li><li>custom wild card value "#" <br/> i.e #search , search# </li><li>parameter is case sensitive, not applied when wrong case</li></ul>
login | string | request parameter | <ul><li>optional</li><li>filter value is case insensitive</li><li>wild card match by default</li><li>custom wild card value "#" <br/> i.e #search , search# </li><li>parameter is case sensitive, not applied when wrong case</li></ul>
name | string | request parameter | <ul><li>optional</li><li>filter value is case insensitive</li><li>wild card match by default</li><li>custom wild card value "#" <br/> i.e #search , search# </li><li>parameter is case sensitive, not applied when wrong case</li></ul>
sort | string | request parameter | <ul><li>optional</li><li>comma separated</li><li>support single column sort</li><li>support coloumn: id, login, name, salary, startDate</li><li>column name case insensitive</li><li>format: column,sort order<br/>i.e id,asc</li><li>sort order: asc or desc</li><li>default when sort order not provided: asc</li><li>ignore when column is not valid</li><li>parameter is case sensitive, not applied when wrong case</li></ul>

### Get API

```
GET http://localhost:8080/users/{$id}
```
Parameter Name| Data Type | Type| Remark
------------ | ------------- | - |-------------
{$id} | string | Path Variable | Required


#### Response
Content | Data Type | Type| Remark
------------ | ------------- | - |-------------
{ <br/>"id": "e0001", <br/>"name": "Harry Potter", <br/>"login": "hpotter", <br/>"salary": 1234.00, <br/>"startDate": "2001-11-16" <br/>}  | application/json | Response Body | http status : <li>200</li>
{"message": "No such employee"}   | application/json | Response Body | http status : <li>400</li>

### Create API

```
POST http://localhost:8080/users/
```

Parameter Name| Data Type | Type| Remark
------------ | ------------- | - |-------------
{ "id": "e0001", <br/> "name": "Harry Potter",  <br/> "login": "pharry",  <br/> "salary": 399,  <br/> "startDate": "2020-01-10"  <br/> } | string | Request Body | Required


#### Response
Content | Data Type | Type| Remark
------------ | ------------- | ------------- |-------------
{"message": "Successfully created"} | application/json | Response Body | http status : <li>201</li>
{"message": "Employee ID already exists"} <br/> {"message": "Employee login not unique"} <br/> {"message": "Invalid salary"} <br/> {"message": "Invalid date"} <br/>  | application/json | Response Body | http status : <li>400</li>


### Update API

```
PUT/PATCH http://localhost:8080/users/${id}
```
#### Request
Parameter Name| Data Type | Type| Remark
------------ | ------------- | - |-------------
{$id} | string | Path Variable | Required
{  <br/> "id": "e0001", <br/> "name": "Harry Potter",  <br/> "login": "pharry",  <br/> "salary": 399,  <br/> "startDate": "2020-01-10"  <br/> } | string | Request Body | Required

#### Response
Content | Data Type | Type| Remark
------------ | ------------- | - |-------------
{"message": "Successfully updated"} | application/json | Response Body | http status : <li>200</li>
 {"message": "No such employee"} <br/> {"message": "Employee login not unique"}  <br/> {"message": "Invalid salary"} <br/> {"message": "Invalid date"} | application/json | Response Body | http status :<li>400</li>


### Delete API

```
DELETE http://localhost:8080/users/${id}
```

#### Request
Parameter Name| Data Type | Type| Remark
------------ | ------------- | - |-------------
{$id} | string | Path Variable | Required

#### Response
Content | Data Type | Type| Remark
------------ | ------------- | - |-------------
{"message": "Successfully deleted"}  | application/json | Response Body | http status :  <li>200</li>
{"message": "No such employee"} | application/json | Response Body | http status :  <li>400</li>

## Assumption

<ul>
  <li>Only to serve web service</li>
  <li>All API return responses are in JSON format</li>
  <li>Allow different error message structure  on top specification</li>
  <li>Preset data not required</li>
  <li>Request parameter name is case sensitive</li>
  <li>All request parameter, request body is provided</li>
  <li>Request body/data is correctly formed</li>
  <li>Local timezone refer to Singapore timezone</li>
  <li>User Story 1
    <ul>
      <li>Validation required against database to check if id or login is unique</li>
      <li>CSV provided complies to specification of 5 columns id,login,name,salary,startDate</li>
    </ul>
  </li>
  <li>User Story 2
    <ul>
      <li>Request parameter will be default value when not provided</li>
      <li>Additional parameter is not used when not provided</li>
      <li>Allow only single column sorting</li>
      <li>"no limit" refer to 2147483647 rows, max value of Integer</li>
      </ul>
  </li>
  </li>
</ul>
