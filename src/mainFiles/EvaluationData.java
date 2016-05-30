package mainFiles;

import java.io.Serializable;

public class EvaluationData implements Serializable {

	private static final long serialVersionUID = 17L;
	double[] weights;
	int numberOfWeights;

	public EvaluationData(int _numberOfWeights) {
		numberOfWeights = _numberOfWeights;
		weights = new double[] { 0, 0, 0, 0 };

	}

}
