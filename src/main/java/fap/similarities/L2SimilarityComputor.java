package fap.similarities;

import fap.core.data.DataPointSerie;
import fap.core.exceptions.IncomparableSeriesException;
import fap.core.series.Serie;

/**
 * L2 similarity computor, assumes that series are sorted by time (x axes) component.
 * Series must be the same length.
 * @author Zoltan Geller
 * @version 19.07.2010.
 */
public class L2SimilarityComputor extends SerializableSimilarityComputor {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new L2SimilarityComputor object.
	 */
	public L2SimilarityComputor() {}

	/** 
	 * @throws IncomparableSeriesException if the series aren't the same length
	 */
	@Override
	public double similarity(Serie serie1, Serie serie2) throws IncomparableSeriesException {
		
		double result=0;
		
		DataPointSerie data1 = serie1.getData();
		DataPointSerie data2 = serie2.getData();
		
		int len1 = data1.getPointsCount();
		int len2 = data2.getPointsCount();
		
		if (len1!=len2)
			throw new IncomparableSeriesException();
		
		for (int i=0; i<len1; i++)
		{
			double x1 = data1.getPoint(i).getX();
			double y1 = data1.getPoint(i).getY();
			double x2 = data2.getPoint(i).getX();
			double y2 = data2.getPoint(i).getY();
			
			if (x1!=x2)
				throw new IncomparableSeriesException();
			
			double tmp = y1 - y2;
			result += tmp*tmp;
		}
		
		return Math.sqrt(result);
		
	}
	
	@Override
	public String toString() {
		return this.getClass().getName();
	}

}