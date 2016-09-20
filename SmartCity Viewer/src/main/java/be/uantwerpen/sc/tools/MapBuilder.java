package be.uantwerpen.sc.tools;

import be.uantwerpen.sc.models.Link;
import be.uantwerpen.sc.models.Point;
import be.uantwerpen.sc.models.sim.SimMap;
import be.uantwerpen.sc.models.sim.SimPath;
import be.uantwerpen.sc.models.sim.SimPoint;

import java.util.*;

enum ProcessingType{
    STRAIGHT,
    BEND,
    ADVANCED,
}

enum Update{
    UP,
    RIGHT
}

//TODO If a road is already detected at a location -> upgrade road to intersection
public class MapBuilder{

    SimMap simMap;

    ArrayList<Link> linkEntities;
    ArrayList<Point> pointEntities;

    ArrayList<Link> linkEntitiesPointGeneration;

    //Keep track of point locations
    ArrayList<SimPoint> simPoints = new ArrayList<>();

    //Keep track of paths
    SimPath path;
    ArrayList<SimPath> simPaths = new ArrayList<>();

    int locX, locY = 0;
    int currSizeX = 1;
    int currSizeY = 1;
    boolean mapReady = false;

    public MapBuilder(Link[] linkEntities, Point[] pointEntities){

        this.linkEntities = new ArrayList<Link>(Arrays.asList(linkEntities));
        this.linkEntitiesPointGeneration = new ArrayList<Link>(Arrays.asList(linkEntities));
        this.pointEntities = new ArrayList<Point>(Arrays.asList(pointEntities));

        simMap = new SimMap();
    }

    public SimMap getSimMap(){
        return simMap;
    }
    public ArrayList<SimPath> getSimPaths(){
        return simPaths;
    }

    public boolean getMapReady()
    {
        return this.mapReady;
    }

    public void logMap(){
        Iterator<ArrayList<Tile>> iterator = simMap.mapTiles.iterator();
        while(iterator.hasNext()){
            ArrayList<Tile> second = iterator.next();
            Iterator<Tile> iterator1 = second.iterator();
            while(iterator1.hasNext()){
                Tile tile = iterator1.next();
                if(tile == null){
                    System.out.print(" ");
                }else {
                    switch (tile) {
                        case PARKING:
                            System.out.print("P");
                            break;
                        case HORIZONTAL:
                            System.out.print("-");
                            break;
                        case VERTICAL:
                            System.out.print("|");
                            break;
                        default:
                            System.out.print("X");
                    }
                }
            }
            System.out.println();

        }
        System.out.println("----------------------------------------");
    }

    public int buildMap(){

        ProcessingType processingType = ProcessingType.STRAIGHT;

        Iterator<Link> linkIterator = linkEntities.iterator();

        boolean firstFound = false;
        while(linkIterator.hasNext() && firstFound == false){
            Link link = linkIterator.next();
            if(processFirstLink(link, processingType)){
                firstFound = true;
            }
        }

        //Process other links //TODO merge with above
        boolean nextMode = true;

        while(!linkEntities.isEmpty()){
            //Links need to be processed    //Reset LinkIterator
            linkIterator = linkEntities.iterator();
            nextMode = true;
            while(linkIterator.hasNext()){
                Link link = linkIterator.next();
                //Check if we can process this link with the current processing type
                System.out.println(link.getId());
                if(canProcessWithCurrentProcessingType(link, processingType)){
                    //Removes link on success, thus, we need to start linkIterator again    //TODO Find alternative
                    if(processLink(link, processingType)){
                        logMap();
                        linkIterator = linkEntities.iterator();
                        nextMode = false;
                    }
                }
            }
            if(nextMode){
                switch (processingType){
                    case STRAIGHT:
                        processingType = ProcessingType.BEND;
                        break;
                    case BEND:
                        processingType = ProcessingType.ADVANCED;
                        break;
                    default:
                        processingType = processingType.ADVANCED;
                        //System.err.println("NextMode selection error?");
                }
            }
        }
        updatePoints();
        simMap.setSizeX(currSizeX);
        simMap.setSizeY(currSizeY);
        return 0;
    }

