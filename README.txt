This is java and can be compile to java bytes
code using javac and run using java.

I used java.json-1.0 to do json handling.
I wans't sure if you use maven or something else 
so I just added to project directory.  You need to add to
java classpath to compile code 

Source code in under src
Unit / integ tests are under tst

capital.one.stock.integ are integ tests.
capital.one.stock.analyzer.test are unit tests.

To run create java class file from java files.

On Windows:
dir /s /B *.java | findstr /V tst > sources.txt
javac -classpath javax.json-1.0.jar @sources.txt

On linux:
find -name "*.java" | grep -v tst > sources.txt
javac -classpath javax.json-1.0.jar @sources.txt

To run program:

java -classpath src capital/one/stock/driver/Driver

You must also add javax.json-1.0.jar to java path.  
Easiest way if just to copy it under lib/ext in your jre.

I only created a command line interface to interact with program.
help will example how to use the system.

Basically just run type in parameters and press enter.  
If no parameters will use default settings.