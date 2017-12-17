Markov Generation Library
=======

This is a Java library that can parse text, then generate phrases from that text using Markov chains.

## Usage

Import the class `com.robut.markov.MarkovChain`. After instantiating, call its `parseString` method with a string as an
argument. It will make generation data from that string, including an end-of-phrase token at the end of the string.
Individual words can be added using the `addWord` method, and the phrase can be ended with the `endString` method.

## Note on Memory Usage

Currently, this library generates a lot of duplicate strings when generating a lot of messages very quickly, and can
use a lot of memory if not launched with string deduplication enabled. This is only available to Java version 8.20 and
above. String deduplication can be enabled with the arguments: `-XX:+UseG1GC -XX:+UseStringDeduplication`.