    private boolean canProcessWithCurrentProcessingType(Link link, ProcessingType type){
        switch(type){
            case STRAIGHT:
                if(link.getStartDirection().equals("N") && link.getStopDirection().equals("S") || link.getStartDirection().equals("S") && link.getStopDirection().equals("N"))  // Vertical
                    return true;
                if(link.getStartDirection().equals("W") && link.getStopDirection().equals("E") || link.getStartDirection().equals("E") && link.getStopDirection().equals("W"))      //Horizontal
                    return true;
                break;
            case BEND:  //Both start and stop are already on map
                SimPoint point = new SimPoint(link.getStartPoint().getId());
                SimPoint point2 = new SimPoint(link.getStopPoint().getId());
                //check if start already on map
                if(!(simPoints.contains(point)) || !(simPoints.contains(point2)))
                    return false;
                //No break here

            case ADVANCED:
                //Just check for bend
                if(link.getStartDirection().equals("N") && link.getStopDirection().equals("E") || link.getStartDirection().equals("E") && link.getStopDirection().equals("S") || link.getStartDirection().equals("S") && link.getStopDirection().equals("W") || link.getStartDirection().equals("W") && link.getStopDirection().equals("N"))     // clockwise
                    return true;
                if(link.getStartDirection().equals("N") && link.getStopDirection().equals("W") || link.getStartDirection().equals("W") && link.getStopDirection().equals("S") || link.getStartDirection().equals("S") && link.getStopDirection().equals("E") || link.getStartDirection().equals("E") && link.getStopDirection().equals("N"))      //counter-clockwise
                    return true;
                break;
        }
        return false;
    }

    private boolean processFirstLink(Link link, ProcessingType type){
        SimPoint point = new SimPoint(link.getStartPoint().getId(), locX, locY);
        //Add first point to map
        simPoints.add(point);
        //Add link's start point to map
        simMap.mapTiles.get(0).set(0, Tile.POINT);

        return processLink(link, type);
    }

    private boolean processLink(Link link, ProcessingType type){
        //Create new path
        path = new SimPath(link.getId());
        path.setLength(link.getLength());

        SimPoint point = new SimPoint(link.getStartPoint().getId(), locX, locY);
        SimPoint point2 = new SimPoint(link.getStopPoint().getId(), locX, locY);
        boolean swap = false;
        //check if start already on map
        if(!(simPoints.contains(point))){   //If starting point of link not yet in list -> check if end is in list
            if(!simPoints.contains(point2)) {   //End point also not located on map -> Try again later
                return false;
            }else{
                //Point already in list and thus already on map
                //We know we can process -> switch current locX and locY to preexisting point
                point = simPoints.get(simPoints.indexOf(point2));
                locX = point.getPosX();
                locY = point.getPosY();
                swap = true;
            }
        }else{
            //Point already in list and thus already on map
            //We know we can process -> switch current locX and locY to preexisting point
            point = simPoints.get(simPoints.indexOf(point));
            locX = point.getPosX();
            locY = point.getPosY();
            //add Starting point to path
            path.addLoc(locX, locY);
        }

       
        int length = (int)(link.getLength()/190.0);

        switch(type){
            case STRAIGHT:  //Only one point needs to exist already
                switch (link.getStartDirection()){
                    case "N":
                        if(swap)
                            addTileBelow(length);
                        else
                            addTileAbove(length);
                        break;
                    case "E":
                        if(swap)
                            addTileLeft(length);
                        else
                            addTileRight(length);
                        break;
                    case "S":
                        if(swap)
                            addTileAbove(length);
                        else
                            addTileBelow(length);
                        break;
                    case "W":
                        if(swap)
                            addTileRight(length);
                        else
                            addTileLeft(length);
                        break;
                }
                break;
            case BEND:  //Both points need to exist

                //Calculate intersection
                int[] intersect = findIntersect(link);
                //Add info to map
                int distVert = locY - intersect[1];
                int distHor = locX - intersect[0];
                int distVert2 = intersect[1] - intersect[3];
                int distHor2 = intersect[0] - intersect[2];
                //Add route to intersect
                if(link.getStartDirection().equals("N") || link.getStartDirection().equals("S")){
                    if(link.getStartDirection().equals("N")) {
                        if(link.getStopDirection().equals("E"))
                        {
                            addTileVertical(distVert, Tile.SOUTH_WEST);
                        } else {   //WEST
                            addTileVertical(distVert, Tile.SOUTH_EAST);
                        }
                    }else{  //SOUTH
                        if(link.getStopDirection().equals("E"))
                        {
                            addTileVertical(distVert, Tile.NORTH_WEST);
                        } else {   //WEST
                            addTileVertical(distVert, Tile.NORTH_EAST);
                        }
                    }
                    locX = intersect[0];
                    locY = intersect[1];
                    addTileHorizontal(distHor2, Tile.POINT);
                }else{
                    if(link.getStartDirection().equals("E")){
                        if(link.getStopDirection().equals("N"))
                        {
                            addTileHorizontal(distHor, Tile.SOUTH_WEST);
                        } else {   //SOUTH
                            addTileHorizontal(distHor, Tile.NORTH_WEST);
                        }
                    }else{  //WEST
                        if(link.getStopDirection().equals("N"))
                        {
                            addTileHorizontal(distHor, Tile.SOUTH_EAST);
                        } else {   //SOUTH
                            addTileHorizontal(distHor, Tile.NORTH_EAST);
                        }
                    }
                    locX = intersect[0];
                    locY = intersect[1];
                    addTileVertical(distVert2, Tile.POINT);
                }
                //add route from intersect to end

                break;
            case ADVANCED:  //Only one point needs to exist
                //TODO Advanced
                break;
        }
        //Add endpoint to simPoints
        SimPoint endPoint;
        if(swap){
            endPoint = new SimPoint(link.getStartPoint().getId(), locX, locY);
        }else {
            endPoint = new SimPoint(link.getStopPoint().getId(), locX, locY);
        }
        //check if stop already on map
        if(!(simPoints.contains(endPoint))){
            simPoints.add(endPoint);
        }
        //Remove link from LinkEntities
        linkEntities.remove(link);
        //Finalize path
        simPaths.add(path);
        return true;
    }

