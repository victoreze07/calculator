# calculator
Simple Calculator using maven and java

Instruction about runnung this project

(1) build this project using maven using command "mvn clean package"

(2) run any of the following commnd from command prompt.Second parameter can be "warn", "info" or "debug". 
If you are not providing second parameter it will take "debug" by default

(a)java -cp target\calculator-1.0-SNAPSHOT-jar-with-dependencies.jar com.App "add(5,5)"

(b)mvn exec:java -Dexec.mainClass=com.App -Dexec.args="add(5,5)"
