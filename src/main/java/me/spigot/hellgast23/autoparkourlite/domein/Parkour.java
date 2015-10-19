package me.spigot.hellgast23.autoparkourlite.domein;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created on: 19/10/2015 at 14:39
 */
public class Parkour {


    private static Set<String> allParkourBlockKeys = new HashSet<String>();
    private static Random rand = new Random();
    private UUID playerId;

    public static boolean isParkourBlock(Block block) {
        if (block != null) {
            String blockKey = convertBlockToKey(block);
            return allParkourBlockKeys.contains(blockKey);
        } else {
            return false;
        }
    }

    /**
     * Convert the location of a block to a @String key that is used in @locationsToLevelMap
     *
     * @param block - the block to convert
     * @return - the string key
     */
    private static String convertBlockToKey(Block block) {
        Location loc = block.getLocation();
        return loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ() + ":" + loc.getWorld().toString();
    }

    private UUID playerID;
    private int directionNumber;
    private long generateSpeedMilli;
    private int savedLocationsAmount;
    private boolean doPlaceTorches;
    private Location startLocation;
    private double skullChance;

    private ArrayList<Location> blockLocations;
    private HashMap<String, Integer> locationsToLevelMap;
    private long millisSinceLastGeneration;
    private int lowestBlockHeight;
    private int score;
    private int jumpAmount;
    private int forceGenerateBlockAmount;
    private boolean isDestroyed;
    private boolean isStarted;
    private String lastGeneratedBlockKey;
    private String secondLastGeneratedBlockKey;
    private int forceHeightOffsetAmount;

    private boolean lastBlockIsSkull;
    private boolean lastBlockIsIronFence;
    private int skullGensAmount;
    private int ironFenceGensAmount;

    private PositionManager posMan;

    /**
     * Main constructor.
     *
     *
     **/
    public Parkour(Player player, PositionManager posMan) {
        this.playerID = player.getUniqueId();
        this.directionNumber = Math.round(player.getLocation().getYaw() / 90f);
        this.generateSpeedMilli = 1000;
        this.savedLocationsAmount = 7;
        this.doPlaceTorches = false;
        this.skullChance = 0.05d;
        this.blockLocations = new ArrayList<>();
        this.locationsToLevelMap = new HashMap<>();
        this.millisSinceLastGeneration = 0L;
        this.lowestBlockHeight = 0;
        this.score = 0;
        this.jumpAmount = 0;
        this.forceGenerateBlockAmount = 0;
        this.isDestroyed = false;
        this.isStarted = false;
        this.lastGeneratedBlockKey = "";
        this.secondLastGeneratedBlockKey = "";
        this.forceHeightOffsetAmount = 5;

        this.lastBlockIsSkull = false;
        this.lastBlockIsIronFence = false;
        this.skullGensAmount = 0;
        this.ironFenceGensAmount = 0;

        if (this.directionNumber < 0) {
            this.directionNumber *= -1;
        }

        this.posMan = posMan;
    }

    /**
     * @return - the player running this parkour
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(playerID);
    }


    /**
     * Generate the next block in the parkour.
     * If needed, a block at the start of the parkour will be removed.
     *
     * @param currentTimeMilli - The current system time.
     */
    public void generateNextBlock(long currentTimeMilli) {
        if (isDestroyed || !isStarted) {
            return;
        }
        if (forceGenerateBlockAmount <= 0) {
            long timePasted = currentTimeMilli - this.millisSinceLastGeneration;
            if (timePasted < generateSpeedMilli) {
                return;
            }
        } else {
            forceGenerateBlockAmount--;
        }
        this.millisSinceLastGeneration = currentTimeMilli;
        boolean isFirstBlock = (getLastPlacedLocation() == null);

        Block newRandomBlock;
        int blockLevel = 0;

        boolean isTorchPlaceble = false;
        Block torchBlock = null;

        int guardMax = 15;
        int guard = 0;

        if (!isFirstBlock) {
            do {
                guard++;

                Location newRandomLoc = getLastPlacedLocation().clone();
                int heightOffset = getRandomHeightOffset();
                blockLevel = getRandomLevel();

                posMan.addRandomLocationOffset(
                        newRandomLoc,
                        this.directionNumber,
                        heightOffset,
                        blockLevel
                );
                newRandomBlock = newRandomLoc.getBlock();

                isTorchPlaceble = false;
                if (this.doPlaceTorches && newRandomBlock != null && newRandomBlock.getType() == Material.AIR) {
                    torchBlock = getTorchBlock(newRandomBlock);
                    if (torchBlock != null && torchBlock.getType() == Material.AIR) {
                        isTorchPlaceble = true;
                    }
                }

            }
            while ((newRandomBlock == null || newRandomBlock.getType() != Material.AIR
                    || (this.doPlaceTorches && !isTorchPlaceble)) && guard < guardMax);

        } else {
            newRandomBlock = getFirstBlockLocation().getBlock();
        }

        if (newRandomBlock == null || newRandomBlock.getType() != Material.AIR) {
            finish("There's no room to continue this parkour.");
            return;
        }
//        boolean changedToSkull = randomChangeToSkull(newRandomBlock, blockLevel);
//        if (!changedToSkull) {
            posMan.setLevelBasedMaterial(newRandomBlock, blockLevel);
//        }
        addNewLocation(newRandomBlock.getLocation(), blockLevel);
//        if (!changedToSkull && doPlaceTorches && isTorchPlaceble) {
//            torchBlock.setType(Material.TORCH);
//        }
    }

