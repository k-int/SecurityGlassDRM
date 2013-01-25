#!/bin/bash

encKey=$1;
inFile=$2;
outFile=$3;

java -cp commons-codec-1.5.jar:. SimpleDecrypter $encKey $inFile $outFile;
