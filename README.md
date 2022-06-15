
Prerequisites
Update the SRC_FILE_PATH variable present in the file src/main/resources/application.conf with the path to the input file
containing the Json Values.

Execution
---------
To launch the http server simply type 'run' at the sbt prompt and hit enter. This will start the server at port 8080.
You can then make requests such as the following:

POST /amazon/best-rated HTTP/1.1
Host: localhost:8080
Content-Type: application/json
{
  "start": "01.01.2010",
  "end": "31.12.2020",
  "limit": 2,
  "min_number_reviews": 2
}

To run tests simply type 'test' at the sbt prompt and hit enter.

Assumptions
-----------
If any of the lines contained in the input file has invalid JSON then the streaming process will fail with the
assumption that other incoming values might/will also be invalid.
