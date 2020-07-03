package creatureitem;

import world.World;

public class Player extends creatureitem.Creature {


    //<editor-fold desc="Instance Variables">
    /**
     * True if player died
     */
    private boolean isDead;

    /**
     * True if the player has seen the tile at this location
     */
    private boolean[][] seenTiles;
    //</editor-fold>

    public Player(World world, String texturePath, String name, int team, int maxHP, int evasion, int defense, int attack, int visionRadius) {
        super(world, texturePath, name, team, maxHP, evasion, defense, attack, visionRadius);
        isDead = false;
        seenTiles = new boolean[world.getWidth()][world.getHeight()];
    }

    //<editor-fold desc="Getters and Setters">
    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public boolean[][] getSeenTiles() {
        return seenTiles;
    }

    public void setSeenTiles(boolean[][] seenTiles) {
        this.seenTiles = seenTiles;
    }

    public void setSeen(int i, int j) {
        seenTiles[i][j] = true;
    }

    public boolean getSeen(int i, int j) {
        return seenTiles[i][j];
    }

    //</editor-fold>

    /**
     * Change the creature's current HP
     * @param mod Amount to change HP by
     * @return true if the creature died
     */
    @Override
    public boolean modifyHP(int mod) {
        if(HP + mod <= 0) {
            world.remove(this);
            isDead = true;
            return true;
        }
        else {
            HP = Math.min(maxHP, HP + mod);
            return false;
        }
    }

    /**
     * Change the creature's max HP
     * @param mod The amount to change maxHP by
     * @return true if the creature died
     */
    @Override
    public boolean modifyMaxHp(int mod) {
        if(maxHP + mod <= 0) {
            world.remove(this);
            isDead = true;
            return true;
        }
        else {
            maxHP += mod;
            return false;
        }
    }

    public void moveBy(int mx, int my) {
        Creature foe = world.getCreatureAt(x + mx, y + my);
        if(foe == null)
            ai.onEnter(x + mx, y + my, world.getTileAt(x + mx, y + my));
        else if(foe.team != team)
            attack(foe);
        lastMovedTime = System.currentTimeMillis();
        world.update();
    }

}
