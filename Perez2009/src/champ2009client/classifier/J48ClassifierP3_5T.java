package champ2009client.classifier;

public class J48ClassifierP3_5T extends Classifier {

    public double classify(Object[] i) throws Exception {
        double p = Double.NaN;
        p = J48ClassifierP3_5T.N18558d20(i);
        return p;
    }
     static double N18558d20(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 2;
    } else if (((Double) i[3]).doubleValue() <= 70.27) {
    p = J48ClassifierP3_5T.N18a47e01(i);
    } else if (((Double) i[3]).doubleValue() > 70.27) {
    p = J48ClassifierP3_5T.Ne2eec890(i);
    } 
    return p;
  }
  static double N18a47e01(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 2;
    } else if (((Double) i[3]).doubleValue() <= 30.2) {
    p = J48ClassifierP3_5T.N174cc1f2(i);
    } else if (((Double) i[3]).doubleValue() > 30.2) {
    p = J48ClassifierP3_5T.Nb02e7a44(i);
    } 
    return p;
  }
  static double N174cc1f2(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 61.02) {
    p = J48ClassifierP3_5T.N1050e1f3(i);
    } else if (((Double) i[1]).doubleValue() > 61.02) {
      p = 2;
    } 
    return p;
  }
  static double N1050e1f3(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() <= 26.62) {
    p = J48ClassifierP3_5T.Ne24e2a4(i);
    } else if (((Double) i[2]).doubleValue() > 26.62) {
    p = J48ClassifierP3_5T.Ndd5b27(i);
    } 
    return p;
  }
  static double Ne24e2a4(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() <= 10.56) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() > 10.56) {
    p = J48ClassifierP3_5T.Nd1e6045(i);
    } 
    return p;
  }
  static double Nd1e6045(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 2;
    } else if (((Double) i[5]).doubleValue() <= 93.3) {
    p = J48ClassifierP3_5T.N54172f6(i);
    } else if (((Double) i[5]).doubleValue() > 93.3) {
      p = 2;
    } 
    return p;
  }
  static double N54172f6(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 0.28) {
    p = J48ClassifierP3_5T.Nbe23587(i);
    } else if (((Double) i[0]).doubleValue() > 0.28) {
    p = J48ClassifierP3_5T.N111370818(i);
    } 
    return p;
  }
  static double Nbe23587(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 0.1) {
    p = J48ClassifierP3_5T.N1027b4d8(i);
    } else if (((Double) i[0]).doubleValue() > 0.1) {
      p = 2;
    } 
    return p;
  }
  static double N1027b4d8(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 2;
    } else if (((Double) i[4]).doubleValue() <= 36.98) {
    p = J48ClassifierP3_5T.N1ed2ae89(i);
    } else if (((Double) i[4]).doubleValue() > 36.98) {
    p = J48ClassifierP3_5T.N758fc916(i);
    } 
    return p;
  }
  static double N1ed2ae89(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= -0.02) {
    p = J48ClassifierP3_5T.N19c26f510(i);
    } else if (((Double) i[0]).doubleValue() > -0.02) {
    p = J48ClassifierP3_5T.N145d06815(i);
    } 
    return p;
  }
  static double N19c26f510(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 2;
    } else if (((Double) i[4]).doubleValue() <= 26.75) {
    p = J48ClassifierP3_5T.Nc1b53111(i);
    } else if (((Double) i[4]).doubleValue() > 26.75) {
    p = J48ClassifierP3_5T.Nab50cd14(i);
    } 
    return p;
  }
  static double Nc1b53111(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() <= 20.66) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() > 20.66) {
    p = J48ClassifierP3_5T.N15eb0a912(i);
    } 
    return p;
  }
  static double N15eb0a912(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 33.93) {
    p = J48ClassifierP3_5T.N1a0530813(i);
    } else if (((Double) i[1]).doubleValue() > 33.93) {
      p = 2;
    } 
    return p;
  }
  static double N1a0530813(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() <= 23.01) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() > 23.01) {
      p = 1;
    } 
    return p;
  }
  static double Nab50cd14(Object []i) {
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
  static double N145d06815(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 2;
    } else if (((Double) i[3]).doubleValue() <= 19.6) {
      p = 2;
    } else if (((Double) i[3]).doubleValue() > 19.6) {
      p = 1;
    } 
    return p;
  }
  static double N758fc916(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() <= 16.68) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() > 16.68) {
    p = J48ClassifierP3_5T.N32fb4f17(i);
    } 
    return p;
  }
  static double N32fb4f17(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= -0.05) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() > -0.05) {
      p = 1;
    } 
    return p;
  }
  static double N111370818(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 2;
    } else if (((Double) i[3]).doubleValue() <= 24.93) {
    p = J48ClassifierP3_5T.N133f1d719(i);
    } else if (((Double) i[3]).doubleValue() > 24.93) {
    p = J48ClassifierP3_5T.N89fbe325(i);
    } 
    return p;
  }
  static double N133f1d719(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 0.29) {
    p = J48ClassifierP3_5T.N14a997220(i);
    } else if (((Double) i[0]).doubleValue() > 0.29) {
    p = J48ClassifierP3_5T.N14d334322(i);
    } 
    return p;
  }
  static double N14a997220(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= 11.89) {
    p = J48ClassifierP3_5T.Na0133521(i);
    } else if (((Double) i[1]).doubleValue() > 11.89) {
      p = 2;
    } 
    return p;
  }
  static double Na0133521(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 8.61) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 8.61) {
      p = 1;
    } 
    return p;
  }
  static double N14d334322(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 9.61) {
    p = J48ClassifierP3_5T.N1608e0523(i);
    } else if (((Double) i[1]).doubleValue() > 9.61) {
      p = 2;
    } 
    return p;
  }
  static double N1608e0523(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.47) {
    p = J48ClassifierP3_5T.Nbf32c24(i);
    } else if (((Double) i[0]).doubleValue() > 0.47) {
      p = 2;
    } 
    return p;
  }
  static double Nbf32c24(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 0.44) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() > 0.44) {
      p = 1;
    } 
    return p;
  }
  static double N89fbe325(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.48) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() > 0.48) {
    p = J48ClassifierP3_5T.Nf8184326(i);
    } 
    return p;
  }
  static double Nf8184326(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() <= 16.68) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() > 16.68) {
      p = 1;
    } 
    return p;
  }
  static double Ndd5b27(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= -0.3) {
    p = J48ClassifierP3_5T.Nc4bcdc28(i);
    } else if (((Double) i[0]).doubleValue() > -0.3) {
    p = J48ClassifierP3_5T.N1100d7a31(i);
    } 
    return p;
  }
  static double Nc4bcdc28(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 2;
    } else if (((Double) i[4]).doubleValue() <= 13.29) {
    p = J48ClassifierP3_5T.N4b433329(i);
    } else if (((Double) i[4]).doubleValue() > 13.29) {
      p = 1;
    } 
    return p;
  }
  static double N4b433329(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 2;
    } else if (((Double) i[4]).doubleValue() <= 12.7) {
      p = 2;
    } else if (((Double) i[4]).doubleValue() > 12.7) {
    p = J48ClassifierP3_5T.N128e20a30(i);
    } 
    return p;
  }
  static double N128e20a30(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= -0.41) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() > -0.41) {
      p = 2;
    } 
    return p;
  }
  static double N1100d7a31(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 0.46) {
    p = J48ClassifierP3_5T.Ne4f97232(i);
    } else if (((Double) i[0]).doubleValue() > 0.46) {
    p = J48ClassifierP3_5T.N1d609643(i);
    } 
    return p;
  }
  static double Ne4f97232(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 0.33) {
    p = J48ClassifierP3_5T.Nb4d3d533(i);
    } else if (((Double) i[0]).doubleValue() > 0.33) {
      p = 2;
    } 
    return p;
  }
  static double Nb4d3d533(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= -0.14) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() > -0.14) {
    p = J48ClassifierP3_5T.N1bf52a534(i);
    } 
    return p;
  }
  static double N1bf52a534(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= -0.13) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() > -0.13) {
    p = J48ClassifierP3_5T.N1cafa9e35(i);
    } 
    return p;
  }
  static double N1cafa9e35(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= -0.06) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() > -0.06) {
    p = J48ClassifierP3_5T.N10b9d0436(i);
    } 
    return p;
  }
  static double N10b9d0436(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= -0.05) {
    p = J48ClassifierP3_5T.N171732b37(i);
    } else if (((Double) i[0]).doubleValue() > -0.05) {
    p = J48ClassifierP3_5T.N140453638(i);
    } 
    return p;
  }
  static double N171732b37(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() <= 15.63) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() > 15.63) {
      p = 2;
    } 
    return p;
  }
  static double N140453638(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 0.32) {
    p = J48ClassifierP3_5T.N7fdcde39(i);
    } else if (((Double) i[0]).doubleValue() > 0.32) {
    p = J48ClassifierP3_5T.N86f24141(i);
    } 
    return p;
  }
  static double N7fdcde39(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() <= 35.11) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() > 35.11) {
    p = J48ClassifierP3_5T.N7d848340(i);
    } 
    return p;
  }
  static double N7d848340(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.16) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() > 0.16) {
      p = 2;
    } 
    return p;
  }
  static double N86f24141(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= 42.69) {
    p = J48ClassifierP3_5T.N18ac73842(i);
    } else if (((Double) i[1]).doubleValue() > 42.69) {
      p = 2;
    } 
    return p;
  }
  static double N18ac73842(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 2;
    } else if (((Double) i[5]).doubleValue() <= 15.9) {
      p = 2;
    } else if (((Double) i[5]).doubleValue() > 15.9) {
      p = 1;
    } 
    return p;
  }
  static double N1d609643(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() <= 27.93) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() > 27.93) {
      p = 1;
    } 
    return p;
  }
  static double Nb02e7a44(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 1;
    } else if (((Double) i[2]).doubleValue() <= 98.54) {
    p = J48ClassifierP3_5T.Nbb6ab645(i);
    } else if (((Double) i[2]).doubleValue() > 98.54) {
      p = 2;
    } 
    return p;
  }
  static double Nbb6ab645(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() <= 47.95) {
    p = J48ClassifierP3_5T.N5afd2946(i);
    } else if (((Double) i[5]).doubleValue() > 47.95) {
    p = J48ClassifierP3_5T.N1891d8f78(i);
    } 
    return p;
  }
  static double N5afd2946(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= 36.14) {
    p = J48ClassifierP3_5T.N1a2961b47(i);
    } else if (((Double) i[1]).doubleValue() > 36.14) {
    p = J48ClassifierP3_5T.Nc5135572(i);
    } 
    return p;
  }
  static double N1a2961b47(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 1;
    } else if (((Double) i[4]).doubleValue() <= 94.17) {
    p = J48ClassifierP3_5T.N12d03f948(i);
    } else if (((Double) i[4]).doubleValue() > 94.17) {
    p = J48ClassifierP3_5T.N1bf677066(i);
    } 
    return p;
  }
  static double N12d03f948(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.16) {
    p = J48ClassifierP3_5T.N5ffb1849(i);
    } else if (((Double) i[0]).doubleValue() > 0.16) {
    p = J48ClassifierP3_5T.N1f630dc55(i);
    } 
    return p;
  }
  static double N5ffb1849(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 1;
    } else if (((Double) i[4]).doubleValue() <= 22.11) {
    p = J48ClassifierP3_5T.N15dfd7750(i);
    } else if (((Double) i[4]).doubleValue() > 22.11) {
      p = 1;
    } 
    return p;
  }
  static double N15dfd7750(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= -0.07) {
    p = J48ClassifierP3_5T.N1abc7b951(i);
    } else if (((Double) i[0]).doubleValue() > -0.07) {
      p = 2;
    } 
    return p;
  }
  static double N1abc7b951(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 2;
    } else if (((Double) i[4]).doubleValue() <= 16.71) {
      p = 2;
    } else if (((Double) i[4]).doubleValue() > 16.71) {
    p = J48ClassifierP3_5T.Nc55e3652(i);
    } 
    return p;
  }
  static double Nc55e3652(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 1;
    } else if (((Double) i[3]).doubleValue() <= 68.8) {
    p = J48ClassifierP3_5T.N1ac3c0853(i);
    } else if (((Double) i[3]).doubleValue() > 68.8) {
      p = 0;
    } 
    return p;
  }
  static double N1ac3c0853(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= -0.26) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() > -0.26) {
    p = J48ClassifierP3_5T.N9971ad54(i);
    } 
    return p;
  }
  static double N9971ad54(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= -0.12) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() > -0.12) {
      p = 1;
    } 
    return p;
  }
  static double N1f630dc55(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 0.25) {
    p = J48ClassifierP3_5T.N1c5c156(i);
    } else if (((Double) i[0]).doubleValue() > 0.25) {
    p = J48ClassifierP3_5T.N5e060257(i);
    } 
    return p;
  }
  static double N1c5c156(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 2;
    } else if (((Double) i[4]).doubleValue() <= 75.79) {
      p = 2;
    } else if (((Double) i[4]).doubleValue() > 75.79) {
      p = 1;
    } 
    return p;
  }
  static double N5e060257(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 8.66) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 8.66) {
    p = J48ClassifierP3_5T.Ndc840f58(i);
    } 
    return p;
  }
  static double Ndc840f58(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 1;
    } else if (((Double) i[3]).doubleValue() <= 67.61) {
    p = J48ClassifierP3_5T.N1621e4259(i);
    } else if (((Double) i[3]).doubleValue() > 67.61) {
    p = J48ClassifierP3_5T.N55571e63(i);
    } 
    return p;
  }
  static double N1621e4259(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.49) {
    p = J48ClassifierP3_5T.Nb09e8960(i);
    } else if (((Double) i[0]).doubleValue() > 0.49) {
    p = J48ClassifierP3_5T.Nfa9cf62(i);
    } 
    return p;
  }
  static double Nb09e8960(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 1;
    } else if (((Double) i[4]).doubleValue() <= 86.38) {
      p = 1;
    } else if (((Double) i[4]).doubleValue() > 86.38) {
    p = J48ClassifierP3_5T.N178703861(i);
    } 
    return p;
  }
  static double N178703861(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 9.33) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 9.33) {
      p = 1;
    } 
    return p;
  }
  static double Nfa9cf62(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 12.3) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 12.3) {
      p = 1;
    } 
    return p;
  }
  static double N55571e63(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.48) {
    p = J48ClassifierP3_5T.Nca832764(i);
    } else if (((Double) i[0]).doubleValue() > 0.48) {
      p = 1;
    } 
    return p;
  }
  static double Nca832764(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 0;
    } else if (((Double) i[1]).doubleValue() <= 10.36) {
      p = 0;
    } else if (((Double) i[1]).doubleValue() > 10.36) {
    p = J48ClassifierP3_5T.N16897b265(i);
    } 
    return p;
  }
  static double N16897b265(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= 13.86) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() > 13.86) {
      p = 0;
    } 
    return p;
  }
  static double N1bf677066(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() <= 29.08) {
    p = J48ClassifierP3_5T.N1201a2567(i);
    } else if (((Double) i[2]).doubleValue() > 29.08) {
    p = J48ClassifierP3_5T.Na401c269(i);
    } 
    return p;
  }
  static double N1201a2567(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 0.19) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() > 0.19) {
    p = J48ClassifierP3_5T.N94948a68(i);
    } 
    return p;
  }
  static double N94948a68(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 0;
    } else if (((Double) i[2]).doubleValue() <= 24.75) {
      p = 0;
    } else if (((Double) i[2]).doubleValue() > 24.75) {
      p = 1;
    } 
    return p;
  }
  static double Na401c269(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 0;
    } else if (((Double) i[3]).doubleValue() <= 60.31) {
      p = 0;
    } else if (((Double) i[3]).doubleValue() > 60.31) {
    p = J48ClassifierP3_5T.N16f8cd070(i);
    } 
    return p;
  }
  static double N16f8cd070(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() <= 37.97) {
    p = J48ClassifierP3_5T.N85af8071(i);
    } else if (((Double) i[5]).doubleValue() > 37.97) {
      p = 1;
    } 
    return p;
  }
  static double N85af8071(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 0;
    } else if (((Double) i[2]).doubleValue() <= 38.36) {
      p = 0;
    } else if (((Double) i[2]).doubleValue() > 38.36) {
      p = 1;
    } 
    return p;
  }
  static double Nc5135572(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 2;
    } else if (((Double) i[4]).doubleValue() <= 29.76) {
    p = J48ClassifierP3_5T.N78717173(i);
    } else if (((Double) i[4]).doubleValue() > 29.76) {
      p = 1;
    } 
    return p;
  }
  static double N78717173(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= -0.09) {
    p = J48ClassifierP3_5T.N15fea6074(i);
    } else if (((Double) i[0]).doubleValue() > -0.09) {
    p = J48ClassifierP3_5T.Na3bcc176(i);
    } 
    return p;
  }
  static double N15fea6074(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= -0.1) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() > -0.1) {
    p = J48ClassifierP3_5T.N18fef3d75(i);
    } 
    return p;
  }
  static double N18fef3d75(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() <= 54.43) {
      p = 2;
    } else if (((Double) i[2]).doubleValue() > 54.43) {
      p = 1;
    } 
    return p;
  }
  static double Na3bcc176(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 0.13) {
    p = J48ClassifierP3_5T.N1bd472277(i);
    } else if (((Double) i[0]).doubleValue() > 0.13) {
      p = 2;
    } 
    return p;
  }
  static double N1bd472277(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 2;
    } else if (((Double) i[5]).doubleValue() <= 15.01) {
      p = 2;
    } else if (((Double) i[5]).doubleValue() > 15.01) {
      p = 1;
    } 
    return p;
  }
  static double N1891d8f78(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 18.7) {
    p = J48ClassifierP3_5T.Nf3d6a579(i);
    } else if (((Double) i[1]).doubleValue() > 18.7) {
    p = J48ClassifierP3_5T.N1f7d13487(i);
    } 
    return p;
  }
  static double Nf3d6a579(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 1;
    } else if (((Double) i[4]).doubleValue() <= 57.7) {
    p = J48ClassifierP3_5T.N911f7180(i);
    } else if (((Double) i[4]).doubleValue() > 57.7) {
    p = J48ClassifierP3_5T.N1b10d4285(i);
    } 
    return p;
  }
  static double N911f7180(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.27) {
    p = J48ClassifierP3_5T.N1a73d3c81(i);
    } else if (((Double) i[0]).doubleValue() > 0.27) {
      p = 1;
    } 
    return p;
  }
  static double N1a73d3c81(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.2) {
    p = J48ClassifierP3_5T.Na56a7c82(i);
    } else if (((Double) i[0]).doubleValue() > 0.2) {
      p = 2;
    } 
    return p;
  }
  static double Na56a7c82(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 1;
    } else if (((Double) i[3]).doubleValue() <= 31.4) {
    p = J48ClassifierP3_5T.N1f20eeb83(i);
    } else if (((Double) i[3]).doubleValue() > 31.4) {
      p = 1;
    } 
    return p;
  }
  static double N1f20eeb83(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 0.08) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() > 0.08) {
    p = J48ClassifierP3_5T.Nb179c384(i);
    } 
    return p;
  }
  static double Nb179c384(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 2;
    } else if (((Double) i[4]).doubleValue() <= 42.35) {
      p = 2;
    } else if (((Double) i[4]).doubleValue() > 42.35) {
      p = 1;
    } 
    return p;
  }
  static double N1b10d4285(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 0.36) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() > 0.36) {
    p = J48ClassifierP3_5T.Ndd87b286(i);
    } 
    return p;
  }
  static double Ndd87b286(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 12.42) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 12.42) {
      p = 1;
    } 
    return p;
  }
  static double N1f7d13487(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.16) {
    p = J48ClassifierP3_5T.Nc7e55388(i);
    } else if (((Double) i[0]).doubleValue() > 0.16) {
      p = 2;
    } 
    return p;
  }
  static double Nc7e55388(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() <= 52.66) {
    p = J48ClassifierP3_5T.N1a0c10f89(i);
    } else if (((Double) i[5]).doubleValue() > 52.66) {
      p = 1;
    } 
    return p;
  }
  static double N1a0c10f89(Object []i) {
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
  static double Ne2eec890(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 0;
    } else if (((Double) i[3]).doubleValue() <= 74.68) {
    p = J48ClassifierP3_5T.Naa983591(i);
    } else if (((Double) i[3]).doubleValue() > 74.68) {
    p = J48ClassifierP3_5T.Ne80a5997(i);
    } 
    return p;
  }
  static double Naa983591(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 0;
    } else if (((Double) i[4]).doubleValue() <= 44.24) {
    p = J48ClassifierP3_5T.N1eec61292(i);
    } else if (((Double) i[4]).doubleValue() > 44.24) {
    p = J48ClassifierP3_5T.N53ba3d96(i);
    } 
    return p;
  }
  static double N1eec61292(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() <= -0.03) {
    p = J48ClassifierP3_5T.N10dd1f793(i);
    } else if (((Double) i[0]).doubleValue() > -0.03) {
      p = 2;
    } 
    return p;
  }
  static double N10dd1f793(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 0;
    } else if (((Double) i[2]).doubleValue() <= 61.28) {
    p = J48ClassifierP3_5T.N53c01594(i);
    } else if (((Double) i[2]).doubleValue() > 61.28) {
      p = 2;
    } 
    return p;
  }
  static double N53c01594(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 0;
    } else if (((Double) i[5]).doubleValue() <= 15.56) {
    p = J48ClassifierP3_5T.N67ac1995(i);
    } else if (((Double) i[5]).doubleValue() > 15.56) {
      p = 0;
    } 
    return p;
  }
  static double N67ac1995(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= 20.13) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() > 20.13) {
      p = 0;
    } 
    return p;
  }
  static double N53ba3d96(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 0;
    } else if (((Double) i[5]).doubleValue() <= 40.57) {
      p = 0;
    } else if (((Double) i[5]).doubleValue() > 40.57) {
      p = 2;
    } 
    return p;
  }
  static double Ne80a5997(Object []i) {
    double p = Double.NaN;
    if (i[3] == null) {
      p = 0;
    } else if (((Double) i[3]).doubleValue() <= 95.41) {
    p = J48ClassifierP3_5T.N1ff5ea798(i);
    } else if (((Double) i[3]).doubleValue() > 95.41) {
    p = J48ClassifierP3_5T.N3e86d0109(i);
    } 
    return p;
  }
  static double N1ff5ea798(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 0;
    } else if (((Double) i[2]).doubleValue() <= 53.37) {
    p = J48ClassifierP3_5T.N9f2a0b99(i);
    } else if (((Double) i[2]).doubleValue() > 53.37) {
    p = J48ClassifierP3_5T.Nbfbdb0108(i);
    } 
    return p;
  }
  static double N9f2a0b99(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 0;
    } else if (((Double) i[1]).doubleValue() <= 21.13) {
    p = J48ClassifierP3_5T.N1813fac100(i);
    } else if (((Double) i[1]).doubleValue() > 21.13) {
      p = 0;
    } 
    return p;
  }
  static double N1813fac100(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 0;
    } else if (((Double) i[2]).doubleValue() <= 41.78) {
    p = J48ClassifierP3_5T.N7b7072101(i);
    } else if (((Double) i[2]).doubleValue() > 41.78) {
      p = 2;
    } 
    return p;
  }
  static double N7b7072101(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 0;
    } else if (((Double) i[2]).doubleValue() <= 21.48) {
    p = J48ClassifierP3_5T.N136228102(i);
    } else if (((Double) i[2]).doubleValue() > 21.48) {
    p = J48ClassifierP3_5T.N1c672d0104(i);
    } 
    return p;
  }
  static double N136228102(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 0;
    } else if (((Double) i[1]).doubleValue() <= 9.35) {
    p = J48ClassifierP3_5T.N913750103(i);
    } else if (((Double) i[1]).doubleValue() > 9.35) {
      p = 2;
    } 
    return p;
  }
  static double N913750103(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 8.48) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 8.48) {
      p = 0;
    } 
    return p;
  }
  static double N1c672d0104(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() <= 0.22) {
    p = J48ClassifierP3_5T.N19bd03e105(i);
    } else if (((Double) i[0]).doubleValue() > 0.22) {
      p = 0;
    } 
    return p;
  }
  static double N19bd03e105(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 0;
    } else if (((Double) i[4]).doubleValue() <= 42.8) {
      p = 0;
    } else if (((Double) i[4]).doubleValue() > 42.8) {
    p = J48ClassifierP3_5T.N84abc9106(i);
    } 
    return p;
  }
  static double N84abc9106(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 2;
    } else if (((Double) i[4]).doubleValue() <= 76.36) {
      p = 2;
    } else if (((Double) i[4]).doubleValue() > 76.36) {
    p = J48ClassifierP3_5T.N2a340e107(i);
    } 
    return p;
  }
  static double N2a340e107(Object []i) {
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
  static double Nbfbdb0108(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 34.26) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 34.26) {
      p = 0;
    } 
    return p;
  }
  static double N3e86d0109(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() <= 0.21) {
    p = J48ClassifierP3_5T.N1050169110(i);
    } else if (((Double) i[0]).doubleValue() > 0.21) {
      p = 0;
    } 
    return p;
  }
  static double N1050169110(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() <= 0.13) {
    p = J48ClassifierP3_5T.N19fcc69111(i);
    } else if (((Double) i[0]).doubleValue() > 0.13) {
    p = J48ClassifierP3_5T.N9fef6f113(i);
    } 
    return p;
  }
  static double N19fcc69111(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 0;
    } else if (((Double) i[1]).doubleValue() <= 18.28) {
    p = J48ClassifierP3_5T.N253498112(i);
    } else if (((Double) i[1]).doubleValue() > 18.28) {
      p = 0;
    } 
    return p;
  }
  static double N253498112(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 0;
    } else if (((Double) i[5]).doubleValue() <= 17.05) {
      p = 0;
    } else if (((Double) i[5]).doubleValue() > 17.05) {
      p = 2;
    } 
    return p;
  }
  static double N9fef6f113(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 2;
    } else if (((Double) i[5]).doubleValue() <= 29.61) {
      p = 2;
    } else if (((Double) i[5]).doubleValue() > 29.61) {
      p = 0;
    } 
    return p;
  }
}