    private int[] findIntersect(Link link){
        int[] intersect = {-1,-1,-1,-1};

        Long startId = link.getStartPoint().getId();
        SimPoint startPoint = new SimPoint(startId);
        int startIndex = simPoints.indexOf(startPoint);
        startPoint = simPoints.get(startIndex);

        Long stopId = link.getStopPoint().getId();
        SimPoint stopPoint = new SimPoint(stopId);
        int stopIndex = simPoints.indexOf(stopPoint);
        stopPoint = simPoints.get(stopIndex);

        if(link.getStartDirection().equals("N") || link.getStartDirection().equals("S"))
        {
            intersect[0] = startPoint.getPosX();
            intersect[1] = stopPoint.getPosY();
        } else if(link.getStartDirection().equals("E") || link.getStartDirection().equals("W"))
        {
            intersect[1] = startPoint.getPosY();
            intersect[0] = stopPoint.getPosX();
        }
        intersect[2] = stopPoint.getPosX();
        intersect[3] = stopPoint.getPosY();

        return intersect;
    }

    private void updateSimPointLocations(Update update){
        for (SimPoint point : simPoints) {
            switch (update) {
                case UP:
                    point.up();
                    break;
                case RIGHT:
                    point.right();
                    break;
            }
        }
        for(SimPath simPath : simPaths){
            switch (update) {
                case UP:
                    simPath.up();
                    break;
                case RIGHT:
                    simPath.right();
                    break;
            }
        }
    }

    private void addTileVertical(int length, Tile tile){
        boolean up = true;
        if(length < 0) {
            up = false;
            length = -length;
        }
        //Create road up untill the last 'length'
        if(up) {
            while (length > 1) {
                addTileAbove(Tile.VERTICAL);
                length--;
            }
            addTileAbove(tile);

        }else{
            while (length > 1) {
                addTileBelow(Tile.VERTICAL);
                length--;
            }
            addTileBelow(tile);
        }
    }

    private void addTileHorizontal(int length, Tile tile){
        boolean right = false;
        if(length < 0) {
            right = true;
            length = -length;
        }
        //Create road up untill the last 'length'
        if(right){
            while (length > 1) {
                addTileRight(Tile.HORIZONTAL);
                length--;
            }
            addTileRight(tile);

        }else{
            while (length > 1) {
                addTileLeft(Tile.HORIZONTAL);
                length--;
            }
            addTileLeft(tile);
        }
    }

    private void addTileAbove(int length){
        //Create road up untill the last 'length'
        while(length > 1){
            addTileAbove(Tile.VERTICAL);
            length--;
        }
        addTileAbove(Tile.POINT);
    }

