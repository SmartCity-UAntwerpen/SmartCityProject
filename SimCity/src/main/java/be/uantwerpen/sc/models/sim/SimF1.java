package be.uantwerpen.sc.models.sim;

import be.uantwerpen.sc.services.sockets.SimSocket;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Thomas on 5/05/2017.
 */
// Class for simulated F1-car
public class SimF1 extends SimVehicle
{
    private String ip;
    private int port;

    public SimF1()
    {
        super("bot", 0, 90);

        this.type = "f1";
        this.ip = "localhost";
        this.port = 0;
    }

    public SimF1(String ip, int port)
    {
        super("bot", 0, 90);

        this.type = "f1";
        this.ip = ip;
        this.port = port;
    }

    public SimF1(String name, int startPoint, long simSpeed)
    {
        super(name, startPoint, simSpeed);

        this.type = "f1";
        this.ip = "localhost";
        this.port = 0;
    }

    @Override
    protected void simulationProcess() {
    }

    @Override
    protected boolean sendCreate() {
        //Create socket connection to F1 Core
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
            //Receive ACK when F1 car is successfully created in the core
            if(response.equalsIgnoreCase("ACK")) {
                System.out.println("Create acknowledge received.");
                simSocket.close();
            } else if(response.equalsIgnoreCase("NACK")) {
                System.out.println("NACK received. Bot could not be created.");
                simSocket.close();
                return false;
            } else {
                System.out.println("Unknown response received. Bot was stopped.");
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
        //Create socket connection to F1 Core
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
            //Receive ACK when F1 car is successfully started in the core
            if(response.equalsIgnoreCase("ACK")) {
                System.out.println("Start acknowledge received.");
                simSocket.close();
            } else if(response.equalsIgnoreCase("NACK")) {
                System.out.println("NACK received. Startpoint property was not set.");
                simSocket.close();
                return false;
            } else {
                System.out.println("Unknown response received. Bot was stopped.");
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
        //Create socket connection to F1 Core
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
            //Receive ACK when F1 car is successfully stopped in the core
            if(response.equalsIgnoreCase("ACK")) {
                System.out.println("Stop acknowledge received.");
                simSocket.close();
            } else if(response.equalsIgnoreCase("NACK")) {
                System.out.println("NACK received. Bot could not be stopped.");
                simSocket.close();
                return false;
            } else {
                System.out.println("Unknown response received. Bot was stopped.");
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
        //Create socket connection to F1 Core
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
            //Receive ACK when F1 car is successfully restarted in the core
            if(response.equalsIgnoreCase("ACK")) {
                System.out.println("Restart acknowledge received.");
                simSocket.close();
            } else if(response.equalsIgnoreCase("NACK")) {
                System.out.println("NACK received. Bot could not be restarted.");
                simSocket.close();
                return false;
            } else {
                System.out.println("Unknown response received. Bot was stopped.");
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
        //Create socket connection to F1 Core
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
            //Receive ACK when F1 car is successfully removed in the core
            if(response.equalsIgnoreCase("ACK")) {
                System.out.println("Remove acknowledge received.");
                simSocket.close();
            } else if(response.equalsIgnoreCase("NACK")) {
                System.out.println("NACK received. Bot could not be removed.");
                simSocket.close();
                return false;
            } else {
                System.out.println("Unknown response received. Bot was stopped.");
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
            //Create socket connection to F1 Core
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
                //Receive ACK when F1 car property is successfully set in the core
                if(response.equalsIgnoreCase("ACK")) {
                    System.out.println("Set acknowledge received.");
                    simSocket.close();
                } else if(response.equalsIgnoreCase("NACK")) {
                    System.out.println("NACK received. Property could not be set in F1 core.");
                    simSocket.close();
                    return false;
                } else {
                    System.out.println("Unknown response received. Bot was stopped.");
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

