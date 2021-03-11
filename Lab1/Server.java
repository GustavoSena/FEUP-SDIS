import java.io.*;
import java.net.*;
import java.util.Hashtable;


public class Server {

    private int port;
    private DatagramSocket serverSocket;
    private Hashtable<String, String> lookupTable;
    private static boolean run=true;
    

    public static void main(String[] args){ //java Server <port number> 
        if(args.length!=1){
            System.out.println("Wrong format");
            return;
        }

        Server server = new Server();
        try{
            
            server.init(args);
            
            
            while(run)
                server.run();
            }
        catch(Exception exp){
            System.out.println("Found exception: "+ exp);
        }
        
            
        server.close();

    }

    private void init(String[] args)throws IOException{
        this.port = Integer.parseInt(args[0]);
        this.serverSocket = new DatagramSocket(this.port);
        this.lookupTable = new Hashtable<>();
    }

    public void run() throws IOException{
        byte[] buffer = new byte[512];
        DatagramPacket packet = new DatagramPacket(buffer, 512);
        this.serverSocket.receive(packet);
        this.processPackage(packet);

    }

    private void processPackage(DatagramPacket packet) throws IOException{
        String received = new String(packet.getData());
        System.out.println("Server: " + received);
        String msg = this.buildReply(this.parseMessage(received));

        byte[] buffer = msg.getBytes();
        int clientPort = packet.getPort();
        InetAddress clientAddress = packet.getAddress();
        DatagramPacket newPacket = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);

        this.serverSocket.send(newPacket);
    }

    
  
    private String buildReply(String msg){
        if(msg.equals("-1"))
            return "ALREADY_IN_TABLE";
        if(msg.contains("."))
            return "SUCCESS - "+ msg;
        if(!msg.equals("ERROR") && !msg.equals("NOT_FOUND"))
            return "SUCCESS - "+ msg +" previous entries";
        return msg;
    }

    private String parseMessage(String received) {
        String[] msg = received.split(" ");
        for(int i = 0; i < msg.length; i++){
            msg[i] = msg[i].trim();
        }

        if(msg[0].equals("REGISTER")){
            if(!this.lookupTable.containsKey(msg[1]))  {
                this.lookupTable.put(msg[1],msg[2]);
                return Integer.toString(this.lookupTable.size()-1);
            }
            else
                return "-1";
        }
        else if(msg[0].equals("LOOKUP")){
            if(!this.lookupTable.containsKey(msg[1])) return "NOT_FOUND";
            return this.lookupTable.get(msg[1]);
        }
           
        return "ERROR";
    }

    private void close(){
        this.serverSocket.close();
    }

}

