import java.io.*;
import java.net.*;


public class Client {

    private int port;
    private DatagramSocket clientSocket;
    private String request = "";
    private String host;


    public static void main(String[] args){//java Client <host> <port> <oper> <opnd>*

        if(args.length !=4 && args.length!=5){
            System.out.println("Wrong format");
            return;
        }

        

        Client client = new Client();
        try{

        
            if(client.parseArgs(args)!=0)return;
            client.run();
        }catch (IOException exp){
            System.out.println("Found exception: "+ exp);
        }

    }

    private int parseArgs(String[] args) throws IOException{
        this.host = args[0].trim();
        this.port = Integer.parseInt(args[1].trim());
        
        String reg = args[2].trim();
        if(reg.toLowerCase().equals("register"))
            this.request = "REGISTER " + args[3].trim() + " " + args[4].trim();
        else if(reg.toLowerCase().equals("lookup"))
            this.request = "LOOKUP " + args[3].trim();
        else{
            System.out.println("Invalid operation");
            return -1;
        }
        this.clientSocket = new DatagramSocket();
        return 0;
    }

    private void run() throws IOException {
        InetAddress add = InetAddress.getByName(this.host);
        byte[] buffer = this.request.getBytes();
        DatagramPacket requestDatagram = new DatagramPacket(buffer, buffer.length, add, this.port);
        this.clientSocket.send(requestDatagram);

        byte[] bufferR = new byte[512];
        DatagramPacket responseDatagram = new DatagramPacket(bufferR, bufferR.length);
        this.clientSocket.receive(responseDatagram);
        String response = new String(responseDatagram.getData(), 0, responseDatagram.getLength());
        
        System.out.println("Client Request: "+this.request+" / Recieved Response: "+ response);
        
        this.clientSocket.close();
    }

}

