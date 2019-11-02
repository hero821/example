cd target\classes
java -javaagent:../../../agent/target/example.javaagent.agent-1.0-SNAPSHOT.jar -Dfile.encoding=UTF-8 io.github.hero821.example.javaagent.Main
cd ../..