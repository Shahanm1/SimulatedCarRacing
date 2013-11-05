/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.janquadflieg.mrracer.behaviour;

import java.util.Properties;

/**
 * A component is just a submodule which has parameters
 *
 * @author quad
 */
public interface Component {

    public void setParameters(Properties params, String prefix);

    public void getParameters(Properties params, String prefix);

    public void paint(String baseFileName, java.awt.Dimension d);
}