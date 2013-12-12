package champ2009client.classifier;

public class J48Classifier5 extends Classifier {

    public double classify(Object[] i) throws Exception {
        double p = Double.NaN;
        p = J48Classifier5.N17e6a960(i);
        return p;
    }
  static double N17e6a960(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 4;
    } else if (((Double) i[5]).doubleValue() <= 70.28) {
    p = J48Classifier5.N87a5cc1(i);
    } else if (((Double) i[5]).doubleValue() > 70.28) {
    p = J48Classifier5.Nf7f54092(i);
    } 
    return p;
  }
  static double N87a5cc1(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 4;
    } else if (((Double) i[0]).doubleValue() <= 0.01) {
    p = J48Classifier5.N1960f052(i);
    } else if (((Double) i[0]).doubleValue() > 0.01) {
    p = J48Classifier5.N116ab4e69(i);
    } 
    return p;
  }
  static double N1960f052(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 1;
    } else if (((Double) i[7]).doubleValue() <= 18.53) {
    p = J48Classifier5.Nb42cbf3(i);
    } else if (((Double) i[7]).doubleValue() > 18.53) {
    p = J48Classifier5.N148cc8c16(i);
    } 
    return p;
  }
  static double Nb42cbf3(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 1;
    } else if (((Double) i[6]).doubleValue() <= 32.54) {
    p = J48Classifier5.Ne5b7234(i);
    } else if (((Double) i[6]).doubleValue() > 32.54) {
    p = J48Classifier5.N1feca6413(i);
    } 
    return p;
  }
  static double Ne5b7234(Object []i) {
    double p = Double.NaN;
    if (i[8] == null) {
      p = 2;
    } else if (((Double) i[8]).doubleValue() <= 6.27) {
    p = J48Classifier5.N15a87675(i);
    } else if (((Double) i[8]).doubleValue() > 6.27) {
    p = J48Classifier5.N1dfafd18(i);
    } 
    return p;
  }
  static double N15a87675(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 3;
    } else if (((Double) i[7]).doubleValue() <= 8.35) {
    p = J48Classifier5.N6f7ce96(i);
    } else if (((Double) i[7]).doubleValue() > 8.35) {
    p = J48Classifier5.N171bbc97(i);
    } 
    return p;
  }
  static double N6f7ce96(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 3;
    } else if (((Double) i[1]).doubleValue() <= -0.47) {
      p = 3;
    } else if (((Double) i[1]).doubleValue() > -0.47) {
      p = 1;
    } 
    return p;
  }
  static double N171bbc97(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 2;
    } else if (((Double) i[5]).doubleValue() <= 68.89) {
      p = 2;
    } else if (((Double) i[5]).doubleValue() > 68.89) {
      p = 0;
    } 
    return p;
  }
  static double N1dfafd18(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= -0.08) {
    p = J48Classifier5.N8fce959(i);
    } else if (((Double) i[1]).doubleValue() > -0.08) {
    p = J48Classifier5.Na8c48812(i);
    } 
    return p;
  }
  static double N8fce959(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 1;
    } else if (((Double) i[3]).doubleValue() <= 84.43) {
      p = 1;
    } else if (((Double) i[3]).doubleValue() > 84.43) {
    p = J48Classifier5.N143c8b310(i);
    } 
    return p;
  }
  static double N143c8b310(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= -0.09) {
    p = J48Classifier5.N12d7a1011(i);
    } else if (((Double) i[1]).doubleValue() > -0.09) {
      p = 3;
    } 
    return p;
  }
  static double N12d7a1011(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.0) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() > 0.0) {
      p = 3;
    } 
    return p;
  }
  static double Na8c48812(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 3;
    } else if (((Double) i[1]).doubleValue() <= 0.03) {
      p = 3;
    } else if (((Double) i[1]).doubleValue() > 0.03) {
      p = 1;
    } 
    return p;
  }
  static double N1feca6413(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 0.01) {
    p = J48Classifier5.N998b0814(i);
    } else if (((Double) i[1]).doubleValue() > 0.01) {
      p = 1;
    } 
    return p;
  }
  static double N998b0814(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 2;
    } else if (((Double) i[6]).doubleValue() <= 33.28) {
    p = J48Classifier5.N76cbf715(i);
    } else if (((Double) i[6]).doubleValue() > 33.28) {
      p = 2;
    } 
    return p;
  }
  static double N76cbf715(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= -0.01) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() > -0.01) {
      p = 2;
    } 
    return p;
  }
  static double N148cc8c16(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 4;
    } else if (((Double) i[3]).doubleValue() <= 21.05) {
    p = J48Classifier5.N6d084b17(i);
    } else if (((Double) i[3]).doubleValue() > 21.05) {
    p = J48Classifier5.N1efb83662(i);
    } 
    return p;
  }
  static double N6d084b17(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 4;
    } else if (((Double) i[0]).doubleValue() <= -0.02) {
    p = J48Classifier5.N3bb2b818(i);
    } else if (((Double) i[0]).doubleValue() > -0.02) {
    p = J48Classifier5.N6e70c738(i);
    } 
    return p;
  }
  static double N3bb2b818(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 4;
    } else if (((Double) i[7]).doubleValue() <= 47.81) {
    p = J48Classifier5.N152544e19(i);
    } else if (((Double) i[7]).doubleValue() > 47.81) {
    p = J48Classifier5.Nece6528(i);
    } 
    return p;
  }
  static double N152544e19(Object []i) {
    double p = Double.NaN;
    if (i[8] == null) {
      p = 4;
    } else if (((Double) i[8]).doubleValue() <= 18.57) {
    p = J48Classifier5.N1cdeff20(i);
    } else if (((Double) i[8]).doubleValue() > 18.57) {
    p = J48Classifier5.N1e04cbf22(i);
    } 
    return p;
  }
  static double N1cdeff20(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 2;
    } else if (((Double) i[6]).doubleValue() <= 76.81) {
    p = J48Classifier5.N17471e021(i);
    } else if (((Double) i[6]).doubleValue() > 76.81) {
      p = 4;
    } 
    return p;
  }
  static double N17471e021(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 0.39) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 0.39) {
      p = 3;
    } 
    return p;
  }
  static double N1e04cbf22(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= -0.45) {
    p = J48Classifier5.Ncec0c523(i);
    } else if (((Double) i[1]).doubleValue() > -0.45) {
    p = J48Classifier5.N1d2068d24(i);
    } 
    return p;
  }
  static double Ncec0c523(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= -0.47) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > -0.47) {
      p = 4;
    } 
    return p;
  }
  static double N1d2068d24(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 4;
    } else if (((Double) i[0]).doubleValue() <= -0.05) {
      p = 4;
    } else if (((Double) i[0]).doubleValue() > -0.05) {
    p = J48Classifier5.N1ac2f9c25(i);
    } 
    return p;
  }
  static double N1ac2f9c25(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 0.47) {
    p = J48Classifier5.N169ca6526(i);
    } else if (((Double) i[1]).doubleValue() > 0.47) {
      p = 4;
    } 
    return p;
  }
  static double N169ca6526(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 4;
    } else if (((Double) i[0]).doubleValue() <= -0.04) {
    p = J48Classifier5.N66e81527(i);
    } else if (((Double) i[0]).doubleValue() > -0.04) {
      p = 2;
    } 
    return p;
  }
  static double N66e81527(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 4;
    } else if (((Double) i[1]).doubleValue() <= 0.45) {
      p = 4;
    } else if (((Double) i[1]).doubleValue() > 0.45) {
      p = 2;
    } 
    return p;
  }
  static double Nece6528(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 4;
    } else if (((Double) i[4]).doubleValue() <= 21.77) {
    p = J48Classifier5.N10608229(i);
    } else if (((Double) i[4]).doubleValue() > 21.77) {
    p = J48Classifier5.Ned033837(i);
    } 
    return p;
  }
  static double N10608229(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 4;
    } else if (((Double) i[1]).doubleValue() <= 0.47) {
    p = J48Classifier5.N1301ed830(i);
    } else if (((Double) i[1]).doubleValue() > 0.47) {
    p = J48Classifier5.N11ddcde35(i);
    } 
    return p;
  }
  static double N1301ed830(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 4;
    } else if (((Double) i[1]).doubleValue() <= 0.14) {
      p = 4;
    } else if (((Double) i[1]).doubleValue() > 0.14) {
    p = J48Classifier5.N3901c631(i);
    } 
    return p;
  }
  static double N3901c631(Object []i) {
    double p = Double.NaN;
    if (i[8] == null) {
      p = 4;
    } else if (((Double) i[8]).doubleValue() <= 50.77) {
      p = 4;
    } else if (((Double) i[8]).doubleValue() > 50.77) {
    p = J48Classifier5.Na3736832(i);
    } 
    return p;
  }
  static double Na3736832(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 4;
    } else if (((Double) i[6]).doubleValue() <= 40.02) {
    p = J48Classifier5.Nedc3a233(i);
    } else if (((Double) i[6]).doubleValue() > 40.02) {
      p = 3;
    } 
    return p;
  }
  static double Nedc3a233(Object []i) {
    double p = Double.NaN;
    if (i[8] == null) {
      p = 4;
    } else if (((Double) i[8]).doubleValue() <= 68.1) {
      p = 4;
    } else if (((Double) i[8]).doubleValue() > 68.1) {
    p = J48Classifier5.N1c6f57934(i);
    } 
    return p;
  }
  static double N1c6f57934(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 4;
    } else if (((Double) i[5]).doubleValue() <= 20.16) {
      p = 4;
    } else if (((Double) i[5]).doubleValue() > 20.16) {
      p = 3;
    } 
    return p;
  }
  static double N11ddcde35(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 4;
    } else if (((Double) i[0]).doubleValue() <= -0.07) {
    p = J48Classifier5.N18fb1f736(i);
    } else if (((Double) i[0]).doubleValue() > -0.07) {
      p = 4;
    } 
    return p;
  }
  static double N18fb1f736(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 4;
    } else if (((Double) i[5]).doubleValue() <= 21.69) {
      p = 4;
    } else if (((Double) i[5]).doubleValue() > 21.69) {
      p = 3;
    } 
    return p;
  }
  static double Ned033837(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 4;
    } else if (((Double) i[0]).doubleValue() <= -0.03) {
      p = 4;
    } else if (((Double) i[0]).doubleValue() > -0.03) {
      p = 2;
    } 
    return p;
  }
  static double N6e70c738(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 2;
    } else if (((Double) i[3]).doubleValue() <= 13.85) {
    p = J48Classifier5.Nae506e39(i);
    } else if (((Double) i[3]).doubleValue() > 13.85) {
    p = J48Classifier5.Nb8f82d51(i);
    } 
    return p;
  }
  static double Nae506e39(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 4;
    } else if (((Double) i[4]).doubleValue() <= 16.72) {
    p = J48Classifier5.N228a0240(i);
    } else if (((Double) i[4]).doubleValue() > 16.72) {
    p = J48Classifier5.N8f4fb343(i);
    } 
    return p;
  }
  static double N228a0240(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 0.48) {
    p = J48Classifier5.N192b99641(i);
    } else if (((Double) i[1]).doubleValue() > 0.48) {
      p = 4;
    } 
    return p;
  }
  static double N192b99641(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 4;
    } else if (((Double) i[1]).doubleValue() <= 0.27) {
    p = J48Classifier5.N1d63e3942(i);
    } else if (((Double) i[1]).doubleValue() > 0.27) {
      p = 2;
    } 
    return p;
  }
  static double N1d63e3942(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 0.04) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 0.04) {
      p = 4;
    } 
    return p;
  }
  static double N8f4fb343(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 1;
    } else if (((Double) i[6]).doubleValue() <= 53.95) {
    p = J48Classifier5.Nb988a644(i);
    } else if (((Double) i[6]).doubleValue() > 53.95) {
    p = J48Classifier5.Nd0a5d948(i);
    } 
    return p;
  }
  static double Nb988a644(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 1;
    } else if (((Double) i[7]).doubleValue() <= 39.65) {
    p = J48Classifier5.Nba6c8345(i);
    } else if (((Double) i[7]).doubleValue() > 39.65) {
    p = J48Classifier5.N12a1e4446(i);
    } 
    return p;
  }
  static double Nba6c8345(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 0.31) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 0.31) {
      p = 1;
    } 
    return p;
  }
  static double N12a1e4446(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 4;
    } else if (((Double) i[1]).doubleValue() <= 0.27) {
    p = J48Classifier5.N29428e47(i);
    } else if (((Double) i[1]).doubleValue() > 0.27) {
      p = 2;
    } 
    return p;
  }
  static double N29428e47(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 0.17) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 0.17) {
      p = 4;
    } 
    return p;
  }
  static double Nd0a5d948(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 2;
    } else if (((Double) i[6]).doubleValue() <= 98.57) {
    p = J48Classifier5.N38899349(i);
    } else if (((Double) i[6]).doubleValue() > 98.57) {
      p = 4;
    } 
    return p;
  }
  static double N38899349(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 2;
    } else if (((Double) i[5]).doubleValue() <= 68.37) {
      p = 2;
    } else if (((Double) i[5]).doubleValue() > 68.37) {
    p = J48Classifier5.N1d0465350(i);
    } 
    return p;
  }
  static double N1d0465350(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() <= 9.26) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() > 9.26) {
      p = 1;
    } 
    return p;
  }
  static double Nb8f82d51(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 0.12) {
    p = J48Classifier5.N1ad77a752(i);
    } else if (((Double) i[1]).doubleValue() > 0.12) {
    p = J48Classifier5.N18aaa1e53(i);
    } 
    return p;
  }
  static double N1ad77a752(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 1;
    } else if (((Double) i[7]).doubleValue() <= 18.83) {
      p = 1;
    } else if (((Double) i[7]).doubleValue() > 18.83) {
      p = 2;
    } 
    return p;
  }
  static double N18aaa1e53(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 4;
    } else if (((Double) i[1]).doubleValue() <= 0.36) {
    p = J48Classifier5.Na6aeed54(i);
    } else if (((Double) i[1]).doubleValue() > 0.36) {
    p = J48Classifier5.N16a78660(i);
    } 
    return p;
  }
  static double Na6aeed54(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 4;
    } else if (((Double) i[1]).doubleValue() <= 0.33) {
    p = J48Classifier5.N126804e55(i);
    } else if (((Double) i[1]).doubleValue() > 0.33) {
    p = J48Classifier5.N1df38fd59(i);
    } 
    return p;
  }
  static double N126804e55(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 4;
    } else if (((Double) i[2]).doubleValue() <= 14.18) {
    p = J48Classifier5.Nb1b4c356(i);
    } else if (((Double) i[2]).doubleValue() > 14.18) {
    p = J48Classifier5.N72ffb58(i);
    } 
    return p;
  }
  static double Nb1b4c356(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() <= 34.67) {
    p = J48Classifier5.Nd2906a57(i);
    } else if (((Double) i[5]).doubleValue() > 34.67) {
      p = 4;
    } 
    return p;
  }
  static double Nd2906a57(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 4;
    } else if (((Double) i[2]).doubleValue() <= 12.73) {
      p = 4;
    } else if (((Double) i[2]).doubleValue() > 12.73) {
      p = 1;
    } 
    return p;
  }
  static double N72ffb58(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 4;
    } else if (((Double) i[1]).doubleValue() <= 0.14) {
      p = 4;
    } else if (((Double) i[1]).doubleValue() > 0.14) {
      p = 1;
    } 
    return p;
  }
  static double N1df38fd59(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 1;
    } else if (((Double) i[7]).doubleValue() <= 47.04) {
      p = 1;
    } else if (((Double) i[7]).doubleValue() > 47.04) {
      p = 4;
    } 
    return p;
  }
  static double N16a78660(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 1;
    } else if (((Double) i[6]).doubleValue() <= 47.19) {
      p = 1;
    } else if (((Double) i[6]).doubleValue() > 47.19) {
    p = J48Classifier5.N1507fb261(i);
    } 
    return p;
  }
  static double N1507fb261(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 2;
    } else if (((Double) i[5]).doubleValue() <= 68.56) {
      p = 2;
    } else if (((Double) i[5]).doubleValue() > 68.56) {
      p = 0;
    } 
    return p;
  }
  static double N1efb83662(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 1;
    } else if (((Double) i[6]).doubleValue() <= 93.74) {
    p = J48Classifier5.N126e85f63(i);
    } else if (((Double) i[6]).doubleValue() > 93.74) {
    p = J48Classifier5.N27391d68(i);
    } 
    return p;
  }
  static double N126e85f63(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 0.08) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 0.08) {
    p = J48Classifier5.N161f10f64(i);
    } 
    return p;
  }
  static double N161f10f64(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 1;
    } else if (((Double) i[6]).doubleValue() <= 53.94) {
    p = J48Classifier5.N119377965(i);
    } else if (((Double) i[6]).doubleValue() > 53.94) {
    p = J48Classifier5.N2ce90867(i);
    } 
    return p;
  }
  static double N119377965(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= 0.41) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() > 0.41) {
    p = J48Classifier5.N8916a266(i);
    } 
    return p;
  }
  static double N8916a266(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 1;
    } else if (((Double) i[2]).doubleValue() <= 11.79) {
      p = 1;
    } else if (((Double) i[2]).doubleValue() > 11.79) {
      p = 3;
    } 
    return p;
  }
  static double N2ce90867(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 0.13) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 0.13) {
      p = 1;
    } 
    return p;
  }
  static double N27391d68(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 0;
    } else if (((Double) i[4]).doubleValue() <= 38.38) {
      p = 0;
    } else if (((Double) i[4]).doubleValue() > 38.38) {
      p = 2;
    } 
    return p;
  }
  static double N116ab4e69(Object []i) {
    double p = Double.NaN;
    if (i[8] == null) {
      p = 3;
    } else if (((Double) i[8]).doubleValue() <= 20.3) {
    p = J48Classifier5.N148aa2370(i);
    } else if (((Double) i[8]).doubleValue() > 20.3) {
    p = J48Classifier5.N1db4f6f88(i);
    } 
    return p;
  }
  static double N148aa2370(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 3;
    } else if (((Double) i[1]).doubleValue() <= 0.15) {
    p = J48Classifier5.N199f91c71(i);
    } else if (((Double) i[1]).doubleValue() > 0.15) {
    p = J48Classifier5.N143423476(i);
    } 
    return p;
  }
  static double N199f91c71(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 3;
    } else if (((Double) i[3]).doubleValue() <= 31.39) {
    p = J48Classifier5.N1b1aa6572(i);
    } else if (((Double) i[3]).doubleValue() > 31.39) {
    p = J48Classifier5.N13f304574(i);
    } 
    return p;
  }
  static double N1b1aa6572(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 3;
    } else if (((Double) i[0]).doubleValue() <= 0.04) {
    p = J48Classifier5.N129f3b573(i);
    } else if (((Double) i[0]).doubleValue() > 0.04) {
      p = 3;
    } 
    return p;
  }
  static double N129f3b573(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 3;
    } else if (((Double) i[7]).doubleValue() <= 7.37) {
      p = 3;
    } else if (((Double) i[7]).doubleValue() > 7.37) {
      p = 1;
    } 
    return p;
  }
  static double N13f304574(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 3;
    } else if (((Double) i[7]).doubleValue() <= 18.06) {
      p = 3;
    } else if (((Double) i[7]).doubleValue() > 18.06) {
    p = J48Classifier5.N17a29a175(i);
    } 
    return p;
  }
  static double N17a29a175(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 1;
    } else if (((Double) i[4]).doubleValue() <= 67.93) {
      p = 1;
    } else if (((Double) i[4]).doubleValue() > 67.93) {
      p = 3;
    } 
    return p;
  }
  static double N143423476(Object []i) {
    double p = Double.NaN;
    if (i[8] == null) {
      p = 3;
    } else if (((Double) i[8]).doubleValue() <= 12.06) {
    p = J48Classifier5.Naf835877(i);
    } else if (((Double) i[8]).doubleValue() > 12.06) {
    p = J48Classifier5.N15e83f983(i);
    } 
    return p;
  }
  static double Naf835877(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 3;
    } else if (((Double) i[2]).doubleValue() <= 39.82) {
    p = J48Classifier5.Nd80be378(i);
    } else if (((Double) i[2]).doubleValue() > 39.82) {
      p = 4;
    } 
    return p;
  }
  static double Nd80be378(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 3;
    } else if (((Double) i[5]).doubleValue() <= 22.29) {
    p = J48Classifier5.N1f4689e79(i);
    } else if (((Double) i[5]).doubleValue() > 22.29) {
    p = J48Classifier5.N112512781(i);
    } 
    return p;
  }
  static double N1f4689e79(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 3;
    } else if (((Double) i[1]).doubleValue() <= 0.3) {
      p = 3;
    } else if (((Double) i[1]).doubleValue() > 0.3) {
    p = J48Classifier5.N1006d7580(i);
    } 
    return p;
  }
  static double N1006d7580(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 4;
    } else if (((Double) i[5]).doubleValue() <= 19.8) {
      p = 4;
    } else if (((Double) i[5]).doubleValue() > 19.8) {
      p = 3;
    } 
    return p;
  }
  static double N112512781(Object []i) {
    double p = Double.NaN;
    if (i[8] == null) {
      p = 4;
    } else if (((Double) i[8]).doubleValue() <= 11.24) {
    p = J48Classifier5.N18dfef882(i);
    } else if (((Double) i[8]).doubleValue() > 11.24) {
      p = 3;
    } 
    return p;
  }
  static double N18dfef882(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 4;
    } else if (((Double) i[4]).doubleValue() <= 81.39) {
      p = 4;
    } else if (((Double) i[4]).doubleValue() > 81.39) {
      p = 3;
    } 
    return p;
  }
  static double N15e83f983(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 3;
    } else if (((Double) i[1]).doubleValue() <= 0.16) {
    p = J48Classifier5.N2a533084(i);
    } else if (((Double) i[1]).doubleValue() > 0.16) {
    p = J48Classifier5.Nbb746585(i);
    } 
    return p;
  }
  static double N2a533084(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 4;
    } else if (((Double) i[0]).doubleValue() <= 0.05) {
      p = 4;
    } else if (((Double) i[0]).doubleValue() > 0.05) {
      p = 3;
    } 
    return p;
  }
  static double Nbb746585(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 3;
    } else if (((Double) i[0]).doubleValue() <= 0.04) {
    p = J48Classifier5.Nd6c16c86(i);
    } else if (((Double) i[0]).doubleValue() > 0.04) {
      p = 3;
    } 
    return p;
  }
  static double Nd6c16c86(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() <= 30.36) {
    p = J48Classifier5.N134bed087(i);
    } else if (((Double) i[5]).doubleValue() > 30.36) {
      p = 3;
    } 
    return p;
  }
  static double N134bed087(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 1;
    } else if (((Double) i[2]).doubleValue() <= 24.24) {
      p = 1;
    } else if (((Double) i[2]).doubleValue() > 24.24) {
      p = 3;
    } 
    return p;
  }
  static double N1db4f6f88(Object []i) {
    double p = Double.NaN;
    if (i[8] == null) {
      p = 1;
    } else if (((Double) i[8]).doubleValue() <= 26.03) {
      p = 1;
    } else if (((Double) i[8]).doubleValue() > 26.03) {
    p = J48Classifier5.N13c1b0289(i);
    } 
    return p;
  }
  static double N13c1b0289(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 3;
    } else if (((Double) i[1]).doubleValue() <= -0.07) {
      p = 3;
    } else if (((Double) i[1]).doubleValue() > -0.07) {
    p = J48Classifier5.N11121f690(i);
    } 
    return p;
  }
  static double N11121f690(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 4;
    } else if (((Double) i[6]).doubleValue() <= 41.0) {
      p = 4;
    } else if (((Double) i[6]).doubleValue() > 41.0) {
    p = J48Classifier5.N1ccce3c91(i);
    } 
    return p;
  }
  static double N1ccce3c91(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 4;
    } else if (((Double) i[3]).doubleValue() <= 11.68) {
      p = 4;
    } else if (((Double) i[3]).doubleValue() > 11.68) {
      p = 3;
    } 
    return p;
  }
  static double Nf7f54092(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() <= 0.01) {
    p = J48Classifier5.N10655dd93(i);
    } else if (((Double) i[0]).doubleValue() > 0.01) {
    p = J48Classifier5.Nf99ff5118(i);
    } 
    return p;
  }
  static double N10655dd93(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 0;
    } else if (((Double) i[5]).doubleValue() <= 74.6) {
    p = J48Classifier5.Nef550294(i);
    } else if (((Double) i[5]).doubleValue() > 74.6) {
    p = J48Classifier5.N65a77f106(i);
    } 
    return p;
  }
  static double Nef550294(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 0;
    } else if (((Double) i[6]).doubleValue() <= 94.99) {
    p = J48Classifier5.Nb61fd195(i);
    } else if (((Double) i[6]).doubleValue() > 94.99) {
    p = J48Classifier5.N3ecfff104(i);
    } 
    return p;
  }
  static double Nb61fd195(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 0;
    } else if (((Double) i[1]).doubleValue() <= 0.01) {
    p = J48Classifier5.Ne2dae996(i);
    } else if (((Double) i[1]).doubleValue() > 0.01) {
    p = J48Classifier5.N19209ea97(i);
    } 
    return p;
  }
  static double Ne2dae996(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 0;
    } else if (((Double) i[1]).doubleValue() <= -0.05) {
      p = 0;
    } else if (((Double) i[1]).doubleValue() > -0.05) {
      p = 3;
    } 
    return p;
  }
  static double N19209ea97(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 4;
    } else if (((Double) i[6]).doubleValue() <= 34.0) {
      p = 4;
    } else if (((Double) i[6]).doubleValue() > 34.0) {
    p = J48Classifier5.Nc8f6f898(i);
    } 
    return p;
  }
  static double Nc8f6f898(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= 0.24) {
    p = J48Classifier5.N1ce2dd499(i);
    } else if (((Double) i[1]).doubleValue() > 0.24) {
    p = J48Classifier5.N1ef9157101(i);
    } 
    return p;
  }
  static double N1ce2dd499(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 1;
    } else if (((Double) i[2]).doubleValue() <= 15.56) {
      p = 1;
    } else if (((Double) i[2]).doubleValue() > 15.56) {
    p = J48Classifier5.N122cdb6100(i);
    } 
    return p;
  }
  static double N122cdb6100(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() <= 71.73) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() > 71.73) {
      p = 0;
    } 
    return p;
  }
  static double N1ef9157101(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() <= 72.32) {
    p = J48Classifier5.N12f0999102(i);
    } else if (((Double) i[5]).doubleValue() > 72.32) {
      p = 0;
    } 
    return p;
  }
  static double N12f0999102(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 1;
    } else if (((Double) i[3]).doubleValue() <= 13.3) {
    p = J48Classifier5.N11f2ee1103(i);
    } else if (((Double) i[3]).doubleValue() > 13.3) {
      p = 0;
    } 
    return p;
  }
  static double N11f2ee1103(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 0;
    } else if (((Double) i[5]).doubleValue() <= 70.63) {
      p = 0;
    } else if (((Double) i[5]).doubleValue() > 70.63) {
      p = 1;
    } 
    return p;
  }
  static double N3ecfff104(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 0;
    } else if (((Double) i[7]).doubleValue() <= 40.57) {
    p = J48Classifier5.N1c99159105(i);
    } else if (((Double) i[7]).doubleValue() > 40.57) {
      p = 4;
    } 
    return p;
  }
  static double N1c99159105(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() <= 0.0) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() > 0.0) {
      p = 2;
    } 
    return p;
  }
  static double N65a77f106(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() <= -0.02) {
    p = J48Classifier5.N1d7ad1c107(i);
    } else if (((Double) i[0]).doubleValue() > -0.02) {
    p = J48Classifier5.N18fd984111(i);
    } 
    return p;
  }
  static double N1d7ad1c107(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 4;
    } else if (((Double) i[4]).doubleValue() <= 21.5) {
    p = J48Classifier5.Na61164108(i);
    } else if (((Double) i[4]).doubleValue() > 21.5) {
    p = J48Classifier5.Nbfc8e0109(i);
    } 
    return p;
  }
  static double Na61164108(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 3;
    } else if (((Double) i[0]).doubleValue() <= -0.03) {
      p = 3;
    } else if (((Double) i[0]).doubleValue() > -0.03) {
      p = 4;
    } 
    return p;
  }
  static double Nbfc8e0109(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 3;
    } else if (((Double) i[1]).doubleValue() <= -0.43) {
      p = 3;
    } else if (((Double) i[1]).doubleValue() > -0.43) {
    p = J48Classifier5.N11d0a4f110(i);
    } 
    return p;
  }
  static double N11d0a4f110(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 4;
    } else if (((Double) i[6]).doubleValue() <= 39.58) {
      p = 4;
    } else if (((Double) i[6]).doubleValue() > 39.58) {
      p = 0;
    } 
    return p;
  }
  static double N18fd984111(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 0;
    } else if (((Double) i[5]).doubleValue() <= 93.66) {
    p = J48Classifier5.N111a775112(i);
    } else if (((Double) i[5]).doubleValue() > 93.66) {
      p = 0;
    } 
    return p;
  }
  static double N111a775112(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 0;
    } else if (((Double) i[1]).doubleValue() <= 0.11) {
    p = J48Classifier5.N91cee113(i);
    } else if (((Double) i[1]).doubleValue() > 0.11) {
    p = J48Classifier5.N1e0ff2f115(i);
    } 
    return p;
  }
  static double N91cee113(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 0;
    } else if (((Double) i[1]).doubleValue() <= -0.05) {
      p = 0;
    } else if (((Double) i[1]).doubleValue() > -0.05) {
    p = J48Classifier5.N4a63d8114(i);
    } 
    return p;
  }
  static double N4a63d8114(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 3;
    } else if (((Double) i[1]).doubleValue() <= 0.01) {
      p = 3;
    } else if (((Double) i[1]).doubleValue() > 0.01) {
      p = 0;
    } 
    return p;
  }
  static double N1e0ff2f115(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 4;
    } else if (((Double) i[6]).doubleValue() <= 38.68) {
      p = 4;
    } else if (((Double) i[6]).doubleValue() > 38.68) {
    p = J48Classifier5.N9173ef116(i);
    } 
    return p;
  }
  static double N9173ef116(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 0;
    } else if (((Double) i[1]).doubleValue() <= 0.14) {
    p = J48Classifier5.N152513a117(i);
    } else if (((Double) i[1]).doubleValue() > 0.14) {
      p = 0;
    } 
    return p;
  }
  static double N152513a117(Object []i) {
    double p = Double.NaN;
    if (i[8] == null) {
      p = 0;
    } else if (((Double) i[8]).doubleValue() <= 21.74) {
      p = 0;
    } else if (((Double) i[8]).doubleValue() > 21.74) {
      p = 4;
    } 
    return p;
  }
  static double Nf99ff5118(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 4;
    } else if (((Double) i[4]).doubleValue() <= 25.14) {
      p = 4;
    } else if (((Double) i[4]).doubleValue() > 25.14) {
    p = J48Classifier5.N74c3aa119(i);
    } 
    return p;
  }
  static double N74c3aa119(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 0;
    } else if (((Double) i[2]).doubleValue() <= 12.34) {
      p = 0;
    } else if (((Double) i[2]).doubleValue() > 12.34) {
    p = J48Classifier5.N1d9fd51120(i);
    } 
    return p;
  }
  static double N1d9fd51120(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 0;
    } else if (((Double) i[1]).doubleValue() <= 0.12) {
    p = J48Classifier5.N860d49121(i);
    } else if (((Double) i[1]).doubleValue() > 0.12) {
      p = 3;
    } 
    return p;
  }
  static double N860d49121(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 3;
    } else if (((Double) i[5]).doubleValue() <= 82.67) {
    p = J48Classifier5.Nd251a3122(i);
    } else if (((Double) i[5]).doubleValue() > 82.67) {
      p = 0;
    } 
    return p;
  }
  static double Nd251a3122(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 3;
    } else if (((Double) i[4]).doubleValue() <= 82.36) {
      p = 3;
    } else if (((Double) i[4]).doubleValue() > 82.36) {
      p = 0;
    } 
    return p;
  }
}

