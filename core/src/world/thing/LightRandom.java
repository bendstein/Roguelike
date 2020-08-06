package world.thing;

import com.badlogic.gdx.graphics.Color;
import world.Tile;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class LightRandom extends Light {

    /**
     * Different colors the light can be
     */
    int[][] colors;

    /**
     * prng
     */
    Random random;

    /**
     * The rate at which the colors change
     */
    long rate;

    long currentRate;

    /**
     * Percent the change rate can vary
     */
    double variance;

    /**
     * Time at which the colors last changed
     */
    long lastChange;

    int[][][] lastColors;

    public LightRandom(Tile tile, int range, float brightness, int intensity, boolean active, Random random, long rate, double variance, int[] colors) {
        super(tile, Light.WHITE, range, brightness, intensity, active);
        this.random = random;
        this.rate = currentRate = rate;
        this.variance = variance;
        this.colors = new int[colors.length][];
        lastColors = new int[(range * 2) + 1][(range * 2) + 1][];

        for(int i = 0; i < colors.length; i++)
            this.colors[i] = new int[]{colors[i]};

        changeColors();
    }

    public LightRandom(Tile tile, boolean open, int range, float brightness, int intensity, boolean active, Random random, long rate, double variance, int[] colors) {
        super(tile, open, Light.WHITE, range, brightness, intensity, active);
        this.random = random;
        this.rate = currentRate = rate;
        this.variance = variance;
        this.colors = new int[colors.length][];
        lastColors = new int[(range * 2) + 1][(range * 2) + 1][];

        for(int i = 0; i < colors.length; i++)
            this.colors[i] = new int[]{colors[i]};

        changeColors();

    }

    public LightRandom(Tile tile, int range, float brightness, int intensity, boolean active, Random random, long rate, double variance, int[][] colors) {
        super(tile, Light.WHITE, range, brightness, intensity, active);
        this.random = random;
        this.rate = currentRate = rate;
        this.variance = variance;
        this.colors = colors;
        lastColors = new int[(range * 2) + 1][(range * 2) + 1][];

        changeColors();
    }

    public LightRandom(Tile tile, boolean open, int range, float brightness, int intensity, boolean active, Random random, long rate, double variance, int[][] colors) {
        super(tile, open, Light.WHITE, range, brightness, intensity, active);
        this.random = random;
        this.rate = currentRate = rate;
        this.variance = variance;
        this.colors = colors;
        lastColors = new int[(range * 2) + 1][(range * 2) + 1][];

        changeColors();

    }

    public LightRandom(Tile tile, int range, float brightness, int intensity, boolean active, Random random, long rate, double variance, int[] colors, Color tint) {
        super(tile, Light.WHITE, tint, range, brightness, intensity, active);
        this.random = random;
        this.rate = currentRate = rate;
        this.variance = variance;
        this.colors = new int[colors.length][];
        lastColors = new int[(range * 2) + 1][(range * 2) + 1][];

        for(int i = 0; i < colors.length; i++)
            this.colors[i] = new int[]{colors[i]};

        changeColors();
    }

    public LightRandom(Tile tile, boolean open, int range, float brightness, int intensity, boolean active, Random random, long rate, double variance, int[] colors, Color tint) {
        super(tile, open, Light.WHITE, tint, range, brightness, intensity, active);
        this.random = random;
        this.rate = currentRate = rate;
        this.variance = variance;
        this.colors = new int[colors.length][];
        lastColors = new int[(range * 2) + 1][(range * 2) + 1][];

        for(int i = 0; i < colors.length; i++)
            this.colors[i] = new int[]{colors[i]};

        changeColors();

    }

    public LightRandom(Tile tile, int range, float brightness, int intensity, boolean active, Random random, long rate, double variance, int[][] colors, Color tint) {
        super(tile, Light.WHITE, tint, range, brightness, intensity, active);
        this.random = random;
        this.rate = currentRate = rate;
        this.variance = variance;
        this.colors = colors;
        lastColors = new int[(range * 2) + 1][(range * 2) + 1][];

        changeColors();
    }

    public LightRandom(Tile tile, boolean open, int range, float brightness, int intensity, boolean active, Random random, long rate, double variance, int[][] colors, Color tint) {
        super(tile, open, Light.WHITE, tint, range, brightness, intensity, active);
        this.random = random;
        this.rate = currentRate = rate;
        this.variance = variance;
        this.colors = colors;
        lastColors = new int[(range * 2) + 1][(range * 2) + 1][];

        changeColors();

    }

    public LightRandom(LightRandom lightRandom) {
        super(lightRandom);
        this.colors = new int[lightRandom.colors.length][];
        this.random = lightRandom.random;
        this.rate = lightRandom.rate;
        this.currentRate = lightRandom.currentRate;
        this.variance = lightRandom.variance;
        this.lastChange = lightRandom.lastChange;
        this.lastColors = new int[lightRandom.lastColors.length][lightRandom.lastColors[0].length][];

        for(int i = 0; i < colors.length; i++) {
            colors[i] = Arrays.copyOf(lightRandom.colors[i], lightRandom.colors[i].length);
        }
        for(int i = 0; i < lastColors.length; i++) {
            for(int j = 0; j < lastColors[0].length; j++) {
                lastColors[i][j] = Arrays.copyOf(lightRandom.lastColors[i][j], lightRandom.lastColors[i][j].length);
            }
        }
    }

    //<editor-fold desc="Getters and Setters">


    @Override
    public int getColor(int i, int j, int num) {
        if(colors.length == 0)
            return color[0];

        /*
        if(System.currentTimeMillis() - lastChange > currentRate)
            changeColors();


         */
        return lastColors[i][j][num];
    }

    @Override
    public int[] getColor(int i, int j) {
        if(colors.length == 0)
            return color;

        /*
        if(System.currentTimeMillis() - lastChange > currentRate)
            changeColors();

         */

        return lastColors[i][j];
    }

    public void changeColors() {
        for(int i = 0; i < lastColors.length; i++) {
            for(int j = 0; j < lastColors[0].length; j++) {
                if(colors.length == 0 || random == null)
                    lastColors[i][j] = color;
                else
                    lastColors[i][j] = colors[random.nextInt(colors.length)];
            }
        }

        setLastChangeNow();
    }

    public int[][] getColors() {
        return colors;
    }

    public void setColors(int[][] colors) {
        this.colors = colors;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    public long getCurrentRate() {
        return currentRate;
    }

    public void setCurrentRate(long currentRate) {
        this.currentRate = currentRate;
    }

    public double getVariance() {
        return variance;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }

    public int[][][] getLastColors() {
        return lastColors;
    }

    public void setLastColors(int[][][] lastColors) {
        this.lastColors = lastColors;
    }

    public long getLastChange() {
        return lastChange;
    }

    public void setLastChange(long lastChange) {
        this.lastChange = lastChange;
        long difference = (long) Math.floor(variance * rate);
        difference *= random == null? 0 : (random.nextBoolean()? 1 : -1) * random.nextDouble();
        currentRate = rate + difference;
    }

    public void setLastChangeNow() {
        setLastChange(System.currentTimeMillis());
    }

    //</editor-fold>


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LightRandom)) return false;
        if (!super.equals(o)) return false;
        LightRandom that = (LightRandom) o;
        return rate == that.rate &&
                Double.compare(that.variance, variance) == 0 &&
                Arrays.equals(colors, that.colors) &&
                Arrays.equals(lastColors, that.lastColors);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), rate, variance);
        result = 31 * result + Arrays.hashCode(colors);
        result = 31 * result + Arrays.hashCode(lastColors);
        return result;
    }
}
