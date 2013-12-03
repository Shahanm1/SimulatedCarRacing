/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.fuzzy;

/**
 * Iterator for all fuzzy sets.
 * @author Diego
 */
public class FuzzyManagerIterator {

    FuzzyManager _iterable;
    int          _pointer;
    boolean      _sets;     //if TRUE, retrieve FuzzySets. If FALSE, retrieve FuzzyPropositions
    
    public FuzzyManagerIterator(FuzzyManager it, boolean sets){
        _iterable = it;
        _sets = sets;
        _pointer = -1;
    }
    
    public void init(){
        _pointer = -1;
    }
    
    public void debug(){
        System.out.println(" POINTER " + _pointer);
    }
    
    public boolean hasMoreElements(){
        if((_pointer+1) == FuzzyConstants.FUZZY_RELATIONS.length)
            return false;
        else return true;
    }
    
    public FuzzySet next(){
        _pointer++;
        String pair[] = FuzzyConstants.FUZZY_RELATIONS[_pointer];
        return _iterable.getFuzzySet(pair[0],pair[1]);
    }
}
