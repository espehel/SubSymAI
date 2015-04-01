package ea.core;

/**
 * Created by espen on 03/03/15.
 */
public abstract class Phenotype implements Comparable<Phenotype> {
    public Genotype genotype;
    public double fitness;
    public int generation;



    public double normalizedFitness;
    private double upperBound;
    private double lowerBound;

    public abstract void develop();

    public Phenotype(){
        generation = State.generationNumber+1;
    }

    @Override
    public int compareTo(Phenotype o) {

        double f = o.fitness - this.fitness;
        return (int) Math.signum(f);
    }

    public void setRange(double upperBound, double lowerBound){
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }
    public boolean inRange(double n){
        return n>=lowerBound && n< upperBound;
    }

    @Override
    public String toString() {
        return "Phenotype{" +
                "generation=" + generation +
                ", fitness=" + fitness +
                ", data=" + getDataString() +
                '}';
    }

    protected abstract String getDataString();

    public abstract double calculateFitness();
}
