package org.example;



import org.example.types.*;

import java.util.ArrayList;
import java.util.List;

public class ProcessReqest {
    private final DataType dataType;

    public ProcessReqest(DataType dataType){
        this.dataType = dataType;
    }

    public DataType process(){

        String commade=getCommande();
        System.out.println(commade);
        switch (commade.toLowerCase()) {
            case "echo":
                return processEcho();
            default:
                return new ErrorType("unknown command");
        }
    }

    private String getCommande(){
        if(!dataType.getType().equals(RedisDataType.ARRAY)){
            System.out.println("n'est pas array Redis");
            return null;
        }
        DataType[] rawValue = ((ArrayType) dataType).getRawValue();

        DataType commande = rawValue[0];
        if(!commande.getType().equals(RedisDataType.BULK_STRING)){
            System.out.println("n'est pas bulk String");
            return null;
        }

        return ((BulkStringType) commande).getRawValue();
    }

    private DataType processEcho(){
        DataType[] rawValue = ((ArrayType) dataType).getRawValue();
        System.out.println(new ArrayList<>(List.of(rawValue)));
        if(rawValue.length !=2){
            return new ErrorType("ERR wrong number of arguments");
        }
        if(!rawValue[1].getType().equals(RedisDataType.BULK_STRING)){
            return new ErrorType("ERR wrong Type");
        }
        return rawValue[1];
    }



}
