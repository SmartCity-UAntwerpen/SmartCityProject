package be.uantwerpen.sc.models.sim;

import be.uantwerpen.sc.models.sim.messages.SimBotStatus;
import be.uantwerpen.sc.services.SimCoresService;
import be.uantwerpen.sc.services.sockets.SimSocketService;
import be.uantwerpen.sc.tools.Terminal;
import be.uantwerpen.sc.tools.simulators.vehicles.drones.smartdrone.SmartDrone;

import java.util.ArrayList;
import java.util.List;

public class SimDrone extends SimVehicle{
    {
        private SimCore droneCore;
        private SimSocketService taskSocketService;
        private SimSocketService eventSocketService;

        public SimDrone()
        {
            super("bot", 0, 70);

            this.taskSocketService = new SimSocketService();
            this.eventSocketService = new SimSocketService();
            this.type = "drone";
            this.droneCore = null;
        }

        public SimDrone(String name, int startPoint, long simSpeed)
        {
            super(name, startPoint, simSpeed);

            this.taskSocketService = new SimSocketService();
            this.eventSocketService = new SimSocketService();
            this.type = "drone";
            this.droneCore = null;
        }

        @Override
        public SimBotStatus getBotStatus()
        {
            String status;

            if(droneCore != null)
            {
                status = droneCore.getStatus().toString();
            }
            else
            {
                status = "Off";
            }

            SimBotStatus simBotStatus = new SimBotStatus(this.id, this.type, this.name, status);

            return simBotStatus;
        }

        @Override
        public String getLog()
        {
            String log;

            if(droneCore != null)
            {
                log = droneCore.getLog();
            }
            else
            {
                log = "No log available yet! Please, start the bot first.";
            }

            return log;
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
        protected void simulationProcess()
        {
            Thread commandSocketServiceThread = new Thread(this.taskSocketService);
            Thread eventSocketServiceThread = new Thread(this.eventSocketService);
            commandSocketServiceThread.start();
            eventSocketServiceThread.start();

            //Wait for server sockets to initialise
            while((this.taskSocketService.getListeningPort() == 0 || this.eventSocketService.getListeningPort() == 0) && this.isRunning());

            List<String> coreArguments = new ArrayList<String>();

            //Create core process arguments
            //Setup ports to simulated D-Core (CREATE THIS CORE!!!)
            coreArguments.add("-Ddrone.dcore.taskport=" + this.taskSocketService.getListeningPort());
            coreArguments.add("-Ddrone.dcore.eventport=" + this.eventSocketService.getListeningPort());
            //Select random free port
            coreArguments.add("-Dserver.port=0");

            if(this.droneCore == null)
            {
                this.droneCore = SimCoresService.getSimulationCore(this.type);
            }

            if(this.droneCore != null)
            {
                this.droneCore.start(coreArguments);
            }
            else
            {
                //No core available
                Terminal.printTerminalError("Could not run Core for Drone simulation!");

                this.stop();

                return;
            }

            //Simulation process of SimDrone
            this.simulateDrone();

            //Stop simulation
            this.droneCore.stop();

            commandSocketServiceThread.interrupt();
            eventSocketServiceThread.interrupt();

            //Wait for socket service to terminate
            while(commandSocketServiceThread.isAlive() || eventSocketServiceThread.isAlive());
        }

        private void simulateDrone()
        {
            SmartDrone droneSimulation = new SmartDrone(this.name, this.simSpeed, this.serverCoreIP, this.serverCorePort);

            boolean commandSocketReset = true;
            boolean eventSocketReset = true;

            long lastSimulationTime = System.currentTimeMillis();

            //Initialise simulation
            if(!droneSimulation.initSimulation(this.startPoint))
            {
                System.err.println("Could not initialise SmartDrone simulation!");
                System.err.println("Simulation will abort...");

                this.running = false;
            }

            while(this.isRunning())
            {
                //Calculated simulation time
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - lastSimulationTime;
                lastSimulationTime = currentTime;

                //Verify sockets
                droneSimulation.checkConnections(taskSocketService, eventSocketService);

                //Update simulation
                droneSimulation.updateSimulation(elapsedTime);

                try
                {
                    //Sleep simulation for 10 ms (simulation resolution > 10 ms)
                    Thread.sleep(10);
                }
                catch(Exception e)
                {
                    //Thread is interrupted
                }
            }

            if(!droneSimulation.stopSimulation())
            {
                System.err.println("Simulation layer is not stopped properly!");
            }
        }
    }

}
