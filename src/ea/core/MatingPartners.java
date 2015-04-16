package ea.core;

/**
 * Created by espen on 10/03/15.
 */
public class MatingPartners {
    public Phenotype partner1;
    public Phenotype partner2;

    public boolean samePartner() {
        return partner1 == partner2;
    }
}
