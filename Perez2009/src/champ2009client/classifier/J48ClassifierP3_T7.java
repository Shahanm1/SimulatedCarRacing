/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.classifier;

/**
 * This is a Classifier. All the functions are created by WEKA and included in this code.
 * @author Diego
 */
public class J48ClassifierP3_T7 extends Classifier{

  public double classify(Object [] i)
    throws Exception {

    double p = Double.NaN;
    p = J48ClassifierP3_T7.N145e0440(i);
    return p;
  }
  static double N145e0440(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 2;
    } else if (((Double) i[4]).doubleValue() <= 70.27) {
    p = J48ClassifierP3_T7.N86c3471(i);
    } else if (((Double) i[4]).doubleValue() > 70.27) {
    p = J48ClassifierP3_T7.N1b1aa6574(i);
    } 
    return p;
  }
  static double N86c3471(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 2;
    } else if (((Double) i[4]).doubleValue() <= 30.2) {
    p = J48ClassifierP3_T7.N17e6a962(i);
    } else if (((Double) i[4]).doubleValue() > 30.2) {
    p = J48ClassifierP3_T7.N11ddcde37(i);
    } 
    return p;
  }
  static double N17e6a962(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() <= 61.02) {
    p = J48ClassifierP3_T7.N87a5cc3(i);
    } else if (((Double) i[2]).doubleValue() > 61.02) {
      p = 2;
    } 
    return p;
  }
  static double N87a5cc3(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 2;
    } else if (((Double) i[7]).doubleValue() <= 24.04) {
    p = J48ClassifierP3_T7.N1960f054(i);
    } else if (((Double) i[7]).doubleValue() > 24.04) {
    p = J48ClassifierP3_T7.N17471e023(i);
    } 
    return p;
  }
  static double N1960f054(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= 21.26) {
    p = J48ClassifierP3_T7.Nb42cbf5(i);
    } else if (((Double) i[1]).doubleValue() > 21.26) {
    p = J48ClassifierP3_T7.N12d7a1013(i);
    } 
    return p;
  }
  static double Nb42cbf5(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() <= 49.33) {
    p = J48ClassifierP3_T7.Ne5b7236(i);
    } else if (((Double) i[5]).doubleValue() > 49.33) {
      p = 2;
    } 
    return p;
  }
  static double Ne5b7236(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= 8.53) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() > 8.53) {
    p = J48ClassifierP3_T7.N15a87677(i);
    } 
    return p;
  }
  static double N15a87677(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.16) {
    p = J48ClassifierP3_T7.N6f7ce98(i);
    } else if (((Double) i[0]).doubleValue() > 0.16) {
    p = J48ClassifierP3_T7.N8fce9511(i);
    } 
    return p;
  }
  static double N6f7ce98(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 2;
    } else if (((Double) i[7]).doubleValue() <= 5.91) {
      p = 2;
    } else if (((Double) i[7]).doubleValue() > 5.91) {
    p = J48ClassifierP3_T7.N171bbc99(i);
    } 
    return p;
  }
  static double N171bbc99(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 1;
    } else if (((Double) i[3]).doubleValue() <= 59.84) {
    p = J48ClassifierP3_T7.N1dfafd110(i);
    } else if (((Double) i[3]).doubleValue() > 59.84) {
      p = 2;
    } 
    return p;
  }
  static double N1dfafd110(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 2;
    } else if (((Double) i[4]).doubleValue() <= 23.81) {
      p = 2;
    } else if (((Double) i[4]).doubleValue() > 23.81) {
      p = 1;
    } 
    return p;
  }
  static double N8fce9511(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 1;
    } else if (((Double) i[2]).doubleValue() <= 37.61) {
    p = J48ClassifierP3_T7.N143c8b312(i);
    } else if (((Double) i[2]).doubleValue() > 37.61) {
      p = 2;
    } 
    return p;
  }
  static double N143c8b312(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 0.27) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() > 0.27) {
      p = 1;
    } 
    return p;
  }
  static double N12d7a1013(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= -0.05) {
    p = J48ClassifierP3_T7.Na8c48814(i);
    } else if (((Double) i[0]).doubleValue() > -0.05) {
      p = 2;
    } 
    return p;
  }
  static double Na8c48814(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 2;
    } else if (((Double) i[5]).doubleValue() <= 12.69) {
    p = J48ClassifierP3_T7.N1feca6415(i);
    } else if (((Double) i[5]).doubleValue() > 12.69) {
    p = J48ClassifierP3_T7.N148cc8c18(i);
    } 
    return p;
  }
  static double N1feca6415(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 43.42) {
    p = J48ClassifierP3_T7.N998b0816(i);
    } else if (((Double) i[1]).doubleValue() > 43.42) {
      p = 2;
    } 
    return p;
  }
  static double N998b0816(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 37.61) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 37.61) {
    p = J48ClassifierP3_T7.N76cbf717(i);
    } 
    return p;
  }
  static double N76cbf717(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 2;
    } else if (((Double) i[3]).doubleValue() <= 22.39) {
      p = 2;
    } else if (((Double) i[3]).doubleValue() > 22.39) {
      p = 1;
    } 
    return p;
  }
  static double N148cc8c18(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= -0.35) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() > -0.35) {
    p = J48ClassifierP3_T7.N6d084b19(i);
    } 
    return p;
  }
  static double N6d084b19(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= 46.75) {
    p = J48ClassifierP3_T7.N3bb2b820(i);
    } else if (((Double) i[1]).doubleValue() > 46.75) {
    p = J48ClassifierP3_T7.N152544e21(i);
    } 
    return p;
  }
  static double N3bb2b820(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 2;
    } else if (((Double) i[7]).doubleValue() <= 8.64) {
      p = 2;
    } else if (((Double) i[7]).doubleValue() > 8.64) {
      p = 1;
    } 
    return p;
  }
  static double N152544e21(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= -0.3) {
    p = J48ClassifierP3_T7.N1cdeff22(i);
    } else if (((Double) i[0]).doubleValue() > -0.3) {
      p = 2;
    } 
    return p;
  }
  static double N1cdeff22(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() <= 43.98) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() > 43.98) {
      p = 1;
    } 
    return p;
  }
  static double N17471e023(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 2;
    } else if (((Double) i[6]).doubleValue() <= 62.26) {
    p = J48ClassifierP3_T7.N1e04cbf24(i);
    } else if (((Double) i[6]).doubleValue() > 62.26) {
      p = 2;
    } 
    return p;
  }
  static double N1e04cbf24(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 0.28) {
    p = J48ClassifierP3_T7.Ncec0c525(i);
    } else if (((Double) i[0]).doubleValue() > 0.28) {
    p = J48ClassifierP3_T7.N1ac2f9c27(i);
    } 
    return p;
  }
  static double Ncec0c525(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= -0.43) {
    p = J48ClassifierP3_T7.N1d2068d26(i);
    } else if (((Double) i[0]).doubleValue() > -0.43) {
      p = 2;
    } 
    return p;
  }
  static double N1d2068d26(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= -0.46) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() > -0.46) {
      p = 2;
    } 
    return p;
  }
  static double N1ac2f9c27(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 2;
    } else if (((Double) i[4]).doubleValue() <= 25.93) {
    p = J48ClassifierP3_T7.N169ca6528(i);
    } else if (((Double) i[4]).doubleValue() > 25.93) {
      p = 1;
    } 
    return p;
  }
  static double N169ca6528(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.29) {
    p = J48ClassifierP3_T7.N66e81529(i);
    } else if (((Double) i[0]).doubleValue() > 0.29) {
    p = J48ClassifierP3_T7.N10608231(i);
    } 
    return p;
  }
  static double N66e81529(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= 9.12) {
    p = J48ClassifierP3_T7.Nece6530(i);
    } else if (((Double) i[1]).doubleValue() > 9.12) {
      p = 2;
    } 
    return p;
  }
  static double Nece6530(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 6.93) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 6.93) {
      p = 1;
    } 
    return p;
  }
  static double N10608231(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 2;
    } else if (((Double) i[3]).doubleValue() <= 10.56) {
      p = 2;
    } else if (((Double) i[3]).doubleValue() > 10.56) {
    p = J48ClassifierP3_T7.N1301ed832(i);
    } 
    return p;
  }
  static double N1301ed832(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 2;
    } else if (((Double) i[7]).doubleValue() <= 72.08) {
    p = J48ClassifierP3_T7.N3901c633(i);
    } else if (((Double) i[7]).doubleValue() > 72.08) {
      p = 2;
    } 
    return p;
  }
  static double N3901c633(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.48) {
    p = J48ClassifierP3_T7.Na3736834(i);
    } else if (((Double) i[0]).doubleValue() > 0.48) {
    p = J48ClassifierP3_T7.Nedc3a235(i);
    } 
    return p;
  }
  static double Na3736834(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 0.39) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() > 0.39) {
      p = 1;
    } 
    return p;
  }
  static double Nedc3a235(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 8.06) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 8.06) {
    p = J48ClassifierP3_T7.N1c6f57936(i);
    } 
    return p;
  }
  static double N1c6f57936(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() <= 36.11) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() > 36.11) {
      p = 2;
    } 
    return p;
  }
  static double N11ddcde37(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 1;
    } else if (((Double) i[3]).doubleValue() <= 98.54) {
    p = J48ClassifierP3_T7.N18fb1f738(i);
    } else if (((Double) i[3]).doubleValue() > 98.54) {
      p = 2;
    } 
    return p;
  }
  static double N18fb1f738(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 1;
    } else if (((Double) i[6]).doubleValue() <= 47.95) {
    p = J48ClassifierP3_T7.Ned033839(i);
    } else if (((Double) i[6]).doubleValue() > 47.95) {
    p = J48ClassifierP3_T7.N1507fb262(i);
    } 
    return p;
  }
  static double Ned033839(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= 24.59) {
    p = J48ClassifierP3_T7.N6e70c740(i);
    } else if (((Double) i[1]).doubleValue() > 24.59) {
    p = J48ClassifierP3_T7.N16a78661(i);
    } 
    return p;
  }
  static double N6e70c740(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 1;
    } else if (((Double) i[4]).doubleValue() <= 38.61) {
    p = J48ClassifierP3_T7.Nae506e41(i);
    } else if (((Double) i[4]).doubleValue() > 38.61) {
    p = J48ClassifierP3_T7.Nd0a5d949(i);
    } 
    return p;
  }
  static double Nae506e41(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 1;
    } else if (((Double) i[2]).doubleValue() <= 88.02) {
    p = J48ClassifierP3_T7.N228a0242(i);
    } else if (((Double) i[2]).doubleValue() > 88.02) {
      p = 2;
    } 
    return p;
  }
  static double N228a0242(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 1;
    } else if (((Double) i[3]).doubleValue() <= 64.58) {
    p = J48ClassifierP3_T7.N192b99643(i);
    } else if (((Double) i[3]).doubleValue() > 64.58) {
      p = 2;
    } 
    return p;
  }
  static double N192b99643(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() <= 66.75) {
    p = J48ClassifierP3_T7.N1d63e3944(i);
    } else if (((Double) i[5]).doubleValue() > 66.75) {
      p = 2;
    } 
    return p;
  }
  static double N1d63e3944(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= 19.63) {
    p = J48ClassifierP3_T7.N8f4fb345(i);
    } else if (((Double) i[1]).doubleValue() > 19.63) {
      p = 2;
    } 
    return p;
  }
  static double N8f4fb345(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.12) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() > 0.12) {
    p = J48ClassifierP3_T7.Nb988a646(i);
    } 
    return p;
  }
  static double Nb988a646(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 2;
    } else if (((Double) i[7]).doubleValue() <= 14.69) {
      p = 2;
    } else if (((Double) i[7]).doubleValue() > 14.69) {
    p = J48ClassifierP3_T7.Nba6c8347(i);
    } 
    return p;
  }
  static double Nba6c8347(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= 13.98) {
    p = J48ClassifierP3_T7.N12a1e4448(i);
    } else if (((Double) i[1]).doubleValue() > 13.98) {
      p = 2;
    } 
    return p;
  }
  static double N12a1e4448(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.49) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() > 0.49) {
      p = 2;
    } 
    return p;
  }
  static double Nd0a5d949(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() <= 94.17) {
    p = J48ClassifierP3_T7.N38899350(i);
    } else if (((Double) i[5]).doubleValue() > 94.17) {
    p = J48ClassifierP3_T7.N126804e56(i);
    } 
    return p;
  }
  static double N38899350(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() <= 8.83) {
    p = J48ClassifierP3_T7.N1d0465351(i);
    } else if (((Double) i[2]).doubleValue() > 8.83) {
    p = J48ClassifierP3_T7.Nb8f82d52(i);
    } 
    return p;
  }
  static double N1d0465351(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() <= 8.68) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() > 8.68) {
      p = 1;
    } 
    return p;
  }
  static double Nb8f82d52(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 1;
    } else if (((Double) i[6]).doubleValue() <= 22.02) {
    p = J48ClassifierP3_T7.N1ad77a753(i);
    } else if (((Double) i[6]).doubleValue() > 22.02) {
      p = 1;
    } 
    return p;
  }
  static double N1ad77a753(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.12) {
    p = J48ClassifierP3_T7.N18aaa1e54(i);
    } else if (((Double) i[0]).doubleValue() > 0.12) {
      p = 2;
    } 
    return p;
  }
  static double N18aaa1e54(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 1;
    } else if (((Double) i[3]).doubleValue() <= 70.66) {
      p = 1;
    } else if (((Double) i[3]).doubleValue() > 70.66) {
    p = J48ClassifierP3_T7.Na6aeed55(i);
    } 
    return p;
  }
  static double Na6aeed55(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 1;
    } else if (((Double) i[2]).doubleValue() <= 24.62) {
      p = 1;
    } else if (((Double) i[2]).doubleValue() > 24.62) {
      p = 2;
    } 
    return p;
  }
  static double N126804e56(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 1;
    } else if (((Double) i[3]).doubleValue() <= 30.86) {
    p = J48ClassifierP3_T7.Nb1b4c357(i);
    } else if (((Double) i[3]).doubleValue() > 30.86) {
    p = J48ClassifierP3_T7.Nd2906a58(i);
    } 
    return p;
  }
  static double Nb1b4c357(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 0.31) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() > 0.31) {
      p = 1;
    } 
    return p;
  }
  static double Nd2906a58(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 0;
    } else if (((Double) i[1]).doubleValue() <= 22.63) {
    p = J48ClassifierP3_T7.N72ffb59(i);
    } else if (((Double) i[1]).doubleValue() > 22.63) {
      p = 1;
    } 
    return p;
  }
  static double N72ffb59(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() <= -0.16) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() > -0.16) {
    p = J48ClassifierP3_T7.N1df38fd60(i);
    } 
    return p;
  }
  static double N1df38fd60(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 1;
    } else if (((Double) i[4]).doubleValue() <= 68.44) {
      p = 1;
    } else if (((Double) i[4]).doubleValue() > 68.44) {
      p = 0;
    } 
    return p;
  }
  static double N16a78661(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.14) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() > 0.14) {
      p = 2;
    } 
    return p;
  }
  static double N1507fb262(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 14.25) {
    p = J48ClassifierP3_T7.N1efb83663(i);
    } else if (((Double) i[1]).doubleValue() > 14.25) {
    p = J48ClassifierP3_T7.N116ab4e71(i);
    } 
    return p;
  }
  static double N1efb83663(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 0.36) {
    p = J48ClassifierP3_T7.N126e85f64(i);
    } else if (((Double) i[0]).doubleValue() > 0.36) {
    p = J48ClassifierP3_T7.N77158a69(i);
    } 
    return p;
  }
  static double N126e85f64(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() <= 56.64) {
    p = J48ClassifierP3_T7.N161f10f65(i);
    } else if (((Double) i[5]).doubleValue() > 56.64) {
      p = 2;
    } 
    return p;
  }
  static double N161f10f65(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 0.08) {
    p = J48ClassifierP3_T7.N119377966(i);
    } else if (((Double) i[0]).doubleValue() > 0.08) {
    p = J48ClassifierP3_T7.N8916a267(i);
    } 
    return p;
  }
  static double N119377966(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 14.09) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 14.09) {
      p = 1;
    } 
    return p;
  }
  static double N8916a267(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 2;
    } else if (((Double) i[6]).doubleValue() <= 56.97) {
    p = J48ClassifierP3_T7.N2ce90868(i);
    } else if (((Double) i[6]).doubleValue() > 56.97) {
      p = 1;
    } 
    return p;
  }
  static double N2ce90868(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 0.27) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() > 0.27) {
      p = 1;
    } 
    return p;
  }
  static double N77158a69(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() <= 98.91) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() > 98.91) {
    p = J48ClassifierP3_T7.N27391d70(i);
    } 
    return p;
  }
  static double N27391d70(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.43) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() > 0.43) {
      p = 2;
    } 
    return p;
  }
  static double N116ab4e71(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.16) {
    p = J48ClassifierP3_T7.N148aa2372(i);
    } else if (((Double) i[0]).doubleValue() > 0.16) {
      p = 2;
    } 
    return p;
  }
  static double N148aa2372(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 1;
    } else if (((Double) i[6]).doubleValue() <= 52.66) {
    p = J48ClassifierP3_T7.N199f91c73(i);
    } else if (((Double) i[6]).doubleValue() > 52.66) {
      p = 1;
    } 
    return p;
  }
  static double N199f91c73(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= -0.11) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() > -0.11) {
      p = 2;
    } 
    return p;
  }
  static double N1b1aa6574(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 0;
    } else if (((Double) i[4]).doubleValue() <= 74.68) {
    p = J48ClassifierP3_T7.N129f3b575(i);
    } else if (((Double) i[4]).doubleValue() > 74.68) {
    p = J48ClassifierP3_T7.N1f4689e81(i);
    } 
    return p;
  }
  static double N129f3b575(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 0;
    } else if (((Double) i[5]).doubleValue() <= 44.24) {
    p = J48ClassifierP3_T7.N13f304576(i);
    } else if (((Double) i[5]).doubleValue() > 44.24) {
    p = J48ClassifierP3_T7.Nd80be380(i);
    } 
    return p;
  }
  static double N13f304576(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() <= -0.03) {
    p = J48ClassifierP3_T7.N17a29a177(i);
    } else if (((Double) i[0]).doubleValue() > -0.03) {
      p = 2;
    } 
    return p;
  }
  static double N17a29a177(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 0;
    } else if (((Double) i[3]).doubleValue() <= 61.28) {
    p = J48ClassifierP3_T7.N143423478(i);
    } else if (((Double) i[3]).doubleValue() > 61.28) {
      p = 2;
    } 
    return p;
  }
  static double N143423478(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 0;
    } else if (((Double) i[6]).doubleValue() <= 15.56) {
    p = J48ClassifierP3_T7.Naf835879(i);
    } else if (((Double) i[6]).doubleValue() > 15.56) {
      p = 0;
    } 
    return p;
  }
  static double Naf835879(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= 13.69) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() > 13.69) {
      p = 0;
    } 
    return p;
  }
  static double Nd80be380(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 0;
    } else if (((Double) i[6]).doubleValue() <= 40.57) {
      p = 0;
    } else if (((Double) i[6]).doubleValue() > 40.57) {
      p = 2;
    } 
    return p;
  }
  static double N1f4689e81(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 0;
    } else if (((Double) i[4]).doubleValue() <= 95.41) {
    p = J48ClassifierP3_T7.N1006d7582(i);
    } else if (((Double) i[4]).doubleValue() > 95.41) {
    p = J48ClassifierP3_T7.N13c1b0291(i);
    } 
    return p;
  }
  static double N1006d7582(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 0;
    } else if (((Double) i[3]).doubleValue() <= 53.37) {
    p = J48ClassifierP3_T7.N112512783(i);
    } else if (((Double) i[3]).doubleValue() > 53.37) {
    p = J48ClassifierP3_T7.N1db4f6f90(i);
    } 
    return p;
  }
  static double N112512783(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 0;
    } else if (((Double) i[1]).doubleValue() <= 14.65) {
    p = J48ClassifierP3_T7.N18dfef884(i);
    } else if (((Double) i[1]).doubleValue() > 14.65) {
      p = 0;
    } 
    return p;
  }
  static double N18dfef884(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 0;
    } else if (((Double) i[3]).doubleValue() <= 41.78) {
    p = J48ClassifierP3_T7.N15e83f985(i);
    } else if (((Double) i[3]).doubleValue() > 41.78) {
      p = 2;
    } 
    return p;
  }
  static double N15e83f985(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 0;
    } else if (((Double) i[3]).doubleValue() <= 21.48) {
    p = J48ClassifierP3_T7.N2a533086(i);
    } else if (((Double) i[3]).doubleValue() > 21.48) {
    p = J48ClassifierP3_T7.Nd6c16c88(i);
    } 
    return p;
  }
  static double N2a533086(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 0;
    } else if (((Double) i[1]).doubleValue() <= 6.34) {
    p = J48ClassifierP3_T7.Nbb746587(i);
    } else if (((Double) i[1]).doubleValue() > 6.34) {
      p = 2;
    } 
    return p;
  }
  static double Nbb746587(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 5.88) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 5.88) {
      p = 0;
    } 
    return p;
  }
  static double Nd6c16c88(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 0;
    } else if (((Double) i[1]).doubleValue() <= 13.48) {
      p = 0;
    } else if (((Double) i[1]).doubleValue() > 13.48) {
    p = J48ClassifierP3_T7.N134bed089(i);
    } 
    return p;
  }
  static double N134bed089(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() <= 0.09) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() > 0.09) {
      p = 2;
    } 
    return p;
  }
  static double N1db4f6f90(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 19.51) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 19.51) {
      p = 0;
    } 
    return p;
  }
  static double N13c1b0291(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() <= 0.21) {
    p = J48ClassifierP3_T7.N11121f692(i);
    } else if (((Double) i[0]).doubleValue() > 0.21) {
      p = 0;
    } 
    return p;
  }
  static double N11121f692(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() <= 0.13) {
    p = J48ClassifierP3_T7.N1ccce3c93(i);
    } else if (((Double) i[0]).doubleValue() > 0.13) {
    p = J48ClassifierP3_T7.N10655dd95(i);
    } 
    return p;
  }
  static double N1ccce3c93(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 0;
    } else if (((Double) i[2]).doubleValue() <= 18.28) {
    p = J48ClassifierP3_T7.Nf7f54094(i);
    } else if (((Double) i[2]).doubleValue() > 18.28) {
      p = 0;
    } 
    return p;
  }
  static double Nf7f54094(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 0;
    } else if (((Double) i[7]).doubleValue() <= 11.56) {
      p = 0;
    } else if (((Double) i[7]).doubleValue() > 11.56) {
      p = 2;
    } 
    return p;
  }
  static double N10655dd95(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 2;
    } else if (((Double) i[7]).doubleValue() <= 20.43) {
      p = 2;
    } else if (((Double) i[7]).doubleValue() > 20.43) {
      p = 0;
    } 
    return p;
  }
}