    /**
     * Check if a the player reached a new parkour block or the player fell off, based on the player location.
     */
    public void updatePlayerPosition() {
        Player player = getPlayer();
        if (isDestroyed || !isStarted || player == null || !player.isOnline()) {
            return;
        }

        Block blockBelowPlayer = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        String blockKey = convertBlockToKey(blockBelowPlayer);
        if (locationsToLevelMap.containsKey(blockKey)) {
            int level = locationsToLevelMap.get(blockKey);
            if (level != -1) {
                int scoreGain = level + 1;
                score += scoreGain;
                jumpAmount++;
                sendMessage("Nice jump! +" + scoreGain + " score! Total score: " + score
                        + ". Total jumps: " + jumpAmount + ".");
                locationsToLevelMap.put(blockKey, -1);
                if (this.secondLastGeneratedBlockKey.equals(blockKey)) {
                    this.forceGenerateBlockAmount = 1;
                } else if (this.lastGeneratedBlockKey.equals(blockKey)) {
                    this.forceGenerateBlockAmount = 2;
                }

                if ((jumpAmount % 10) == 0) {
                    player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1f, 1f);
                }
            }
        } else if (player.getLocation().getBlockY() <= (lowestBlockHeight - 1)) {
            finish("You fell...");
        }
    }

    /**
     * Start the parkour.
     */
    public void start() {
        this.startLocation = getPlayer().getLocation();
        double blocksPerSecond = 1D / (((double) this.generateSpeedMilli) / 1000D);
//        sendMessage("Parkour - ");
//        sendMessage("Speed: " + blocksPerSecond + " blocks/second.");
//        sendMessage("Max amount of blocks: " + this.savedLocationsAmount + " blocks");
//        sendMessage(ChatColor.GOLD + "The parkour has started!");
        this.isStarted = true;
    }

    /**
     * Give the player feedback that the parkour has stopped, teleport them back to the start and destroy the parkour.
     * TODO: save all stats earned in this parkour
     *
     * @param reason - the stop reason to display the player
     */
    public void finish(String reason) {
        try {
            Player player = getPlayer();
            int amount = this.score / 50;
//        System.out.println(this.score + "score -- " + amount);
//        for (int x = 0; x < amount; x++) {
//            instance.fireRandomFirework(rand, player);
//        }
            player.teleport(startLocation);
            sendMessage(reason);
            sendMessage("You finished your parkour!");
            sendMessage("Final score: " + score);
            sendMessage("Total amount of jumps: " + jumpAmount);
        }catch(Exception e){
            e.printStackTrace();
        }
        destroy();
    }

    /**
     * Set all the parkour blocks back to air.
     * Disables the generating of new blocks.
     */
    private void destroy() {
        for (Location loc : blockLocations) {
            try {
                if (doPlaceTorches) {
                    Block inFront = getTorchBlock(loc.getBlock());
                    inFront.setType(Material.AIR);
                    String key = convertBlockToKey(loc.getBlock());
                    Parkour.allParkourBlockKeys.remove(key);
                }
                loc.getBlock().setType(Material.AIR);
                String blockKey = convertBlockToKey(loc.getBlock());
                Parkour.allParkourBlockKeys.remove(blockKey);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        isDestroyed = true;
    }


    /**
     * Add a new location to the saved parkour locations.
     * The oldest location will be removed if needed.
     * Mappings of the location vs block level are updated.
     *
     * @param location - the location to add
     * @param level    - the level of the block jump
     */
    private void addNewLocation(Location location, int level) {
        if (blockLocations.size() == savedLocationsAmount) {
            Location locToRemove = blockLocations.get(blockLocations.size() - 1);
            Block blockToRemove = locToRemove.getBlock();

            if (doPlaceTorches) {
                Block torchBlock = getTorchBlock(blockToRemove);
                torchBlock.setType(Material.AIR);
            }
            blockToRemove.setType(Material.AIR);

            blockLocations.remove(locToRemove);
            String blockKey = convertBlockToKey(locToRemove.getBlock());
            locationsToLevelMap.remove(blockKey);
            Parkour.allParkourBlockKeys.remove(blockKey);
        }

        String blockKey = convertBlockToKey(location.getBlock());

        blockLocations.add(0, location);
        locationsToLevelMap.put(blockKey, level);
        Parkour.allParkourBlockKeys.add(blockKey);
        this.secondLastGeneratedBlockKey = this.lastGeneratedBlockKey;
        this.lastGeneratedBlockKey = blockKey;
        calculateLowestHeight();
        updatePlayerPosition();
    }

    /**
     * Get the last location where a block was placed.
     * If none have been placed, null will be returned.
     *
     * @return - The location.
     */
    private Location getLastPlacedLocation() {
        if (blockLocations.isEmpty()) {
            return null;
        } else {
            return blockLocations.get(0);
        }
    }

    /**
     * Get the position of where the very first block of the parkour should be.
     *
     * @return - the location
     */
    private Location getFirstBlockLocation() {
        return posMan.addLocationOffset(
                getPlayer().getLocation().clone(),
                this.directionNumber,
                0,
                2,
                0
        );
    }

    /**
     * Calculates the lowest y value of all stored locations and store it in @lowestBlockHeight
     * Called every time a new block location is added to the parkour.
     */
    private void calculateLowestHeight() {
        this.lowestBlockHeight = Integer.MAX_VALUE;
        if (blockLocations.isEmpty()) {
            this.lowestBlockHeight = 0;
        }
        for (Location loc : blockLocations) {
            int blockY = loc.getBlockY();
            if (lowestBlockHeight > blockY) {
                lowestBlockHeight = blockY;
            }
        }
    }

    /**
     * Send a message to the player running the parkour.
     *
     * @param message - the message
     */
    private void sendMessage(String message) {
        Player player = getPlayer();
        if (player != null && player.isOnline()) {
            player.sendMessage(ChatColor.GREEN + "[AutoParkour] " + ChatColor.GRAY + message);
        }
    }

    /**
     * Change to block to a skull at random.
     *
     * @param block - the block to change
     * @param level - the difficulty level of the block
     * @return - if the block was changed to skull or not
     */
    private boolean randomChangeToSkull(Block block, int level) {
        lastBlockIsSkull = false;
        if (level != PositionManager.LVL_VERY_HARD) {
            double roll = rand.nextDouble();
            if (roll < this.skullChance) {
                skullGensAmount = rand.nextInt(5)+3;
            }
        }
        if ( skullGensAmount > 0){
            block.setType(Material.SKULL);
            Skull skull = (Skull) block.getState();
            skull.setOwner("Hellgast23");
            block.getState().update();
            lastBlockIsSkull = true;
            skullGensAmount--;
        }
        return lastBlockIsSkull;
    }

    /**
     * Get the torch block based on the parkour block.
     *
     * @param ref - the parkour block
     * @return - the torch block
     */
    private Block getTorchBlock(Block ref) {
        return ref.getRelative(BlockFace.UP);
    }

    /**
     * Chance of staying same height: 40%
     * Chance of going one up: 40%
     * Chance of going one down: 20%
     *
     * @return - the height offset
     */
    private int getRandomHeightOffset() {
        if (this.forceHeightOffsetAmount > 0) {
            this.forceHeightOffsetAmount--;
            return 1;
        }

        int offset = -1;
        double roll = rand.nextDouble();
        if (roll < 0.40d) {
            offset = 0;
        } else if (roll < 0.80d) {
            offset = 1;
            if (lastBlockIsSkull) {
                offset = 0;
            }
        }
        return offset;
    }

    /**
     * Random level from EASY to HARD.
     * TODO: Make this configurable for each individual parkour.
     *
     * @return - the difficulty level
     */
    private int getRandomLevel() {
        // TODO: if lastBlockIsSkull is true, never put on LVL_VERY_HARD or it would be an impossible jump
        int level;
        level = getRandomNumber(PositionManager.LVL_EASY, PositionManager.LVL_HARD);

        if ( this.lastBlockIsSkull ){
            if (level == PositionManager.LVL_HARD || level == PositionManager.LVL_VERY_HARD ){
                level = PositionManager.LVL_NORMAL;
            }
        }
        return level;
    }

    /**
     * Generate a random number, between min and max.
     * The borders (min and max) are included.
     *
     * @param min - min value
     * @param max - max value
     * @return - the random number
     */
    private int getRandomNumber(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min parameter can't be bigger than max parameter");
        }
        if (min == max) {
            return max;
        }
        int dif = max - min;
        dif++;
        return rand.nextInt(dif) + min;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public UUID getPlayerId() {
        return playerId;
    }
}