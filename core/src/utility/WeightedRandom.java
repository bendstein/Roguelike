package utility;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class WeightedRandom {

    /**
     * The prng behind WeightedRandom
     */
    private Random random;

    public WeightedRandom(Random random) {
        this.random = random;
    }

    public <T> T next(TreeMap<T,Float> weights) {
        weights = normalize(weights);

        float r = random.nextFloat();
        T val = null;
        float sum = 0;

        for(Map.Entry<T, Float> entry : weights.entrySet()) {
            sum += entry.getValue();
            if(r < sum) {
                val = entry.getKey();
                break;
            }
        }
        
        return val;

    }

    public <T> TreeMap<T, Float> normalize(TreeMap<T, Float> weights) {
        float sum = 0;

        for(Float f : weights.values()) {
            sum += f;
        }

        for(Map.Entry<T, Float> entry : weights.entrySet()) {
            weights.put(entry.getKey(), entry.getValue()/sum);
        }

        return weights;
    }

}
