package be.uantwerpen.sc.models.sim;

import be.uantwerpen.sc.services.sockets.SimSocket;
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
            socket.setSoTimeout(1000);
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Send data over socket
            String text = ("run " + id + name + startPoint + simSpeed);
            socketOut.println(text);

            //Receive ACK when drone is succesfully registered in the SmartCityCore
            String line = socketIn.readLine();
            if(line.equalsIgnoreCase("ack")) {
                System.out.println("Start acknowledge received: " + line);
                socket.close();
            } else {
                socket.close();
                this.stop();
                return;
            }

        } catch (UnknownHostException e) {
            this.stop();
            System.out.println("Unknown host");
            return;
        } catch  (IOException e) {
            this.stop();
            System.out.println("No I/O");
            return;
        }

        //ALTERNATIVE USING SIMSOCKET
        /*try {
            SimSocket simSocket = new SimSocket(new Socket("146.175.40.35", 4321));
            simSocket.setTimeOut(1000);
            simSocket.sendMessage("run " + id);
            if(simSocket.getMessage().equalsIgnoreCase("ACK")) {
                System.out.println("Start drone acknowledge received");
            } else {
                simSocket.close();
                this.stop();
            }
        } catch (IOException e) {
            this.stop();
            System.out.println("I/O exception occurred!");
        }*/
    }

    @Override
    protected boolean sendStop() {
        //Create socket connection to Drone Core
        Socket socket;
        try{
            socket = new Socket("146.175.40.35", 4321);
            socket.setSoTimeout(1000);
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Send data over socket
            String text = ("stop " + id);
            socketOut.println(text);

            //Receive ACK when drone is succesfully registered in the SmartCityCore
            String line = socketIn.readLine();
            if(line.equalsIgnoreCase("ack")) {
                System.out.println("Stop acknowledge received: " + line);
                socket.close();
            } else {
                socket.close();
                this.stop();
                return false;
            }

        } catch (UnknownHostException e) {
            this.stop();
            System.out.println("Unknown host");
            return false;
        } catch  (IOException e) {
            this.stop();
            System.out.println("No I/O");
            return false;
        }
        return true;
    }

    @Override
    protected boolean sendRemove() {
        //Create socket connection to Drone Core
        Socket socket;
        try{
            socket = new Socket("146.175.40.35", 4321);
            socket.setSoTimeout(1000);
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Send data over socket
            String text = ("stop " + id);
            socketOut.println(text);

            //Receive ACK when drone is succesfully registered in the SmartCityCore
            String line = socketIn.readLine();
            if(line.equalsIgnoreCase("ack")) {
                System.out.println("Remove acknowledge received: " + line);
                socket.close();
            } else {
                socket.close();
                this.stop();
                return false;
            }

        } catch (UnknownHostException e) {
            this.stop();
            System.out.println("Unknown host");
            return false;
        } catch  (IOException e) {
            this.stop();
            System.out.println("No I/O");
            return false;
        }
        return true;
    }

    @Override
    public boolean parseProperty(String property, String value) throws Exception
    {
        if(super.parseProperty(property, value))
        {
            return true;
        }

        switch(property.toLowerCase().trim())
        {
            default:
                return false;
        }
    }

    @Override
    public boolean parseProperty(String property) throws Exception
    {
        if(super.parseProperty(property))
        {
            return true;
        }

        switch(property.toLowerCase().trim())
        {
            default:
                return false;
        }
    }

    @Override
    public boolean printProperty(String property)
    {
        if(super.printProperty(property))
        {
            return true;
        }

        switch(property.toLowerCase().trim())
        {
            default:
                return false;
        }
    }
}

