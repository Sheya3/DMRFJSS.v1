/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package yimei.jss.gp.data;

import ec.gp.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The GPData that stores double value.
 *
 * @author yimei
 *
 */

public class DoubleData extends GPData {

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;


	public double value;    // return value

	public List<Double> valueSet = new ArrayList<>(); //LIUFEGE it's multree, and have many output

    public void copyTo(final GPData gpd) {
    	((DoubleData)gpd).value = value;
    }

}


