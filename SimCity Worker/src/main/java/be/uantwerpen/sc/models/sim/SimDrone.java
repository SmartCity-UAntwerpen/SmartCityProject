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
    @Value("${ds.core.ip:146.175.140.35}")
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
    }

    @Override
    protected boolean sendCreate() {
        //Create socket connection to Drone Core
        Socket socket;
        try{
            socket = new Socket("146.175.140.35", 4321);
            socket.setSoTimeout(200);
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Send data over socket
            String text = ("create " + id);
            socketOut.println(text);

            //Receive ACK when drone is succesfully registered in the SmartCityCore
            String line = socketIn.readLine();
            if(line.equalsIgnoreCase("ack")) {
                System.out.println("Create acknowledge received: " + line);
                socket.close();
            } else {
                socket.close();
                this.stop();
                return false;
            }

        } catch (UnknownHostException e) {
            System.out.println("Unknown host");
            this.stop();
            return false;
        } catch  (IOException e) {
            System.out.println("No I/O");
            this.stop();
            return false;
        }
        return true;
    }

    @Override
    protected boolean sendStart() {
        //Create socket connection to Drone Core
        Socket socket;
        try{
            socket = new Socket("146.175.140.35", 4321);
            socket.setSoTimeout(200);
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Send data over socket
            String text = ("run " + id);
            socketOut.println(text);

            //Receive ACK when drone is succesfully started in the SmartCityCore
            String line = socketIn.readLine();
            if(line.equalsIgnoreCase("ack")) {
                System.out.println("Start acknowledge received: " + line);
                socket.close();
            } else {
                socket.close();
                this.stop();
                return false;
            }

        } catch (UnknownHostException e) {
            System.out.println("Unknown host");
            this.stop();
            return false;
        } catch  (IOException e) {
            System.out.println("No I/O");
            this.stop();
            return false;
        }

        /*//ALTERNATIVE USING SIMSOCKET
        try {
            SimSocket simSocket = new SimSocket(new Socket("146.175.140.35", 4321));
            simSocket.setTimeOut(1000);
            simSocket.
            simSocket.sendMessage("run " + id + " " + name + " " + startPoint + " " + simSpeed);
            if(simSocket.getMessage().equalsIgnoreCase("ACK")) {
                System.out.println("Start drone acknowledge received");
            } else {
                System.out.print("TEST");
                simSocket.close();
                this.stop();
                return false;
            }
        } catch (IOException e) {
            System.out.println("I/O exception occurred!");
            this.stop();
            return false;
        }*/
        return true;
    }

    @Override
    protected boolean sendStop() {
        //Create socket connection to Drone Core
        Socket socket;
        try{
            socket = new Socket("146.175.140.35", 4321);
            socket.setSoTimeout(1000);
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Send data over socket
            String text = ("stop " + id);
            socketOut.println(text);

            //Receive ACK when drone is succesfully stopped in the DroneCore
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
            System.out.println("Unknown host");
            this.stop();
            return false;
        } catch  (IOException e) {
            System.out.println("No I/O");
            this.stop();
            return false;
        }
        return true;
    }

    @Override
    protected boolean sendRemove() {
        //Create socket connection to Drone Core
        Socket socket;
        try{
            socket = new Socket("146.175.140.35", 4321);
            socket.setSoTimeout(1000);
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Send data over socket
            String text = ("kill " + id);
            socketOut.println(text);

            //Receive ACK when drone is succesfully removed from the SmartCityCore
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
            System.out.println("Unknown host");
            this.stop();
            return false;
        } catch  (IOException e) {
            System.out.println("No I/O");
            this.stop();
            return false;
        }
        return true;
    }

    @Override
    public boolean parseProperty(String property, String value) throws Exception
    {
        if(super.parseProperty(property, value))
        {
            //Create socket connection to Drone Core
            Socket socket;
            try{
                socket = new Socket("146.175.140.35", 4321);
                socket.setSoTimeout(1000);
                PrintWriter socketOut = new PrintWriter(socket.getOutputStream(),true);
                BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //Send data over socket
                String text = ("set " + id + " " + property + " " + value);
                socketOut.println(text);

                //Receive ACK when drone property is succesfully set in the DroneCore
                String line = socketIn.readLine();
                if(line.equalsIgnoreCase("ack")) {
                    System.out.println("Set acknowledge received: " + line);
                    socket.close();
                } else {
                    socket.close();
                    this.stop();
                    return false;
                }

            } catch (UnknownHostException e) {
                System.out.println("Unknown host");
                this.stop();
                return false;
            } catch  (IOException e) {
                System.out.println("No I/O");
                this.stop();
                return false;
            }
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