    //TODO merge addTileAbove and below -> Same for Left and Right
    private void addTileAbove(Tile tile){
        if(locY == 0){
            simMap.mapTiles.add(0, new ArrayList<Tile>());  //Add new arraylist at first position
            //Update all point's location data
            updateSimPointLocations(Update.UP);
            //Fill new arrayList untill equal in size to current map
            for(int i = 0; i< currSizeX; i++){
                if(locX != i)
                    simMap.mapTiles.get(0).add(null);
                else{
                    simMap.mapTiles.get(0).add(tile);
                }
            }
            currSizeY++;
        }else{
            locY--;
            updateTile(simMap.mapTiles.get(locY).get(locX), tile);
        }
        path.addLoc(locX, locY);
    }

    private void addTileBelow(int length) {
        //Create road up untill the last 'length'
        while (length > 1) {
            addTileBelow(Tile.VERTICAL);
            length--;
        }
        addTileBelow(Tile.POINT);
    }

    private void addTileBelow(Tile tile){
        locY++;
        if(locY > currSizeY-1){
            simMap.mapTiles.add(new ArrayList<Tile>()); //Add new arraylist
            //No need to update simPoints
            currSizeY++;
            //Fill new arrayList untill equal in size to current map
            for(int i = 0; i< currSizeX; i++){
                if(locX != i)
                    simMap.mapTiles.get(currSizeY-1).add(null);
                else
                    simMap.mapTiles.get(currSizeY-1).add(tile);
            }
        }else{
            updateTile(simMap.mapTiles.get(locY).get(locX), tile);

        }
        path.addLoc(locX, locY);
    }

    private void addTileLeft(int length) {
        //Create road up untill the last 'length'
        while (length > 1) {
            addTileLeft(Tile.HORIZONTAL);
            length--;
        }
        addTileLeft(Tile.POINT);
    }

    private void addTileLeft(Tile tile){
        if(locX == 0){  //Add one element at start of every every row
            int i = -1;
            Iterator<ArrayList<Tile>> iterator = simMap.mapTiles.iterator();
            while(iterator.hasNext()) {
                ArrayList<Tile> rows = iterator.next();
                i++;
                if (i == locX) {
                    rows.add(0, tile);
                } else {
                    rows.add(0, null);
                }
            }
            updateSimPointLocations(Update.RIGHT);
            currSizeX++;
        }else {  //Add one element
            locX--;
            updateTile(simMap.mapTiles.get(locY).get(locX), tile);
        }
        path.addLoc(locX, locY);

    }

    private void addTileRight(int length) {
        //Create road up untill the last 'length'
        while (length > 1) {
            addTileRight(Tile.HORIZONTAL);
            length--;
        }
        addTileRight(Tile.POINT);
    }

    private void addTileRight(Tile tile){
        locX++;
        if(locX > currSizeX-1){   //Add one element at end of every row
            int i = -1;
            Iterator<ArrayList<Tile>> iterator = simMap.mapTiles.iterator();
            while(iterator.hasNext()) {
                ArrayList<Tile> rows = iterator.next();
                i++;
                if (i == locY) {
                    rows.add(tile);
                } else {
                    rows.add(null);
                }
            }
            currSizeX++;
        }else{  //Add one element
            updateTile(simMap.mapTiles.get(locY).get(locX), tile);
        }
        path.addLoc(locX, locY);
    }

