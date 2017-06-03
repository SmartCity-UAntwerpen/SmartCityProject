package be.uantwerpen.sc.models.sim;

import be.uantwerpen.sc.services.sockets.SimSocket;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Thomas on 5/05/2017.
 */
// Class for simulated RobotCity robot
public class SimCar extends SimVehicle
{
    private String ip;
    private int port;

    public SimCar()
    {
        super("bot", 0, 90);

        this.type = "car";
        this.ip = "localhost";
        this.port = 0;
    }

    public SimCar(String ip, int port)
    {
        super("bot", 0, 90);

        this.type = "car";
        this.ip = ip;
        this.port = port;
    }

    public SimCar(String name, int startPoint, long simSpeed)
    {
        super(name, startPoint, simSpeed);

        this.type = "car";
        this.ip = "localhost";
        this.port = 0;
    }

    @Override
    protected void simulationProcess() {
    }

    @Override
    protected boolean sendCreate() {
        //Create socket connection to RobotCity Core
        try {
            SimSocket simSocket = new SimSocket(new Socket(this.ip, this.port));
            simSocket.setTimeOut(500);

            //Send data over socket
            simSocket.sendMessage("create " + id + "\n");

            String response = simSocket.getMessage();
            while(response == null)
            {
                response = simSocket.getMessage();
            }
            //Receive ACK when RobotCity car is successfully created in the core
            if(response.equalsIgnoreCase("ACK")) {
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
        //Create socket connection to RobotCity Core
        try {
            SimSocket simSocket = new SimSocket(new Socket(this.ip, this.port));
            simSocket.setTimeOut(500);

            //Send data over socket
            simSocket.sendMessage("run " + id + "\n");

            String response = simSocket.getMessage();
            while(response == null)
            {
                response = simSocket.getMessage();
            }
            //Receive ACK when RobotCity car is successfully started in the core
            if(response.equalsIgnoreCase("ACK")) {
                System.out.println("Start acknowledge received.");
                simSocket.close();
            } else if(response.equalsIgnoreCase("NACK")) {
                System.out.println("NACK received. Startpoint property was not set.");
                simSocket.close();
                return false;
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
        //Create socket connection to RobotCity Core
        try {
            SimSocket simSocket = new SimSocket(new Socket(this.ip, this.port));
            simSocket.setTimeOut(500);

            //Send data over socket
            simSocket.sendMessage("stop " + id + "\n");

            String response = simSocket.getMessage();
            while(response == null)
            {
                response = simSocket.getMessage();
            }
            //Receive ACK when RobotCity car is successfully stopped in the core
            if(response.equalsIgnoreCase("ACK")) {
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
    protected boolean sendRestart() {
        //Create socket connection to RobotCity Core
        try {
            SimSocket simSocket = new SimSocket(new Socket(this.ip, this.port));
            simSocket.setTimeOut(500);

            //Send data over socket
            simSocket.sendMessage("restart " + id + "\n");

            String response = simSocket.getMessage();
            while(response == null)
            {
                response = simSocket.getMessage();
            }
            //Receive ACK when RobotCity car is successfully restarted in the core
            if(response.equalsIgnoreCase("ACK")) {
                System.out.println("Restart acknowledge received.");
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
        //Create socket connection to RobotCity Core
        try {
            SimSocket simSocket = new SimSocket(new Socket(this.ip, this.port));
            simSocket.setTimeOut(500);

            //Send data over socket
            simSocket.sendMessage("kill " + id + "\n");

            String response = simSocket.getMessage();
            while(response == null)
            {
                response = simSocket.getMessage();
            }
            //Receive ACK when RobotCity car is successfully removed in the core
            if(response.equalsIgnoreCase("ACK")) {
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
            //Create socket connection to RobotCity Core
            try {
                SimSocket simSocket = new SimSocket(new Socket(this.ip, this.port));
                simSocket.setTimeOut(500);

                //Send data over socket
                simSocket.sendMessage("set " + id + " " + property + " " + value + "\n");

                String response = simSocket.getMessage();
                while(response == null)
                {
                    response = simSocket.getMessage();
                }
                //Receive ACK when RobotCity car property is successfully set in the core
                if(response.equalsIgnoreCase("ACK")) {
                    System.out.println("Set acknowledge received.");
                    simSocket.close();
                } else if(response.equalsIgnoreCase("NACK")) {
                    System.out.println("NACK received. Property could not be set in RobotCity core.");
                    simSocket.close();
                    return false;
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

