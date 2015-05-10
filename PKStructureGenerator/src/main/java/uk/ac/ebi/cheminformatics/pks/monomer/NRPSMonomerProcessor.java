package uk.ac.ebi.cheminformatics.pks.monomer;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 10/5/15
 * Time: 00:58
 * To change this template use File | Settings | File Templates.
 */
public class NRPSMonomerProcessor implements MonomerProcessor {

    String aminoAcid;

    public NRPSMonomerProcessor(String aminoAcid) {
        this.aminoAcid = aminoAcid;
    }

    @Override
    public void modify(PKMonomer monomer) {
        // Read mol file according for the amino acid,

        //Join K in amino acid mol file to the K in the Monomer

        //If L is present in the aminoacid, join it with N of the monomer.
    }
}
