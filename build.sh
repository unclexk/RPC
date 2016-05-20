mvn clean
mvn assembly:assembly
mkdir ./target/lib
cp ./target/rpc-api-1.0-jar-with-dependencies.jar ./target/lib