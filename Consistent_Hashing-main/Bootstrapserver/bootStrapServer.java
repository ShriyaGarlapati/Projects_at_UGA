import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.TreeSet;

public class bootStrapServer{
    

    
    public static void main(String[] args) throws Exception
    {
        Globals g=new Globals();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        //PrintWriter writer = new PrintWriter(); // autoflush set to true
        if(args.length!=1)
        {
            System.out.println("Check the syntax to run this file");

        }
        

        String filename=args[0];
        BufferedReader reader1 = new BufferedReader(new FileReader(filename));
            String line;
            line =reader1.readLine();
            int id=Integer.parseInt(line);
            line=reader1.readLine();
            int port=Integer.parseInt(line);
            Globals.bsport=port;
            Globals.keyvalue=new HashMap<Integer,String>();

            while ((line = reader1.readLine()) != null) {
                String[] parts=line.split(" ");
                Globals.keyvalue.put(Integer.parseInt(parts[0]),parts[1]);

            }
            
            System.out.println(Globals.keyvalue);
            Thread connectThread=new Thread(new ConnectingThread(id,port));
            connectThread.start();
            Thread userThread=new Thread(new UserThread(id,port));
            userThread.start();
        
    }
}
class Globals{
    public static HashMap<Integer, String> keyvalue=new HashMap<Integer,String>();
    public static HashMap<Integer, Socket> successors=new HashMap<Integer, Socket>();
    public static HashMap<Integer, Socket> predecessors=new HashMap<Integer,Socket>();
    public static int predid=0;
    public static int succid=0;

    public static int bsport;
    public static int predport;
    public static String predip;
    public static int succport;
    public static String succip;
    public static HashMap<Integer,Integer> ports=new HashMap<Integer,Integer>();
    public static HashMap<Integer,String> ipAddress=new HashMap<Integer,String>();
    public static int id=0;
    public static TreeSet<Integer> ids=new TreeSet<Integer>();
    

   static{
        ids.add(0);
        predid=0;
        try{
        InetAddress localhost = InetAddress.getLocalHost();
        String ipAdd=localhost.getHostAddress();

        ipAddress.put(0,ipAdd);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }

}
class ConnectingThread implements Runnable{
    int id;
    int port;
    ServerSocket serv;
    Socket socket;
    ConnectingThread(int id, int port){
        this.id=id;
        this.port=port;
        Globals.ports.put(0,port);
    }
    public void run(){
        try{
        System.out.println("Inside Connecting thread");
        serv=new ServerSocket(port);
        System.out.println(serv);

        while(true)
        {
            //System.out.println("Waiting for Connections on port: "+port);
            socket=serv.accept();
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out=new PrintWriter(socket.getOutputStream());
            Thread connect1=new Thread(new Connect1(input,in,out,socket,id));
            connect1.start();


        }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }
}
class Connect1 implements Runnable{
    BufferedReader input;
    BufferedReader in;
    PrintWriter out;
    Socket socket;
    int id;
    //int port;

