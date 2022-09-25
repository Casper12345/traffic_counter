# Traffic Counter 

Counts cars in traffic

## Assumptions

* All timestamps are not zoned
* The data is input cleanly and a record is written every 30 minutes

## Libraries/Tools used

* SBT - simple build tool
* FS2 streams - provides concurrent functional reactive streams
* ScalaTest for unit testing

## How to setup and run the project

1. Make sure that java version 17 is on the path
2. Make sure sbt is on the path
3. Make sure that the `./files` folder contains a csv data file 
4. Run the command `sbt run` from the project root


## Running tests
1. To run the tests run `sbt test` from the project root.
