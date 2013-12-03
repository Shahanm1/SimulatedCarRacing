package champ2009client.fuzzy;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Diego
 */
public class FuzzySet {

    //Type of fuzzy set
    private int _type;
    private double _limits[];
    private String TEXT;
    private double _truthValue;
    private double _min;
    private double _max;
    private boolean _isLeft;
    private int _numVariables;
    
    private Deffuzifier _deff;
    private int _deffType;
    private boolean _isCrispValid;
    private double _crispValue;
    private double _mutPrecision;
    
    public FuzzySet(){
        _type = -1;
        _deffType = -1;
        _truthValue = -1;
        _crispValue = -1;
        _numVariables = -1;
        _mutPrecision = 0.0f;
        _isLeft = false;
        _isCrispValid = false;
        _limits = new double[4];
        TEXT = "";
        _deff = new Deffuzifier(0,0);
    }

    public FuzzySet(int type, int deffType, String text){ 
        _type = type;
        _deffType = deffType;
        TEXT = text;
        _isCrispValid = false;
        _crispValue = -1;
        _limits = new double[4];
    }

    public int getType() { return _type; }
    public boolean isLeft() { return _isLeft; }
    public int getNumVars() { return _numVariables; } 
    
    public void setMinMax(double min, double max){
        _min = min;
        _max = max;
    }
    
    public double[] getMinMax(){
        return new double[]{_min,_max};
    }
    
    public void initialize(double x1, double x2, double x3, double x4){
        //Adjustments to make fuzzy formula correct
        if(_type == FuzzyConstants.SMOOTH_TRAPEZOIDAL_LEFT || _type == FuzzyConstants.TRAPEZOIDAL_LEFT){
            x1 = -Double.MAX_VALUE;
            x2 = -Double.MAX_VALUE;
            _isLeft = true;
            _numVariables = 2;
        }else  if(_type == FuzzyConstants.SMOOTH_TRAPEZOIDAL_RIGHT || _type == FuzzyConstants.TRAPEZOIDAL_RIGHT){
            x3 = Double.MAX_VALUE;
            x4 = Double.MAX_VALUE;
            _isLeft = false;
            _numVariables = 2;
        }else  if(_type == FuzzyConstants.STEP_LEFT){
            x1 = -Double.MAX_VALUE;
            x2 = -Double.MAX_VALUE;
            x3 = -Double.MAX_VALUE;
            _isLeft = true;
            _numVariables = 0;
        }else  if(_type == FuzzyConstants.STEP_RIGHT){
            x2 = Double.MAX_VALUE;
            x3 = Double.MAX_VALUE;
            x4 = Double.MAX_VALUE;
            _isLeft = false;
            _numVariables = 0;
        }else  if(_type == FuzzyConstants.SMOOTH_TRAPEZOIDAL_CENTER || _type == FuzzyConstants.TRAPEZOIDAL_CENTER){
            _isLeft = false;
            _numVariables = 4;
        }
        
        /*_limits[0] = x1;
        _limits[1] = x2;
        _limits[2] = x3;
        _limits[3] = x4;*/
        setFuzzyLimit(0,x1);
        setFuzzyLimit(1,x2);
        setFuzzyLimit(2,x3);
        setFuzzyLimit(3,x4);
        _isCrispValid = false;
    }
     
    
    public void initDeffuzifier(){
        _deff = new Deffuzifier(_min,_max);
        _isCrispValid = false;
        int points = Deffuzifier.NUMBER_OF_POINTS;
        double step = _min;
        double stepInc = (_max - _min)/points;
        for(int i = 0; i < points; i++, step+=stepInc){
            _deff.setValue(i, truth_value(step));
            //System.out.println("["+TEXT+"] Setting value[" + step + "]: " + _deff.getValue(i));
        }
        
        //Calculate first Crisp value
        calculateCrispValue();
        
    }
   
    private double calculateCrispValue(){
        _isCrispValid = true;
        switch(_deffType){
            case FuzzyConstants.CENTER_OF_GRAVITY:
                _crispValue = _deff.cog();
                break;
            case FuzzyConstants.ABSOLUTE:
                _crispValue = 1;
                break;
            default:
                System.out.println("Un recognizad deffuzification type: " + _deffType); 
                _isCrispValid = false;
                _crispValue = -1;
        }
        return _crispValue;
    }
    
    public double getCrispValue(){
        
        if(_isCrispValid) return _crispValue;
        else return calculateCrispValue();
        
    }
    
    public void setFuzzyLimit(int which, double value){
        //All limits must have a precision of 3 decimals after the point
        int num = (int) (value*1000);
        value = (double)num / 1000.0f;
        _limits[which] = value;
        _isCrispValid = false;
    } 
    
    public double getFuzzyLimit(int which){
        return _limits[which];
    }
    
    public double[] getFuzzyLimits(){
        return _limits;
    }
    
    public String getText(){
        return TEXT;
    }
    
    public double getTruthValue(){
        return _truthValue;
    }

    public double getMutPrecision() {
        return _mutPrecision;
    }

    public void setMutPrecision(double mutPrecision) {
        _mutPrecision = mutPrecision;
    }
    
    
    
    private double sig_formula_left(double a, double x, double b){
        return 0.5 + 0.5*Math.cos(Math.PI * (x-b)/(b-a));
    }
    
    private double sig_formula_right(double c, double x, double d){
        return 0.5 + 0.5*Math.cos(Math.PI * (x-c)/(d-c));
    }
    
    private double truth_value_smooth(double x){
        if(x <= _limits[0]) _truthValue = 0;
        else if(x > _limits[0] && x <= _limits[1]) _truthValue = sig_formula_left(_limits[0],x,_limits[1]);
        else if(x > _limits[1] && x <= _limits[2]) _truthValue = 1;
        else if(x > _limits[2] && x <= _limits[3]) _truthValue = sig_formula_right(_limits[2],x,_limits[3]);
        else if(x > _limits[3]) _truthValue = 0;

        return _truthValue;
    }
    
    public double truth_value(double x){
        if(_type == FuzzyConstants.SMOOTH_TRAPEZOIDAL_CENTER || 
           _type == FuzzyConstants.SMOOTH_TRAPEZOIDAL_LEFT || 
           _type == FuzzyConstants.SMOOTH_TRAPEZOIDAL_RIGHT){
            return truth_value_smooth(x);
        }else if( (_type == FuzzyConstants.STEP_LEFT && x <= _limits[3]) ||
                 (_type == FuzzyConstants.STEP_RIGHT && x >= _limits[0]) ||
                 (_type == FuzzyConstants.STEP_CENTER && x >= _limits[1]) && x <= _limits[2] ){
            //_truthValue = 1;
            _truthValue = FuzzyConstants.STEP_ABSOLUTE_VALUE;//Double.MAX_VALUE;
        }else _truthValue = 0;
        
        return _truthValue;
    }
    
}
