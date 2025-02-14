package NameServer;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;


public class nameServer {
    

    public static void main(String[] args) throws Exception
    {
        int id;
        int portlisten;
        String ipbsserver;
        int portbsserver;
        if(args.length!=1)
        {
            System.out.println("Check the syntax of running this file");
        }
        String filename=args[0];
        BufferedReader reader1=null;
        try {
            reader1 = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        InetAddress localhost = InetAddress.getLocalHost();
                    String ip=localhost.getHostAddress();
                    Globals.ip=ip;
            String line;
            line =reader1.readLine();
            id=Integer.parseInt(line);
            Globals.id=id;
            portlisten=Integer.parseInt(reader1.readLine());
            Globals.portlisten=portlisten;
            line=reader1.readLine();
            String[] parts=line.split(" ");
            ipbsserver=parts[0];
            portbsserver=Integer.parseInt(parts[1]);
            Thread thread1=new Thread(new ConnectBSServer(id,ipbsserver,portbsserver,portlisten));
            thread1.start();
            


    }
}
class ConnectBSServer implements Runnable{

    String ipbsserver;
    int portbsserver;
    int id;
    int portlisten;
    public ConnectBSServer(int id, String ipbsserver,int portbsserver,int portlisten)
    {
        this.id=id;
        this.ipbsserver=ipbsserver;
        this.portbsserver=portbsserver;
        this.portlisten=portlisten;
    }
    public void run()
    {
        try{
            Socket s1=new Socket(ipbsserver, portbsserver);
            System.out.println("Connected to BootstrapServer "+s1);
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader reader = new BufferedReader(new InputStreamReader(s1.getInputStream()));
            PrintWriter writer=new PrintWriter(s1.getOutputStream()); 
            System.out.print("BootStrapServer>");
            String userInput=input.readLine();
            Thread connection=null;
            
            while(!userInput.equals("quit"))
            {
                if(userInput.equals("enter"))
                {
                    connection=new Thread(new Listen(Globals.portlisten,Globals.id));
                    connection.start();
                    writer.println("enter");
                    writer.flush();
                    writer.println(Globals.id);
                    writer.flush();
                    writer.println(Globals.portlisten);
                    writer.flush();
                    
                    writer.println(Globals.ip);
                    writer.flush();
        // Create a new TreeSet
        
                    int succid=Integer.parseInt(reader.readLine());
                    int predid=Integer.parseInt(reader.readLine());
                    /*TreeSet<String> treeSet = new TreeSet<>(Arrays.asList(elements));
        String seq="";
                    for(String element:treeSet)
                    {
                        int d=Integer.parseInt(element);
                        if(d<=succid)
                        {
                            seq=seq+" "+String.valueOf(d);
                        }   
                    }*/
                    String se=reader.readLine();
                    //String[] elements = se.substring(1, se.length() - 1).split(", ");
                    System.out.println(se);

                    Globals.succid=succid;
                    Globals.predid=predid;
                    String succip=reader.readLine();
                    String predip=reader.readLine();
                    System.out.println("Succip is: "+succip);
                    System.out.println("Predip is: "+predip);
                    Globals.succip=succip;
                    Globals.predip=predip;
                    int succport=Integer.parseInt(reader.readLine());
                    int predport=Integer.parseInt(reader.readLine());
                    Globals.succport=succport;
                    Globals.predport=predport;
                    Socket succ=new Socket(succip,succport);
                    Socket pred=new Socket(predip,predport);
                    Globals.successors.put(succid,succ);
                    Globals.predecessors.put(predid,pred);
                    System.out.println("Pred "+Globals.predid+" Predport "+
                    Globals.predport+" Predip "+
                    Globals.predip+" Predecessors "+
                    Globals.predecessors);
                    System.out.println("Succ "+Globals.succid+" Succport "+
                    Globals.succport+" Succip "+
                    Globals.succip+" Successors "+
                    Globals.successors);
                    if(succid==0){
                    String hashMapString=reader.readLine();
                    System.out.println(hashMapString);
                    HashMap<Integer, String> receivedHashMap = new HashMap<>();
                    
                    String[] pairs = hashMapString.split(",");
                    for (String pair : pairs) {
                        System.out.println("Pair is: "+pair);
                        String[] keyValue = pair.split(":");
                        receivedHashMap.put(Integer.parseInt(keyValue[0]), keyValue[1]);
                    }

                    Globals.keyvalue=receivedHashMap;
                    System.out.println("Key value updated is: "+Globals.keyvalue);
                }
                
                    else{
                        Socket ss=Globals.successors.get(Globals.succid);
                        System.out.println(ss);
                        BufferedReader br=new BufferedReader(new InputStreamReader(ss.getInputStream()));
                        PrintWriter pw=new PrintWriter(ss.getOutputStream());
                        pw.println("entertoSucc");
                        pw.flush();
                        pw.println(Globals.id);
                        pw.flush();
                        String hashMapString = br.readLine();
                        System.out.println("Received hashMapStrig is: "+hashMapString);
                        // Convert the string back to a HashMap
                        Globals.keyvalue=new HashMap<Integer,String>();
                        String[] pairs = hashMapString.split(",");
                        for (String pair : pairs) {
                            String[] keyValue = pair.split(":");
                            Globals.keyvalue.put(Integer.parseInt(keyValue[0]), keyValue[1]);
                        }
                        System.out.println("Key value updated is: "+Globals.keyvalue);

                       }
                    if(Globals.succid!=0)
                    {
                        Socket ss=Globals.successors.get(Globals.succid);
                        BufferedReader br=new BufferedReader(new InputStreamReader(ss.getInputStream()));
                        PrintWriter pw=new PrintWriter(ss.getOutputStream());
                        pw.println("modifyPredInSucc");
                        pw.flush();
                        pw.println(Globals.id);
                        pw.flush();
                        pw.println(Globals.ip);
                        pw.flush();
                        pw.println(Globals.portlisten);
                        pw.flush();


                    }
                    if(Globals.predid!=0)
                    {
                        Socket ss=Globals.predecessors.get(Globals.predid);
                        BufferedReader br=new BufferedReader(new InputStreamReader(ss.getInputStream()));
                        PrintWriter pw=new PrintWriter(ss.getOutputStream());
                        pw.println("modifySuccInPred");
                        pw.flush();
                        pw.println(Globals.id);
                        pw.flush();
                        pw.println(Globals.ip);
                        pw.flush();
                        pw.println(Globals.portlisten);
                        pw.flush();
                    }
                    System.out.println("Successful entry");
                    System.out.println("The key-value pairs managed by this server are: "+Globals.keyvalue);
                    System.out.println("The succesor id is: "+Globals.succid);
                    System.out.println("The predecessor id is: "+Globals.predid);
                    System.out.println("The servers traversed in the process are: "+se); //implemented

                }
                else if(userInput.equals("exit"))
                {
                    System.out.println("Sending these values for exit: in exit ");
                    System.out.println("Predid "+Globals.predid+" predip "+
                    Globals.predip+" predport "+
                    Globals.predport+" Predecessors "+
                    Globals.predecessors);
        
                    
                    if(Globals.succid==0)
                    {
                        writer.println("ExittoSucc");
                        writer.flush();
                        writer.println(Globals.id);
                        writer.flush();
                        StringBuilder sb = new StringBuilder();
            for (Integer key : Globals.keyvalue.keySet()) {
                sb.append(key).append(":").append(Globals.keyvalue.get(key)).append(",");
            }
            String hashMapString = sb.toString();
            writer.println(hashMapString);
            writer.flush();
            System.out.println("Sending these values for exit: ");
            System.out.println("Predid "+Globals.predid+" predip "+
            Globals.predip+" predport "+
            Globals.predport+" Predecessors "+
            Globals.predecessors);

            writer.println(Globals.predid);
            writer.flush();
            writer.println(Globals.predip);
            writer.flush();
            writer.println(Globals.predport);
            writer.flush();

                    }
                    else
                    {
                        Socket ss=Globals.successors.get(Globals.succid);
                        BufferedReader br=new BufferedReader(new InputStreamReader(ss.getInputStream()));
                        PrintWriter pw=new PrintWriter(ss.getOutputStream());
                        pw.println("ExittoSucc");
                        pw.flush();
                        StringBuilder sb = new StringBuilder();
                        for (Integer key : Globals.keyvalue.keySet()) {
                            sb.append(key).append(":").append(Globals.keyvalue.get(key)).append(",");
                        }
                        String hashMapString = sb.toString();
                        pw.println(hashMapString);
                        pw.flush();
                        pw.println(Globals.predid);
            pw.flush();
            pw.println(Globals.predip);
            pw.flush();
            pw.println(Globals.predport);
            pw.flush();


                    }
                    if(Globals.predid==0)
                    {
                        writer.println("ExittoPred");
                        writer.flush();
                        writer.println(Globals.id);
                        writer.flush();
            writer.println(Globals.succid);
            writer.flush();
            writer.println(Globals.succip);
            writer.flush();
            writer.println(Globals.succport);
            writer.flush();
                    }
                    else{
                        Socket ss=Globals.predecessors.get(Globals.predid);
                        BufferedReader br=new BufferedReader(new InputStreamReader(ss.getInputStream()));
                        PrintWriter pw=new PrintWriter(ss.getOutputStream());
                        pw.println("ExittoPred");
                        pw.flush();
                        pw.println(Globals.succid);
            pw.flush();
            pw.println(Globals.succip);
            pw.flush();
            pw.println(Globals.succport);
            pw.flush();

                    }
                   System.out.println("Successful exit");
                   System.out.println("Successor id is: "+Globals.succid);
                   System.out.println("Keys successfully handed over to the successor are: "+Globals.keyvalue);

                }
                System.out.print("BootStrapServer>");

                userInput=input.readLine();
            }
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
}
}
class Listen implements Runnable{
    int id;
    int portlisten;
    public Listen(int portlisten, int id){
        this.id=id;
        this.portlisten=portlisten;
    }
    public void run(){
        try{
            ServerSocket s1=new ServerSocket(portlisten);
            Socket socket1;
            while(true)
            {
                System.out.println("Waiting for connections from other servers on port: "+portlisten);
                socket1=s1.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
                PrintWriter writer=new PrintWriter(socket1.getOutputStream()); 
                Thread thread2=new Thread(new ConnectwithServer(reader,writer,id,portlisten));
                thread2.start();
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
class ConnectwithServer implements Runnable{
    BufferedReader reader;
    PrintWriter writer;
    int id;
    int portlisten;
    ConnectwithServer(BufferedReader reader, PrintWriter writer, int id, int portlisten)
    {
        this.reader=reader;
        this.writer=writer;
        this.id=id;
        this.portlisten=portlisten;
    }
    public void run(){
        try{
            String input=reader.readLine();
            while(!input.equals("quit"))
            {
                
                if(input.equals("entertoSucc"))
                {
                    System.out.println("In enter to successor");
                    int id2=Integer.parseInt(reader.readLine());
                    HashMap<Integer, String> temp=new HashMap<Integer,String>();
                    StringBuilder sb = new StringBuilder();
                    for (Integer key : Globals.keyvalue.keySet()) {
                        if((int)key<id2)
                        {
                            temp.put(key,Globals.keyvalue.get(key));
                        sb.append(key).append(":").append(Globals.keyvalue.get(key)).append(",");
                    }
                    }
                    for(Integer key: temp.keySet())
                    {
                        Globals.keyvalue.remove(key);
                    }
                    String hashMapString = sb.toString();
                    System.out.println("Sending hashMapString: "+hashMapString);
                    writer.println(hashMapString);
                    writer.flush();
                }
                else if(input.equals("modifyPredInSucc"))
                {
                    Globals.predid=Integer.parseInt(reader.readLine());
                    Globals.predip=reader.readLine();
                    Globals.predport=Integer.parseInt(reader.readLine());
                    Globals.predecessors.clear();
                    Globals.predecessors.put(Globals.predid,new Socket(Globals.predip,Globals.predport));

                }
                else if(input.equals("modifySuccInPred"))
                {
                    Globals.succid=Integer.parseInt(reader.readLine());
                    Globals.succip=reader.readLine();
                    Globals.succport=Integer.parseInt(reader.readLine());
                    Globals.successors.clear();
                    Globals.successors.put(Globals.succid,new Socket(Globals.succip,Globals.succport));

                }
                else if(input.equals("ExittoSucc"))
                {
                    String hashMapString=reader.readLine();

                    String[] pairs = hashMapString.split(",");
                    for (String pair : pairs) {
                        String[] keyValue = pair.split(":");
                        Globals.keyvalue.put(Integer.parseInt(keyValue[0]), keyValue[1]);
                    }
                    System.out.println("The updated hash map is: "+Globals.keyvalue);
                    int oldpredid=Globals.predid;
                    Globals.predid=Integer.parseInt(reader.readLine());
                    Globals.predip=reader.readLine();
                    Globals.predport=Integer.parseInt(reader.readLine());
                    Globals.predecessors.remove(oldpredid);
                    System.out.println("The pred ip is: "+Globals.predip);
                    System.out.println("The pred port is: "+Globals.predport);
                    Globals.predecessors.put(Globals.predid,new Socket(Globals.predip,Globals.predport));
                    System.out.println("The modified values are: ");
                    System.out.println("Predid "+Globals.predid+" predip "+
                    Globals.predip+" predport "+
                    Globals.predport+" Predecessors "+
                    Globals.predecessors);
                    System.out.println("The updated hash map is: "+Globals.keyvalue);


                }
                else if(input.equals("ExittoPred"))
                {
                    int oldsuccid=Globals.succid;
                    Globals.succid=Integer.parseInt(reader.readLine());
                    Globals.succip=reader.readLine();
                    Globals.succport=Integer.parseInt(reader.readLine());
                    Globals.successors.remove(oldsuccid);
                    Globals.successors.put(Globals.succid,new Socket(Globals.succip,Globals.succport));
                    System.out.println("The modified values are: ");
                    System.out.println("Succid "+Globals.succid+" succip "+
                    Globals.succip+" succport "+
                    Globals.succport+" successors "+
                    Globals.successors);
                    System.out.println("The updated hash map is: "+Globals.keyvalue);

                }
                else if(input.equals("lookup"))
                {
                    System.out.println("Inside Name server lookup: "+Globals.id);
                    int flag;
                    String serverseq=String.valueOf(Globals.id);
                    int key=Integer.parseInt(reader.readLine());
                    System.out.println("Key received is: "+key);
                    if(key>Globals.predid&&key<Globals.id)
                    {
                        if(Globals.keyvalue.containsKey(key))
                        {
                            System.out.println("flag=1");
                            System.out.println("Found value of key is: "+Globals.keyvalue.get(key));
                            flag=1;
                            writer.println(flag);
                            writer.flush();
                            writer.println(Globals.keyvalue.get(key));
                            writer.flush();
                            writer.println(serverseq);
                            writer.flush();
                        }
                        else if(Globals.succid==0){
                            flag=0;
                            writer.println(flag);
                            writer.flush();
                            writer.println(serverseq);
                            writer.flush();
                        }
                        else{
                            flag=0;
                            writer.println(flag);
                            writer.flush();
                            writer.println(serverseq);
                            writer.flush();

                        }
                    }
                    
                        else if(Globals.succid==0){
                            flag=0;
                            writer.println(flag);
                            writer.flush();
                            writer.println(serverseq);
                            writer.flush();
                        }
                        
                    
                        
                        else{
                            System.out.println("Inside esl");
                            Socket s=Globals.successors.get(Globals.succid);
                            System.out.println("Try to connect to successor: "+s);
                            System.out.println("Connecting to successor: "+Globals.succid);

                            BufferedReader reader5 = new BufferedReader(new InputStreamReader(s.getInputStream()));
                            PrintWriter pw2=new PrintWriter(s.getOutputStream());
                            pw2.println("lookup");
                            pw2.flush();
                            pw2.println(key);
                            pw2.flush();
                            int flag1=Integer.parseInt(reader5.readLine());
                            System.out.println("Flag received is: "+flag1);
                            writer.println(flag1);
                            if(flag1==0)
                            {
                                String sr2=reader5.readLine();
                                serverseq=serverseq+" "+sr2;
                                writer.println(serverseq);
                                writer.flush();
                            }
                            else{
                                String value=reader5.readLine();
                                writer.println(value);
                                writer.flush();
                                String sr2=reader5.readLine();
                                serverseq=serverseq+" "+sr2;
                                writer.println(serverseq);
                                writer.flush();
                            }
                        }
                    }

                    else if(input.equals("insert"))
                {
                    System.out.println("Inside Name server insert: "+Globals.id);
                    int flag;
                    String serverseq=String.valueOf(Globals.id);
                    int key=Integer.parseInt(reader.readLine());
                    System.out.println("Key received is: "+key);
                    String value1=reader.readLine();
                    if(key>Globals.predid&&key<Globals.id)
                    {
                        Globals.keyvalue.put(key,value1);
                        writer.println(serverseq);
                        writer.flush();
                    }
                    
                    else if(Globals.succid==0&&key>1023){
                            writer.println("Key should be less than 1023");
                            writer.flush();
                        }
                        
                    
                        
                        else{
                            System.out.println("Inside esl");
                            Socket s=Globals.successors.get(Globals.succid);
                            System.out.println("Try to connect to successor: "+s);
                            System.out.println("Connecting to successor: "+Globals.succid);

                            BufferedReader reader5 = new BufferedReader(new InputStreamReader(s.getInputStream()));
                            PrintWriter pw2=new PrintWriter(s.getOutputStream());
                            pw2.println("insert");
                            pw2.flush();
                            pw2.println(key);
                            pw2.flush();
                            pw2.println(value1);
                            pw2.flush();
                            //int flag1=Integer.parseInt(reader5.readLine());
                            //System.out.println("Flag received is: "+flag1);
                            //writer.println(flag1);
                            String seq=reader5.readLine();
                            serverseq=serverseq+seq;
                            System.out.println("Received server seq is: "+seq);
                            writer.println(serverseq);
                            writer.flush();
                        }
                    }
                    else if(input.equals("delete"))
                {
                    System.out.println("Inside Name server delete: "+Globals.id);
                    int flag;
                    String serverseq=String.valueOf(Globals.id);
                    int key=Integer.parseInt(reader.readLine());
                    System.out.println("Key received is: "+key);
                    if(key>Globals.predid&&key<Globals.id)
                    {
                        if(Globals.keyvalue.containsKey(key))
                        {
                            System.out.println("flag=1");
                            System.out.println("Found value of key is: "+Globals.keyvalue.get(key));
                            flag=1;
                            writer.println(flag);
                            writer.flush();
                            writer.println(Globals.keyvalue.get(key));
                            writer.flush();
                            writer.println(serverseq);
                            writer.flush();
                            Globals.keyvalue.remove(key);

                        }
                        else if(Globals.succid==0){
                            flag=0;
                            writer.println(flag);
                            writer.flush();
                            writer.println(serverseq);
                            writer.flush();
                        }
                        else{
                            flag=0;
                            writer.println(flag);
                            writer.flush();
                            writer.println(serverseq);
                            writer.flush();

                        }
                    }
                    
                        else if(Globals.succid==0){
                            flag=0;
                            writer.println(flag);
                            writer.flush();
                            writer.println(serverseq);
                            writer.flush();
                        }
                        
                    
                        
                        else{
                            System.out.println("Inside esl");
                            Socket s=Globals.successors.get(Globals.succid);
                            System.out.println("Try to connect to successor: "+s);
                            System.out.println("Connecting to successor: "+Globals.succid);

                            BufferedReader reader5 = new BufferedReader(new InputStreamReader(s.getInputStream()));
                            PrintWriter pw2=new PrintWriter(s.getOutputStream());
                            pw2.println("delete");
                            pw2.flush();
                            pw2.println(key);
                            pw2.flush();
                            int flag1=Integer.parseInt(reader5.readLine());
                            System.out.println("Flag received is: "+flag1);
                            writer.println(flag1);
                            if(flag1==0)
                            {
                                String sr2=reader5.readLine();
                                serverseq=serverseq+" "+sr2;
                                writer.println(serverseq);
                                writer.flush();
                            }
                            else{
                                String value=reader5.readLine();
                                writer.println(value);
                                writer.flush();
                                String sr2=reader5.readLine();
                                serverseq=serverseq+" "+sr2;
                                writer.println(serverseq);
                                writer.flush();
                            }
                        }
                    }
                    input=reader.readLine();

                }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}
class Globals{
    public static HashMap<Integer, String> keyvalue=new HashMap<Integer,String>();
    public static HashMap<Integer, Socket> successors=new HashMap<Integer,Socket>();
    public static HashMap<Integer, Socket> predecessors=new HashMap<Integer,Socket>();
    public static int predid;
    public static int predport;
    public static int succport;
    public static String succip;
    public static String predip;
    public static int portlisten;
    public static int succid;
    public static int id;
    public static String ip;

    //public static HashMap<Integer, Socket> sockets;
    
    

}