    private void updateTile(Tile originalTile, Tile newTile){
        if(newTile == Tile.POINT){
            simMap.mapTiles.get(locY).set(locX, Tile.POINT);
            return;
        }else if(newTile == Tile.INTERSECT){
            simMap.mapTiles.get(locY).set(locX, Tile.INTERSECT);
            return;
        }else if(originalTile == Tile.INTERSECT){
            return;
        }

        if(originalTile == null){
            simMap.mapTiles.get(locY).set(locX, newTile);
            return;
        }

        switch(originalTile){
            case POINT:
                //Tile stays a point
                break;
            case VERTICAL:
                switch(newTile){
                    case HORIZONTAL:
                    case NORTH_EAST_WEST:
                    case SOUTH_WEST_EAST:
                        simMap.mapTiles.get(locY).set(locX, Tile.INTERSECT);
                        break;
                    case NORTH_EAST:
                    case NORTH_WEST:
                    case WEST_NORTH_SOUTH:
                        simMap.mapTiles.get(locY).set(locX, Tile.WEST_NORTH_SOUTH);
                        break;
                    case SOUTH_EAST:
                    case SOUTH_WEST:
                    case EAST_SOUTH_NORTH:
                        simMap.mapTiles.get(locY).set(locX, Tile.EAST_SOUTH_NORTH);
                        break;
                    default:
                        //Do nothing
                }
                break;
            case HORIZONTAL:
                switch(newTile){
                    case VERTICAL:
                    case EAST_SOUTH_NORTH:
                    case WEST_NORTH_SOUTH:
                        simMap.mapTiles.get(locY).set(locX, Tile.INTERSECT);
                        break;
                    case NORTH_EAST:
                    case NORTH_WEST:
                    case NORTH_EAST_WEST:
                        simMap.mapTiles.get(locY).set(locX, Tile.NORTH_EAST_WEST);
                        break;
                    case SOUTH_EAST:
                    case SOUTH_WEST:
                    case SOUTH_WEST_EAST:
                        simMap.mapTiles.get(locY).set(locX, Tile.SOUTH_WEST_EAST);
                        break;
                    default:
                        //Do nothing
                }
                break;
            case NORTH_EAST:
                switch (newTile){
                    case VERTICAL:
                    case SOUTH_EAST:
                    case EAST_SOUTH_NORTH:
                        simMap.mapTiles.get(locY).set(locX, Tile.EAST_SOUTH_NORTH);
                        break;
                    case HORIZONTAL:
                    case NORTH_WEST:
                    case NORTH_EAST_WEST:
                        simMap.mapTiles.get(locY).set(locX, Tile.NORTH_EAST_WEST);
                        break;
                    case SOUTH_WEST:
                    case SOUTH_WEST_EAST:
                    case WEST_NORTH_SOUTH:
                        simMap.mapTiles.get(locY).set(locX, Tile.INTERSECT);
                        break;
                    default:
                        //do nothing
                }
                break;
            case NORTH_WEST:
                switch(newTile){
                    case VERTICAL:
                    case SOUTH_WEST:
                    case WEST_NORTH_SOUTH:
                        simMap.mapTiles.get(locY).set(locX, Tile.WEST_NORTH_SOUTH);
                        break;
                    case HORIZONTAL:
                    case NORTH_EAST:
                    case NORTH_EAST_WEST:
                        simMap.mapTiles.get(locY).set(locX, Tile.NORTH_EAST_WEST);
                        break;
                    case SOUTH_EAST:
                    case EAST_SOUTH_NORTH:
                    case SOUTH_WEST_EAST:
                        simMap.mapTiles.get(locY).set(locX, Tile.INTERSECT);
                        break;
                    default:
                        //Do nothing
                }
                break;
            case SOUTH_EAST:
                switch (newTile){
                    case VERTICAL:
                    case NORTH_EAST:
                    case EAST_SOUTH_NORTH:
                        simMap.mapTiles.get(locY).set(locX, Tile.EAST_SOUTH_NORTH);
                        break;
                    case HORIZONTAL:
                    case SOUTH_WEST:
                    case SOUTH_WEST_EAST:
                        simMap.mapTiles.get(locY).set(locX, Tile.SOUTH_WEST_EAST);
                        break;
                    case NORTH_WEST:
                    case NORTH_EAST_WEST:
                    case WEST_NORTH_SOUTH:
                        simMap.mapTiles.get(locY).set(locX, Tile.INTERSECT);
                        break;
                    default:
                        //do nothing
                }
                break;
            case SOUTH_WEST:
                switch(newTile){
                    case VERTICAL:
                    case NORTH_WEST:
                    case WEST_NORTH_SOUTH:
                        simMap.mapTiles.get(locY).set(locX, Tile.WEST_NORTH_SOUTH);
                        break;
                    case HORIZONTAL:
                    case SOUTH_EAST:
                    case SOUTH_WEST_EAST:
                        simMap.mapTiles.get(locY).set(locX, Tile.SOUTH_WEST_EAST);
                        break;
                    case NORTH_EAST:
                    case NORTH_EAST_WEST:
                    case EAST_SOUTH_NORTH:
                        simMap.mapTiles.get(locY).set(locX, Tile.INTERSECT);
                        break;
                    default:
                        ///do nothing
                }
                break;
            case NORTH_EAST_WEST:
                switch (newTile){
                    case VERTICAL:
                    case SOUTH_EAST:
                    case SOUTH_WEST:
                    case EAST_SOUTH_NORTH:
                    case SOUTH_WEST_EAST:
                    case WEST_NORTH_SOUTH:
                        simMap.mapTiles.get(locY).set(locX, Tile.INTERSECT);
                        break;
                    default:
                        //do nothing
                }
                break;
            case EAST_SOUTH_NORTH:
                switch (newTile){
                    case HORIZONTAL:
                    case NORTH_WEST:
                    case SOUTH_WEST:
                    case NORTH_EAST_WEST:
                    case SOUTH_WEST_EAST:
                    case WEST_NORTH_SOUTH:
                        simMap.mapTiles.get(locY).set(locX, Tile.INTERSECT);
                        break;
                    default:
                        //do nothing
                }
                break;
            case SOUTH_WEST_EAST:
                switch (newTile){
                    case VERTICAL:
                    case NORTH_EAST:
                    case NORTH_WEST:
                    case NORTH_EAST_WEST:
                    case EAST_SOUTH_NORTH:
                    case WEST_NORTH_SOUTH:
                        simMap.mapTiles.get(locY).set(locX, Tile.INTERSECT);
                        break;
                    default:
                        //do nothing
                }
                break;
            case WEST_NORTH_SOUTH:
                switch (newTile){
                    case HORIZONTAL:
                    case NORTH_EAST:
                    case SOUTH_EAST:
                    case NORTH_EAST_WEST:
                    case EAST_SOUTH_NORTH:
                    case SOUTH_WEST_EAST:
                        simMap.mapTiles.get(locY).set(locX, Tile.INTERSECT);
                        break;
                    default:
                        //do nothing
                }
                break;
            default:
                //do nothing
                break;
        }
    }

