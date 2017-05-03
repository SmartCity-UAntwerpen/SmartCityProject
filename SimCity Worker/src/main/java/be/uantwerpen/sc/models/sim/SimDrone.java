package be.uantwerpen.sc.models.sim;

import be.uantwerpen.sc.services.sockets.SimSocket;
import be.uantwerpen.sc.services.sockets.SimSocketService;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class SimDrone extends SimVehicle
{
    private String ip;
    private int port;

    public SimDrone()
    {
        super("bot", 0, 90);

        this.type = "drone";
        this.ip = "localhost";
        this.port = 0;
    }

    public SimDrone(String ip, int port)
    {
        super("bot", 0, 90);

        this.type = "drone";
        this.ip = ip;
        this.port = port;
    }

    public SimDrone(String name, int startPoint, long simSpeed)
    {
        super(name, startPoint, simSpeed);

        this.type = "drone";
        this.ip = "localhost";
        this.port = 0;
    }

    @Override
    protected void simulationProcess() {
    }

    @Override
    protected boolean sendCreate() {
        //Create socket connection to Drone Core
        try {
            SimSocket simSocket = new SimSocket(new Socket(this.ip, this.port));
            simSocket.setTimeOut(500);

            //Send data over socket
            simSocket.sendMessage("create " + id + "\n");

            //Receive ACK when drone is succesfully created in the core
            if(simSocket.getMessage().equalsIgnoreCase("ACK")) {
                System.out.println("Create acknowledge received.");
                simSocket.close();
            } else {
                simSocket.close();
                this.stop();
                return false;
            }
        } catch (IOException e) {
            System.out.println("I/O exception occurred!");
            this.stop();
            return false;
        }
        return true;
    }

    @Override
    protected boolean sendStart() {
        //Create socket connection to Drone Core
        try {
            SimSocket simSocket = new SimSocket(new Socket(this.ip, this.port));
            simSocket.setTimeOut(500);

            //Send data over socket
            simSocket.sendMessage("run " + id + "\n");

            //Receive ACK when drone is succesfully started in the core
            if(simSocket.getMessage().equalsIgnoreCase("ACK")) {
                System.out.println("Start acknowledge received.");
                simSocket.close();
            } else {
                simSocket.close();
                this.stop();
                return false;
            }
        } catch (IOException e) {
            System.out.println("I/O exception occurred!");
            this.stop();
            return false;
        }
        return true;
    }

    @Override
    protected boolean sendStop() {
        //Create socket connection to Drone Core
        try {
            SimSocket simSocket = new SimSocket(new Socket(this.ip, this.port));
            simSocket.setTimeOut(500);

            //Send data over socket
            simSocket.sendMessage("stop " + id + "\n");

            //Receive ACK when drone is succesfully stopped in the core
            if(simSocket.getMessage().equalsIgnoreCase("ACK")) {
                System.out.println("Stop acknowledge received.");
                simSocket.close();
            } else {
                simSocket.close();
                this.stop();
                return false;
            }
        } catch (IOException e) {
            System.out.println("I/O exception occurred!");
            this.stop();
            return false;
        }
        return true;
    }

    @Override
    protected boolean sendRemove() {
        //Create socket connection to Drone Core
        try {
            SimSocket simSocket = new SimSocket(new Socket(this.ip, this.port));
            simSocket.setTimeOut(500);

            //Send data over socket
            simSocket.sendMessage("kill " + id + "\n");

            //Receive ACK when drone is succesfully removed in the core
            if(simSocket.getMessage().equalsIgnoreCase("ACK")) {
                System.out.println("Remove acknowledge received.");
                simSocket.close();
            } else {
                simSocket.close();
                this.stop();
                return false;
            }
        } catch (IOException e) {
            System.out.println("I/O exception occurred!");
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
            try {
                SimSocket simSocket = new SimSocket(new Socket(this.ip, this.port));
                simSocket.setTimeOut(500);

                //Send data over socket
                simSocket.sendMessage("set " + id + " " + property + " " + value + "\n");

                //Receive ACK when drone property is succesfully set in the core
                if(simSocket.getMessage().equalsIgnoreCase("ACK")) {
                    System.out.println("Set acknowledge received.");
                    simSocket.close();
                } else {
                    simSocket.close();
                    this.stop();
                    return false;
                }
            } catch (IOException e) {
                System.out.println("I/O exception occurred!");
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

