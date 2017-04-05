package be.uantwerpen.sc.models.sim;

import be.uantwerpen.sc.models.Bot;

/**
 * Created by Thomas on 27/02/2016.
 */
public class SimBot
{
    Long linkID;
    double percentageComplete;  //from 0-1
    int[] loc;

    public SimBot()
    {
        linkID = -1L;
        loc = new int[]{0,0};
        percentageComplete = 0;
    }

    public SimBot(Bot bot)
    {
        if(bot.getLinkId() == null)
        {
            this.linkID = -1L;
        }
        else
        {
            this.linkID = bot.getLinkId().getId();
        }
        
        if(bot.getPercentageCompleted() == null)
        {
            this.percentageComplete = 0;
        }
        else
        {
            this.percentageComplete = bot.getPercentageCompleted();
        }
        loc = new int[]{0,0};
    }

    public void update(SimPath currentPath)
    {
        int size = currentPath.getLocs().size();
        Long length = currentPath.getLength();
        double perc = percentageComplete / length;
        
        if(perc >= 1)
        {
            perc = 0.999;
        }
        
        int selected = (int)Math.floor(size * perc);
        
        try
        {
            loc = currentPath.getLocs().get(selected);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void add10percent()
    {
        this.percentageComplete += 0.1;
        
        if(percentageComplete >= 1)
        {
            percentageComplete = 0.99;
        }
    }

    public void setLinkID(Long linkID)
    {
        this.linkID = linkID;
    }

    public Long getLinkID()
    {
        return this.linkID;
    }

    public double getPercentageComplete()
    {
        return percentageComplete;
    }
    
    public int[] getLoc()
    {
        return loc;
    }
}
