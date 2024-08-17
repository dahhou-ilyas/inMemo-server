import org.example.Store;
import org.example.types.*;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProcessReqest {
    private final DataType redisData;

    public ProcessReqest(DataType redisData){
        this.redisData=redisData;
    }

    public DataType process(){
        String commade=getCommande();
        switch (commade.toLowerCase()) {
            case "echo":
                return processEcho();
            case "ping":
                return processPing();
            case "set":
                return processSet();
            default:
                return new ErrorType("unknown command");
        }
    }

    private String getCommande(){
        if(!redisData.getType().equals(TypeDataType.ARRAY)){
            System.out.println("n'est pas array Redis");
            return null;
        }
        DataType[] rawValue = getArray();

        DataType commande = rawValue[0];
        if(!commande.getType().equals(TypeDataType.BULK_STRING)){
            System.out.println("n'est pas bulk String");
            return null;
        }

        return ((BulkStringType) commande).getRawValue();
    }

    private DataType processEcho(){
        DataType[] rawValue = getArray();
        System.out.println(new ArrayList<>(List.of(rawValue)));
        if(rawValue.length !=2){
            return new ErrorType("ERR wrong number of arguments");
        }
        if(!rawValue[1].getType().equals(TypeDataType.BULK_STRING)){
            return new ErrorType("ERR wrong Type");
        }
        return rawValue[1];
    }

    public DataType processPing(){
        DataType[] completeRequst=getArray();
        if(completeRequst.length<1){
            return new ErrorType("ERR wrong number of arguments");
        }
        if(!completeRequst[0].getType().equals(TypeDataType.BULK_STRING)){
            return new ErrorType("ERR wrong Type");
        }
        return new SimpleStringType("PONG");
    }

    public DataType processSet(){
        DataType[] completeRequst=getArray();
        if(completeRequst.length<3){
            return new ErrorType("ERR wrong number of arguments");
        }
        String Key=((BulkStringType) completeRequst[1]).getRawValue();
        String value=((BulkStringType) completeRequst[2]).getRawValue();

        if(completeRequst.length==3){
            String res= Store.set(Key,value,null);
            return new SimpleStringType(res);
        }
        if(completeRequst.length==5){
            String expArg = ((BulkStringType) completeRequst[3]).getRawValue();
            if(!expArg.toLowerCase().equals("px")){
                return new ErrorType("ERR wrong syntax "+expArg.toLowerCase());
            }
            Long exp = Long.parseLong(((BulkStringType) completeRequst[4]).getRawValue());
            String res= Store.set(Key,value,new Date().getTime()+exp);
            return new SimpleStringType(res);
        }
        return new ErrorType("ERR wrong number of arguments");
    }

    public DataType[] getArray(){
        return ((ArrayType) redisData).getRawValue();
    }



}
