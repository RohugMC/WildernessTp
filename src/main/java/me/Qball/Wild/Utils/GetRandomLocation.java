package me.Qball.Wild.Utils;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import me.Qball.Wild.Wild;

public class GetRandomLocation {
    private final Wild wild;
    public Checks check;
    private int retry;
    private int retries = 0;
    private WorldInfo wInfo;

    public GetRandomLocation(Wild wild) {
        this.wild = wild;
        check = new Checks(this.wild);
        retry = this.wild.retries;
        wInfo = new WorldInfo(this.wild);
    }

    public void getWorldInfo(Player p, String world){
        int minX = wInfo.getMinX(world);
        int maxX = wInfo.getMaxX(world);
        int minZ = wInfo.getMinZ(world);
        int maxZ = wInfo.getMaxZ(world);
        getRandomLoc(p, Bukkit.getWorld(world),maxX,minX,maxZ,minZ);
    }

    public void getWorldInfo(Player p) {
        String w = wInfo.getWorldName(p);
        int minX = wInfo.getMinX(w);
        int maxX = wInfo.getMaxX(w);
        int minZ = wInfo.getMinZ(w);
        int maxZ = wInfo.getMaxZ(w);
        getRandomLoc(p, Bukkit.getWorld(w), maxX, minX, maxZ, minZ);
    }

    private void getRandomLoc(Player p, World w, int maxX, int minX, int maxZ, int minZ) {
        Random rand = new Random();
        int x = rand.nextInt(maxX - minX + 1) + minX;
        int z = rand.nextInt(maxZ - minZ + 1) + minZ;
        double y = 0;
        if(!w.getName().equals(p.getWorld().getName()))
            y = check.getSolidBlock(x,z,w.getName(),p);
        else
            y = check.getSolidBlock(x,z,p);
        while ((y >= 10||y<250) && retries <= retry) {
            retries += 1;
            x = rand.nextInt(maxX - minX + 1) + minX;
            z = rand.nextInt(maxZ - minZ + 1) + minZ;
            y = 0;
            if(!w.getName().equals(p.getWorld().getName()))
                y = check.getSolidBlock(x,z,w.getName(),p);
            else
                y = check.getSolidBlock(x,z,p);
        }
        /*if (((y >= 10.0D) || (y < 250.0D)) && (retries <= retry))
        {
            this.retries += 1;
            getRandomLoc(p, w, maxX, minX, maxZ, minZ);
        }*/
        Location loc = new Location(w, x+.5, y, z+.5, 0.0F, 0.0F);

        if (loc.getBlock().isLiquid() || Arrays.stream(Biome.values()).filter(b -> b.name().contains("OCEAN")).anyMatch(b -> loc.getBlock().getBiome() == b)) {
            getRandomLoc(p, w, maxX, minX, maxZ, minZ);
        } else {
            if (loc.getY() <= 0) {
                loc.setY(loc.getBlock().getWorld().getHighestBlockYAt(loc));
            }

            if (!loc.getBlock().isEmpty() || !loc.getBlock().isPassable()) {
                loc.setY(loc.getY() + 1);
            }

            wild.random(p, loc);
            retries = 0;
        }
    }

    public String getWorldInformation(Location loc) {
        String world = loc.getWorld().getName();
        String minX = String.valueOf(wInfo.getMinX(world));
        String maxX = String.valueOf(wInfo.getMaxX(world));
        String minZ = String.valueOf(wInfo.getMinZ(world));
        String maxZ = String.valueOf(wInfo.getMaxZ(world));
        return world + ":" + minX + ":" + maxX + ":" + minZ + ":" + maxZ;
    }

    public Location getRandomLoc(String info, Player p) {
        Random rand = new Random();
        String[] worldInfo = info.split(":");
        World w = Bukkit.getWorld(worldInfo[0]);
        int minX = Integer.parseInt(worldInfo[1]);
        int maxX = Integer.parseInt(worldInfo[2]);
        int minZ = Integer.parseInt(worldInfo[3]);
        int maxZ = Integer.parseInt(worldInfo[4]);
        int x = rand.nextInt(maxX - minX + 1) + minX;
        int z = rand.nextInt(maxZ - minZ + 1) + minZ;
        double y = check.getSolidBlock(x,z,p);
        while ((y == 0 ||y >250) && retries <= retry) {
            retries += 1;
            minX = Integer.parseInt(worldInfo[1]);
            maxX = Integer.parseInt(worldInfo[2]);
            minZ = Integer.parseInt(worldInfo[3]);
            maxZ = Integer.parseInt(worldInfo[4]);
            x = rand.nextInt(maxX - minX + 1) + minX;
            z = rand.nextInt(maxZ - minZ + 1) + minZ;
            y = check.getSolidBlock(x,z,p);
        }/*
        if ((y == 0.0D) && (retries <= retry))
        {
            this.retries += 1;
            getRandomLoc(info, p);
        }*/
        retries = 0;
        return new Location(w, x+.5, y, z+.5, 0.0F, 0.0F);

    }


}
