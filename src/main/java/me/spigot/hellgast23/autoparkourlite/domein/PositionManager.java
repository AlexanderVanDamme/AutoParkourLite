package me.spigot.hellgast23.autoparkourlite.domein;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Random;

public class PositionManager {

    private static Random rand = new Random();

    public static final int LVL_VERY_EASY = 0;
    public static final int LVL_EASY = 1;
    public static final int LVL_NORMAL = 2;
    public static final int LVL_HARD = 3;
    public static final int LVL_VERY_HARD = 4;

    private static final int POS_Z = 0;
    private static final int POS_Z_2 = 4;
    private static final int NEG_X = 1;
    private static final int NEG_Z = 2;
    private static final int POS_X = 3;

    private Object[][] difficultyMaterialMap = new Object[][]{
            {Material.WOOL, (byte) 0},
            {Material.WOOL, (byte) 11},
            {Material.GOLD_BLOCK, (byte) 0},
            {Material.LAPIS_BLOCK, (byte) 0},
            {Material.REDSTONE_BLOCK, (byte) 0}
    };

    private Short[][][][] jumpableBlockOffsets = new Short[][][][]/* offsetHeight - difficultyLevel - offsetForward,offsetHori */
            {
                    /* offsetHeight 1 */
                    {
                            /* White 0 */
                            {
                                    {1, 0},
                                    {1, -1},
                                    {1, 1}
                            },
                            /* Purple 1 */
                            {
                                    {2, 0},
                                    {2, 1},
                                    {2, -1},
                                    {2, 2},
                                    {2, -2},
                                    {1, 2},
                                    {1, -2}
                            },
                            /* Yellow 2 */
                            {
                                    {3, 0},
                                    {3, -1},
                                    {3, 1},
                                    {3, -2},
                                    {3, 2},
                                    {2, 3},
                                    {2, -3},
                                    {1, 3},
                                    {1, -3}
                            },
                            /* Blue 3 */
                            {
                                    {3, 3},
                                    {3, -3}
                            },
                            /* Red 4 */
                            {
                                    {4, 0},
                                    {4, 1},
                                    {4, -1},
                                    {4, 2},
                                    {4, -2},
                                    {2, 4},
                                    {2, -4},
                                    {1, 4},
                                    {1, -4}
                            }
                    },

                    /* offsetHeight 0 */
                    {
                            /* White 0 */
                            {
                                    {1, 0},
                                    {1, -1},
                                    {1, 1}
                            },
                            /* Purple 1 */
                            {
                                    {2, 0},
                                    {2, 1},
                                    {2, -1},
                                    {1, 2},
                                    {1, -2}
                            },
                            /* Yellow 2 */
                            {
                                    {3, 0},
                                    {3, 1},
                                    {3, -1},
                                    {3, 2},
                                    {3, -2},
                                    {2, 2},
                                    {2, -2},
                                    {1, 3},
                                    {1, -3}
                            },
                            /* Blue 3 */
                            {
                                    {4, 0},
                                    {4, 1},
                                    {4, -1},
                                    {4, 2},
                                    {4, -2},
                                    {3, 3},
                                    {3, -3},
                                    {2, 3},
                                    {2, -3},
                                    {1, 4},
                                    {1, -4}
                            },
                            /* Red 4 */
                            {
                                    {5, 0},
                                    {5, 1},
                                    {5, -1},
                                    {5, 2},
                                    {5, -2},
                                    {4, 3},
                                    {4, -3},
                                    {3, 4},
                                    {3, -4},
                                    {2, 4},
                                    {2, -4},
                                    {1, 5},
                                    {1, -5}
                            }
                    },

                    /* offsetHeight -1 */
                    {
                            /* White 0 */
                            {
                                    {1, 0},
                                    {1, 1},
                                    {1, -1}
                            },
                            /* Purple 1 */
                            {
                                    {3, 0},
                                    {3, 1},
                                    {3, -1},
                                    {3, 2},
                                    {3, -2},
                                    {2, 0},
                                    {2, 1},
                                    {2, -1},
                                    {2, 2},
                                    {2, -2},
                                    {2, 3},
                                    {2, -3},
                                    {1, 3},
                                    {1, -3},
                                    {1, 2},
                                    {1, -2}

                            },
                            /* Yellow 2 */
                            {
                                    {4, 0},
                                    {4, 1},
                                    {4, -1},
                                    {4, 2},
                                    {4, -2},
                                    {4, 3},
                                    {4, -3},
                                    {3, 3},
                                    {3, -3},
                                    {2, 4},
                                    {2, -4},
                                    {1, 4},
                                    {1, -4}
                            },
                            /* Blue 3 */
                            {
                                    {5, 0},
                                    {5, 1},
                                    {5, -1},
                                    {5, 2},
                                    {5, -2},
                                    {5, 3},
                                    {5, -3},
                                    {4, 4},
                                    {4, -4},
                                    {3, 4},
                                    {3, -4},
                                    {2, 5},
                                    {2, -5},
                                    {1, 5},
                                    {1, -5}
                            },
                            /* Red 4 */
                            {
                                    {6, 0},
                                    {6, 1},
                                    {6, -1},
                                    {6, 2},
                                    {6, -2},
                                    {6, 3},
                                    {6, -3},
                                    {5, 4},
                                    {5, -4},
                                    {4, 5},
                                    {4, -5},
                                    {3, 5},
                                    {3, -5},
                                    {2, 6},
                                    {2, -6},
                                    {1, 6},
                                    {1, -6}
                            }
                    }

            };

