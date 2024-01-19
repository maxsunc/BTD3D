package me.ChristopherW.core.custom;

public class Spawner {
    
    private float timeElapsed = 0;
    private float spawnInterval;
    private float nextSpawnInterval;
    private BloonType type;
    private float spawnTime;
    private int bloonQuantity;
    private float timeToNextSpawn;

    // takes in bloonAttribute to spawn, amount of bloons to spawn and time to spawn all the bloons
    public Spawner(BloonType type, int bloonQuantity, float spawnTime, float timeToNextSpawn){
        spawnInterval = spawnTime / bloonQuantity;
        System.out.println("Spawn ever " + spawnInterval + " SECONDS");
        nextSpawnInterval = spawnInterval;
        this.type = type;
        this.bloonQuantity = bloonQuantity;
        this.spawnTime = spawnTime;
        this.timeToNextSpawn = timeToNextSpawn;
    }

    public boolean canSpawnNext(float interval){
        timeToNextSpawn -= interval;

        // if the thing is less than 0 then we can spawn the next one
        if(timeToNextSpawn <= 0){
            return true;
        }
        // return false, time is still positive
        return false;
    }

    // checks if can spawn and returns true if can false otherwise
    public boolean checkSpawn(float interval){
        // increment tick according to time fact
        timeElapsed += interval;
        // check if it's time to sapwn a bloon
        if(nextSpawnInterval <= timeElapsed){
            // get nextSpawnInterval
            nextSpawnInterval += spawnInterval;
            System.out.println(nextSpawnInterval);
            return true;
        }
        return false;
    }
    // return the BloonAttribute
    public BloonType getType(){
        return type;
    }

    // checks if client spawn Time is done
    public boolean checkDone(){
        return timeElapsed > spawnTime;
    }
}