    private void updatePoints() {
        //for every point -> FInd its location on the map and its type, then update
        for (SimPoint point : simPoints) {
            //Find out type
            updateType(point);
        }
    }

    private void updateType(SimPoint point){
        Long id = point.getId();

        int up, down, left, right;
        up = 0;
        down = 0;
        left = 0;
        right = 0;

        //search for all of a point's accessed directions
        String c = "!";
        for(Link link : linkEntitiesPointGeneration){
            c="!";
            if(link.getStartPoint().getId()==id) {
                c = link.getStartDirection();
            }else if(link.getStopPoint().getId() == id) {
                c = link.getStopDirection();
            }
            switch (c){
                case "N":
                    up = 1;
                    break;
                case "E":
                    right = 1;
                    break;
                case "S":
                    down = 1;
                    break;
                case "W":
                    left = 1;
                    break;
                default:
                    break;
            }
        }
        setType(point.getPosY(), point.getPosX(), up, right, down, left);
    }
    
    private void setType(int i, int j, int up, int right, int down, int left){
        //SetType
        switch(up+down+right+left){
            case 0:
                //impossible
                break;
            case 1:
                simMap.mapTiles.get(i).set(j, Tile.PARKING);
                break;
            case 2:
                if(up+down == 2)
                    simMap.mapTiles.get(i).set(j, Tile.VERTICAL);
                if(left+right == 2)
                    simMap.mapTiles.get(i).set(j, Tile.HORIZONTAL);
                if(up+right == 2)
                    simMap.mapTiles.get(i).set(j, Tile.NORTH_EAST);
                if(up+left == 2)
                    simMap.mapTiles.get(i).set(j, Tile.NORTH_WEST);
                if(down+right == 2)
                    simMap.mapTiles.get(i).set(j, Tile.SOUTH_EAST);
                if(down+left == 2)
                    simMap.mapTiles.get(i).set(j, Tile.SOUTH_WEST);
                break;
            case 3:
                if(up+left+right == 3)
                    simMap.mapTiles.get(i).set(j, Tile.NORTH_EAST_WEST);
                if(left+down+up == 3)
                    simMap.mapTiles.get(i).set(j, Tile.EAST_SOUTH_NORTH);
                if(down+left+right == 3)
                    simMap.mapTiles.get(i).set(j, Tile.SOUTH_WEST_EAST);
                if(left+down+up ==3)
                    simMap.mapTiles.get(i).set(j, Tile.WEST_NORTH_SOUTH);
                break;
            case 4:
                simMap.mapTiles.get(i).set(j, Tile.INTERSECT);
                break;
            default:
                break;
        }
    }
}
