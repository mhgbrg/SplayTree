all: build

build:
	javac -d bin/ *.java

tests: build
	java -cp bin/ TestSetCorrectness 1000 100 20