    public Connect1(BufferedReader input,BufferedReader in,PrintWriter out, Socket socket, int id){
        this.input=input;
        this.in=in;
        this.out=out;
        this.socket=socket;
        this.id=id;
        //this.port=port;


    }
    public void run()
    {
        try{
        String inputfromServer=in.readLine();
        while(!inputfromServer.equals("quit"))
        {
            if(inputfromServer.equals("enter"))
            {
                System.out.println("Beginning of enter ip Address: "+Globals.ipAddress);

                id=Integer.parseInt(in.readLine());
                int portlisten=Integer.parseInt(in.readLine());
                String ip=in.readLine();
                Globals.ports.put(id,portlisten);
                Globals.ipAddress.put(id,ip);
                
                
                    String seq="";
                    int possible_pred;
                    int possible_succesor;
                    possible_pred=0;
                    possible_succesor=0;
                    int flag=0;
                    System.out.println("TreeSet is: "+Globals.ids);
                    for(Integer element:Globals.ids)
                    {
                        possible_succesor=(int)element;
                        if(possible_succesor>id)
                        {
                            flag=1;
                            break;
                        }
                        possible_pred=(int)element;
                    }
                    
                    if(possible_pred==Globals.ids.last()&&flag==0)
                    possible_succesor=0;
                    out.println(possible_succesor);//successor
                    out.flush();
                    out.println(possible_pred);//predecessor
                    out.flush();
                    TreeSet<Integer> t1=new TreeSet<Integer>();
                    for(Integer y:Globals.ids)
                    {
                        t1.add(y);
                    }
                    String seq1="";
                    for(Integer x:Globals.ids)
                    {
                        if((int)x<=possible_succesor)
                        seq1=seq1+" "+String.valueOf(x);
                    }
                    out.println(seq1);
                    out.flush();
                    System.out.println("Successor and predecessor are: "+possible_succesor+" "+possible_pred);
                    InetAddress localhost = InetAddress.getLocalHost();
                    String ipAdd=localhost.getHostAddress();
                    System.out.println("ipAddress is: "+Globals.ipAddress);
                    System.out.println("Globals.ipAddress.get(possible_succesor) is: "+Globals.ipAddress.get(possible_succesor));
                    System.out.println("Globals.ipAddress.get(possible_predecessor) is: "+Globals.ipAddress.get(possible_pred));

                    out.println(Globals.ipAddress.get(possible_succesor));//successor ip
                    out.flush();
                    out.println(Globals.ipAddress.get(possible_pred));//predecessor ip
                    out.flush();
                    System.out.println("ports is: "+Globals.ports);
                    out.println(Globals.ports.get(possible_succesor));//successor port
                    out.flush();
                    out.println(Globals.ports.get(possible_pred));//predecessor port
                    out.flush();

                    if(possible_succesor==0)
                    {
                        StringBuilder sb = new StringBuilder();

                        HashMap<Integer,String> temp=new HashMap<Integer,String>();
                        for (Integer key : Globals.keyvalue.keySet()) {
                            int x=(int)key;
                            if(x<id)
                            {sb.append(key).append(":").append(Globals.keyvalue.get(key)).append(",");
                            temp.put(key,Globals.keyvalue.get(key));
                        }
                        }
                        for(Integer key:temp.keySet())
                        {
                            Globals.keyvalue.remove(key);
                        }
                        String hashMapString = sb.toString();
                        System.out.println("Keys are: "+hashMapString);
                        out.println(hashMapString);
                        out.flush();
                        Globals.predid=id;
                        Globals.predport=portlisten;
                        Globals.predip=ip;
                        System.out.println(ip+" "+portlisten);
                        Socket ss=new Socket(ip,portlisten);
                        Globals.predecessors.clear();
                        Globals.predecessors.put(id,ss);

                    }
                    if(possible_pred==0)
                    {
                        Globals.succid=id;
                        Globals.succport=portlisten;
                        Globals.succip=ip;
                        Globals.successors.clear();
                        Globals.successors.put(id,new Socket(ip,portlisten));
                    }
                    if(possible_succesor==0)
                    {
                        Globals.predid=id;
                        Globals.predport=portlisten;
                        Globals.predip=ip;
                        Globals.predecessors.clear();
                        Globals.predecessors.put(id,new Socket(ip,portlisten));   
                    }
                    System.out.println("Pred "+Globals.predid+" Predport "+
                    Globals.predport+" Predip "+
                    Globals.predip+" Predecessors "+
                    Globals.predecessors);   

                    System.out.println("Succ "+Globals.succid+" Succport "+
                    Globals.succport+" Succip "+
                    Globals.succip+" Successors "+
                    Globals.successors);

                    System.out.println("ipAddress is: "+Globals.ipAddress);
                    System.out.println("Possible_successor is: "+possible_succesor);
                    Socket succ=new Socket(Globals.ipAddress.get(possible_succesor),Globals.ports.get(possible_succesor));
                    Socket pred=new Socket(Globals.ipAddress.get(possible_pred),Globals.ports.get(possible_pred));

                    Globals.successors.put(possible_succesor,succ);
                    Globals.predecessors.put(possible_pred,pred);
                    Globals.ids.add(id);
                    System.out.println("Beginning of enter ip Address: "+Globals.ipAddress);

                }
                else if(inputfromServer.equals("ExittoSucc"))
                {
                    int idrem=Integer.parseInt(in.readLine());
                    Globals.ports.remove(idrem);
                    Globals.ipAddress.remove(idrem);
                    Globals.ids.remove(idrem);

                    String hashMapString=in.readLine();

                    String[] pairs = hashMapString.split(",");
                    for (String pair : pairs) {
                        String[] keyValue = pair.split(":");
                        Globals.keyvalue.put(Integer.parseInt(keyValue[0]), keyValue[1]);
                    }
                    System.out.println("The updated hash map is: "+Globals.keyvalue);
                    Globals.predid=Integer.parseInt(in.readLine());
                    Globals.predip=in.readLine();
                    Globals.predport=Integer.parseInt(in.readLine());
                    Globals.predecessors.remove(idrem);
                    Globals.predecessors.put(Globals.predid,new Socket(Globals.predip,Globals.predport));
                    System.out.println("The modified values are: ");
                    System.out.println("Predid "+Globals.predid+" predip "+
                    Globals.predip+" predport "+
                    Globals.predport+" Predecessors "+
                    Globals.predecessors);


                }
                else if(inputfromServer.equals("ExittoPred"))
                {
                    int idrem=Integer.parseInt(in.readLine());
                    Globals.ports.remove(idrem);
                    Globals.ipAddress.remove(idrem);
                    Globals.ids.remove(idrem);

                    Globals.succid=Integer.parseInt(in.readLine());
                    Globals.succip=in.readLine();
                    Globals.succport=Integer.parseInt(in.readLine());
                    Globals.successors.remove(idrem);
                    Globals.successors.put(Globals.succid,new Socket(Globals.succip,Globals.succport));

                    System.out.println("The modified values are: ");
                    System.out.println("Succid "+Globals.succid+" succip "+
                    Globals.succip+" succport "+
                    Globals.succport+" successors "+
                    Globals.successors);
                }
                inputfromServer=in.readLine();
            }
        

    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
}
}
class UserThread implements Runnable{
    int port;
    int id;
    UserThread(int id, int port){
        this.id=id;
        this.port=port;
    }
    public void run()
    {
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("bootstrapserver>");
            String input=br.readLine();

            while(!input.equals("quit"))
            {  
                String serverseq="0 "; 
                if(input.startsWith("lookup"))
                {
                    System.out.println("Inside Look up "+Globals.id);
                    String[] parts=input.split(" ");
                    int key=Integer.parseInt(parts[1]);
                    
                    if(key>Globals.predid)
                    {
                        if(Globals.keyvalue.containsKey(key))
                        {
                            System.out.println("Key Found with Value: "+Globals.keyvalue.get(key));
                            System.out.println("Servers traversed for this lookup: "+serverseq);
                        }
                        else
                        System.out.println("Key Not Found");
                    }
                    else{
                        Socket s=Globals.successors.get(Globals.succid);
                        System.out.println("Connecting to successor: "+Globals.succid);
                        BufferedReader reader5 = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        PrintWriter pw2=new PrintWriter(s.getOutputStream());
                        pw2.println("lookup");
                        pw2.flush();
                        pw2.println(key);
                        pw2.flush();
                        int flag;
                        flag=Integer.parseInt(reader5.readLine());

                        if(flag==0)
                        {
                            String result="Key Not Found";
                            String sr1=reader5.readLine();
                            serverseq=serverseq+" "+sr1;
                            System.out.println(result);
                            System.out.println("The servers traversed are: "+serverseq);

                        }
                        else{
                        String response1=reader5.readLine();
                        System.out.println("The value of the key is: "+response1);
                        String sr1=reader5.readLine();
                        serverseq=serverseq+" "+sr1;

                        System.out.println("The servers traversed are: "+serverseq);
                        String[] parts1=serverseq.split(" ");
                        System.out.println("Key is found in: "+parts1[parts1.length-1]);

                        }

                    }
                    
                }
                else if(input.startsWith("insert"))
                {
                    serverseq="0";
                    System.out.println("Inside insert "+Globals.id);
                    String[] parts=input.split(" ");
                    int key=Integer.parseInt(parts[1]);
                    String value1=parts[2];
                    if(key>Globals.predid)
                    {
                        Globals.keyvalue.put(key,value1);
                        System.out.println("The key is inserted into server: "+Globals.id);
                        System.out.println("Server sequesnce is: "+Globals.id);

                    }
                    else{
                        Socket s=Globals.successors.get(Globals.succid);
                        System.out.println("Connecting to successor: "+Globals.succid);
                        System.out.println(s);
                        BufferedReader reader5 = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        PrintWriter pw2=new PrintWriter(s.getOutputStream());
                        pw2.println("insert");
                        pw2.flush();
                        pw2.println(key);
                        pw2.flush();
                        pw2.println(value1);
                        pw2.flush();
                        String response1=reader5.readLine();
                        serverseq=serverseq+" "+response1;

                        System.out.println("Serversequence is: "+serverseq);
                        String[] parts1=serverseq.split(" ");
                        String res=parts1[parts1.length-1];

                        System.out.println("Inserted in the server with server id: "+res);

                        

                    }
                    
                }
                if(input.startsWith("delete"))
                {
                    System.out.println("Inside Delete "+Globals.id);
                    String[] parts=input.split(" ");
                    int key=Integer.parseInt(parts[1]);
                    
                    if(key>Globals.predid)
                    {
                        if(Globals.keyvalue.containsKey(key))
                        {
                            Globals.keyvalue.remove(key);
                            System.out.println("Key Deleted with Value: "+Globals.keyvalue.get(key));
                            System.out.println("Servers traversed for this lookup: "+serverseq);
                        }
                        else
                        System.out.println("Key Not Found");
                    }
                    else{
                        Socket s=Globals.successors.get(Globals.succid);
                        System.out.println("Connecting to successor: "+Globals.succid);
                        BufferedReader reader5 = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        PrintWriter pw2=new PrintWriter(s.getOutputStream());
                        pw2.println("delete");
                        pw2.flush();
                        pw2.println(key);
                        pw2.flush();
                        int flag;
                        flag=Integer.parseInt(reader5.readLine());

                        if(flag==0)
                        {
                            String result="Key Not Found";
                            String sr1=reader5.readLine();
                            serverseq=serverseq+sr1;
                            System.out.println(result);
                            System.out.println("The servers traversed are: "+serverseq);
                        }
                        else{
                        String response1=reader5.readLine();
                        System.out.println("Deleted successfully");
                        String sr1=reader5.readLine();
                        serverseq=serverseq+" "+sr1;

                        System.out.println("The servers traversed are: "+serverseq);

                        }

                    }
                    
                }

                System.out.print("bootstrapserver>");
                input=br.readLine();

                }
              
    
            }



        
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

