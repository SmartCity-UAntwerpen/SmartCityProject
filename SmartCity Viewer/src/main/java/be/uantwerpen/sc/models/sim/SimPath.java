package be.uantwerpen.sc.models.sim;

import java.util.ArrayList;

/**
 * Created by Arthur on 16/04/2016.
 */
public class SimPath
{
    private Long linkID;
    private Long length;
    private ArrayList<int[]> locs;

    public SimPath(Long linkID)
    {
        this.linkID = linkID;
        locs = new ArrayList<>();
    }

    public void addLoc(int x, int y)
    {
        int loc[] = {x,y};
        locs.add(loc);
    }

    public Long getLinkID()
    {
        return linkID;
    }
    
    public ArrayList<int[]> getLocs()
    {
        return locs;
    }

    public Long getLength()
    {
        return length;
    }

    public void setLength(Long length)
    {
        this.length = length;
    }

    public void up()
    {
        for(int[] loc : locs)
        {
            loc[1]++;
        }

    }

    public void right()
    {
        for(int[] loc : locs)
        {
            loc[0]++;
        }
    }
}
