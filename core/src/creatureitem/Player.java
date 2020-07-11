package creatureitem;

import world.Level;

public class Player extends creatureitem.Creature {


    //<editor-fold desc="Instance Variables">
    /**
     * True if player died
     */
    private boolean isDead;
    //</editor-fold>

    public Player(Level level, String texturePath, String name, char glyph, int team, int maxHP, int evasion, int defense, int attack, int visionRadius) {
        super(level, texturePath, name, glyph, team, maxHP, evasion, defense, attack, visionRadius);
        isDead = false;
    }

    //<editor-fold desc="Getters and Setters">
    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public boolean[][] getSeenTiles() {
        return level.getSeen();
    }

    public void setSeenTiles(boolean[][] seenTiles) {
        level.setSeen(seenTiles);
    }

    public void setSeen(int i, int j) {
        level.setSeen(i, j);
    }

    public boolean getSeen(int i, int j) {
        return level.getSeen(i, j);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if(level != null) level.setPlayer(this);
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
            level.remove(this);
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
            level.remove(this);
            isDead = true;
            return true;
        }
        else {
            maxHP += mod;
            return false;
        }
    }

    /**
     * Move the player by <mx, my>
     * @param mx X distance
     * @param my Y distance
     */
    public void moveBy(int mx, int my) {
        if(x + mx < 0 || x + mx >= level.getWidth() || y + my < 0 || y + my >= level.getHeight()) return;

        Creature foe = level.getCreatureAt(x + mx, y + my);
        if(foe == null)
            ai.onEnter(x + mx, y + my, level.getTileAt(x + mx, y + my));
        else if(foe.team != team)
            attack(foe);
        lastMovedTime = System.currentTimeMillis();
        level.update();
    }

}
