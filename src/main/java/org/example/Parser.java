package org.example;

import org.example.types.*;

import java.io.IOException;
import java.io.InputStream;

public class Parser {

    private final InputStream inputStream;

    public Parser(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public DataType parseRequest() throws IOException{
        char c=readChar();
        if(c == '+' ){
            return parseSimpleString();
        }else if(c=='-'){
            return parseError();
        } else if (c==':') {
            return parseInteger();
        }else if(c=='$'){
            return parseBulkString();
        } else if (c=='*') {
            return parseArray();
        } else {
            return null;
        }
    }

    private DataType parseArray() throws IOException{
        int length=(int) readInteger();
        DataType[] value=new DataType[length];
        for (int i=0;i<length;i++){
            value[i]=parseRequest();
        }
        return new ArrayType(value);
    }

    private DataType parseSimpleString() throws IOException {
        return new SimpleStringType(readString());
    }
    private DataType parseError() throws IOException {
        return new ErrorType(readString());
    }

    private DataType parseInteger() throws IOException{
        return new IntegerType(readInteger());
    }

    private DataType parseBulkString() throws IOException{
        int length=(int) readInteger();
        //cette verification unitil
        if(length < 0){
            skipNBytes(2);
            return new BulkStringType(null);
        }
        return new BulkStringType(readString());
    }

    private String readString() throws IOException {
        StringBuilder sb=new StringBuilder();
        char c=readChar();
        while (c!='\r'){
            sb.append(c);
            c=readChar();
        }
        skipNBytes(1);
        return sb.toString();

    }

    private long readInteger() throws IOException{
        long value=0;
        boolean isNegative=false;
        char c=readChar();
        while (c!='\r'){
            if(c=='-') isNegative =true;
            else value = value*10 + c-'0';
            c=readChar();
        }
        skipNBytes(1);
        if (isNegative){
            return -value;
        }
        return value;
    }

    private char readChar() throws IOException {
        int c=-1;
        while (c == -1){
            c = inputStream.read();
        }
        return (char) c;
    }
    private void skipNBytes(int n) throws IOException {
        while (n != 0) {
            n -= inputStream.skip(n);
        }
    }

}