    /**
     * Generate blocks on all the jumpable block offsets in the @jumpableBlockOffsets array.
     *
     * @param jumpBlockLoc - the location jumped from
     */
    public void generateAllJumpableOffsets(Location jumpBlockLoc) {
        jumpBlockLoc.getBlock().setType(Material.EMERALD_BLOCK);

        for (int layerIndex = 0; layerIndex < jumpableBlockOffsets.length; layerIndex++) {
            Short[][][] layer = jumpableBlockOffsets[layerIndex];

            for (int difficultyIndex = 0; difficultyIndex < layer.length; difficultyIndex++) {
                Short[][] difficulty = layer[difficultyIndex];

                for (Short[] offsetPair : difficulty) {
                    Location offsetLoc = jumpBlockLoc.clone().add(
                            offsetPair[1],
                            convertToHeightOffset(layerIndex),
                            offsetPair[0]
                    );

                    Block block = offsetLoc.getBlock();
                    if (block.getType() != Material.AIR) {

                        System.out.println("|PositionMapGen| The block at layerIndex: " + layerIndex +
                                ", difficultyIndex: " + difficultyIndex + ", offsetPair: ( " + offsetPair[0]
                                + ", " + offsetPair[1] + " ) was not AIR when placed.");

                    }
                    setLevelBasedMaterial(block, difficultyIndex);

                }

            }

        }
    }

    /**
     * Add a random location offset to the given location.
     *
     * @param loc             - the location to manipulate
     * @param directionNumber - the direction number
     * @param heightOffset    - the height offset
     * @param level           - the difficulty level
     * @return - the same modified location object
     */
    public Location addRandomLocationOffset(Location loc, int directionNumber, int heightOffset, int level) {
        Short[][] offsetPairs = jumpableBlockOffsets[convertToHeightIndex(heightOffset)][level];
        Short[] offsetPair = offsetPairs[rand.nextInt(offsetPairs.length)];
        int forwardOffset = offsetPair[0];
        int horiOffset = offsetPair[1];
        return addLocationOffset(loc, directionNumber, heightOffset, forwardOffset, horiOffset);
    }

    /**
     * Add the offsets to the location.
     * @param loc - the location to manipulate
     * @param directionNumber - the direction number
     * @param heightOffset - the height offset
     * @param forwardOffset - the forward offset
     * @param horiOffset - the horizontal offset
     * @return - the same modified location object
     */
    public Location addLocationOffset(Location loc, int directionNumber, int heightOffset, int forwardOffset, int horiOffset) {
        switch (directionNumber) {
            case POS_X:
                return loc.add(
                        forwardOffset,
                        heightOffset,
                        horiOffset
                );
            case POS_Z_2: //fall through
            case POS_Z:
                return loc.add(
                        horiOffset,
                        heightOffset,
                        forwardOffset
                );
            case NEG_X:
                return loc.add(
                        -1 * forwardOffset,
                        heightOffset,
                        horiOffset
                );
            case NEG_Z:
                return loc.add(
                        horiOffset,
                        heightOffset,
                        -1 * forwardOffset
                );
            default:
                throw new IllegalArgumentException("Illegal direction number. directionNumber: " + String.valueOf(directionNumber));
        }
    }

    /**
     * Set the block type of a block based on the difficulty to reach the block.
     *
     * @param block - the block to set the material
     * @param level - the level to base on
     */
    @SuppressWarnings("deprecated")
    public void setLevelBasedMaterial(Block block, int level) {
        block.setType((Material) difficultyMaterialMap[level][0]);
        block.setData((Byte) difficultyMaterialMap[level][1]);
    }

    /**
     * Convert the height-offset in blocks to the usable index in the arrays.
     *
     * @param heightOffset - the height offset
     * @return - the height index
     */
    private int convertToHeightIndex(int heightOffset) {
        switch (heightOffset) {
            case 1:
                return 0;
            case 0:
                return 1;
            default:
                return (heightOffset - 1) * -1;
        }
    }

    /**
     * Convert the height index to a usable height offset in blocks.
     *
     * @param heightIndex - the height index
     * @return - the height offset
     */
    private int convertToHeightOffset(int heightIndex) {
        switch (heightIndex) {
            case 0:
                return 1;
            case 1:
                return 0;
            default:
                return (heightIndex - 1) * -1;
        }
    }

}