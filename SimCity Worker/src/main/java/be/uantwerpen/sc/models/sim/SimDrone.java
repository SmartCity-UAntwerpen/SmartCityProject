package be.uantwerpen.sc.models.sim;

import be.uantwerpen.sc.services.sockets.SimSocketService;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

//TODO LOAD IP AND PORT FOR DRONE CORE FROM PROPERTIES FILE
public class SimDrone extends SimVehicle
{
    private SimSocketService taskSocketService;
    //DOES NOT WORK YET
    @Value("${ds.core.ip:146.175.40.35}")
    private String ip;
    @Value("${ds.core.port:4321}")
    private int port;

    public SimDrone()
    {
        super("bot", 0, 90);

        this.taskSocketService = new SimSocketService();
        this.type = "drone";
    }

    public SimDrone(String name, int startPoint, long simSpeed)
    {
        super(name, startPoint, simSpeed);

        this.taskSocketService = new SimSocketService();
        this.type = "drone";
    }

    @Override
    protected void simulationProcess() {
        //Create socket connection to Drone Core
        Socket socket;
        try{
            socket = new Socket("146.175.40.35", 4321);
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Send data over socket
            String text = ("create drone");
            socketOut.println(text);

            //Receive ACK when drone is succesfully registered in the SmartCityCore
            String line = socketIn.readLine();
            if(line.equalsIgnoreCase("ack")) {
                System.out.println("Start acknowledge received: " + line);
            } else {
                socket.close();
                this.stop();
            }

        } catch (UnknownHostException e) {
            this.stop();
            System.out.println("Unknown host");
        } catch  (IOException e) {
            this.stop();
            System.out.println("No I/O");
        }
    }
}

