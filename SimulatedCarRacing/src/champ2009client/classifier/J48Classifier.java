package champ2009client.classifier;

public class J48Classifier extends Classifier {

    public double classify(Object[] i) throws Exception {
        double p = Double.NaN;
        p = J48Classifier.N6d084b0(i);
        return p;
    }
    static double N6d084b0(Object[] i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 2;
        } else if (((Double) i[11]).doubleValue() <= 70.27) {
            p = J48Classifier.N3bb2b81(i);
        } else if (((Double) i[11]).doubleValue() > 70.27) {
            p = J48Classifier.N77158a50(i);
        }
        return p;
    }

    static double N3bb2b81(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 0.01) {
            p = J48Classifier.N152544e2(i);
        } else if (((Double) i[0]).doubleValue() > 0.01) {
            p = J48Classifier.N16a78643(i);
        }
        return p;
    }

    static double N152544e2(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= -0.02) {
            p = J48Classifier.N1cdeff3(i);
        } else if (((Double) i[0]).doubleValue() > -0.02) {
            p = J48Classifier.N1c6f57917(i);
        }
        return p;
    }

    static double N1cdeff3(Object[] i) {
        double p = Double.NaN;
        if (i[14] == null) {
            p = 2;
        } else if (((Double) i[14]).doubleValue() <= 18.52) {
            p = J48Classifier.N17471e04(i);
        } else if (((Double) i[14]).doubleValue() > 18.52) {
            p = J48Classifier.N1d2068d7(i);
        }
        return p;
    }

    static double N17471e04(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= -0.36) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() > -0.36) {
            p = J48Classifier.N1e04cbf5(i);
        }
        return p;
    }

    static double N1e04cbf5(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 0.09) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > 0.09) {
            p = J48Classifier.Ncec0c56(i);
        }
        return p;
    }

    static double Ncec0c56(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 0.37) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() > 0.37) {
            p = 2;
        }
        return p;
    }

    static double N1d2068d7(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= -0.05) {
            p = J48Classifier.N1ac2f9c8(i);
        } else if (((Double) i[0]).doubleValue() > -0.05) {
            p = J48Classifier.N66e81510(i);
        }
        return p;
    }

    static double N1ac2f9c8(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= -0.41) {
            p = J48Classifier.N169ca659(i);
        } else if (((Double) i[1]).doubleValue() > -0.41) {
            p = 2;
        }
        return p;
    }

    static double N169ca659(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= -0.45) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() > -0.45) {
            p = 2;
        }
        return p;
    }

    static double N66e81510(Object[] i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 2;
        } else if (((Double) i[3]).doubleValue() <= 8.04) {
            p = J48Classifier.Nece6511(i);
        } else if (((Double) i[3]).doubleValue() > 8.04) {
            p = J48Classifier.Nedc3a216(i);
        }
        return p;
    }

    static double Nece6511(Object[] i) {
        double p = Double.NaN;
        if (i[19] == null) {
            p = 2;
        } else if (((Double) i[19]).doubleValue() <= 9.07) {
            p = J48Classifier.N10608212(i);
        } else if (((Double) i[19]).doubleValue() > 9.07) {
            p = J48Classifier.N1301ed813(i);
        }
        return p;
    }

    static double N10608212(Object[] i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() <= 4.84) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() > 4.84) {
            p = 2;
        }
        return p;
    }

    static double N1301ed813(Object[] i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() <= 4.59) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() > 4.59) {
            p = J48Classifier.N3901c614(i);
        }
        return p;
    }

    static double N3901c614(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 0.48) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > 0.48) {
            p = J48Classifier.Na3736815(i);
        }
        return p;
    }

    static double Na3736815(Object[] i) {
        double p = Double.NaN;
        if (i[14] == null) {
            p = 1;
        } else if (((Double) i[14]).doubleValue() <= 72.31) {
            p = 1;
        } else if (((Double) i[14]).doubleValue() > 72.31) {
            p = 2;
        }
        return p;
    }

    static double Nedc3a216(Object[] i) {
        double p = Double.NaN;
        if (i[13] == null) {
            p = 1;
        } else if (((Double) i[13]).doubleValue() <= 76.28) {
            p = 1;
        } else if (((Double) i[13]).doubleValue() > 76.28) {
            p = 2;
        }
        return p;
    }

    static double N1c6f57917(Object[] i) {
        double p = Double.NaN;
        if (i[13] == null) {
            p = 1;
        } else if (((Double) i[13]).doubleValue() <= 43.33) {
            p = J48Classifier.N11ddcde18(i);
        } else if (((Double) i[13]).doubleValue() > 43.33) {
            p = J48Classifier.Nb8f82d34(i);
        }
        return p;
    }

    static double N11ddcde18(Object[] i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 1;
        } else if (((Double) i[9]).doubleValue() <= 64.75) {
            p = J48Classifier.N18fb1f719(i);
        } else if (((Double) i[9]).doubleValue() > 64.75) {
            p = J48Classifier.Nd0a5d931(i);
        }
        return p;
    }

    static double N18fb1f719(Object[] i) {
        double p = Double.NaN;
        if (i[20] == null) {
            p = 1;
        } else if (((Double) i[20]).doubleValue() <= 7.63) {
            p = J48Classifier.Ned033820(i);
        } else if (((Double) i[20]).doubleValue() > 7.63) {
            p = J48Classifier.Nb988a627(i);
        }
        return p;
    }

    static double Ned033820(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 0.15) {
            p = J48Classifier.N6e70c721(i);
        } else if (((Double) i[1]).doubleValue() > 0.15) {
            p = 2;
        }
        return p;
    }

    static double N6e70c721(Object[] i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 12.34) {
            p = J48Classifier.Nae506e22(i);
        } else if (((Double) i[2]).doubleValue() > 12.34) {
            p = J48Classifier.N8f4fb326(i);
        }
        return p;
    }

    static double Nae506e22(Object[] i) {
        double p = Double.NaN;
        if (i[14] == null) {
            p = 2;
        } else if (((Double) i[14]).doubleValue() <= 6.02) {
            p = J48Classifier.N228a0223(i);
        } else if (((Double) i[14]).doubleValue() > 6.02) {
            p = J48Classifier.N192b99624(i);
        }
        return p;
    }

    static double N228a0223(Object[] i) {
        double p = Double.NaN;
        if (i[16] == null) {
            p = 2;
        } else if (((Double) i[16]).doubleValue() <= 3.94) {
            p = 2;
        } else if (((Double) i[16]).doubleValue() > 3.94) {
            p = 1;
        }
        return p;
    }

    static double N192b99624(Object[] i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 1;
        } else if (((Double) i[10]).doubleValue() <= 70.41) {
            p = 1;
        } else if (((Double) i[10]).doubleValue() > 70.41) {
            p = J48Classifier.N1d63e3925(i);
        }
        return p;
    }

    static double N1d63e3925(Object[] i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 1;
        } else if (((Double) i[9]).doubleValue() <= 24.62) {
            p = 1;
        } else if (((Double) i[9]).doubleValue() > 24.62) {
            p = 2;
        }
        return p;
    }

    static double N8f4fb326(Object[] i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 0;
        } else if (((Double) i[6]).doubleValue() <= 16.27) {
            p = 0;
        } else if (((Double) i[6]).doubleValue() > 16.27) {
            p = 1;
        }
        return p;
    }

    static double Nb988a627(Object[] i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 1;
        } else if (((Double) i[8]).doubleValue() <= 6.02) {
            p = J48Classifier.Nba6c8328(i);
        } else if (((Double) i[8]).doubleValue() > 6.02) {
            p = J48Classifier.N12a1e4429(i);
        }
        return p;
    }

    static double Nba6c8328(Object[] i) {
        double p = Double.NaN;
        if (i[18] == null) {
            p = 1;
        } else if (((Double) i[18]).doubleValue() <= 9.61) {
            p = 1;
        } else if (((Double) i[18]).doubleValue() > 9.61) {
            p = 2;
        }
        return p;
    }

    static double N12a1e4429(Object[] i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 1;
        } else if (((Double) i[12]).doubleValue() <= 29.02) {
            p = J48Classifier.N29428e30(i);
        } else if (((Double) i[12]).doubleValue() > 29.02) {
            p = 1;
        }
        return p;
    }

    static double N29428e30(Object[] i) {
        double p = Double.NaN;
        if (i[20] == null) {
            p = 1;
        } else if (((Double) i[20]).doubleValue() <= 10.04) {
            p = 1;
        } else if (((Double) i[20]).doubleValue() > 10.04) {
            p = 2;
        }
        return p;
    }

    static double Nd0a5d931(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 0.02) {
            p = J48Classifier.N38899332(i);
        } else if (((Double) i[1]).doubleValue() > 0.02) {
            p = 1;
        }
        return p;
    }

    static double N38899332(Object[] i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 2;
        } else if (((Double) i[8]).doubleValue() <= 16.48) {
            p = J48Classifier.N1d0465333(i);
        } else if (((Double) i[8]).doubleValue() > 16.48) {
            p = 2;
        }
        return p;
    }

    static double N1d0465333(Object[] i) {
        double p = Double.NaN;
        if (i[20] == null) {
            p = 1;
        } else if (((Double) i[20]).doubleValue() <= 5.47) {
            p = 1;
        } else if (((Double) i[20]).doubleValue() > 5.47) {
            p = 2;
        }
        return p;
    }

    static double Nb8f82d34(Object[] i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 2;
        } else if (((Double) i[8]).doubleValue() <= 14.32) {
            p = J48Classifier.N1ad77a735(i);
        } else if (((Double) i[8]).doubleValue() > 14.32) {
            p = 1;
        }
        return p;
    }

    static double N1ad77a735(Object[] i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() <= 5.8) {
            p = J48Classifier.N18aaa1e36(i);
        } else if (((Double) i[3]).doubleValue() > 5.8) {
            p = J48Classifier.N72ffb41(i);
        }
        return p;
    }

    static double N18aaa1e36(Object[] i) {
        double p = Double.NaN;
        if (i[19] == null) {
            p = 1;
        } else if (((Double) i[19]).doubleValue() <= 13.64) {
            p = J48Classifier.Na6aeed37(i);
        } else if (((Double) i[19]).doubleValue() > 13.64) {
            p = 2;
        }
        return p;
    }

    static double Na6aeed37(Object[] i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() <= 3.18) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() > 3.18) {
            p = J48Classifier.N126804e38(i);
        }
        return p;
    }

    static double N126804e38(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 0.27) {
            p = J48Classifier.Nb1b4c339(i);
        } else if (((Double) i[1]).doubleValue() > 0.27) {
            p = J48Classifier.Nd2906a40(i);
        }
        return p;
    }

    static double Nb1b4c339(Object[] i) {
        double p = Double.NaN;
        if (i[19] == null) {
            p = 1;
        } else if (((Double) i[19]).doubleValue() <= 6.74) {
            p = 1;
        } else if (((Double) i[19]).doubleValue() > 6.74) {
            p = 2;
        }
        return p;
    }

    static double Nd2906a40(Object[] i) {
        double p = Double.NaN;
        if (i[20] == null) {
            p = 1;
        } else if (((Double) i[20]).doubleValue() <= 13.4) {
            p = 1;
        } else if (((Double) i[20]).doubleValue() > 13.4) {
            p = 2;
        }
        return p;
    }

    static double N72ffb41(Object[] i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 1;
        } else if (((Double) i[11]).doubleValue() <= 36.82) {
            p = J48Classifier.N1df38fd42(i);
        } else if (((Double) i[11]).doubleValue() > 36.82) {
            p = 2;
        }
        return p;
    }

    static double N1df38fd42(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 0.19) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() > 0.19) {
            p = 2;
        }
        return p;
    }

    static double N16a78643(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 0.46) {
            p = J48Classifier.N1507fb244(i);
        } else if (((Double) i[1]).doubleValue() > 0.46) {
            p = J48Classifier.N161f10f47(i);
        }
        return p;
    }

    static double N1507fb244(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 0.04) {
            p = J48Classifier.N1efb83645(i);
        } else if (((Double) i[0]).doubleValue() > 0.04) {
            p = 2;
        }
        return p;
    }

    static double N1efb83645(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= -0.14) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > -0.14) {
            p = J48Classifier.N126e85f46(i);
        }
        return p;
    }

    static double N126e85f46(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 0.11) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() > 0.11) {
            p = 2;
        }
        return p;
    }

    static double N161f10f47(Object[] i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 2;
        } else if (((Double) i[9]).doubleValue() <= 59.57) {
            p = J48Classifier.N119377948(i);
        } else if (((Double) i[9]).doubleValue() > 59.57) {
            p = 1;
        }
        return p;
    }

    static double N119377948(Object[] i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() <= 5.61) {
            p = J48Classifier.N8916a249(i);
        } else if (((Double) i[7]).doubleValue() > 5.61) {
            p = 2;
        }
        return p;
    }

    static double N8916a249(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 0.04) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() > 0.04) {
            p = 2;
        }
        return p;
    }

    static double N77158a50(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 0.01) {
            p = J48Classifier.N27391d51(i);
        } else if (((Double) i[0]).doubleValue() > 0.01) {
            p = J48Classifier.Nd80be361(i);
        }
        return p;
    }

    static double N27391d51(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= -0.02) {
            p = J48Classifier.N116ab4e52(i);
        } else if (((Double) i[0]).doubleValue() > -0.02) {
            p = J48Classifier.N129f3b556(i);
        }
        return p;
    }

    static double N116ab4e52(Object[] i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 2;
        } else if (((Double) i[9]).doubleValue() <= 18.41) {
            p = J48Classifier.N148aa2353(i);
        } else if (((Double) i[9]).doubleValue() > 18.41) {
            p = 0;
        }
        return p;
    }

    static double N148aa2353(Object[] i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 2;
        } else if (((Double) i[12]).doubleValue() <= 73.7) {
            p = 2;
        } else if (((Double) i[12]).doubleValue() > 73.7) {
            p = J48Classifier.N199f91c54(i);
        }
        return p;
    }

    static double N199f91c54(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 0.44) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() > 0.44) {
            p = J48Classifier.N1b1aa6555(i);
        }
        return p;
    }

    static double N1b1aa6555(Object[] i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= 4.53) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() > 4.53) {
            p = 2;
        }
        return p;
    }

    static double N129f3b556(Object[] i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 0;
        } else if (((Double) i[11]).doubleValue() <= 92.54) {
            p = J48Classifier.N13f304557(i);
        } else if (((Double) i[11]).doubleValue() > 92.54) {
            p = 0;
        }
        return p;
    }

    static double N13f304557(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 0.28) {
            p = J48Classifier.N17a29a158(i);
        } else if (((Double) i[1]).doubleValue() > 0.28) {
            p = 0;
        }
        return p;
    }

    static double N17a29a158(Object[] i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() <= 6.2) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() > 6.2) {
            p = J48Classifier.N143423459(i);
        }
        return p;
    }

    static double N143423459(Object[] i) {
        double p = Double.NaN;
        if (i[16] == null) {
            p = 0;
        } else if (((Double) i[16]).doubleValue() <= 13.67) {
            p = 0;
        } else if (((Double) i[16]).doubleValue() > 13.67) {
            p = J48Classifier.Naf835860(i);
        }
        return p;
    }

    static double Naf835860(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 0.14) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > 0.14) {
            p = 0;
        }
        return p;
    }

    static double Nd80be361(Object[] i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 2;
        } else if (((Double) i[4]).doubleValue() <= 8.91) {
            p = J48Classifier.N1f4689e62(i);
        } else if (((Double) i[4]).doubleValue() > 8.91) {
            p = 0;
        }
        return p;
    }

    static double N1f4689e62(Object[] i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 0;
        } else if (((Double) i[7]).doubleValue() <= 9.06) {
            p = J48Classifier.N1006d7563(i);
        } else if (((Double) i[7]).doubleValue() > 9.06) {
            p = 2;
        }
        return p;
    }

    static double N1006d7563(Object[] i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 2;
        } else if (((Double) i[3]).doubleValue() <= 3.91) {
            p = 2;
        } else if (((Double) i[3]).doubleValue() > 3.91) {
            p = 0;
        }
        return p;
    }
}
